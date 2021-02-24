import json
import logging
import os
import socket
from datetime import datetime

import requests

from src.db.database import Notification, TestStatusDTO, DeviceInfo, DeviceStatus, DeviceType, Test
from src.db.database_schema import CompanyLicensePublicSchema, DeviceInfoSchema
from src.db.session_manager import SessionManager
from src.util.db_utils import update, update_test, update_pod_status_connection, update_pod_status_auth, get_pod, \
    get_license, \
    clear_pod_status_auth

log = logging.getLogger('pod.util.rest_utils')

# -------------------------------------------
# Main POST and GET functions used internally
# -------------------------------------------


def portal_get(app, url, use_without_authentication=False):
    """
    Utility function for getting data from the PORTAL. Required are the application context, the URL from which a GET
    should be performed and a boolean indicating if authentication should be sent or not. If no authentication is sent
    but would be required an error will be logged and usually either none or an response code indicating the problem is
    returned.

    :param app:  The application context
    :param url: The URL from which a get should be performed
    :param use_without_authentication: True if the authentication token should be provided in the request
    :return: The response data as obtained from the call
    """
    headers = create_headers(app, use_without_authentication)

    if not headers:
        return None

    log.info('Sending request to portal using (portal_get)')
    try:
        r = requests.get(url, verify=False, headers=headers)
        if r.status_code == 401:
            renew_token(app)
            #portal_get(app, url, False)
        else:
            return r
    except requests.exceptions.ConnectionError as ce:
        log.error('Sending request (portal_get) to portal failed. Reason {}'.format(ce.strerror))
        update_pod_status_connection(False)
        return None


def portal_post(app, url, data, use_without_authentication=False):
    """
    Utility function for posting data to the PORTAL. Required are the application context, the URL from which a GET
    should be performed, the data which should be transmitted to the PORTAL and a boolean indicating if authentication
    should be sent or not. If no authentication is sent  but would be required an error will be logged and usually
    either none or an response code indicating the problem is returned.

    :param app:  The application context
    :param url: The URL from which a get should be performed
    :param data: The data which should be sent in to PORTAL
    :param use_without_authentication: True if the authentication token should be provided in the request
    :return: The response data as obtained from the call
    """
    headers = create_headers(app, use_without_authentication)

    if not headers:
        return None

    log.info('Sending request to portal using (portal_post)')
    try:
        r = requests.post(url, data=data, verify=False, headers=headers)
        if r.status_code == 401:
            renew_token(app)
            #portal_post(app, url, data, False)
        else:
            return r
    except requests.exceptions.ConnectionError as ce:
        log.error('Sending request (portal_post) to portal failed. Reason {}'.format(ce.strerror))
        update_pod_status_connection(False)
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
    pod_info = get_pod()
    if not pod_info.authToken:
        send_license(app, None)
        pod_info = get_pod()

    return pod_info.authToken


def renew_token(app, license_public=None):

    """
    Retrieved new auth Token from the current refresh token. After retrieving the new token, device status is also updated
    Args:
        app:
        license_public:

    Returns:

    """
    if license_public is None:
        license_public = get_license(app)

    license_schema = CompanyLicensePublicSchema()

    license_json = json.dumps(license_schema.dump(license_public))

    resp_data = portal_post(app, app.API_LICENSE_RENEW_TOKEN, license_json, True)

    if resp_data is not None and resp_data.status_code == 200 and resp_data.text:
        access_token = json.loads(resp_data.text)['accessToken']
        refresh_token = json.loads(resp_data.text)['refreshToken']

        if access_token and refresh_token:
            update_license_dev_info(app, license_public, access_token, refresh_token, license_json)
        else:
            log.error('No access token was provided as response to the authentication')

    elif resp_data is not None:
        message = json.loads(resp_data.text)['errorMessage']
        log.error('Error occurred while activating the pod: %s', message)
        clear_pod_status_auth()
    else:
        log.error('No connection to PORTAL, thus not sending the license')
        clear_pod_status_auth()


# noinspection PyBroadException
def update_license_dev_info(app, license_public, access_token, refresh_token, license_json):
    """
    This function updates the license locally and device info after retrieving the first token or renewing the tokens
    Args:
        app:
        license_public:
        access_token:
        refresh_token:
        license_json:

    Returns:

    """
    with SessionManager() as session:
        license_public.accessToken = access_token
        license_public.refreshToken = refresh_token
        license_public.activated = True
        update(license_public)
        update_pod_status_auth(access_token)
        send_device_info(app, license_json)
        log.info('Obtained new access token from portal using refresh token')


def send_license(app, license_public=None):
    """
    Sends the license to the PORTAL for obtaining the current authentication token.
    If the license is provided it is used otherwise the currently stored license is obtained from the
    database and used.

    :param app: The application context object
    :param license_public: A license if available.
    """
    if license_public is None:
        license_public = get_license(app)

    license_schema = CompanyLicensePublicSchema()

    license_json = json.dumps(license_schema.dump(license_public))

    resp_data = portal_post(app, app.API_LICENSE_ACTIVATE, license_json, True)

    if resp_data is not None and resp_data.status_code == 200 and resp_data.text:

        access_token = json.loads(resp_data.text)['accessToken']
        refresh_token = json.loads(resp_data.text)['refreshToken']

        if access_token and refresh_token:
            update_license_dev_info(app, license_public, access_token, refresh_token, license_json)
        else:
            log.error('No access token was provided as response to the authentication')

    elif resp_data is not None:
        message = json.loads(resp_data.text)['errorMessage']
        log.error('Error occurred while activating the pod: %s', message)
        clear_pod_status_auth()
    else:
        log.error('No connection to PORTAL, thus not sending the license')
        clear_pod_status_auth()


