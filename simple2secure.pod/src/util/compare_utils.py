import hashlib
import json

from src.db.database import PodInfo
from src.util.db_utils import update_pod_info


def compare_hash_values(current_hash_string):
    pod_info = PodInfo.query.first()

    if pod_info is not None:
        if pod_info.hash_value_service is None:
            pod_info.hash_value_service = current_hash_string
            update_pod_info(pod_info)
            return False
        else:
            if pod_info.hash_value_service == current_hash_string:
                return True
            else:
                pod_info.hash_value_service = current_hash_string
                update_pod_info(pod_info)
                return False

    return False


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
    prov_descr = prov_test_def['description']
    prov_version = prov_test_def['version']
    prov_precond = prov_test_def['precondition']
    prov_step = prov_test_def['step']
    prov_postcond = prov_test_def['postcondition']
    db_test_def = db_test_def_['test_definition']
    db_descr = db_test_def['description']
    db_version = db_test_def['version']
    db_precond = db_test_def['precondition']
    db_step = db_test_def['step']
    db_postcond = db_test_def['postcondition']

    if prov_descr == db_descr and prov_version == db_version and is_same_test_definition_task(prov_precond, db_precond) \
            and is_same_test_definition_task(prov_step, db_step) and is_same_test_definition_task(prov_postcond,
                                                                                                  db_postcond):
        return True
    else:
        return False


def is_same_test_definition_task(prov_task, db_task):
    prov_descr = prov_task['description']
    prov_command = prov_task['command']

    db_descr = db_task['description']
    db_command = db_task['command']

    if prov_descr == db_descr and is_same_command(prov_command, db_command):
        return True
    else:
        return False


def is_same_command(prov_command, db_command):
    prov_executable = prov_command['executable']
    prov_param = prov_command['parameter']

    db_executable = db_command['executable']
    db_param = db_command['parameter']

    if prov_executable == db_executable and is_same_param(prov_param, db_param):
        return True
    else:
        return False


def is_same_param(prov_param, db_param):
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
