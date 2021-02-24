import json
import logging

from src.db.database import Test
from src.util.db_utils import update_test
from src.util.file_utils import read_json_testfile
from src.util.rest_utils import sync_tests_with_portal

log = logging.getLogger('pod.util.test_utils')


def update_tests(tests):
    """
    Reads the provided JSON data and obtains the test descriptions contained in it. It is checked if the contained
    test is already stored locally in the database. If not it is added or if it contains changes the entry is
    updated.

    Parameters:
        tests: A JSON containing an list of tests. The individual tests must follow the structure of the Test object.
    """
    tests_json = json.loads(tests)

    for test in tests_json:
        test_obj = generate_test_object(test)
        update_test(test_obj)


def sync_tests(app):
    """
    Synchronizes the tests from services.json with the PORTAl

    Parameters:
        app: Context object
    """
    try:
        tests_json = read_json_testfile()

        # Send tests from services.json to portal after authentication
        resp = sync_tests_with_portal(tests_json, app)

        if resp is not None and resp.status_code == 200:
            update_tests(resp.text)
        else:
            log.error('Failed to synchronize tests with portal.')

    except RuntimeError as re:
        app.logger.error('Error occurred while synchronizing tests with portal: %s', re)


def generate_test_object(sync_test_json):
    test = Test(sync_test_json["id"], sync_test_json["name"], sync_test_json["testContent"],
                sync_test_json["lastChangedTimestamp"], sync_test_json["podId"])
    return test
