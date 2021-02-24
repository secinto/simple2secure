import json
import logging

log = logging.getLogger('pod.util.json_utils')


def prepare_test_section_for_execution(test, section):
    executable = [test[section]['command']['executable']]
    parameters = test[section]['command']['parameter']
    for param in parameters:
        executable.append(param["prefix"])
        executable.append(param["value"])
    return executable


def prepare_sequence_test_section_for_execution(test):
    if test and test['test_definition']:
        executable = [test['test_definition']['step']['command']['executable']]
        parameters = test['test_definition']['step']['command']['parameter']
        for param in parameters:
            executable.append(param["prefix"])
            executable.append(param["value"])
        return executable
