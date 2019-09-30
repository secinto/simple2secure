from src.db.database import db, Test, TestSequence, PodInfo, CompanyLicensePod
from src.db.database_schema import TestSchema
from src.util.compare_utils import is_same_sequence_content, create_secure_hash
from src.util.rest_utils import sync_test_with_portal
from src.util.util import generate_test_object

import time
import json



def update_license(license_object):
    if isinstance(license_object, CompanyLicensePod):
        db.session.add(license)
        db.session.commit()


def update_pod_info(pod_info):
    if isinstance(pod_info, PodInfo):
        db.session.add(pod_info)
        db.session.commit()


def update_insert_tests_to_db(tests, app_obj):
    tests_json = json.loads(tests)
    current_milli_time = int(round(time.time() * 1000))
    test_schema = TestSchema()
    for test in tests_json:

        test_hash = create_secure_hash(json.dumps(test))
        current_test_name = test["name"]

        db_test = Test.query.filter_by(name=current_test_name).first()

        if db_test is None:
            current_test = Test(test["name"], json.dumps(test), test_hash,
                                current_milli_time, app_obj.config['POD_ID'])
            output = test_schema.dump(current_test)
            sync_test = sync_test_with_portal(output, app_obj)
            test_obj = generate_test_object(sync_test)
            db.session.add(test_obj)
            db.session.commit()

        else:
            if not db_test.hash_value == test_hash:
                db_test.test_content = json.dumps(test)
                db_test.hash_value = test_hash
                db_test.lastChangedTimestamp = current_milli_time

            output = test_schema.dump(db_test)
            sync_test = sync_test_with_portal(output, app_obj)
            test_obj = generate_test_object(sync_test)
            db_test.test_content = test_obj.test_content
            db_test.hash_value = test_obj.hash_value
            db.session.commit()


def update_add_sequence_to_db(test_sequence):
    provided_sequence_name = test_sequence['name']
    provided_sequence_podid = test_sequence['podId']
    provided_sequence_hash_value = test_sequence['hash_value']
    provided_sequence_timestamp = test_sequence['lastChangedTimestamp']
    provided_sequence_content_string = test_sequence['sequence_content']
    provided_sequence_content = json.loads(provided_sequence_content_string)

    task_seq_from_db = TestSequence.query.filter_by(name=provided_sequence_name).first()
    if task_seq_from_db:
        db_sequence_name = task_seq_from_db.name
        db_sequence_podid = task_seq_from_db.podId
        db_sequence_content = json.loads(task_seq_from_db.sequence_content)

        if provided_sequence_name != db_sequence_name and provided_sequence_podid != db_sequence_podid and not is_same_sequence_content(
                provided_sequence_content, db_sequence_content):
            task_seq_from_db.name = test_sequence['name']
            task_seq_from_db.podId = test_sequence['podId']
            task_seq_from_db.hash_value = test_sequence['hash_value']
            task_seq_from_db.lastChangedTimestamp = test_sequence['lastChangedTimestamp']
            task_seq_from_db.sequence_content = test_sequence['sequence_content']
            sequence = task_seq_from_db
        else:
            sequence = task_seq_from_db
    else:
        sequence = TestSequence(provided_sequence_name, json.dumps(provided_sequence_content),
                                provided_sequence_hash_value, provided_sequence_timestamp, provided_sequence_podid)

    return sequence
