import glob
import logging
import os
import zipfile

ALLOWED_EXTENSIONS = {'zip'}

log = logging.getLogger('pod.util.file_utils')


def read_json_testfile():
    # Read test file and return it
    tests_file = open('services.json', 'r')
    return tests_file.read()


def get_license_file(app):
    """
    Reads the license file from the file system if the directory is available and a license ZIP file is contained in
    it. If several licenses are available from the directory, the newest one is returned.

    Parameters:
        app: Context object

    Returns:
        A byte stream of the decoded ZIP license file
    """
    license_folder = app.LICENSE_FOLDER

    if os.path.exists(license_folder):
        files = glob.glob(license_folder + '/*.zip')
    else:
        log.error('Provided path %s does not exist', license_folder)
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
