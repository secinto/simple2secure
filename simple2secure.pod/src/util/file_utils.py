from src.db.database import db, PodInfo, Test, CompanyLicensePod, TestSequence
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

    converted_tests = []
    tests = Test.query.all()
    
    for crnt_test in tests:
        test_schema = TestSchema()
        output = test_schema.dump(crnt_test).data
        converted_tests.append(output)
    
      
    return json.dumps(converted_tests)


def parse_license_file(license_file, appObj):
    if license_file is None:
        dummy_license_obj = CompanyLicensePod("NO_ID", "NO_ID", appObj.config['POD_ID'], socket.gethostname(), "NONE")
        return dummy_license_obj
    else:
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
            output = test_schema.dump(current_test)
            sync_test = sync_test_with_portal(output, app_obj)
            test_obj = generate_test_object(sync_test)
            db.session.add(test_obj)
            db.session.commit()

        else:
            output = test_schema.dump(db_test)
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
    sequence = ""
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
        db_sequence_has_value = task_seq_from_db.hash_value
        db_sequence_timestamp = task_seq_from_db.lastChangedTimestamp
        db_sequence_content_string = task_seq_from_db.sequence_content
        db_sequence_content = json.loads(task_seq_from_db.sequence_content)
        
        if provided_sequence_name != db_sequence_name and provided_sequence_podid != db_sequence_podid and not is_same_sequence_content(provided_sequence_content, db_sequence_content):
            task_seq_from_db.name = test_sequence['name']
            task_seq_from_db.podId = test_sequence['podId']
            task_seq_from_db.hash_value = test_sequence['hash_value']
            task_seq_from_db.lastChangedTimestamp = test_sequence['lastChangedTimestamp']
            task_seq_from_db.sequence_content = test_sequence['sequence_content']
            sequence = task_seq_from_db
        else:
            sequence = task_seq_from_db
    else:
        sequence = TestSequence(provided_sequence_name, json.dumps(provided_sequence_content), provided_sequence_hash_value, provided_sequence_timestamp, provided_sequence_podid)
        
    #db.session.merge(sequence)
    #db.session.commit()
    
    return sequence

def is_same_sequence_content(prov_seq_cont, db_seq_cont):
    result = []
    for i, task in enumerate(prov_seq_cont, 0):
        if is_same_test(task, db_seq_cont[i]):
            result.append(True)
        else:
            result.append(False)
    return all(result)


def is_same_test(prov_test, db_test):
    prov_name = prov_test['name']
    prov_def_str = prov_test['test_content']
    prov_def = json.loads(prov_def_str)
    
    db_name = db_test['name']
    db_def_str = db_test['test_content']
    db_def = json.loads(db_def_str)
    
    if prov_name == db_name and is_same_test_definition(prov_def, db_def):
        return True
    else:
        return False


def is_same_test_definition(prov_test_def_, db_test_def_):
    prov_test_def = prov_test_def_['test_definition']
    db_test_def = db_test_def_['test_definition']
    prov_descr = prov_test_def['description']
    prov_version = prov_test_def['version']
    
    #prov_precond_str = prov_test_def['precondition']
    #prov_precond = json.loads(prov_precond_str)
    prov_precond = prov_test_def['precondition']
    
    #prov_step_str = prov_test_def['step']
    #prov_step = json.loads(prov_step_str)
    prov_step = prov_test_def['step']
    
    #prov_postcond_str = prov_test_def['postcondition']
    #prov_postcond = json.loads(prov_postcond_str)
    prov_postcond = prov_test_def['postcondition']
    
    
    db_descr = db_test_def['description']
    db_version = db_test_def['version']
    
    #db_precond_str = db_test_def['precondition']
    #db_precond = json.loads(db_precond_str)
    db_precond = db_test_def['precondition']
    
    #db_step_str = db_test_def['step']
    #db_step = json.loads(db_step_str)
    db_step = db_test_def['step']
    
    #db_postcond_str = db_test_def['postcondition']
    #db_postcond = json.loads(db_postcond_str)
    db_postcond = db_test_def['postcondition']
    
    if prov_descr == db_descr and prov_version == db_version and is_same_test_definition_task(prov_precond, db_precond) and is_same_test_definition_task(prov_step, db_step) and is_same_test_definition_task(prov_postcond, db_postcond):
        return True
    else:
        return False

def is_same_test_definition_task(prov_task, db_task):
    prov_descr = prov_task['description']
    #prov_command_str = prov_task['command']
    #prov_command = json.loads(prov_command_str)
    prov_command = prov_task['command']
    
    db_descr = db_task['description']
    #db_command_str = db_task['command']
    #db_command = json.loads(db_command_str)
    db_command = db_task['command']
    
    if prov_descr == db_descr and is_same_command(prov_command, db_command):
        return True
    else:
        return False
    

def is_same_command(prov_command, db_command):
    prov_executable = prov_command['executable']
    #prov_param_str = prov_command['parameter']
    #prov_param = json.loads(prov_param_str)
    prov_param = prov_command['parameter']
    
    db_executable = db_command['executable']
    #db_param_str = db_command['parameter']
    #db_param = json.loads(db_param_str)
    db_param = db_command['parameter']
    
    if prov_executable == db_executable and is_same_param(prov_param, db_param):
        return True
    else:
        return False

def is_same_param(prov_param, db_param):
    result = []
    prov_descr = prov_param['description']
    prov_prefix = prov_param['prefix']
    prov_value = prov_param['value']
    
    db_descr = db_param['description']
    db_prefix = db_param['prefix']
    db_value = db_param['value']
    
    if prov_descr == db_descr and prov_prefix == db_prefix and prov_value == db_value:
        return True
    else:
        return False
        

# def is_same_sequence_content(sequence_content_prov, sequence_content_db):
#     db_task = {}
#     len_of_prov_seq = len(sequence_content_prov)
#     len_of_db_seq = len(sequence_content_db)
#     for i,task in enumerate(sequence_content_prov, 0):
#         if task['id'] == sequence_content_db[i]['id']:
#             prov_task_name = task['name']
#             prov_task_hash_value = task['hash_value']
#             prov_task_time_stamp = task['lastChangedTimestamp']
#             prov_task_content_str = task['test_content']
#             prov_task_content = json.loads(prov_task_content_str)
#         
#             db_task_id = sequence_content_db[i]['id']
#             db_task_name = sequence_content_db[i]['name']
#             db_task_hash_value = sequence_content_db[i]['hash_value']
#             db_task_time_stamp = sequence_content_db[i]['lastChangedTimestamp']
#             db_task_content_str = sequence_content_db[i]['test_content']
#             db_task_content = json.loads(db_task_content_str)
#             test = is_same_task_content(prov_task_content, db_task_content)
#         
#         
#     return True
# 
# 
# def is_same_task_content(task_content_prov, task_content_db):
#     return task_content_prov.keys() - task_content_db.keys()
    
def sync_test_with_portal(test, app_obj):
    response = rest_utils.portal_post_test(app_obj.config['PORTAL_URL'] + "test/saveTestPod", test, app_obj)
    return response


def schedule_test_on_the_portal(test, app_obj, pod_id):
    response = rest_utils.portal_post_test_response(app_obj.config['PORTAL_URL'] + "test/scheduleTestPod/" + pod_id,
                                                    test, app_obj)
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
