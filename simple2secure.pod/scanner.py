import subprocess


def scanner(command, results, type):
    # Process the provided command and write the result to the array results
    process = subprocess.Popen(command, stdout=subprocess.PIPE)
    stdout = process.communicate()[0]
    report = str(stdout, 'iso-8859-1')
    results[type] = report
