import subprocess
import base64
import os

curr_dir = os.getcwd()


def scanner(executable, results, type):
    executable = list(filter(None, executable))
    if "scripts/" in executable[0]:
        executable = get_scripts_executables(executable)
    elif "adapter/" in executable[0]:
        executable = get_adapter_executables(executable)
    process = subprocess.Popen(executable, stdout=subprocess.PIPE)
    stdout = process.communicate()[0]
    result = bytearray(stdout)
    sanitized_result = bytearray()
    for ind, elem in enumerate(result, start=0):
        if elem != 13 and elem != 10:
            sanitized_result.append(elem)
    report = base64.b64encode(sanitized_result)
#    report = stdout.decode("utf-8", "backslashreplace")
    results[type] = report


def get_scripts_executables(executable):
    scripts_dir = curr_dir + os.path.sep + 'scripts' + os.path.sep
    replaced_exec_0 = executable[0].split(" ")[1].replace("scripts/", scripts_dir)
    executable[0] = executable[0].replace(executable[0].split(" ")[1], "")
    new_exec = executable[1].split(" ")
    executable[1] = replaced_exec_0
    for param in new_exec:
        executable.append(param)
    return executable


def get_adapter_executables(executable):
    adapter_dir = curr_dir + os.path.sep + 'adapter' + os.path.sep
    replaced_exec_0 = executable[0].split(" ")[1].replace("adapter/", adapter_dir)
    executable[0] = executable[0].replace(executable[0].split(" ")[1], "")
    executable[1] = replaced_exec_0
    return executable
