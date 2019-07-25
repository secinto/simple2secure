from src.db.database import db, PodInfo, Test, CompanyLicensePod
from src.db.database_schema import TestSchema
from src.util import rest_utils
import zipfile
import os
import socket
import hashlib
import json
import time

ALLOWED_EXTENSIONS = set(['zip'])
EXPIRATION_DATE = "expirationDate"
GROUP_ID = "groupId"
LICENSE_ID = "licenseId"
SIGNATURE = "signature"
LICENSE_FOLDER = 'static/license'


def read_json_testfile(appObj):
    # Read test file and return it
    tests_file = open('services.json', 'r')
    content = tests_file.read()
    if not compare_hash_values(check_md5(content)):
        # TODO: Update the database with the tests or insert new ones
        update_insert_tests_to_db(content, appObj)

    return content


def parse_license_file(license_file, appObj):
    lines = license_file.split("\n")
    group_id = ""
    pod_id = appObj.config['POD_ID']

    for line in lines:
        if "#" not in line:
            row = line.split("=")
            if row[0] == GROUP_ID:
                group_id = row[1]
            elif row[0] == LICENSE_ID:
                appObj.config['LICENSE_ID'] = row[1]

    if group_id and appObj.config['LICENSE_ID']:
        # send post to the portal to activate license
        license_obj = CompanyLicensePod(group_id.rstrip(), appObj.config['LICENSE_ID'].rstrip(), pod_id,
                                        socket.gethostname(), read_json_testfile(appObj))
        return license_obj


def get_license_file():

    files = os.listdir(LICENSE_FOLDER)

    if len(files) == 1:
        archive = zipfile.ZipFile(LICENSE_FOLDER + "/" + files[0], 'r')

        zip_files = [name for name in archive.namelist() if name.endswith('.dat')]

        if len(zip_files) == 1:
            zip_file_content = archive.read(zip_files[0])
            decoded_license_file = zip_file_content.decode()
            archive.close()
            return decoded_license_file


def allowed_file(filename):
    # Check if uploaded file has the allowed extension
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS


def write_to_result_log(content):
    # Write the result to the log file
    log_file = open("result/result.json", "w")
    log_file.write(content)
    log_file.close()


def check_md5(content):
    current_hash_string = hashlib.md5(content.encode('utf-8')).hexdigest()
    return current_hash_string


def compare_hash_values(current_hash_string):
    pod_info = PodInfo.query.first()

    if pod_info is not None:
        if pod_info.hash_value_service is None:
            pod_info.hash_value_service = current_hash_string
            db.session.commit()
            return False
        else:
            if pod_info.hash_value_service == current_hash_string:
                return True
            else:
                pod_info.hash_value_service = current_hash_string
                db.session.commit()
                return False

    return False


def update_insert_tests_to_db(tests, app_obj):

    tests_json = json.loads(tests)
    current_milli_time = int(round(time.time() * 1000))
    test_schema = TestSchema()
    for test in tests_json:

        test_hash = check_md5(json.dumps(test))
        current_test_name = test["name"]

        db_test = Test.query.filter_by(name=current_test_name).first()

        if db_test is None:
            current_test = Test(test["name"], json.dumps(test), test_hash,
                                current_milli_time, app_obj.config['POD_ID'])
            output = test_schema.dump(current_test).data
            sync_test = sync_test_with_portal(output, app_obj)
            test_obj = generate_test_object(sync_test)
            db.session.add(test_obj)
            db.session.commit()

        else:
            output = test_schema.dump(db_test).data
            if not db_test.hash_value == test_hash:
                db_test.test_content = json.dumps(test)
                db_test.hash_value = test_hash
                db_test.lastChangedTimestamp = current_milli_time

            output = test_schema.dump(db_test).data
            sync_test = sync_test_with_portal(output, app_obj)
            test_obj = generate_test_object(sync_test)
            db_test.test_content = test_obj.test_content
            db_test.hash_value = test_obj.hash_value
            db.session.commit()


def sync_test_with_portal(test, app_obj):
    response = rest_utils.portal_post_test(app_obj.config['PORTAL_URL'] + "test/saveTestPod", test, app_obj)
    return response


def sync_all_tests_with_portal(test, app_obj):
    response = rest_utils.portal_post_test_response(app_obj.config['PORTAL_URL'] + "test/syncTests", test, app_obj)
    return response


def generate_test_object(sync_test):
    sync_test_json = json.loads(sync_test)
    test = Test(sync_test_json["name"], sync_test_json["test_content"], sync_test_json["hash_value"],
                sync_test_json["lastChangedTimestamp"], sync_test_json["podId"])
    test.id = sync_test_json["id"]
    return test


def generate_test_object_from_json(sync_test_json):
    test = Test(sync_test_json["name"], sync_test_json["test_content"], sync_test_json["hash_value"],
                sync_test_json["lastChangedTimestamp"], sync_test_json["podId"])
    test.id = sync_test_json["id"]
    return test


def update_services_file():
    data = []
    tests = Test.query.all()
    if tests is not None:
        for test in tests:
            data.append(json.loads(test.test_content))

    if data is not None:
        with open('services.json', 'w') as outfile:
            json.dump(data, outfile, indent=4)
