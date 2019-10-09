import logging

from src.util.db_utils import update_pod_status_license, get_license
from src.util.rest_utils import send_license

log = logging.getLogger('pod.util.auth_utils')


def authenticate(app):
    log.info('Obtaining the license')
    stored_license = get_license(app, True)
    if stored_license is not None and stored_license.licenseId != 'NO_ID':
        log.info('License is available and contains a license ID')
        update_pod_status_license(app, stored_license.groupId, stored_license.licenseId)
        log.info('POD will be authenticated on the PORTAL, obtaining an auth token')
        send_license(app, stored_license)
    else:
        log.info('No license available either from the DB or the file system')

