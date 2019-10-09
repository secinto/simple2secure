import logging
import socket
import sys

from flask import json

from scanner import scanner
from src import create_celery_app, entrypoint
from src.db.database import db, TestResult
from src.util import json_utils
from src.util import rest_utils
from src.util.db_utils import update
from src.util.util import get_current_timestamp

app = entrypoint(sys.argv, 'celery')
celery = create_celery_app(app)

log = logging.getLogger('celery.celery_tasks')


@celery.task(name='celery.send_test_result')
def send_test_result(test_result):
    """
    Celery task which sends the provided test result to the PORTAL and updates the status of the result in the database.

    :param test_result: The test result as JSON which should be sent to the PORTAL
    """
    with app.app_context():
        response = rest_utils.portal_post(app, app.config['PORTAL_URL'] + "test/saveTestResult", test_result)

        if response is not None and response.status_code == 200:
            stored_test_result = TestResult.query.filter_by(id=test_result['id']).first()
            stored_test_result.isSent = True
            update(stored_test_result)
            log.info('Test result {} updated in DB'.format(stored_test_result.name))
        else:
            log.error('Sending test result {} was not successful'.format(test_result['name']))


@celery.task(name='celery.execute_test')
def execute_test(test, test_id, test_name, test_run_id):
    """
    Celery task which executed the provided test using the preconditions, steps and postconditions specified in it.
    After the test has finished the results are stored in the database. A notification and the test status are sent
    to the portal. In the end the test results are sent to the PORTAL, if this doesn't work immediately it is tried
    from time to time again.

    :param test: The test definition which should be executed
    :param test_id: The id of the test
    :param test_name: The name of the test
    :param test_run_id: The test run ID assigned to this test execution
    """
    results = {}

    tool_precondition = json_utils.get_json_test_object_new(test, "precondition", "command")
    parameter_precondition = json_utils.get_json_test_object_new(test, "precondition", "parameter")
    tool_postcondition = json_utils.get_json_test_object_new(test, "postcondition", "command")
    parameter_postcondition = json_utils.get_json_test_object_new(test, "postcondition", "parameter")
    tool_step = json_utils.get_json_test_object_new(test, "step", "command")
    parameter_step = json_utils.get_json_test_object_new(test, "step", "parameter")

    # TODO: Create folder for execution of each celery task

    scanner(json_utils.construct_command(json_utils.get_tool(
        tool_precondition), parameter_precondition), results, "precondition")
    scanner(json_utils.construct_command(json_utils.get_tool(
        tool_step), parameter_step), results, "step")
    scanner(json_utils.construct_command(json_utils.get_tool(
        tool_postcondition), parameter_postcondition), results, "postcondition")

    timestamp = get_current_timestamp()
    test_result = TestResult("Result - " + timestamp.__str__(), json.dumps(results), test_run_id,
                             socket.gethostname(), timestamp, False)

    db.session.add(test_result)
    db.session.commit()

    rest_utils.send_notification("Test " + test_name + " has been executed by the pod " + socket.gethostname(), app)

    rest_utils.update_test_status(app, test_run_id, test_id, "EXECUTED")

