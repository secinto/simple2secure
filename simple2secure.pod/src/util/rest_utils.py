from datetime import datetime
import logging

import requests
import os
from flask import json

from src.db.database import Notification, TestStatusDTO, DeviceInfo, DeviceStatus, DeviceType
from src.db.database_schema import CompanyLicensePublicSchema, TestSchema, DeviceInfoSchema
from src.util.db_utils import update, update_pod_status_connection, update_pod_status_auth, get_pod, get_license, \
    clear_pod_status_auth

log = logging.getLogger('pod.util.rest_utils')

# -------------------------------------------
# Main POST and GET functions used internally
# -------------------------------------------


def portal_get(app, url, useWithoutAuthentication=False):
    """
    Utility function for getting data from the PORTAL. Required are the application context, the URL from which a GET
    should be performed and a boolean indicating if authentication should be sent or not. If no authentication is sent
    but would be required an error will be logged and usually either none or an response code indicating the problem is
    returned.

    :param app:  The application context
    :param url: The URL from which a get should be performed
    :param useWithoutAuthentication: True if the authentication token should be provided in the request
    :return: The response data as obtained from the call
    """
    headers = create_headers(app, useWithoutAuthentication)

    if not headers:
        return None

    log.info('Sending request to portal using (portal_get)')
    try:
        return requests.get(url, verify=False, headers=headers)
    except requests.exceptions.ConnectionError as ce:
        log.error('Sending request (portal_get) to portal failed. Reason {}'.format(ce.strerror))
        update_pod_status_connection(app, False)
        return None


def portal_post(app, url, data, useWithoutAuthentication=False):
    """
    Utility function for posting data to the PORTAL. Required are the application context, the URL from which a GET
    should be performed, the data which should be transmitted to the PORTAL and a boolean indicating if authentication
    should be sent or not. If no authentication is sent  but would be required an error will be logged and usually
    either none or an response code indicating the problem is returned.

    :param app:  The application context
    :param url: The URL from which a get should be performed
    :param data: The data which should be sent in to PORTAL
    :param useWithoutAuthentication: True if the authentication token should be provided in the request
    :return: The response data as obtained from the call
    """
    headers = create_headers(app, useWithoutAuthentication)

    if not headers:
        return None

    log.info('Sending request to portal using (portal_post)')
    try:
        return requests.post(url, data=data, verify=False, headers=headers)
    except requests.exceptions.ConnectionError as ce:
        log.error('Sending request (portal_post) to portal failed. Reason {}'.format(ce.strerror))
        update_pod_status_connection(app, False)
        return None


# ----------------------------------------
# Functions for PORTAL interaction
# ----------------------------------------

def get_auth_token(app):
    """
    Obtains the AUTH TOKEN for this application. Either the authentication token has already been obtained from the
    database with the help of PodInfo which is performed during startup or we need to communicate with the portal to
    obtain a new one. This can be achieved by providing the used license to the portal.

    Parameters:
        app: Context object
    Returns:
       authToken: Returns the current authToken to use for authentication
    """
    podInfo = get_pod(app)
    if not podInfo.authToken:
        send_license(app, None)
        podInfo = get_pod(app)

    return podInfo.authToken


def send_license(app, licensePublic=None):
    """
    Sends the license to the PORTAL for obtaining the current authentication token.
    If the license is provided it is used otherwise the currently stored license is obtained from the
    database and used.

    :param app: The application context object
    :param licensePublic: A license if available.
    :param perform_check: Specifies if the connection check should be performed
    """
    url = app.config['PORTAL_URL'] + "license/authenticate"

    if licensePublic is None:
        licensePublic = get_license(app)

    license_schema = CompanyLicensePublicSchema()

    license_json = json.dumps(license_schema.dump(licensePublic))

    resp_data = portal_post(app, url, license_json, True)

    if resp_data is not None and resp_data.status_code == 200 and resp_data.text:
        accessToken = json.loads(resp_data.text)['accessToken']
        if accessToken:
            licensePublic.accessToken = accessToken
            licensePublic.activated = True
            update(licensePublic)
            update_pod_status_auth(app, accessToken)
            devInfo = ""
            try:
                devInfo = DeviceInfo.query.one()
            except:
                log.error("Could not retreive DeviceInfo from DB!")
            if not devInfo:
                send_device_info(app, license_json)
            log.info('Obtained new access token from portal')
        else:
            log.error('No access token was provided as response to the authentication')





    elif resp_data is not None:
        message = json.loads(resp_data.text)['errorMessage']
        log.error('Error occurred while activating the pod: %s', message)
        clear_pod_status_auth(app)
    else:
        log.error('No connection to PORTAL, thus not sending the license')
        clear_pod_status_auth(app)