# noinspection PyBroadException
def send_device_info(app, license_obj):
    with SessionManager() as session:
        last_online_timestamp = datetime.now().timestamp() * 1000
        license_obj = json.loads(license_obj)

        try:
            pod_name = os.environ['COMPUTERNAME']
        except:
            pod_name = socket.gethostname()
        device_info = DeviceInfo(license_obj['deviceId'], pod_name, None, None, last_online_timestamp, None, DeviceStatus.ONLINE, DeviceType.POD)

        device_info_schema = DeviceInfoSchema()
        device_info_json = json.dumps(device_info_schema.dump(device_info))
        resp_data = portal_post(app, app.API_DEVICE_UPDATE_INFO, device_info_json, False)
        if resp_data is not None and resp_data.status_code == 200 and resp_data.text:
            device_info_response = json.loads(resp_data.content)
            device_info_from_db = session.query(DeviceInfo).filter_by(id=device_info_response['id']).first()
            if device_info_from_db:
                device_info_from_db.deviceStatus = device_info_response['deviceStatus']
                device_info_from_db.lastOnlineTimestamp = device_info_response['lastOnlineTimestamp']
                update(device_info_from_db)
            else:
                device_info_for_db = DeviceInfo(device_info_response['id'], device_info_response['name'], None, None, device_info_response['lastOnlineTimestamp'], device_info_response['publiclyAvailable'], device_info_response['deviceStatus'])
                update(device_info_for_db)


def send_notification(content, app):
    """
    Sends the provided content as notification to the PORTAL. This is then shown in the PORTAL as information.

    :param content: A string with the notification which should be shown in the PORTAL
    :param app: The application context
    :return:
    """
    api_url = app.API_NOTIFICATION_SAVE.replace("{deviceId}", app.POD_ID)

    notification = Notification(content)
    return portal_post(app, api_url, json.dumps(notification.__dict__))


def update_test_status(app, test_run_id, test_id, test_status):
    """
    Updates the status of the specified test run to the specified test status. This information is shown in the PORTAL

    :param app: The application context
    :param test_run_id: The id of the test run
    :param test_id: The id of the test
    :param test_status: The current status of the test run
    :return:
    """
    test_run_dto = TestStatusDTO(test_run_id, test_id, test_status)
    return portal_post(app, app.API_TEST_UPDATE_STATUS, json.dumps(test_run_dto.__dict__))


def update_test_by_id(app, test_id):
    """
    This function updates the test in the local database after the execution
    :param app: The application context
    :param test_id: id of the test
    :return:
    """
    api_url = app.API_TEST_BY_ID.replace("{testId}", test_id)
    test_response = portal_get(app, api_url, True)
    if test_response is not None and test_response.status_code == 200:
        test_obj = Test(test_response["id"], test_response["name"], test_response["testContent"],
                    test_response["lastChangedTimestamp"], test_response["podId"])
        update_test(test_obj)


def update_sequence_status(app, sequence_run_id, sequence_id, status):
    """
    Sends the status of the sequence to the PORTAL.

    :param app: The application context
    :param sequence_run_id: The id of the sequence test run
    :param sequence_id: The id of the sequence
    :param status: The current status of the sequence test run
    :return:
    """
    api_url = app.API_SEQUENCE_UPDATE_STATUS.replace("{sequenceId}", sequence_run_id)
    info = {'sequence_run_id': sequence_run_id, 'sequence_id': sequence_id, 'status': status}
    dumped_info = json.dumps(info)

    return portal_post(app, api_url, dumped_info)


def sync_tests_with_portal(tests, app):
    """
    Synchronizes the list of all tests with the PORTAL after the authentication.

    :param tests: The test to be synchronized
    :param app: The application context
    :return:
    """

    api_url = app.API_TEST_SYNC.replace("{deviceId}", app.POD_ID)

    tests_array = json.loads(tests)

    test_list = []
    for test in tests_array:
        test_obj = Test(None, test["name"], json.dumps(test), None, None)
        test_list.append(test_obj.to_json())

    return portal_post(app, api_url, json.dumps(test_list))


# ----------------------------------------
# Helper functions
# ----------------------------------------


def create_headers(app, use_without_authentication=False):
    """"
    Creates the required header information for communicating with the PORTAL. Especially the  authentication token
    is obtained and added to the header parameters.

    Parameters:
        app: The application context for access of the configuration
        use_without_authentication: Specifies if an auth token must not be present in the headers
    Returns:
       authToken: Returns the current authToken to use for authentication
     """
    pod_info = get_pod()

    headers = None

    if use_without_authentication:
        headers = {'Content-Type': 'application/json', 'Accept-Language': 'en-EN'}
        log.debug('Created headers without auth token {}'.format(headers))
        return headers

    if pod_info.connected:
        if not pod_info.authToken:
            auth_token = get_auth_token(app)
        else:
            auth_token = pod_info.authToken

        headers = {'Content-Type': 'application/json', 'Accept-Language': 'en-EN',
                   'Authorization': "Bearer " + auth_token}
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
    response = portal_get(app, app.API_SERVICE, True)

    if response is not None and response.status_code == 200:
        log.info('PORTAL ' + app.API_SERVICE + ' is alive')
        update_pod_status_connection(True)
    else:
        log.info('PORTAL ' + app.API_SERVICE + ' is dead')
        update_pod_status_connection(False)
