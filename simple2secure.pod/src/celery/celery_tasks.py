import logging
import sys

from src.util import rest_utils
from src.util import json_utils
from scanner import scanner
from src.db.database import db, TestResult,  TestSequenceResult
from flask import json
from src import create_celery_app, entrypoint
import threading
import socket
import time

from src.util.task_utils import update_add_sequence_to_db
from src.util.util import get_current_timestamp

app = entrypoint(sys.argv, 'celery')
celery = create_celery_app(app)

log = logging.getLogger('pod.celery.start_celery')


@celery.task(name='celery.send_test_results')
def send_test_results(test_result):
    with app.app_context():
        response = rest_utils.portal_post_celery(app.config['PORTAL_URL'] + "test/saveTestResult", test_result, app)

        if response.status_code == 200:
            test_res = TestResult.query.filter_by(id=test_result['id']).first()
            test_res.isSent = True
            db.session.commit()


@celery.task(name='celery.schedule_test')
def schedule_test(test, test_id, test_name, auth_token, pod_id, test_run_id):
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

        timestamp = get_current_timestamp()
        test_result = TestResult("Result - " + timestamp.__str__(), json.dumps(results), test_run_id,
                                 socket.gethostname(), timestamp, False)

        rest_utils.send_notification("Test " + test_name + " has been executed by the pod " + socket.gethostname(), app,
                                     auth_token, pod_id)

        rest_utils.update_test_status(app, auth_token, test_run_id, test_id, "EXECUTED")

        log.info('Before sending test result: %s', test_result)

        db.session.add(test_result)
        db.session.commit()

        send_test_results(json.dumps(test_result.as_dict()))
        log.info('After sending test result')


@celery.task(name='celery.schedule_test_for_sequence')
def schedule_test_for_sequence(parameter, command):
    with app.app_context():
        results = {}
        scan = scanner(json_utils.construct_command(json_utils.get_tool(
            command), parameter), results, "sequence_result")
        return results["sequence_result"]


@celery.task(name='celery.schedule_sequence')
def schedule_sequence(test_sequence):
    with app.app_context():
        sequence = update_add_sequence_to_db(test_sequence)

        current_milli_time = int(round(time.time() * 1000))
        test_sequence_result_obj = {}
        firstRun = True
        nextResult = ""
        for i, task in enumerate(json.loads(sequence.sequence_content), 0):
            if firstRun:
                test_content = json.loads(task['test_content'])
                test_command = test_content['test_definition']['step']['command']['executable']
                test_parameter = test_content['test_definition']['step']['command']['parameter']['value']
                scan = schedule_test_for_sequence(test_parameter, test_command)
                nextResult = scan.strip()
                test_sequence_result_obj[test_content['name']] = nextResult
                firstRun = False
            else:
                test_content = json.loads(task['test_content'])
                test_command = test_content['test_definition']['step']['command']['executable']
                scan = schedule_test_for_sequence(nextResult, test_command)
                nextResult = scan.strip()
                test_sequence_result_obj[test_content['name']] = nextResult

        test_sequence_result_json = json.dumps(test_sequence_result_obj)
        test_sequence_result = TestSequenceResult(sequence.podId, sequence.name, test_sequence_result_json, current_milli_time)

        db.session.add(test_sequence_result)
        db.session.merge(sequence)
        db.session.commit()

        return sequence