def send_device_info(app, license):
    url = app.config['PORTAL_URL'] + "devices/update"
    lastOnlineTimestamp = datetime.now().timestamp() * 1000
    license_obj = json.loads(license)
    deviceId = license_obj['deviceId']
    pod_name = os.environ['COMPUTERNAME']
    deviceInfo = DeviceInfo(deviceId, pod_name, None, None, lastOnlineTimestamp, DeviceStatus.ONLINE, DeviceType.POD)

    device_info_schema = DeviceInfoSchema()
    device_info_json = json.dumps(device_info_schema.dump(deviceInfo))
    resp_data = portal_post(app, url, device_info_json, False)
    if resp_data is not None and resp_data.status_code == 200 and resp_data.text:
        device_info_response = json.loads(resp_data.content)
        device_info_from_db = DeviceInfo.query.filter_by(deviceId=device_info_response['deviceId']).first()
        if device_info_from_db:
            device_info_from_db.deviceStatus = device_info_response['deviceStatus']
            device_info_from_db.lastOnlineTimestamp = device_info_response['lastOnlineTimestamp']
            update(device_info_from_db)
        else:
            device_info_for_db = DeviceInfo(device_info_response['deviceId'], device_info_response['name'], None, None, device_info_response['lastOnlineTimestamp'], device_info_response['deviceStatus'])
            update(device_info_for_db)


def send_notification(content, app):
    """
    Sends the provided content as notification to the PORTAL. This is then shown in the PORTAL as information.

    :param content: A string with the notification which should be shown in the PORTAL
    :param app: The application context
    :return:
    """
    url = app.config['PORTAL_URL'] + "notification/" + app.config['POD_ID']

    notification = Notification(content)
    return portal_post(app, url, json.dumps(notification.__dict__))


def update_test_status(app, test_run_id, test_id, test_status):
    """
    Updates the status of the specified test run to the specified test status. This information is shown in the PORTAL

    :param app: The application context
    :param test_run_id: The id of the test run
    :param test_id: The id of the test
    :param test_status: The current status of the test run
    :return:
    """
    url = app.config['PORTAL_URL'] + "test/updateTestStatus"
    test_run_dto = TestStatusDTO(test_run_id, test_id, test_status)
    return portal_post(app, url, json.dumps(test_run_dto.__dict__))


def update_sequence_status(app, sequence_run_id, sequence_id, status):
    """
    Sends the status of the sequence to the PORTAL.

    :param app: The application context
    :param sequence_run_id: The id of the sequence test run
    :param sequence_id: The id of the sequence
    :param status: The current status of the sequence test run
    :return:
    """
    url = app.config['PORTAL_URL'] + "sequence/update/status/" + sequence_run_id
    info = {'sequence_run_id': sequence_run_id, 'sequence_id': sequence_id, 'status': status}
    dumped_info = json.dumps(info)

    with app.app_context():
        return portal_post(app, url, dumped_info)


def schedule_test_on_the_portal(test, app):
    """
    Schedules the test on the PORTAL, actually just updating the status on the PORTAL for this test to be shown in
    the scheduled tests view

    :param test:
    :param app:
    :return:
    """
    url = app.config['PORTAL_URL'] + "test/scheduleTestPod/" + app.config['POD_ID']
    return portal_post(app, url, test)


def sync_test_with_portal(test, app):
    """
    Synchronizes the provided test with the PORTAL.

    :param test: The test to be synchronized
    :param app: The application context
    :return:
    """
    test_schema = TestSchema()
    test_json = test_schema.dump(test)
    return portal_post(app, app.config['PORTAL_URL'] + "test/syncTest", json.dumps(test_json))


def sync_tests_with_portal(tests, app):
    """
    Synchronizes the list of all tests with the PORTAL.

    :param test: The test to be synchronized
    :param app: The application context
    :return:
    """
    tests_array = []

    if tests is not None:
        for test in tests:
            test_schema = TestSchema()
            test_json = test_schema.dump(test)
            tests_array.append(test_json)

    return portal_post(app, app.config['PORTAL_URL'] + "test/syncTests/" + app.config['POD_ID'], json.dumps(tests_array))


# ----------------------------------------
# Helper functions
# ----------------------------------------


def create_headers(app, useWithoutAuthentication=False):
    """"
    Creates the required header information for communicating with the PORTAL. Especially the  authentication token
    is obtained and added to the header parameters.

    Parameters:
        app: The application context for access of the configuration
        useWithoutAuthentication: Specifies if an auth token must not be present in the headers
    Returns:
       authToken: Returns the current authToken to use for authentication
     """
    podInfo = get_pod(app)

    headers = None

    if useWithoutAuthentication:
        headers = {'Content-Type': 'application/json', 'Accept-Language': 'en-EN'}
        log.debug('Created headers without auth token {}'.format(headers))
        return headers

    if podInfo.connected:
        if not podInfo.authToken:
            authToken = get_auth_token(app)
        else:
            authToken = podInfo.authToken

        headers = {'Content-Type': 'application/json', 'Accept-Language': 'en-EN',
                   'Authorization': "Bearer " + authToken}
        log.debug('Created headers {}'.format(headers))
    else:
        log.info('Not connected to PORTAL, not creating headers')

    return headers


def check_portal_alive(app):
    """
    Verifies if a connection to the PORTAL is available. Stores the connection status in the PodInfo.

    Parameters:
        app: Context object
    Returns:
       authToken: Returns the current authToken to use for authentication
    """
    response = portal_get(app, app.config['PORTAL_URL'] + "service", True)

    if response is not None and response.status_code == 200:
        log.info('PORTAL ' + app.config['PORTAL_URL'] + ' is alive')
        update_pod_status_connection(app, True)
    else:
        log.info('PORTAL ' + app.config['PORTAL_URL'] + ' is dead')
        update_pod_status_connection(app, False)
