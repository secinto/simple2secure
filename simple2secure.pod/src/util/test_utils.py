import json
import time

import requests

from src.db.database import Test
from src.db.database_schema import TestSchema
from src.util.db_utils import update
from src.util.file_utils import read_json_testfile, update_services_file
from src.util.rest_utils import sync_test_with_portal
from src.util.util import create_secure_hash, generate_test_object_from_json


def get_tests(app):
    tests = Test.query.all()

    if tests is not None and len(tests) > 0:
        return tests
    else:
        return get_and_store_tests_from_file(app)


def get_and_store_tests_from_file(app):
    tests_as_string = read_json_testfile()
    update_insert_tests_to_db(tests_as_string, app)
    return Test.query.all()


def update_insert_tests_to_db(tests, app_obj):
    tests_json = json.loads(tests)
    current_milli_time = int(round(time.time() * 1000))

    for test in tests_json:
        current_test_name = test["name"]
        db_test = Test.query.filter_by(name=current_test_name).first()

        test_dump = json.dumps(test)
        test_hash = create_secure_hash(test_dump)

        if db_test is None:
            db_test = Test(test["name"], test_dump, test_hash,
                           current_milli_time, app_obj.config['POD_ID'])
            update(db_test)
        else:
            if not db_test.hash_value == test_hash:
                db_test.test_content = test_dump
                db_test.hash_value = test_hash
                db_test.lastChangedTimestamp = current_milli_time
                update(db_test)


def sync_tests(app):
    with app.app_context():
        try:
            tests = get_tests(app)
            if tests is not None:
                sync_ok = False

                for test in tests:
                    resp = sync_test_with_portal(test, app)
                    if resp is not None and resp.status_code == 200:
                        synchronized_test = json.loads(resp.text)
                        if synchronized_test is not None:
                            test_obj = generate_test_object_from_json(synchronized_test, test)
                            update(test_obj)
                            sync_ok = True
                    else:
                        sync_ok = False
                        if resp is not None:
                            app.logger.info('Failed to synchronize test %s with portal. %s', test.name, resp.text)
                        else:
                            app.logger.info('Failed to synchronize test %s with portal.', test.name)

                if sync_ok:
                    update_services_file()
                    app.logger.info('Synchronized tests with portal successfully!')

        except requests.exceptions.ConnectionError as ce:
            app.logger.error('Error occurred while activating the pod: %s', ce)
        except RuntimeError as re:
            app.logger.error('Error occurred while synchronizing tests with portal: %s', re)


def convert_tests():
    converted_tests = []

    tests = Test.query.all()

    for test in tests:
        test_schema = TestSchema()
        output = test_schema.dump(test).data
        converted_tests.append(output)

    return json.dumps(converted_tests)
