import logging

from apscheduler.schedulers.background import BackgroundScheduler
from flask import json

from src.db.database import TestResult
from src.db.database_schema import TestSequenceSchema, TestResultSchema
from src.util.db_utils import clear_pod_status_auth
from src.util.rest_utils import portal_get, send_notification, update_test_status, check_portal_alive, \
    update_sequence_status
from src.util.test_sequence_utils import get_sequence_from_run
from src.util.test_utils import sync_tests

log = logging.getLogger('pod.scheduler.scheduler_tasks')


def start_scheduler_tasks(app_obj, celery_tasks):
    """
    Initializes the scheduler and schedules the specified tasks.

    :param app_obj: The application context
    :param celery_tasks: The celery_tasks object as created during application setup
    """
    scheduler = BackgroundScheduler()
    scheduler.add_job(func=get_scheduled_tests, trigger="interval", seconds=15,
                      kwargs={'app_obj': app_obj, 'celery_tasks': celery_tasks})

    scheduler.add_job(func=get_test_results_from_db, trigger="interval", seconds=60,
                      kwargs={'app_obj': app_obj, 'celery_tasks': celery_tasks})

    scheduler.add_job(func=get_scheduled_sequence, trigger="interval", seconds=15,
                      kwargs={'app_obj': app_obj, 'celery_tasks': celery_tasks})

    scheduler.add_job(func=sync_tests, trigger="interval", seconds=60, kwargs={'app': app_obj})

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
    with app_obj.app_context():
        request_test = portal_get(app_obj, app_obj.config['PORTAL_URL'] + "device/scheduledTests/" +
                                  app_obj.config['POD_ID'])
        if request_test is not None and request_test.status_code == 200:
            test_run_array = json.loads(request_test.text)

            for test_run in test_run_array:
                current_test = json.loads(test_run["testContent"])
                celery_tasks.execute_test.delay(current_test["test_definition"], test_run["testId"],
                                                test_run["testName"], test_run["id"])
                send_notification("Test " + test_run["testName"] + " has been scheduled for the execution in the pod",
                                  app_obj)
                update_test_status(app_obj, test_run["id"], test_run["testId"], "SCHEDULED")
        else:
            clear_pod_status_auth(app_obj)
            if request_test is None:
                log.error('Call to get scheduled tests returned nothing')
            else:
                log.error('Status code for get scheduled tests is not as expected: {}'.format(request_test.status_code))


def get_test_results_from_db(app_obj, celery_tasks):
    """
    Task that obtains the results from the database which have not be sent to the PORTAL already and sends them. If
    successful the status of the test result is updated in the local database.

    :param app_obj: The application context
    :param celery_tasks: The celery_tasks object as created during application setup
    """
    with app_obj.app_context():
        test_results = TestResult.query.filter_by(isSent=False).all()
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
    with app_obj.app_context():
        request_sequence = portal_get(app_obj,
                                      app_obj.config['PORTAL_URL'] + "sequence/scheduledSequence/" + app_obj.config[
                                          'POD_ID'])

        if request_sequence is not None and request_sequence.status_code == 200:
            test_sequence_schema = TestSequenceSchema()
            sequence_run_content = json.loads(request_sequence.text)

            if len(sequence_run_content) != 0:
                sequence_run_id = sequence_run_content[0]['id']
                sequence_id = sequence_run_content[0]['sequenceId']

                for sequence_run in sequence_run_content:
                    curr_sequence = get_sequence_from_run(sequence_run)
                    sequence_to_provide = test_sequence_schema.dump(curr_sequence).data
                    celery_tasks.schedule_sequence.delay(sequence_to_provide, sequence_run_id, sequence_id)
                    send_notification(
                        "Sequence " + curr_sequence.name + " has been scheduled for the execution in the pod",
                        app_obj)
                    update_sequence_status(app_obj, sequence_run_id, sequence_id, "SCHEDULED")
        else:
            clear_pod_status_auth(app_obj)
            if request_sequence is None:
                log.error('Call to get scheduled test sequence returned nothing')
            else:
                log.error('Status code for get scheduled test sequence is not as expected: {}'.format(request_sequence.status_code))
