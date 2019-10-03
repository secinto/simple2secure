import datetime
import glob
import os
import secrets
import socket
import zipfile

from src.db.database import CompanyLicensePublic, PodInfo
from src.util.db_utils import update
from src.util.file_utils import read_json_testfile
from src.util.util import get_date_from_string

EXPIRATION_DATE = "expirationDate"
GROUP_ID = "groupId"
LICENSE_ID = "licenseId"
SIGNATURE = "signature"
LICENSE_FOLDER = 'static/license'

HOSTNAME = socket.gethostname()


def get_pod(app):
    with app.app_context():
        pod_info = PodInfo.query.first()
        if pod_info is None:
            create_pod(app)
        else:
            app.config['POD_ID'] = pod_info.generated_id
            app.config['AUTH_TOKEN'] = pod_info.access_token
            app.logger.info('Using existing pod id from the database: %s', app.config['POD_ID'])


def create_pod(app):
    with app.app_context():
        app.config['POD_ID'] = secrets.token_urlsafe(20)
        app.logger.info('Generating new pod id: %s', app.config['POD_ID'])
        pod_info = PodInfo(app.config['POD_ID'], app.config['AUTH_TOKEN'], "")
        update(pod_info)
        return pod_info


# ----------------------------------------------------------------------
# License part of license utils
# ----------------------------------------------------------------------

def get_license(app, check_for_new=False):
    if check_for_new:
        get_and_store_license(app)
    else:
        stored_license = CompanyLicensePublic.query.first()
        if stored_license is not None and stored_license.licenseId != 'NO_ID':
            return stored_license
        else:
            return get_and_store_license(app)


def get_and_store_license(app):
    created_license = create_license(app)
    if created_license.licenseId != 'NO_ID':
        update(created_license)
    return created_license


def create_license(app):
    """Reads the license from file if available and returns a CompanyLicensePod object from it if the POD_ID has been
    already created and the license file is available from the default folder

    Parameters:
        app: Context object

    Returns:
        CompanyLicensePod: either a dummy object if no license is available or the correct object
    """
    license_file = get_license_file(app)

    if license_file is None:
        dummy_license_obj = CompanyLicensePublic("NO_ID", "NO_ID", app.config['POD_ID'], datetime.datetime.now().date(), HOSTNAME)
        return dummy_license_obj
    else:
        lines = license_file.split("\n")
        expiration_date = datetime.datetime.now().date()
        for line in lines:
            if "#" not in line:
                row = line.split("=")
                if row[0] == GROUP_ID:
                    app.config['GROUP_ID'] = row[1].rstrip()
                elif row[0] == EXPIRATION_DATE:
                    expiration_date = get_date_from_string(row[1].rstrip())
                elif row[0] == LICENSE_ID:
                    app.config['LICENSE_ID'] = row[1].rstrip()

        if app.config['GROUP_ID'] and app.config['LICENSE_ID'] and app.config['POD_ID']:
            license_obj = CompanyLicensePublic(app.config['GROUP_ID'], app.config['LICENSE_ID'],
                                            app.config['POD_ID'], expiration_date, HOSTNAME)
            return license_obj


def get_license_file(app):
    if os.path.exists(LICENSE_FOLDER):
        files = glob.glob(LICENSE_FOLDER + '/*.zip')
    else:
        app.logger.error('Provided path %s does not exist', LICENSE_FOLDER)
        return None

    file = None

    if len(files) == 1:
        file = files[0]
    elif len(files) > 1:
        file = min(files, key=os.path.getctime)

    if file is not None and os.path.exists(file):
        archive = zipfile.ZipFile(file, 'r')

        zip_files = [name for name in archive.namelist() if name.endswith('.dat')]

        if len(zip_files) == 1:
            zip_file_content = archive.read(zip_files[0])
            decoded_license_file = zip_file_content.decode()
            archive.close()
            return decoded_license_file


