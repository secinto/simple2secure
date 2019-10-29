import json
import logging
import socket
import time

import requests

from src.db.database import Test
from src.db.database_schema import TestSchema
from src.util.db_utils import update, clear_pod_status_auth
from src.util.file_utils import read_json_testfile, update_services_file
from src.util.rest_utils import sync_test_with_portal
from src.util.util import create_secure_hash, generate_test_object_from_json

log = logging.getLogger('pod.util.test_utils')


def get_tests(app):
    """
    Returns the list of tests currently stored in the DB. If none are stored it is checked if the local services.json
    contains any tests and fills the database with it and returns them.

    Parameters:
        app: Context object
    Return:
        A list of all tests currently stored in the database
    """
    tests = Test.query.all()

    if tests is not None and len(tests) > 0:
        return tests
    else:
        return get_and_store_tests_from_file(app)


def get_and_store_tests_from_file(app):
    """
    Reads the local services.json file and provides it to the update_insert_tests_to_db function for verification and
    update.

    Parameters:
        app: Context object
    Return:
        A list of all tests currently stored in the database
    """
    tests_as_string = read_json_testfile()
    update_tests(tests_as_string, app)
    return Test.query.all()


def update_tests(tests, app):
    """
    Reads the provided JSON data and obtains the test descriptions contained in it. It is checked if the contained
    test is already stored locally in the database. If not it is added or if it contains changes the entry is
    updated.

    Parameters:
        tests: A JSON containing an list of tests. The individual tests must follow the structure of the Test object.
        app: Context object
    """
    tests_json = json.loads(tests)
    current_milli_time = int(round(time.time() * 1000))

    for test in tests_json:
        current_test_name = test["name"]
        db_test = Test.query.filter_by(name=current_test_name).first()
        test_dump = json.dumps(test)
        test_hash = create_secure_hash(test_dump)

        if db_test is None:
            db_test = Test(test["name"], test_dump, test_hash,
                           current_milli_time, app.config['POD_ID'])
            update(db_test)
        else:
            if not db_test.hash_value == test_hash:
                db_test.test_content = test_dump
                db_test.hash_value = test_hash
                db_test.lastChangedTimestamp = current_milli_time
                update(db_test)


def sync_tests(app):
    """
    Synchronizes the locally stored tests with the PORTAl and updates the services.json and the DB if updates from
    the PORTAL are available

    Parameters:
        app: Context object
    """
    with app.app_context():
        try:
            tests = get_tests(app)
            if tests is not None:
                sync_ok = False
                amount_tests = len(tests)
                synced_tests = 0
                for test in tests:
                    resp = sync_test_with_portal(test, app)

                    if resp is not None and resp.status_code == 200:
                        log.info('Received response with status code 200 from PORTAL for syncTest')
                        synchronized_test = json.loads(resp.text)
                        if synchronized_test is not None:
                            test_obj = generate_test_object_from_json(synchronized_test, test)
                            log.info('Synchronized test with id {} and name {} with PORTAL'.format(test.id, test_obj.name))
                            update(test_obj)
                            sync_ok = True
                            synced_tests = synced_tests + 1
                        else:
                            log.error('Could not parse provided data {} to Test object'.format(resp.text))
                    else:
                        sync_ok = False
                        if resp is not None:
                            log.info('Failed to synchronize test {} with portal. Response data: {}'.format(test.name, resp.text))
                            clear_pod_status_auth(app)
                        else:
                            log.info('Failed to synchronize test {} with portal.'.format(test.name))

                if sync_ok and synced_tests > 0:
                    update_services_file()
                    if synced_tests != amount_tests:
                        log.error('Not all contained tests have been synchronized successfully')
                    else:
                        log.info('Synchronized tests with portal successfully!')
                elif not sync_ok:
                    log.info('Synchronization of tests was not successful')

        except requests.exceptions.ConnectionError as ce:
            app.logger.error('Error occurred while activating the pod: %s', ce)
            clear_pod_status_auth(app)
        except RuntimeError as re:
            app.logger.error('Error occurred while synchronizing tests with portal: %s', re)


def convert_tests():
    """

    :return:
    """
    converted_tests = []

    tests = Test.query.all()

    for test in tests:
        test_schema = TestSchema()
        output = test_schema.dump(test).data
        converted_tests.append(output)

    return json.dumps(converted_tests)
