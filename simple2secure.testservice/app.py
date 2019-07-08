from flask import Flask, Response, session, json, request, render_template
from src.config.celery_config import make_celery
from flask_sqlalchemy import SQLAlchemy
from flask_marshmallow import Marshmallow
from flask_cors import CORS
from src.util import rest_utils
from src.util import file_utils
from src.util import json_utils
from apscheduler.schedulers.background import BackgroundScheduler
from src.models.CompanyLicensePod import CompanyLicensePod
from datetime import datetime
from scanner import scanner
import webbrowser
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
app.config['CELERY_BROKER_URL'] = 'redis://redis:6379'
app.config['CELERY_RESULT_BACKEND'] = 'redis://redis:6379'
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///pod.sqlite3'
app.config['POD_ID'] = ''
app.config['GROUP_ID'] = ''
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
    hash_value_service = db.Column(db.Text)

    def __init__(self, generated_id, hash_value_service):
        self.generated_id = generated_id
        self.hash_value_service = hash_value_service


class Test(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.Text)
    test_content = db.Column(db.Text)
    hash_value = db.Column(db.Text)

    def __init__(self, name, test_content, hash_value):
        self.name = name
        self.test_content = test_content
        self.hash_value = hash_value


class TestResultSchema(ma.ModelSchema):
    class Meta:
        model = TestResult


class PodInfoSchema(ma.ModelSchema):
    class Meta:
        model = PodInfo


class TestSchema(ma.ModelSchema):

    class Meta:
        model = Test


db.create_all()
db.session.commit()


@app.before_first_request
def init():
    session.clear()
    pod_info = PodInfo.query.first()
    # If there is not pod_info object in database, generate new pod_id and save object to db
    if pod_info is None:
        app.config['POD_ID'] = secrets.token_urlsafe(20)
        pod_info = PodInfo(app.config['POD_ID'], "")
        db.session.add(pod_info)
        db.session.commit()
    # if there is a podInfo object in database, set saved pod_id into the app.config[POD_ID] variable
    else:
        app.config['POD_ID'] = pod_info.generated_id

    try:
        auth_token_obj = rest_utils.get_auth_token_object(app)

        if auth_token_obj.status_code == 200:
            app.config['AUTH_TOKEN'] = auth_token_obj.text
            license_from_file = file_utils.get_license_file()
            app.licenseFile = file_utils.parse_license_file(license_from_file, app)
            app.config['LICENSE_ID'] = app.licenseFile.licenseId

            print(rest_utils.print_success_message_auth(app))

            scheduler = BackgroundScheduler()
            scheduler.add_job(func=check_configuration, trigger="interval",
                              seconds=15)
            scheduler.add_job(func=get_test_results_from_db, trigger="interval",
                              seconds=20)
            scheduler.start()

        else:
            print(rest_utils.print_error_message())
            shutdown_server()

    except requests.exceptions.ConnectionError:
            print(rest_utils.print_error_message())
            shutdown_server()


def check_configuration():

    test_array = rest_utils.portal_get(app.config['PORTAL_URL'] + "pod/scheduledTests/" + app.config['POD_ID'], app)

    for test in test_array:
        print(test)
        schedule_test.delay(test, test.id)
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
def schedule_test(test, test_id):
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
    test_result = TestResult("Result - " + timestamp.__str__(), json.dumps(results), test_id,
                             socket.gethostname(), timestamp, False)

    db.session.add(test_result)
    db.session.commit()


@celery.task(name='celery.send_test_results')
def send_test_results(test_result, auth_token):
        rest_utils.portal_post_celery(app.config['PORTAL_URL'] + "test/saveTestResult", test_result, auth_token)


@app.route("/services")
def parse_tests():
    tests_string = file_utils.read_json_testfile()
    resp = Response(tests_string, status=200, mimetype='application/json')
    return resp


@app.route("/results")
def show_test_results():
    test_results = TestResult.query.all()
    return render_template('testresults.html', len=len(test_results), test_results=test_results)


@app.route("/")
def index():
    return '<html><body><h1>HI</h1></body></html>'


@app.route("/services/run")
def run_service():
    response_text = ""

    response = file_utils.read_json_testfile()
    # response_json_object = json.loads(response)
    file_utils.update_insert_tests_to_db(response)

    if json_utils.is_blank(request.query_string) is True:
        tests = Test.query.all()

        for test in tests:
            current_test = json.loads(test.test_content)
            schedule_test.delay(current_test["test_definition"], test.id)

        response_text = "All available tests from services.json have been scheduled"

    else:
        test_name_response = request.args.get("test")
        db_test = Test.query.filter_by(name=test_name_response).first()

        if db_test is None:
            response_text = "Test with provided test name cannot be found"
        else:
            current_test = json.loads(db_test.test_content)
            schedule_test.delay(current_test["test_definition"], db_test.id)
            response_text = "Test " + test_name_response + " has been scheduled"

    return response_text


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


def shutdown_server():
    func = request.environ.get('werkzeug.server.shutdown')
    if func is None:
        raise RuntimeError('Not running with the Werkzeug Server')
    func()


if __name__ == '__main__':
    webbrowser.open('https://localhost:5000/services')
    app.run(debug=True, ssl_context='adhoc', host='0.0.0.0', threaded=True, use_reloader=True)
