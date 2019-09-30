from src.db.database_schema import TestSequenceSchema, TestSchema
from src.db.database import TestSequence, Test
from src.util import file_utils

import time
import json

from src.util.compare_utils import create_secure_hash


def get_sequence_from_url(url_query, app):
    tasks = url_query["task"]
    sequence_name = url_query["name"]
    sequence_content = []
    current_milli_time = int(round(time.time() * 1000))
    test_schema = TestSchema()

    response = file_utils.read_json_testfile(app)

    file_utils.update_insert_tests_to_db(response, app)

    for task in tasks:
        test = Test.query.filter_by(name=task).first()
        if test:
            sequence_content.append(test_schema.dump(test).data)
        else:
            response_text = "Some of the tasks from the sequence are not included in the services.json config. To be " \
                            "scheduled in the sequence they have to be included in services.json! "

    sequence_hash = create_secure_hash(json.dumps(sequence_content))

    test_sequence = TestSequence(sequence_name, json.dumps(sequence_content), sequence_hash, current_milli_time,
                                 app.config['POD_ID'])

    return test_sequence


def craft_command_from_test_content(test_content):
    test_definition = test_content['test_definition']
    executable = test_definition['step']['command']['executable']
    options = test_definition['step']['command']['parameter']['prefix']
    parameter = test_definition['step']['command']['parameter']['value']

    if options:
        return executable + ' ' + options + ' ' + parameter
    else:
        return executable + ' ' + parameter
