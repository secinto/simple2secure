import logging

import requests
from flask import json
from src.db.database import Notification, TestStatusDTO, PodInfo
from src.db.database_schema import CompanyLicensePublicSchema, TestSchema
from src.util.db_utils import update
from src.util.license_utils import get_license

log = logging.getLogger('pod.util.rest_utils')


def get_auth_token(app):
    """
    Obtains the AUTH TOKEN for this application. Either the authentication token has already been obtained from the
    database with the help of PodInfo which is performed during startup or we need to communicate with the portal to
    obtain a new one. This can be achieved by providing the used license to the portal.
    """
    with app.app_context():
        if not app.config['AUTH_TOKEN']:
            licensePublic = get_license(app)
            authenticate_pod(app, licensePublic)

        return app.config['AUTH_TOKEN']


def authenticate_pod(app, licensePublic):
    send_license(app, app.config['PORTAL_URL'] + "license/authenticate", licensePublic, False)


def send_license(app, url, licensePublic=None, perform_check=True):
    with app.app_context():
        if licensePublic is None:
            licensePublic = get_license(app)

        license_schema = CompanyLicensePublicSchema()
        license_json = json.dumps(license_schema.dump(licensePublic))

        resp_data = portal_post(url, license_json, app, False)

        if resp_data is not None and resp_data.status_code == 200 and resp_data.text:
            accessToken = json.loads(resp_data.text)['accessToken']
            if accessToken:
                licensePublic.accessToken = accessToken
                licensePublic.activated = True
                app.config['AUTH_TOKEN'] = accessToken
                app.config['CONNECTED_WITH_PORTAL'] = True
                update(licensePublic)
                log.info('Obtained new access token from portal')
            else:
                log.error('No access token was provided as response to the authentication')
        elif resp_data is not None:
            message = json.loads(resp_data.text)['errorMessage']
            log.error('Error occurred while activating the pod: %s', message)
            app.config['CONNECTED_WITH_PORTAL'] = False
        else:
            log.error('No connection to PORTAL, thus not sending the license')


def create_headers(app):
    if not app.config['AUTH_TOKEN']:
        headers = {'Content-Type': 'application/json', 'Accept-Language': 'en-EN'}
    else:
        headers = {'Content-Type': 'application/json', 'Accept-Language': 'en-EN',
                   'Authorization': "Bearer " + app.config['AUTH_TOKEN']}

    log.info('Created headers {}'.format(headers))
    return headers


def portal_get(url, app, perform_check=True):
    if perform_check:
        if not online_and_authenticated(app):
            return None

    with app.app_context():
        log.info('Sending request to portal using (portal_get)')
        try:
            return requests.get(url, verify=False, headers=create_headers(app))
        except requests.exceptions.ConnectionError as ce:
            log.error('Sending request (portal_get) to portal failed. Reason {}'.format(ce.strerror))
            update_connection_status(app, False)
            return None


def portal_post(url, data, app, perform_check=True):
    if perform_check:
        if not online_and_authenticated(app):
            return None

    with app.app_context():
        log.info('Sending request to portal using (portal_post)')
        try:
            return requests.post(url, data=data, verify=False, headers=create_headers(app))
        except requests.exceptions.ConnectionError as ce:
            log.error('Sending request (portal_post) to portal failed. Reason {}'.format(ce.strerror))
            update_connection_status(app, False)
            return None


def send_notification(content, app, pod_id, perform_check=True):
    url = app.config['PORTAL_URL'] + "notification/pod/" + pod_id
    notification = Notification(content)
    return portal_post(url, json.dumps(notification.__dict__), app, perform_check)


def update_test_status(app, test_run_id, test_id, test_status, perform_check=True):
    url = app.config['PORTAL_URL'] + "test/updateTestStatus"
    test_run_dto = TestStatusDTO(test_run_id, test_id, test_status)
    return portal_post(url, json.dumps(test_run_dto.__dict__), app, perform_check)


def schedule_test_on_the_portal(test, app, pod_id, perform_check=True):
    url = app.config['PORTAL_URL'] + "test/scheduleTestPod/" + pod_id
    return portal_post(url, test, app, perform_check)


def sync_test_with_portal(test, app, perform_check=True):
    test_schema = TestSchema()
    test_json = test_schema.dump(test)
    return portal_post(app.config['PORTAL_URL'] + "test/syncTest", json.dumps(test_json), app, perform_check)


def online_and_authenticated(app):
    with app.app_context():
        log.info('Checking online and authenticated for CELERY: {}'.format(app.config['IS_CELERY']))

        if app.config['CONNECTED_WITH_PORTAL'] and not app.config['AUTH_TOKEN'] == '':
            log.info('POD is connected to PORTAL and is authenticated')
            return True
        elif app.config['AUTH_TOKEN'] == '' and not app.config['CONNECTED_WITH_PORTAL']:
            log.info("No connection to PORTAL and no AUTH_TOKEN is available")
            return False
        elif app.config['CONNECTED_WITH_PORTAL']:
            log.info('POD is connected to PORTAL but authentication is missing')
            if get_auth_token(app):
                log.info('Authentication against PORTAL was successful')
                return True
            else:
                log.info('Authentication against PORTAL was NOT successful')
                return False
        else:
            log.info("No connection to PORTAL")
            return False


def update_connection_status(app, connected=False):
    with app.app_context():
        app.config['CONNECTED_WITH_PORTAL'] = connected
        pod_info = PodInfo.query.first()
        pod_info.connected = connected


def check_portal_alive(app):
    response = portal_get(app.config['PORTAL_URL'] + "service", app, False)

    if response is not None and response.status_code == 200:
        app.config['CONNECTED_WITH_PORTAL'] = True
    else:
        app.config['CONNECTED_WITH_PORTAL'] = False

    # celery_tasks.update_info.delay(app.config['POD_ID'], app.config['AUTH_TOKEN'], app.config['CONNECTED_WITH_PORTAL'])
