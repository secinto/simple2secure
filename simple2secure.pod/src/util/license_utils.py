import os
import socket
import zipfile

from src.db.database import CompanyLicensePod
from src.util.db_utils import update_license
from src.util.file_utils import read_json_testfile

EXPIRATION_DATE = "expirationDate"
GROUP_ID = "groupId"
LICENSE_ID = "licenseId"
SIGNATURE = "signature"
LICENSE_FOLDER = 'static/license'

HOSTNAME = socket.gethostname()


def get_license(app_obj):
    store_license = CompanyLicensePod.query.first()
    if store_license is not None:
        return store_license
    else:
        created_license = create_company_license_pod(app_obj)
        update_license(created_license)
        return created_license


def create_company_license_pod(app_obj):
    license_file = get_license_file()
    configuration = read_json_testfile(app_obj)

    if license_file is None:
        dummy_license_obj = CompanyLicensePod("NO_ID", "NO_ID", app_obj.config['POD_ID'], HOSTNAME, configuration)
        return dummy_license_obj
    else:
        lines = license_file.split("\n")

        for line in lines:
            if "#" not in line:
                row = line.split("=")
                if row[0] == GROUP_ID:
                    app_obj.config['GROUP_ID'] = row[1].rstrip()
                elif row[0] == LICENSE_ID:
                    app_obj.config['LICENSE_ID'] = row[1].rstrip()

        if app_obj.config['GROUP_ID'] and app_obj.config['LICENSE_ID'] and app_obj.config['POD_ID']:
            # send post to the portal to activate license
            license_obj = CompanyLicensePod(app_obj.config['GROUP_ID'], app_obj.config['LICENSE_ID'],
                                            app_obj.config['POD_ID'], HOSTNAME, configuration)
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
