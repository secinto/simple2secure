import datetime
import logging
import secrets
import socket

from src.db.database import db, PodInfo, CompanyLicensePublic
from src.util.file_utils import get_license_file
from src.util.util import get_date_from_string

EXPIRATION_DATE = "expirationDate"
GROUP_ID = "groupId"
LICENSE_ID = "licenseId"
SIGNATURE = "signature"

HOSTNAME = socket.gethostname()

log = logging.getLogger('pod.util.db_utils')


def init_db():
    db.create_all()
    db.session.commit()


def update(some_object):
    db.session.add(some_object)
    db.session.commit()


def get_pod(app):
    """
    Obtains the PodInfo object for this POD if available from the database or creates it and stores it in the DB.

    Returns:
        CompanyLicensePod: either a dummy object if no license is available or the correct object
    """
    with app.app_context():
        pod_info = PodInfo.query.first()
        if pod_info is None:
            create_pod(app)
            pod_info = PodInfo.query.first()

        log.debug('Using existing pod id from the database: {}'.format(pod_info.generated_id))

        return pod_info


def create_pod(app):
    """
    Creates a new pod Id and storing it as PodInfo in the database

    Returns:
        PodInfo: Returns the currently created PodInfo object from the
    """
    with app.app_context():
        podId = secrets.token_urlsafe(20)
        log.info('Generated new pod id: %s', podId)

        pod_info = PodInfo(podId)
        update(pod_info)
        log.info("Stored new PodInfo in DB")


def update_pod_status_license(app, groupId, licenseId):
    with app.app_context():
        podInfo = PodInfo.query.first()

        if podInfo is None:
            log.error('PodInfo has not been created, creating a new one')
            podInfo = create_pod(app)

        if groupId and licenseId:
            podInfo.authToken = groupId
            podInfo.licenseId = licenseId
            log.info('Updating PodInfo with groupId and licenseId')
            update(podInfo)

        return podInfo


def update_pod_status_auth(app, authToken):
    with app.app_context():
        podInfo = PodInfo.query.first()
        if podInfo is None:
            log.error('PodInfo has not been created, creating a new one')
            podInfo = create_pod(app)

        if authToken:
            podInfo.authToken = authToken
            log.info('Updating PodInfo with authToken')
            update(podInfo)

        return podInfo


def clear_pod_status_auth(app):
    with app.app_context():
        podInfo = PodInfo.query.first()
        if podInfo is not None:
            podInfo.authToken = ''
            update(podInfo)


def update_pod_status_connection(app, connected=True):
    with app.app_context():
        podInfo = PodInfo.query.first()
        if podInfo is None:
            log.error('PodInfo has not been created, creating a new one')
            podInfo = create_pod(app)

        podInfo.connected = connected
        log.info('Updating connected status for PodInfo')
        update(podInfo)

        return podInfo


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
    with app.app_context():
        stored_license = CompanyLicensePublic.query.first()

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
    podInfo = get_pod(app)

    if license_file is None:
        dummy_license_obj = CompanyLicensePublic("NO_ID", "NO_ID", podInfo.generated_id, datetime.datetime.now().date(),
                                                 HOSTNAME)
        return dummy_license_obj
    else:
        lines = license_file.split("\n")
        expiration_date = datetime.datetime.now().date()

        groupId = None
        licenseId = None

        for line in lines:
            if "#" not in line:
                row = line.split("=")
                if row[0] == GROUP_ID:
                    groupId = row[1].rstrip()
                elif row[0] == EXPIRATION_DATE:
                    expiration_date = get_date_from_string(row[1].rstrip())
                elif row[0] == LICENSE_ID:
                    licenseId = row[1].rstrip()

        if groupId and licenseId:
            license_obj = CompanyLicensePublic(groupId, licenseId, podInfo.generated_id, expiration_date, HOSTNAME)
            update(license_obj)
            return license_obj
