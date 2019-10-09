import logging

from apscheduler.schedulers.background import BackgroundScheduler
from flask import json

from src.db.database import TestResult
from src.db.database_schema import TestResultSchema
from src.util.rest_utils import portal_get, send_notification, update_test_status, check_portal_alive
from src.util.test_utils import sync_tests

log = logging.getLogger('pod.scheduler.scheduler_tasks')


def start_scheduler_tasks(app_obj, celery_tasks):
    scheduler = BackgroundScheduler()
    scheduler.add_job(func=get_scheduled_tests, trigger="interval", seconds=15,
                      kwargs={'app_obj': app_obj, 'celery_task': celery_tasks})

    scheduler.add_job(func=get_test_results_from_db, trigger="interval", seconds=60,
                      kwargs={'app_obj': app_obj, 'celery_task': celery_tasks})

    scheduler.add_job(func=sync_tests_with_the_portal, trigger="interval", seconds=60, kwargs={'app_obj': app_obj})

    scheduler.add_job(func=check_portal_alive, trigger="interval", seconds=60,
                      kwargs={'app': app_obj})

    scheduler.start()


def get_scheduled_tests(app_obj, celery_task):
    with app_obj.app_context():
        request_test = portal_get(app_obj, app_obj.config['PORTAL_URL'] + "pod/scheduledTests/" +
                                  app_obj.config['POD_ID'])
        if request_test is not None and request_test.status_code == 200:
            test_run_array = json.loads(request_test.text)

            for test_run in test_run_array:
                current_test = json.loads(test_run["testContent"])
                celery_task.schedule_test.delay(current_test["test_definition"], test_run["testId"],
                                                test_run["testName"], app_obj.config['POD_ID'], test_run["id"])
                send_notification("Test " + test_run["testName"] + " has been scheduled for the execution in the pod",
                                  app_obj)
                update_test_status(app_obj, test_run["id"], test_run["testId"], "SCHEDULED")
        else:
            if request_test is None:
                log.error('Call to get scheduled tests returned nothing')
            else:
                log.error('Status code is not as expected: {}'.format(request_test.status_code))


def get_test_results_from_db(app_obj, celery_task):
    with app_obj.app_context():
        test_results = TestResult.query.filter_by(isSent=False).all()
        for test_result in test_results:
            test_result_schema = TestResultSchema()
            output = test_result_schema.dump(test_result)
            celery_task.send_test_results.delay(output)


def sync_tests_with_the_portal(app_obj):
    return sync_tests(app_obj)
