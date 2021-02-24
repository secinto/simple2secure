import json
import logging

from apscheduler.schedulers.background import BlockingScheduler

from src.db.database import TestResult
from src.db.database_schema import TestSequenceSchema, TestResultSchema
from src.db.session_manager import SessionManager
from src.util.db_utils import clear_pod_status_auth
from src.util.rest_utils import portal_get, send_notification, update_test_status, check_portal_alive, \
    update_sequence_status, update_test_by_id
from src.util.test_sequence_utils import get_sequence_from_run

log = logging.getLogger('pod.scheduler.scheduler_tasks')


def start_scheduler_tasks(app_obj, celery_tasks):
    """
    Initializes the scheduler and schedules the specified tasks.

    :param app_obj: The application context
    :param celery_tasks: The celery_tasks object as created during application setup
    """
    scheduler = BlockingScheduler()
    scheduler.add_job(func=get_scheduled_tests, trigger="interval", seconds=15,
                      kwargs={'app_obj': app_obj, 'celery_tasks': celery_tasks})

    scheduler.add_job(func=get_test_results_from_db, trigger="interval", seconds=60,
                      kwargs={'celery_tasks': celery_tasks})

    scheduler.add_job(func=get_scheduled_sequence, trigger="interval", seconds=15,
                      kwargs={'app_obj': app_obj, 'celery_tasks': celery_tasks})

    scheduler.add_job(func=check_portal_alive, trigger="interval", seconds=60,
                      kwargs={'app': app_obj})

    scheduler.start()


def get_scheduled_tests(app_obj, celery_tasks):
    """
    Task which obtains the scheduled tests from the PORTAL and updates the local database as well as the service.json
    file.

    :param app_obj: The application context
    :param celery_tasks: The celery_tasks object as created during application setup
    """
    api_url = app_obj.API_DEVICE_SCHEDULED_TESTS.replace("{deviceId}", app_obj.POD_ID)
    request_test = portal_get(app_obj, api_url)
    if request_test is not None and request_test.status_code == 200:
        test_run_array = json.loads(request_test.text)

        for test_run in test_run_array:
            current_test = json.loads(test_run["testContent"])
            update_test_status(app_obj, test_run["id"], test_run["testId"], "SCHEDULED")
            update_test_by_id(app_obj, test_run["testId"])
            send_notification("Test " + test_run["testName"] + " has been scheduled for the execution in the pod",
                              app_obj)
            celery_tasks.execute_test.delay(current_test["test_definition"], test_run["testId"],
                                            test_run["testName"], test_run["id"], test_run["podId"])

    else:
        clear_pod_status_auth()
        if request_test is None:
            log.error('Call to get scheduled tests returned nothing')
        else:
            log.error('Status code for get scheduled tests is not as expected: {}'.format(request_test.status_code))


def get_test_results_from_db(celery_tasks):
    """
    Task that obtains the results from the database which have not be sent to the PORTAL already and sends them. If
    successful the status of the test result is updated in the local database.

    :param celery_tasks: The celery_tasks object as created during application setup
    """
    with SessionManager() as session:
        test_results = session.query(TestResult).filter_by(isSent=False).all()
        for test_result in test_results:
            test_result_schema = TestResultSchema()
            output = test_result_schema.dump(test_result)
            celery_tasks.send_test_result.delay(output, test_result.id)


def get_scheduled_sequence(app_obj, celery_tasks):
    """

    :param app_obj:
    :param celery_tasks:
    :return:
    """
    api_url = app_obj.API_SEQUENCE_SCHEDULED_SEQUENCES.replace("{deviceId}", app_obj.POD_ID)
    request_sequence = portal_get(app_obj, api_url)

    if request_sequence is not None and request_sequence.status_code == 200:
        test_sequence_schema = TestSequenceSchema()
        sequence_run_content = json.loads(request_sequence.text)

        if len(sequence_run_content) != 0:
            sequence_run_id = sequence_run_content[0]['id']
            sequence_id = sequence_run_content[0]['sequenceId']

            for sequence_run in sequence_run_content:
                curr_sequence = get_sequence_from_run(sequence_run)
                sequence_to_provide = test_sequence_schema.dumps(curr_sequence)
                celery_tasks.schedule_sequence.delay(sequence_to_provide, sequence_run_id, sequence_id)
                send_notification(
                    "Sequence " + curr_sequence.name + " has been scheduled for the execution in the pod",
                    app_obj)
                update_sequence_status(app_obj, sequence_run_id, sequence_id, "SCHEDULED")
        else:
            log.info("There are no scheduled test sequences")
    else:
        clear_pod_status_auth()
        if request_sequence is None:
            log.error('Call to get scheduled test sequence returned nothing')
        else:
            log.error('Status code for get scheduled test sequence is not as expected: {}'.format(
                request_sequence.status_code))
