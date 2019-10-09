import logging

from src.db.database_schema import TestSchema
from src.db.database import TestSequence, Test

import time
import json

from src.util.compare_utils import is_same_sequence_content
from src.util.file_utils import read_json_testfile
from src.util.test_utils import update_insert_tests_to_db
from src.util.util import create_secure_hash

log = logging.getLogger('pod.util.task_utils')


def get_sequence_from_url(url_query, app):
    tasks = url_query["task"]
    sequence_name = url_query["name"]
    sequence_content = []
    current_milli_time = int(round(time.time() * 1000))
    test_schema = TestSchema()

    response = read_json_testfile()

    update_insert_tests_to_db(response, app)

    for task in tasks:
        test = Test.query.filter_by(name=task).first()
        if test:
            sequence_content.append(test_schema.dump(test).data)

    sequence_hash = create_secure_hash(json.dumps(sequence_content))

    test_sequence = TestSequence(sequence_name, json.dumps(sequence_content), sequence_hash, current_milli_time,
                                 app.config['POD_ID'])

    return test_sequence


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

        if provided_sequence_name != db_sequence_name and provided_sequence_podid != db_sequence_podid \
                and not is_same_sequence_content(provided_sequence_content, db_sequence_content):
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


def craft_command_from_test_content(test_content):
    test_definition = test_content['test_definition']
    executable = test_definition['step']['command']['executable']
    options = test_definition['step']['command']['parameter']['prefix']
    parameter = test_definition['step']['command']['parameter']['value']

    if options:
        return executable + ' ' + options + ' ' + parameter
    else:
        return executable + ' ' + parameter
