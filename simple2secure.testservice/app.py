from flask import Flask, Response, session, json
from src.config.celery_config import make_celery
from flask_sqlalchemy import SQLAlchemy
from flask_marshmallow import Marshmallow
from flask_cors import CORS
from src.util import rest_utils
from src.util import file_utils
from src.util import json_utils
from src.models.CompanyLicensePod import CompanyLicensePod
from apscheduler.schedulers.background import BackgroundScheduler
from datetime import datetime
from scanner import scanner
import socket
import os
import threading
import urllib3
import secrets
import time
import requests


# Setting env variable for celery to work on windows
os.environ.setdefault('FORKED_BY_MULTIPROCESSING', '1')
urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)

# App initialization
app = Flask(__name__)
CORS(app)

# Setting some static variables
app.secret_key = "ChangeIt2019!"
app.config['CELERY_BROKER_URL'] = 'redis://localhost:6379'
app.config['CELERY_RESULT_BACKEND'] = 'redis://localhost:6379'
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///pod.sqlite3'
app.config['POD_ID'] = ''
app.config['LICENSE_ID'] = ''
app.config['PORTAL_URL'] = 'https://localhost:8443/api/'
app.config['AUTH_TOKEN'] = ''
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
celery = make_celery(app)
# DB, marshmallow and Celery initialization
db = SQLAlchemy(app)
ma = Marshmallow(app)

# https://144.76.93.104:51001/s2s/api/
licenseFile = CompanyLicensePod("", "", "", "", "")


class TestResult(db.Model):

    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(50))
    result = db.Column(db.Text)
    testId = db.Column(db.String(120))
    hostname = db.Column(db.String(120))
    timestamp = db.Column(db.String(120))
    isSent = db.Column(db.Boolean)

    def __init__(self, name, result, test_id, hostname, timestamp, is_sent):
        self.name = name
        self.result = result
        self.testId = test_id
        self.hostname = hostname
        self.timestamp = timestamp
        self.isSent = is_sent


class PodInfo(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    generated_id = db.Column(db.Text)

    def __init__(self, generated_id):
        self.generated_id = generated_id


class TestResultSchema(ma.ModelSchema):
    class Meta:
        model = TestResult


class PodInfoSchema(ma.ModelSchema):
    class Meta:
        model = PodInfo


db.create_all()
db.session.commit()


@app.before_first_request
def init():
    session.clear()
    print("-----------------------------")
    print("-------Initialization--------")
    print("-----------------------------")
    print(" * Extracting the pod license")

    pod_info = PodInfo.query.first()
    # If there is not pod_info object in database, generate new pod_id and save object to db
    if pod_info is None:
        app.config['POD_ID'] = secrets.token_urlsafe(20)
        pod_info = PodInfo(app.config['POD_ID'])
        db.session.add(pod_info)
        db.session.commit()
    # if there is a podInfo object in database, set saved pod_id into the app.config[POD_ID] variable
    else:
        app.config['POD_ID'] = pod_info.generated_id

    license_from_file = file_utils.get_license_file()
    app.licenseFile = file_utils.parse_license_file(license_from_file, app)
    app.config['LICENSE_ID'] = app.licenseFile.licenseId
    print(" * Pod License Id : " + app.licenseFile.licenseId)
    print(" * Pod Group Id : " + app.licenseFile.groupId)
    print(" * Pod Id : " + app.licenseFile.podId)
    print(" ****************************")
    app.config['AUTH_TOKEN'] = rest_utils.get_auth_token(app)
    scheduler = BackgroundScheduler()
    scheduler.add_job(func=check_configuration, trigger="interval",
                      seconds=15)
    scheduler.add_job(func=get_test_results_from_db, trigger="interval",
                      seconds=20)
    scheduler.start()
    print(" * Activating the license")
    print(" * Auth Token : " + app.config['AUTH_TOKEN'])
    print(" ****************************")
    print("-----------------------------")
    print("-----Initialization END------")
    print("-----------------------------")


def check_configuration():

    test_array = rest_utils.portal_get(app.config['PORTAL_URL'] + "pod/scheduledTests/" + app.config['POD_ID'], app)

    for test in test_array:

        schedule_test.delay(test)
        # rest_utils.send_notification(test["id"], "Test has been scheduled", app)


def get_test_results_from_db():
    test_results = TestResult.query.filter_by(isSent=False).all()
    for test_result in test_results:
        test_result_schema = TestResultSchema()
        output = test_result_schema.dump(test_result).data

        if not app.config['AUTH_TOKEN']:
            app.config['AUTH_TOKEN'] = rest_utils.get_auth_token(app)

        send_test_results.delay(output, app.config['AUTH_TOKEN'])
        # rest_utils.send_notification(test_result.testId, "Test has been finished", app)
        test_result.isSent = True
        db.session.commit()


@celery.task(name='celery.schedule_test')
def schedule_test(test):
    results = {}

    tool_precondition = json_utils.get_json_test_object_new(test, "precondition", "command")
    parameter_precondition = json_utils.get_json_test_object_new(test, "precondition", "parameter")
    tool_postcondition = json_utils.get_json_test_object_new(test, "postcondition", "command")
    parameter_postcondition = json_utils.get_json_test_object_new(test, "postcondition", "parameter")
    tool_step = json_utils.get_json_test_object_new(test, "step", "command")
    parameter_step = json_utils.get_json_test_object_new(test, "step", "parameter")

    precondition_scan = threading.Thread(target=scanner(json_utils.construct_command(json_utils.get_tool(
        tool_precondition), parameter_precondition), results, "precondition"))
    step_scan = threading.Thread(target=scanner(json_utils.construct_command(json_utils.get_tool(
        tool_step), parameter_step), results, "step"))
    postcondition_scan = threading.Thread(target=scanner(json_utils.construct_command(json_utils.get_tool(
        tool_postcondition), parameter_postcondition), results, "postcondition"))

    precondition_scan.start()
    step_scan.start()
    postcondition_scan.start()

    timestamp = datetime.now().timestamp() * 1000
    test_result = TestResult("Result - " + timestamp.__str__(), json.dumps(results), test['id'],
                             socket.gethostname(), timestamp, False)

    db.session.add(test_result)
    db.session.commit()


@celery.task(name='celery.send_test_results')
def send_test_results(test_result, auth_token):
        rest_utils.portal_post_celery(app.config['PORTAL_URL'] + "test/saveTestResult", test_result, auth_token)


@app.route("/services")
def parse_tests():
    tests_string = json_utils.read_json_testfile()
    resp = Response(tests_string, status=200, mimetype='application/json')
    return resp


@app.route("/")
def hello():
    return "Hello World!"


def start_runner():
    def start_loop():
        not_started = True

        while not_started:
            r = requests.get('https://localhost:5000/', verify=False)
            if r.status_code == 200:
                print('Server started, quiting start_loop')
                not_started = False
            time.sleep(2)

    print('Started runner thread')
    thread = threading.Thread(target=start_loop)
    thread.start()


if __name__ == '__main__':
    start_runner()
    app.run(ssl_context='adhoc', host='0.0.0.0', threaded=True)
