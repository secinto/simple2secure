import json
import time

from src.db.database import Test
from src.db.database_schema import TestSchema
from src.util.db_utils import update
from src.util.file_utils import read_json_testfile
from src.util.util import create_secure_hash


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

        # TODO: Synchronizing with portal needs to be performed but should be done as scheduled task


def convert_tests():
    converted_tests = []

    tests = Test.query.all()

    for test in tests:
        test_schema = TestSchema()
        output = test_schema.dump(test).data
        converted_tests.append(output)

    return json.dumps(converted_tests)
