import base64
import json
import logging
import socket
from bson.objectid import ObjectId

from scanner import scanner
from src import create_celery_app
from src.db.database import TestResult, TestSequenceResult, TestSequenceStepResult
from src.db.database_schema import TestSequenceResultSchema, TestSequenceSchema, TestSequenceStepResultSchema, \
    TestResultSchema
from src.util import json_utils
from src.util.db_utils import update
from src.util.rest_utils import portal_post, update_sequence_status
from src.util.test_sequence_utils import schedule_test_for_sequence
from src.util.test_sequence_utils import update_sequence
from src.util.util import get_current_timestamp
import src.config.config as config_module

app = config_module.DevelopmentConfig
celery = create_celery_app(app)

HOSTNAME = socket.gethostname()

log = logging.getLogger('celery.celery_tasks')


@celery.task(name='celery.send_test_result')
def send_test_result(test_result, test_id):
    """
    Celery task which sends the provided test result to the PORTAL and updates the status of the result in the database.

    :param test_result: The test result as JSON which should be sent to the PORTAL
    :param test_id:
    """
    test_result_schema = TestResultSchema()
    log.info(test_result)
    response = portal_post(app, app.API_TEST_SAVE_RESULT, json.dumps(test_result_schema.dump(test_result)))

    if response is not None and response.status_code == 200:
        test_result.isSent = True
        update(test_result)
        log.info('Test result {} updated in DB'.format(test_result.name))
    else:
        update(test_result)
        log.error('Sending test result {} was not successful'.format(test_result['name']))


# noinspection PyBroadException
@celery.task(name='celery.execute_test')
def execute_test(test, test_id, test_name, test_run_id, pod_id):
    """
    Celery task which executed the provided test using the preconditions, steps and postconditions specified in it.
    After the test has finished the results are stored in the database. A notification and the test status are sent
    to the portal. In the end the test results are sent to the PORTAL, if this doesn't work immediately it is tried
    from time to time again.

    :param pod_id: Pod Id
    :param test: The test definition which should be executed
    :param test_id: The id of the test
    :param test_name: The name of the test
    :param test_run_id: The test run ID assigned to this test execution
    """
    results = {}
    test_result_schema = TestResultSchema()

    executable_precondition = json_utils.prepare_test_section_for_execution(test, 'precondition')
    executable_step = json_utils.prepare_test_section_for_execution(test, 'step')
    executable_postcondition = json_utils.prepare_test_section_for_execution(test, 'postcondition')

    try:
        scanner(executable_precondition, results, 'precondition')
    except Exception as e:
        # log.error("The pod failed to execute the task properly!", e)
        pass

    try:
        scanner(executable_step, results, 'step')
    except Exception as e:
        log.error("The pod failed to execute the task properly!", e)
        pass

    try:
        scanner(executable_postcondition, results, 'postcondition')
    except Exception as e:
        # log.error("The pod failed to execute the task properly!", e)
        pass

    timestamp = get_current_timestamp()
    result = bytearray(json.dumps(results), encoding='utf8')
    report = base64.b64encode(result)
    test_result = TestResult("Result - " + timestamp.__str__(), report, test_run_id, pod_id,
                             test_name, HOSTNAME, timestamp, False)
    send_test_result(test_result, test_id)

    return results


@celery.task(name='celery.schedule_sequence')
def schedule_sequence(test_sequence, sequence_run_id, sequence_id):
    """
    Schedule a test sequence to be executed

    :param test_sequence:
    :param sequence_run_id:
    :param sequence_id:
    :return:
    """
    sequence_schema = TestSequenceSchema()
    sequence = update_sequence(test_sequence)
    update_sequence_status(app, sequence_run_id, sequence_id, "RUNNING")
    current_milli_time = get_current_timestamp()

    complete_result = ""
    previous_test_result = ""
    for key in sequence.testSequenceContent:

        test = check_previous_test_input_data(sequence.testSequenceContent[key], previous_test_result)

        # Construct executable from the provided test content
        executable = json_utils.prepare_sequence_test_section_for_execution(test)

        # Execute the test content
        scan = schedule_test_for_sequence(executable)

        # Create Test Sequence Step Object
        test_sequence_result_obj = json.dumps({key: scan})

        previous_test_result = scan

        # Add value to Complete sequence result
        if not complete_result:
            complete_result += test_sequence_result_obj
        else:
            complete_result += "," + test_sequence_result_obj

        # Save test sequence step result
        result = bytearray(test_sequence_result_obj, encoding='utf8')
        test_result = base64.b64encode(result)
        step_result = TestSequenceStepResult(str(ObjectId()), sequence_run_id,
                                             key, sequence.podId, test_result, get_current_timestamp(), True)
        update(step_result)
        seq_step_result_schema = TestSequenceStepResultSchema()
        output = seq_step_result_schema.dump(step_result)
        portal_post(app, app.API_SEQUENCE_SAVE_STEP_RESULT, json.dumps(output))

    # save complete test sequence result and send to portal
    result_json = json.loads('[' + complete_result + ']')
    complete_result_obj = base64.b64encode(bytearray(json.dumps(result_json), encoding='utf8'))

    test_seq_res = TestSequenceResult(str(ObjectId()), sequence_run_id, sequence_id, sequence.podId,
                                      sequence.name, complete_result_obj, current_milli_time)
    update(test_seq_res)

    test_result_schema = TestSequenceResultSchema()
    output = test_result_schema.dump(test_seq_res)
    portal_post(app, app.API_SEQUENCE_SAVE_RESULT, json.dumps(output))

    return sequence_schema.dump(sequence)


def check_previous_test_input_data(test, previous_data):
    test_content = json.loads(test)
    parameters = test_content['test_definition']['step']['command']['parameter']
    for param in parameters:
        if param["value"] == "{USE_INPUT_DATA_PREV}":
            param["value"] = previous_data

    return test_content
