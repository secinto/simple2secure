from os import *
import subprocess


def scanner(command, results, type):
    #    process = subprocess.Popen(command, stdout=subprocess.PIPE)
    #    stdout = process.communicate()[0]
    #    report = str(stdout, 'iso-8859-1')
    #    results[type] = report
    # Process the provided command and write the result to the array results
    process = popen(command)
    report = process.read()

    results[type] = report
