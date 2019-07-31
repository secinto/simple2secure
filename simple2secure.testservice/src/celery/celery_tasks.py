from src.util import rest_utils
from src.util import json_utils
from scanner import scanner
from src.db.database import db, TestResult
from src.db.database_schema import TestResultSchema
from flask import json
from src import create_celery_app, entrypoint
import threading
import socket

app = entrypoint('celery')
celery = create_celery_app(app)


@celery.task(name='celery.send_test_results')
def send_test_results(test_result, auth_token, portal_url):
    with app.app_context():
        response = rest_utils.portal_post_celery(portal_url + "test/saveTestResult", test_result, auth_token, app)

        if response.status_code == 200:
            test_res = TestResult.query.filter_by(id=test_result["id"]).first()
            test_res.isSent = True
            db.session.commit()


@celery.task(name='celery.schedule_test')
def schedule_test(test, test_id, test_name, auth_token, pod_id):
    with app.app_context():
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

        timestamp = rest_utils.get_current_timestamp()
        test_result = TestResult("Result - " + timestamp.__str__(), json.dumps(results), test_id,
                                 socket.gethostname(), timestamp, False)

        rest_utils.send_notification("Test " + test_name + " has been executed", app, auth_token, pod_id)

        db.session.add(test_result)
        db.session.commit()


