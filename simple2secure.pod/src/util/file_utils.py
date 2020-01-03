import glob
import json
import logging
import os
import zipfile

from src.db.database import Test, TestSequence

ALLOWED_EXTENSIONS = {'zip'}

log = logging.getLogger('pod.util.file_utils')


def read_json_testfile():
    # Read test file and return it
    tests_file = open('services.json', 'r')
    return tests_file.read()


def allowed_file(filename):
    # Check if uploaded file has the allowed extension
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS


def write_to_result_log(content):
    # Write the result to the log file
    log_file = open("result/result.json", "w")
    log_file.write(content)
    log_file.close()


def update_services_file():
    """
    Synchronizes the services.json file with the Tests stored in the DB locally. The locally stored tests are
    synchronized with the Portal. Thus, all locations should be synchronized. This is done because if no DB entries
    are available the services.json is used as backup.
    """
    data = []
    tests = Test.query.all()
    if tests is not None:
        for test in tests:
            data.append(json.loads(test.test_content))

    if data is not None:
        with open('services.json', 'w') as outfile:
            json.dump(data, outfile, indent=4)


def get_license_file(app):
    """
    Reads the license file from the file system if the directory is available and a license ZIP file is contained in
    it. If several licenses are available from the directory, the newest one is returned.

    Parameters:
        app: Context object

    Returns:
        A byte stream of the decoded ZIP license file
    """
    licenseFolder = app.config['LICENSE_FOLDER']

    if os.path.exists(licenseFolder):
        files = glob.glob(licenseFolder + '/*.zip')
    else:
        log.error('Provided path %s does not exist', licenseFolder)
        return None

    file = None

    if len(files) == 1:
        file = files[0]
    elif len(files) > 1:
        file = max(files, key=os.path.getctime)

    if file is not None and os.path.exists(file):
        archive = zipfile.ZipFile(file, 'r')

        zip_files = [name for name in archive.namelist() if name.endswith('.dat')]

        if len(zip_files) == 1:
            zip_file_content = archive.read(zip_files[0])
            decoded_license_file = zip_file_content.decode()
            archive.close()
            return decoded_license_file
