from src.db.database import Test
from src.db.database_schema import TestSchema

import json

ALLOWED_EXTENSIONS = {'zip'}


def read_json_testfile():
    # Read test file and return it
    tests_file = open('services.json', 'r')
    return tests_file.read()


def allowed_file(filename):
    # Check if uploaded file has the allowed extension
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS


def write_to_result_log(content):
    # Write the result to the log file
    log_file = open("result/result.json", "w")
    log_file.write(content)
    log_file.close()


def update_services_file():
    data = []
    tests = Test.query.all()
    if tests is not None:
        for test in tests:
            data.append(json.loads(test.test_content))

    if data is not None:
        with open('services.json', 'w') as outfile:
            json.dump(data, outfile, indent=4)

