from flask import Flask, Response, jsonify, json, request
from flask_cors import CORS
from scanner import scanner
from utils import *
import threading

app = Flask(__name__)
CORS(app)


@app.route("/")
def get_available_tests():
    data = json.loads(read_json_testfile())
    test_id = request.args.get('test')
    return get_test_item_value(data, request.args.get('step'), test_id, 'step', 'command')


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
    step_scan = threading.Thread(target=scanner(construct_command(get_tool(tool_step), parameter_step), results, "Step"))
    postcondition_scan = threading.Thread(target=scanner(construct_command(get_tool(tool_postcondition), parameter_postcondition), results, "Postcondition"))

    # Start each tread
    precondition_scan.start()
    step_scan.start()
    postcondition_scan.start()

    # Write to the result.log
    # write_to_result_log(json.dumps(results))

    return jsonify(results)


if __name__ == '__main__':
    app.run(host='0.0.0.0')
