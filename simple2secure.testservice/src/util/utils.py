import platform
import os
import app
import zipfile
import requests
from src.models.CompanyLicensePublic import CompanyLicensePublic
from flask import json, session
import socket

ALLOWED_EXTENSIONS = set(['zip'])
EXPIRATION_DATE = "expirationDate"
GROUP_ID = "groupId"
LICENSE_ID = "licenseId"
SIGNATURE = "signature"


def read_json_testfile():
    # Read test file and return it
    tests_file = open('services.json', 'r')
    return tests_file.read()


def is_blank(mystring):
    # Check if provided string is null or empty
    if mystring and mystring.strip():
        return False
    return True


def get_tool(argument):
    # Check the provided argument and return the real
    # command according to the current OS
    if argument == 'SAVE_RESULT':
        if platform.system().lower() == 'windows':
            return 'type nul >'
        else:
            return 'mkdir'
    elif argument == 'DELETE_RESULT':
        if platform.system().lower() == 'windows':
            return 'del'
        else:
            return 'rm -rf'
    else:
        return argument


def construct_command(tool, argument):
    # Construct command from the tool name and argument
    return tool + ' ' + argument


def write_to_result_log(content):
    # Write the result to the log file
    log_file = open("result/result.json", "w")
    log_file.write(content)
    log_file.close()


def get_json_test_object(data, test_id, key, attribute):
    # Iterate over data object
    for test in data:
        # Check if provided test id is equal to the test_id of the current object
        if test["id"] == test_id:
            # Check what is the provided attribute name and get value of that attribute
            if attribute == "command":
                return test[key][attribute]["executable"]
            elif attribute == "parameter":
                return test[key]["command"][attribute]["value"]


def parse_query_test(query_string, item_type):
    # Split query params by "%"
    mylist = query_string.split("%")
    print(enumerate(mylist))
    for index, item in enumerate(mylist):
        # Split sub params by "="
        mysublist = item.split("=")
        # Check if current attribute equals the provided item_type
        if mysublist[0] == item_type:
            correct_item = mylist[index+1].split("=")
            return correct_item[1]


def get_test_item_value(data, query_string, test_id, test_type, attribute):
    # if query_string is empty get the object from the file
    if is_blank(query_string):
        value = get_json_test_object(data, test_id, test_type, attribute)
    else:
        # If query string of the searched attribute is empty get it from the file
        if is_blank(parse_query_test(query_string, attribute)):
            value = get_json_test_object(data, test_id, test_type, attribute)
        # Read the value from the query_string
        else:
            value = parse_query_test(query_string, attribute)

    return value


def allowed_file(filename):
    # Check if uploaded file has the allowed extension
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS


def get_license_file():
    files = os.listdir(app.LICENSE_FOLDER)
    if len(files) == 1:
        archive = zipfile.ZipFile(app.LICENSE_FOLDER + "/" + files[0], 'r')

        zip_files = [name for name in archive.namelist() if name.endswith('.dat')]

        if len(zip_files) == 1:
            zip_file_content = archive.read(zip_files[0])
            decoded_license_file = zip_file_content.decode()
            archive.close()
            return decoded_license_file


def parse_license_file(license_file):
    lines = license_file.split("\n")
    group_id = ""
    pod_id = app.POD_ID
    for line in lines:
        if "#" not in line:
            row = line.split("=")
            if row[0] == GROUP_ID:
                group_id = row[1]
            elif row[0] == LICENSE_ID:
                app.license_id = row[1]

    if group_id and app.license_id:
        # send post to the portal to activate license
        licenseObj = CompanyLicensePublic(group_id.rstrip(), app.license_id.rstrip(), pod_id, socket.gethostname())
        return licenseObj


def portal_post(url, data):

    if not session['auth_token']:
        session['auth_token'] = get_auth_token()
    print(" * Auth Token before posting function: " + session['auth_token'])
    headers = {'Content-Type': 'application/json', 'Accept-Language': 'en-EN', 'Authorization': "Bearer " +
                                                                                                session['auth_token']}
    requests.post(url, data=json.dumps(data), verify=False, headers=headers)


def get_auth_token():
    # TODO: get complete object with both tokens
    headers = {'Content-Type': 'application/json', 'Accept-Language': 'en-EN'}
    return requests.post(app.PORTAL_URL + "license/activatePod",
                         data=json.dumps(parse_license_file(get_license_file()).__dict__),
                         verify=False,
                         headers=headers).text















