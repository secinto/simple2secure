import subprocess
import chardet


def scanner(executable, results, type):
    # Process the provided command and write the result to the array results
    process = subprocess.Popen(executable, stdout=subprocess.PIPE)
    stdout = process.communicate()[0]
    # TODO: The result encoding has a confidence, in future check wether the confidence is high enough to use this encoding or return an error message.
    # Determines the encoding of the byte array, the result
    # contains a confidence property which indicates how certain the encoding is.
    result_encoding = chardet.detect(stdout)
    report = stdout.decode(result_encoding['encoding'])
    results[type] = report
