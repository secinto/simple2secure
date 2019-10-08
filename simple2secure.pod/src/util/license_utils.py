import datetime
import glob
import logging
import os
import secrets
import socket
import zipfile

from src.db.database import CompanyLicensePublic, PodInfo
from src.util.db_utils import update
from src.util.util import get_date_from_string

EXPIRATION_DATE = "expirationDate"
GROUP_ID = "groupId"
LICENSE_ID = "licenseId"
SIGNATURE = "signature"
LICENSE_FOLDER = 'static/license'

HOSTNAME = socket.gethostname()

log = logging.getLogger('pod.util.license_utils')


def get_pod(app):
    """
    Obtains the PodInfo object for this POD if available from the database or creates it and stores it in the DB.

    Parameters:
        app: Context object
    Returns:
        CompanyLicensePod: either a dummy object if no license is available or the correct object
    """
    with app.app_context():
        pod_info = PodInfo.query.first()
        if pod_info is None:
            pod_info = create_pod(app)
        else:
            app.config['POD_ID'] = pod_info.generated_id
            log.info('Using existing pod id from the database: %s', app.config['POD_ID'])

    return pod_info

def create_pod(app):
    """

    Parameters:
        app: Context object
    Returns:
        PodInfo: Returns the currently created PodInfo object from the

    """
    with app.app_context():
        app.config['POD_ID'] = secrets.token_urlsafe(20)
        log.info('Generated new pod id: %s', app.config['POD_ID'])

        if app.config['AUTH_TOKEN']:
            log.info("Currently not storing the AUTH_TOKEN with the PodInfo since it is not available")

        pod_info = PodInfo(app.config['POD_ID'], "")
        update(pod_info)
        log.info("Stored new PodInfo in DB")
        return pod_info


# ----------------------------------------------------------------------
# License part of license utils
# ----------------------------------------------------------------------

def get_license(app, check_for_new=False):
    """
    Checks if a license is available. If a license is already contained in the database this is returned. If not a
    new one is created. If check_for_new is specified it is checked if a new license in the file system exists and
    uses this one as the new license.

    Parameters:
        app: Context object
        check_for_new: True if it should be checked if a new license file is available from the file system
    Returns:
        CompanyLicensePod: either a dummy object if no license is available or the correct object
    """
    if check_for_new:
        return get_and_store_license(app, check_for_new)
    else:
        stored_license = CompanyLicensePublic.query.first()
        if stored_license is not None and stored_license.licenseId != 'NO_ID':
            return stored_license
        else:
            return get_and_store_license(app)


def get_and_store_license(app, check_for_new=False):
    """
    Parameters:
        app: Context object
        check_for_new: True if it should be checked if a new license file is available from the file system
    Returns:
        CompanyLicensePod: either a dummy object if no license is available or the correct object
    """
    created_license = create_license(app)
    if check_for_new:
        stored_license = CompanyLicensePublic.query.first()
        if stored_license is not None and stored_license.licenseId == created_license.licenseId:
            return stored_license

    if created_license.licenseId != 'NO_ID':
        update(created_license)
    return created_license


def create_license(app):
    """Reads the license from file if available and returns a CompanyLicensePod object from it if the POD_ID has been
    already created and the license file is available from the default folder. If no license is available from the
    file system a dummy license object is returned.

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
    """
    Reads the license file from the file system if the directory is available and a license ZIP file is contained in
    it. If several licenses are available from the directory, the newest one is returned.

    Parameters:
        app: Context object

    Returns:
        A byte stream of the decoded ZIP license file
    """
    if os.path.exists(LICENSE_FOLDER):
        files = glob.glob(LICENSE_FOLDER + '/*.zip')
    else:
        log.error('Provided path %s does not exist', LICENSE_FOLDER)
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


