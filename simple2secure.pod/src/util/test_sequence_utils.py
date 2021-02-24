import json
import logging

from scanner import scanner
from src.db.database import TestSequence
from src.db.session_manager import SessionManager
from src.util.db_utils import update
from src.util.util import get_current_timestamp

log = logging.getLogger('pod.util.test_sequence_utils')


def get_sequence_from_run(sequence_run):
    current_milli_time = get_current_timestamp().__str__()
    seq_content = json.dumps(sequence_run['testSequenceContent'])
    test_sequence = TestSequence(sequence_run['sequenceId'], sequence_run['sequenceName'], seq_content,
                                 current_milli_time, sequence_run['deviceId'])
    return test_sequence


def update_sequence(test_sequence):
    with SessionManager() as session:
        test_sequence = json.loads(test_sequence)
        sequence = TestSequence(test_sequence['id'], test_sequence['name'],
                                json.loads(test_sequence['testSequenceContent']),
                                test_sequence['lastChangedTimeStamp'],
                                test_sequence['podId'])

        # TODO: check sync mechanism for sequences, currently not working properly
        task_seq_from_db = session.query(TestSequence).filter_by(id=sequence.id).first()
        if task_seq_from_db:
            if not task_seq_from_db == sequence:
                task_seq_from_db.sequence_content = test_sequence['testSequenceContent']
                update(task_seq_from_db)
        return sequence


def schedule_test_for_sequence(executable):
    """
    Schedule test for a sequence

    :param executable:
    :return:
    """
    results = {}
    scanner(executable, results, "sequence_result")
    return results["sequence_result"]
