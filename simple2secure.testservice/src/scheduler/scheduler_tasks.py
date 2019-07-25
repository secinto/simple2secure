from apscheduler.schedulers.background import BackgroundScheduler
from flask import json
from src.db.database import db, TestResult, Test
from src.db.database_schema import TestResultSchema, TestSchema
from src.util import rest_utils, file_utils


def start_tasks(app_obj, celery_tasks):
    scheduler = BackgroundScheduler()
    scheduler.add_job(func=check_configuration, trigger="interval", seconds=15,
                      kwargs={'app_obj': app_obj, 'celery_task': celery_tasks})

    scheduler.add_job(func=get_test_results_from_db, trigger="interval", seconds=20,
                      kwargs={'app_obj': app_obj, 'celery_task': celery_tasks})

    scheduler.add_job(func=sync_tests_with_the_portal, trigger="interval", seconds=30, kwargs={'app_obj': app_obj})
    scheduler.start()


def check_configuration(app_obj, celery_task):
    with app_obj.app_context():
        request_test = rest_utils.portal_get(app_obj.config['PORTAL_URL'] + "pod/scheduledTests/" +
                                             app_obj.config['POD_ID'], app_obj)
        if request_test.status_code == 200:
            test_array = json.loads(request_test.text)

            for test in test_array:
                print("testing")
                celery_task.schedule_test.delay(test, test.id)
                # rest_utils.send_notification(test["id"], "Test has been scheduled", app)

        else:
            print(request_test.status_code)


def get_test_results_from_db(app_obj, celery_task):
    with app_obj.app_context():
        test_results = TestResult.query.filter_by(isSent=False).all()
        for test_result in test_results:
            test_result_schema = TestResultSchema()
            output = test_result_schema.dump(test_result).data

            if not app_obj.config['AUTH_TOKEN']:
                app_obj.config['AUTH_TOKEN'] = rest_utils.get_auth_token(app_obj)

            celery_task.send_test_results.delay(output, app_obj.config['AUTH_TOKEN'], app_obj.config['PORTAL_URL'])
            # rest_utils.send_notification(test_result.testId, "Test has been finished", app)
            test_result.isSent = True
            db.session.commit()


def sync_tests_with_the_portal(app_obj):
    test_schema = TestSchema()
    with app_obj.app_context():
        tests = Test.query.all()

        if tests is not None:
            new_tests = []
            for test in tests:
                output = test_schema.dump(test).data
                new_tests.append(output)

            if new_tests is not None:
                sync_test = file_utils.sync_all_tests_with_portal(new_tests, app_obj)
                test_array = json.loads(sync_test)

                for test_new in test_array:
                    test_obj = file_utils.generate_test_object_from_json(test_new)
                    db.session.add(test_obj)
                    db.session.commit()

                file_utils.update_services_file()
