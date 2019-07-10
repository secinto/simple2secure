from src.models.CompanyLicensePod import CompanyLicensePod
from src.util import rest_utils
import zipfile
import os
import app
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


def read_json_testfile():
    # Read test file and return it
    tests_file = open('services.json', 'r')
    content = tests_file.read()
    if not compare_hash_values(check_md5(content)):
        # TODO: Update the database with the tests or insert new ones
        update_insert_tests_to_db(content)

    return content


def parse_license_file(license_file, app):
    lines = license_file.split("\n")
    group_id = ""
    pod_id = app.config['POD_ID']

    for line in lines:
        if "#" not in line:
            row = line.split("=")
            if row[0] == GROUP_ID:
                group_id = row[1]
            elif row[0] == LICENSE_ID:
                app.config['LICENSE_ID'] = row[1]

    if group_id and app.config['LICENSE_ID']:
        # send post to the portal to activate license
        license_obj = CompanyLicensePod(group_id.rstrip(), app.config['LICENSE_ID'].rstrip(), pod_id,
                                        socket.gethostname(), read_json_testfile())
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
    pod_info = app.PodInfo.query.first()

    if pod_info is not None:
        if pod_info.hash_value_service is None:
            pod_info.hash_value_service = current_hash_string
            app.db.session.commit()
            return False
        else:
            if pod_info.hash_value_service == current_hash_string:
                return True
            else:
                pod_info.hash_value_service = current_hash_string
                app.db.session.commit()
                return False

    return False


def update_insert_tests_to_db(tests, app_obj):

    tests_json = json.loads(tests)
    current_milli_time = int(round(time.time() * 1000))
    for test in tests_json:

        test_hash = check_md5(json.dumps(test["test"]))
        current_test_name = test["test"]["name"]

        db_test = app.Test.query.filter_by(name=current_test_name).first()

        if db_test is None:
            current_test = app.Test(test["test"]["name"], json.dumps(test["test"]), test_hash, current_milli_time)
            # sync_test_with_portal(test, app_obj)
            app.db.session.add(current_test)
            app.db.session.commit()

        else:
            test_schema = app.TestSchema()
            output = test_schema.dump(db_test).data
            # sync_test_with_portal(output, app_obj)
            if not db_test.hash_value == test_hash:
                db_test.test_content = json.dumps(test["test"])
                db_test.hash_value = test_hash
                db_test.lastChangedTimestamp = current_milli_time
                app.db.session.commit()


def sync_test_with_portal(test, app_obj):
    rest_utils.portal_post(app_obj.config['PORTAL_URL'] + "test", test, app_obj)
