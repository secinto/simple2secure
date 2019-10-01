import json
import time

from src.db.database import db, Test, TestSequence, PodInfo, CompanyLicensePod
from src.util.util import create_secure_hash


def update_license(license_object):
    if isinstance(license_object, CompanyLicensePod):
        db.session.add(license_object)
        db.session.commit()


def update_pod_info(pod_info):
    if isinstance(pod_info, PodInfo):
        db.session.add(pod_info)
        db.session.commit()


def update_test(test):
    if isinstance(test, Test):
        db.session.add(test)
        db.session.commit()


def update_insert_tests_to_db(tests, app_obj):
    tests_json = json.loads(tests)
    current_milli_time = int(round(time.time() * 1000))

    for test in tests_json:

        test_hash = create_secure_hash(json.dumps(test))

        current_test_name = test["name"]

        db_test = Test.query.filter_by(name=current_test_name).first()

        if db_test is None:
            db_test = Test(test["name"], json.dumps(test), test_hash,
                           current_milli_time, app_obj.config['POD_ID'])
            update_test(db_test)
        else:
            if not db_test.hash_value == test_hash:
                db_test.test_content = json.dumps(test)
                db_test.hash_value = test_hash
                db_test.lastChangedTimestamp = current_milli_time
                update_test(db_test)

        # TODO: Synchronizing with portal needs to be performed but should be done as scheduled task
