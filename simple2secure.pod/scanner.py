import subprocess
import chardet

def scanner(executable, results, type):
    executable = list(filter(None, executable))
    process = subprocess.Popen(executable, stdout=subprocess.PIPE)
    stdout = process.communicate()[0]
    report = stdout.decode("utf-8", "backslashreplace")
    results[type] = report