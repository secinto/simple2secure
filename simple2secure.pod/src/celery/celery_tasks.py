import logging
import socket
import sys
import threading

from flask import json

from scanner import scanner
from src import create_celery_app, entrypoint
from src.db.database import db, TestResult
from src.db.database_schema import TestResultSchema
from src.util import json_utils
from src.util import rest_utils
from src.util.db_utils import update
from src.util.util import get_current_timestamp

app = entrypoint(sys.argv, 'celery')
celery = create_celery_app(app)

log = logging.getLogger('celery.celery_tasks')


@celery.task(name='celery.send_test_results')
def send_test_results(test_result):
    with app.app_context():
        response = rest_utils.portal_post(app, app.config['PORTAL_URL'] + "test/saveTestResult", test_result)

        if response is not None and response.status_code == 200:
            test_res = TestResult.query.filter_by(id=test_result['id']).first()
            test_res.isSent = True
            update(test_res)
            log.info('Test result {} updated in DB'.format(test_res.name))
        else:
            log.error('Sending test result {} was not successful'.format(test_result['name']))


@celery.task(name='celery.schedule_test')
def schedule_test(test, test_id, test_name, test_run_id):
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

    rest_utils.send_notification("Test " + test_name + " has been executed by the pod " + socket.gethostname(), app)

    rest_utils.update_test_status(app, test_run_id, test_id, "EXECUTED")

    db.session.add(test_result)
    db.session.commit()

    test_result_schema = TestResultSchema()
    output = test_result_schema.dump(test_result)
    log.info('Before sending test result: {}'.format(output))

    send_test_results(output)
    log.info('After sending test result')
