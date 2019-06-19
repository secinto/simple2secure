from flask import Flask, Response, jsonify, request, render_template, flash, redirect, session, copy_current_request_context
from flask_cors import CORS
from scanner import scanner
from src.models.TestResult import TestResult
from src.util.utils import *
from werkzeug.utils import secure_filename
from datetime import datetime
from src.models.CompanyLicensePod import CompanyLicensePod
from apscheduler.schedulers.background import BackgroundScheduler
import threading
import os
import urllib3
import secrets


app = Flask(__name__)
CORS(app)
app.secret_key = "ChangeIt2019!"
LICENSE_FOLDER = 'static/license'
# PORTAL_URL = 'https://144.76.93.104:51001/s2s/api/'
PORTAL_URL = 'https://localhost:8443/api/'
POD_ID = secrets.token_urlsafe(20)
license_id = ""
licenseFile = CompanyLicensePod("", "", "", "", "")


def check_configuration():
    portal_get(PORTAL_URL + "pod/config/" + POD_ID + "/" + socket.gethostname())


@app.before_first_request
def init():
    urllib3.disable_warnings()
    session.clear()
    print("-----------------------------")
    print("-------Initialization--------")
    print("-----------------------------")
    print(" * Extracting the pod license")
    app.licenseFile = parse_license_file(get_license_file())
    app.license_id = app.licenseFile.licenseId
    print(" * Pod License Id : " + app.licenseFile.licenseId)
    print(" * Pod Group Id : " + app.licenseFile.groupId)
    print(" * Pod Id : " + app.licenseFile.podId)
    print(" ****************************")
    session['auth_token'] = get_auth_token()
    session['license_id'] = app.licenseFile.licenseId
    session['group_id'] = app.licenseFile.groupId
    session['pod_id'] = app.licenseFile.podId
    scheduler = BackgroundScheduler()
    scheduler.add_job(func=check_configuration, trigger="interval",
                      seconds=10)
    scheduler.start()
    print(" * Activating the license")
    print(" * Auth Token : " + session['auth_token'])
    print("-----------------------------")
    print("-----Initialization END------")
    print("-----------------------------")


@app.route("/")
def get_available_tests():
    return "haha"


@app.route("/services")
def parse_tests():
    tests_string = read_json_testfile()
    resp = Response(tests_string, status=200, mimetype='application/json')
    return resp


@app.route("/services/run")
def run_service():

    data = json.loads(read_json_testfile())
    results = {}

    if is_blank(request.query_string) is True:

        # This is current temporary solution to read only first test
        tool_precondition = get_json_test_object(data, "test1", "precondition", "command")
        parameter_precondition = get_json_test_object(data, "test1", "precondition", "parameter")

        tool_postcondition = get_json_test_object(data, "test1", "postcondition", "command")
        parameter_postcondition = get_json_test_object(data, "test1", "postcondition", "parameter")

        tool_step = get_json_test_object(data, "test1", "step", "command")
        parameter_step = get_json_test_object(data, "test1", "step", "parameter")

    else:
        test_id = request.args.get('test')

        tool_precondition = get_test_item_value(data, request.args.get('precondition'), test_id, 'precondition', 'command')
        parameter_precondition = get_test_item_value(data, request.args.get('precondition'), test_id, 'precondition', 'parameter')
        tool_step = get_test_item_value(data, request.args.get('step'), test_id, 'step', 'command')
        parameter_step = get_test_item_value(data, request.args.get('step'), test_id, 'step', 'parameter')
        tool_postcondition = get_test_item_value(data, request.args.get('postcondition'), test_id, 'postcondition', 'command')
        parameter_postcondition = get_test_item_value(data, request.args.get('postcondition'), test_id, 'postcondition', 'parameter')

    # Create threads for each part
    precondition_scan = threading.Thread(target=scanner(construct_command(get_tool(tool_precondition), parameter_precondition), results, "Precondition"))
    step_scan = threading.Thread(target=scanner(construct_command(get_tool(tool_step), parameter_step), results, "step"))
    postcondition_scan = threading.Thread(target=scanner(construct_command(get_tool(tool_postcondition), parameter_postcondition), results, "Postcondition"))

    # Start each tread
    precondition_scan.start()
    step_scan.start()
    postcondition_scan.start()

    # Write to the result.log
    # write_to_result_log(json.dumps(results))

    timestamp = datetime.now().timestamp()*1000
    test_result = TestResult("Result - " + timestamp.__str__(), results, session['license_id'], session['group_id'],
                             socket.gethostname(), timestamp)

    portal_post(PORTAL_URL + "test/saveTestResult", test_result.__dict__)

    return jsonify(results)


@app.route('/license/upload')
def upload_license():
    return render_template('uploadLicense.html')


@app.route('/license/doUpload', methods=['POST'])
def do_license_upload():
    if request.method == 'POST':
        # Check if post request has file in it
        if 'file' not in request.files:
            flash('No file part!')
            return redirect(request.url)
        file = request.files['file']
        if file.filename == '':
            flash('No file selected for uploading')
            return redirect(request.url)
        if file and allowed_file(file.filename):
            filename = secure_filename(file.filename)
            file.save(os.path.join(app.config['UPLOAD_FOLDER'], filename))
            flash('File(s) successfully uploaded')
            return redirect('/license/upload')


if __name__ == '__main__':
    app.run(ssl_context=('adhoc'), host='0.0.0.0', threaded=True)
