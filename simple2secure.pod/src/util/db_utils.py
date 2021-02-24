import datetime
import logging
import socket

from bson.objectid import ObjectId

from src.db.database import PodInfo, CompanyLicensePublic, Test
from src.db.session_manager import SessionManager
from src.util.file_utils import get_license_file
from src.util.util import get_date_from_string

EXPIRATION_DATE = "expirationDate"
GROUP_ID = "groupId"
LICENSE_ID = "licenseId"
SIGNATURE = "signature"

HOSTNAME = socket.gethostname()

log = logging.getLogger('pod.util.db_utils')


def update(some_object):
    with SessionManager() as session:
        session.add(some_object)


def update_test(test):
    with SessionManager() as session:
        test_db = session.query(Test).filter_by(id=test.id).first()
        if test_db is None:
            update(test)
        else:
            test_db.name = test.name
            test_db.testContent = test.testContent
            test_db.lastChangedTimestamp = test.lastChangedTimestamp
            session.flush()
            session.commit()


def get_pod():
    """
    Obtains the PodInfo object for this POD if available from the database or creates it and stores it in the DB.

    Returns:
        CompanyLicensePod: either a dummy object if no license is available or the correct object
    """
    with SessionManager() as session:
        pod_info = session.query(PodInfo).first()
        if pod_info is None:
            create_pod()
            pod_info = session.query(PodInfo).first()

        log.debug('Using existing pod id from the database: {}'.format(pod_info.id))
        return pod_info


def create_pod():
    """
    Creates a new pod Id and storing it as PodInfo in the database

    Returns:
        PodInfo: Returns the currently created PodInfo object from the
    """
    pod_id = str(ObjectId())
    log.info('Generated new pod id: %s', pod_id)

    pod_info = PodInfo(pod_id)
    update(pod_info)
    log.info("Stored new PodInfo in DB")


def update_pod_status_license(group_id, license_id):
    with SessionManager() as session:
        pod_info = session.query(PodInfo).first()
        if pod_info is None:
            log.error('PodInfo has not been created, creating a new one')
            pod_info = create_pod()

        if group_id and license_id:
            pod_info.groupId = group_id
            pod_info.licenseId = license_id
            log.info('Updating PodInfo with groupId and licenseId')
            update(pod_info)

        return pod_info


def update_pod_status_auth(auth_token):
    with SessionManager() as session:
        pod_info = session.query(PodInfo).first()
        if pod_info is None:
            log.error('PodInfo has not been created, creating a new one')
            pod_info = create_pod()

        if auth_token:
            pod_info.authToken = auth_token
            log.info('Updating PodInfo with authToken')
            update(pod_info)

        return pod_info


def clear_pod_status_auth():
    with SessionManager() as session:
        pod_info = session.query(PodInfo).first()
        if pod_info is not None:
            pod_info.authToken = ''
            update(pod_info)


def update_pod_status_connection(connected=True):
    with SessionManager() as session:
        pod_info = session.query(PodInfo).first()
        if pod_info is None:
            log.error('PodInfo has not been created, creating a new one')
            pod_info = create_pod()

        pod_info.connected = connected
        log.info('Updating connected status for PodInfo')
        update(pod_info)

        return pod_info


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
    with SessionManager() as session:
        stored_license = session.query(CompanyLicensePublic).first()
        if check_for_new:
            created_license = create_license(app)
            if stored_license is not None and stored_license.licenseId == created_license.licenseId:
                return stored_license

            if created_license.licenseId != 'NO_ID':
                return created_license

        return stored_license


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
    pod_info = get_pod()

    if license_file is None:
        dummy_license_obj = CompanyLicensePublic(str(ObjectId()), "NO_ID", "NO_ID", pod_info.id, datetime.datetime.now().date())
        return dummy_license_obj
    else:
        lines = license_file.split("\n")
        expiration_date = datetime.datetime.now().date()

        group_id = None
        license_id = None

        for line in lines:
            if "#" not in line:
                row = line.split("=")
                if row[0] == GROUP_ID:
                    group_id = row[1].rstrip()
                elif row[0] == EXPIRATION_DATE:
                    expiration_date = get_date_from_string(row[1].rstrip())
                elif row[0] == LICENSE_ID:
                    license_id = row[1].rstrip()

        if group_id and license_id:
            license_obj = CompanyLicensePublic(str(ObjectId()), group_id, license_id, pod_info.id, expiration_date)
            return license_obj
