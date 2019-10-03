from email._header_value_parser import get_token

from apscheduler.schedulers.background import BackgroundScheduler
from flask import json

from src.db.database import TestResult, Test
from src.db.database_schema import TestResultSchema, TestSchema
from src.util.db_utils import update
from src.util.file_utils import update_services_file
from src.util.rest_utils import portal_get, send_notification, update_test_status, sync_all_tests_with_portal
from src.util.util import generate_test_object_from_json


def start_scheduler_tasks(app_obj, celery_tasks):
    scheduler = BackgroundScheduler()
    scheduler.add_job(func=get_scheduled_tests, trigger="interval", seconds=15,
                      kwargs={'app_obj': app_obj, 'celery_task': celery_tasks})

    scheduler.add_job(func=get_test_results_from_db, trigger="interval", seconds=20,
                      kwargs={'app_obj': app_obj, 'celery_task': celery_tasks})

    scheduler.add_job(func=sync_tests_with_the_portal, trigger="interval", seconds=30, kwargs={'app_obj': app_obj})
    scheduler.start()


def get_scheduled_tests(app_obj, celery_task):
    with app_obj.app_context():
        request_test = portal_get(app_obj.config['PORTAL_URL'] + "pod/scheduledTests/" +
                                             app_obj.config['POD_ID'], app_obj)
        if request_test.status_code == 200:
            test_run_array = json.loads(request_test.text)

            for test_run in test_run_array:
                current_test = json.loads(test_run["testContent"])
                celery_task.schedule_test.delay(current_test["test_definition"], test_run["testId"],
                                                test_run["testName"], app_obj.config['AUTH_TOKEN'], app_obj.config['POD_ID'], test_run["id"])
                send_notification("Test " + test_run["testName"] + " has been scheduled for the execution in the pod",
                                             app_obj,
                                             app_obj.config['AUTH_TOKEN'], app_obj.config['POD_ID'])
                update_test_status(app_obj, app_obj.config['AUTH_TOKEN'], test_run["id"], test_run["testId"], "SCHEDULED")
        else:
            print(request_test.status_code)


def get_test_results_from_db(app_obj, celery_task):
    with app_obj.app_context():
        test_results = TestResult.query.filter_by(isSent=False).all()
        for test_result in test_results:
            test_result_schema = TestResultSchema()
            output = test_result_schema.dump(test_result)

            if not app_obj.config['AUTH_TOKEN']:
                app_obj.config['AUTH_TOKEN'] = get_token(app_obj)

            celery_task.send_test_results.delay(output, app_obj.config['AUTH_TOKEN'], app_obj.config['PORTAL_URL'])


def sync_tests_with_the_portal(app_obj):
    test_schema = TestSchema()
    with app_obj.app_context():
        tests = Test.query.all()

        if tests is not None:
            new_tests = []
            for test in tests:
                output = test_schema.dump(test)
                new_tests.append(output)

            if new_tests is not None:

                if len(new_tests) > 0:
                    sync_test = sync_all_tests_with_portal(new_tests, app_obj)

                    if sync_test.status_code == 200:
                        test_array = json.loads(sync_test.text)

                        if len(test_array) > 0 :
                            for test_new in test_array:
                                test_obj = generate_test_object_from_json(test_new)
                                update(test_obj)

                            update_services_file()

                    else:
                        print(sync_test.text)
