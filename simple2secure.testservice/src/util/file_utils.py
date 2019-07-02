from src.models.CompanyLicensePod import CompanyLicensePod
from src.util import json_utils
import zipfile
import os
import socket

ALLOWED_EXTENSIONS = set(['zip'])
EXPIRATION_DATE = "expirationDate"
GROUP_ID = "groupId"
LICENSE_ID = "licenseId"
SIGNATURE = "signature"
LICENSE_FOLDER = 'static/license'


def parse_license_file(license_file, app):
    lines = license_file.split("\n")
    group_id = ""
    #TODO: POD_ID is empty?
    pod_id = app.config['POD_ID']

    for line in lines:
        if "#" not in line:
            row = line.split("=")
            if row[0] == GROUP_ID:
                group_id = row[1]
            elif row[0] == LICENSE_ID:
                app.config['LICENSE_ID'] = row[1]

    if group_id and app.config['LICENSE_ID']:
        # send post to the portal to activate license
        license_obj = CompanyLicensePod(group_id.rstrip(), app.config['LICENSE_ID'].rstrip(), pod_id,
                                        socket.gethostname(), json_utils.read_json_testfile())
        return license_obj


def get_license_file():

    files = os.listdir(LICENSE_FOLDER)

    if len(files) == 1:
        archive = zipfile.ZipFile(LICENSE_FOLDER + "/" + files[0], 'r')

        zip_files = [name for name in archive.namelist() if name.endswith('.dat')]

        if len(zip_files) == 1:
            zip_file_content = archive.read(zip_files[0])
            decoded_license_file = zip_file_content.decode()
            archive.close()
            return decoded_license_file


def allowed_file(filename):
    # Check if uploaded file has the allowed extension
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS


def write_to_result_log(content):
    # Write the result to the log file
    log_file = open("result/result.json", "w")
    log_file.write(content)
    log_file.close()


