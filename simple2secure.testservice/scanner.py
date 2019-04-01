from os import *


def scanner(command, results, type):
    # Process the provided command and write the result to the array results
    process = popen(command)
    report = str(process.read())
    results[type + " - (" +command + ")"] = report
