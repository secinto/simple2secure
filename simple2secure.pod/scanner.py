import subprocess
import base64

def scanner(executable, results, type):
    executable = list(filter(None, executable))
    process = subprocess.Popen(executable, stdout=subprocess.PIPE)
    stdout = process.communicate()[0]
    result = bytearray(stdout)
    sanitized_result = bytearray()
    for ind, elem in enumerate(result, start=0):
        if elem != 13 and elem != 10:
            sanitized_result.append(elem)
    report = base64.b64encode(sanitized_result)
    #report = stdout.decode("utf-8", "backslashreplace")
    results[type] = report
