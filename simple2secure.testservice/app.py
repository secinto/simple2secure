from flask import Flask, Response, jsonify, request, render_template, flash, redirect
from flask_cors import CORS
from scanner import scanner
from src.models.TestResult import TestResult
from src.util.utils import *
from werkzeug.utils import secure_filename
from datetime import datetime
import threading
import os
import secrets

app = Flask(__name__)
CORS(app)
app.secret_key = "ChangeIt2019!"
auth_token = ""
LICENSE_FOLDER = 'static/license'
app.config['LICENSE_FOLDER'] = LICENSE_FOLDER
PORTAL_URL = "https://localhost:8443/api/"
podId = secrets.token_urlsafe(20)
licenseFile = parse_license_file(get_license_file())
license_id = licenseFile.licenseId


@app.route("/")
def get_available_tests():
    timestamp = datetime.now().timestamp()*1000
    test_result = TestResult("Result - " + timestamp.__str__(), "test", license_id, licenseFile.groupId, timestamp)

    if not auth_token:
        get_auth_token()

    portal_post(PORTAL_URL + "test/saveTestResult", test_result.__dict__)
    # testResults.append(test_result)
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
    test_result = TestResult("Result - " + timestamp.__str__(), results, license_id, licenseFile.groupId, timestamp)

    # testResults.append(test_result)
    if not auth_token:
        get_auth_token()

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
    app.run(host='0.0.0.0', threaded=True)
