import requests
from flask import json
from src.db.database import Notification, TestStatusDTO
from src.db.database_schema import CompanyLicensePublicSchema, TestSchema
from src.util.license_utils import get_license
from src.util.util import print_error_message


def get_auth_token(app):
    with app.app_context():
        if not app.config['AUTH_TOKEN']:
            activate_pod(app)

        return app.config['AUTH_TOKEN']


def activate_pod(app):
    with app.app_context():
        headers = {'Content-Type': 'application/json', 'Accept-Language': 'en-EN'}
        license_schema = CompanyLicensePublicSchema()
        dumped_license = json.dumps(license_schema.dump(get_license(app)))
        resp_data = requests.post(app.config['PORTAL_URL'] + "license/activatePod",
                                  data=dumped_license,
                                  verify=False,
                                  headers=headers)
        if resp_data.status_code == 200:
            app.config['AUTH_TOKEN'] = resp_data.text
            app.config['CONNECTED_WITH_PORTAL'] = True
        else:
            app.logger.error('Error occurred while activating the pod: %s', print_error_message())
            app.config['CONNECTED_WITH_PORTAL'] = False


def portal_post(url, data, app):
    with app.app_context():
        app.logger.info('Token before sending post request from (portal_post): %s', app.config['AUTH_TOKEN'])
        if not app.config['AUTH_TOKEN']:
            app.config['AUTH_TOKEN'] = get_auth_token(app)

        headers = {'Content-Type': 'application/json', 'Accept-Language': 'en-EN', 'Authorization': "Bearer " +
                                                                                                    app.config[
                                                                                                        'AUTH_TOKEN']}
        requests.post(url, data=json.dumps(data), verify=False, headers=headers)


def portal_get(url, app):
    with app.app_context():
        if not app.config['AUTH_TOKEN']:
            app.config['AUTH_TOKEN'] = get_auth_token(app)

        app.logger.info('Token before sending get request from (portal_get): %s', app.config['AUTH_TOKEN'])
        headers = {'Content-Type': 'application/json', 'Accept-Language': 'en-EN', 'Authorization': "Bearer " +
                                                                                                    app.config[
                                                                                                        'AUTH_TOKEN']}
        data_request = requests.get(url, verify=False, headers=headers)

        return data_request


def portal_post(url, data, app):
    with app.app_context():
        if not app.config['AUTH_TOKEN']:
            app.config['AUTH_TOKEN'] = get_auth_token(app)

        app.logger.info('Token before sending post request from (portal_post_test_response): %s',
                        app.config['AUTH_TOKEN'])
        headers = {'Content-Type': 'application/json', 'Accept-Language': 'en-EN', 'Authorization': "Bearer " +
                                                                                                    app.config[
                                                                                                        'AUTH_TOKEN']}
        return requests.post(url, data=json.dumps(data), verify=False, headers=headers)



def portal_post_celery(url, data, auth_token, app):
    with app.app_context():
        app.logger.info('Token before sending post request from (portal_post_celery): %s', app.config['AUTH_TOKEN'])
        headers = {'Content-Type': 'application/json', 'Accept-Language': 'en-EN',
                   'Authorization': "Bearer " + auth_token}
        return requests.post(url, data=json.dumps(data.as_dict()), verify=False, headers=headers)


def send_notification(content, app, auth_token, pod_id):
    url = app.config['PORTAL_URL'] + "notification/pod/" + pod_id
    notification = Notification(content)

    with app.app_context():
        app.logger.info('Token before sending post request from (send_notification): %s',
                        auth_token)
        headers = {'Content-Type': 'application/json', 'Accept-Language': 'en-EN',
                   'Authorization': "Bearer " + auth_token}
        requests.post(url, data=json.dumps(notification.__dict__), verify=False, headers=headers)


def update_test_status(app, auth_token, test_run_id, test_id, test_status):
    url = app.config['PORTAL_URL'] + "test/updateTestStatus"
    test_run_dto = TestStatusDTO(test_run_id, test_id, test_status)

    with app.app_context():
        app.logger.info('Token before sending post request from (update_test_status): %s',
                        auth_token)
        headers = {'Content-Type': 'application/json', 'Accept-Language': 'en-EN',
                   'Authorization': "Bearer " + auth_token}
        requests.post(url, data=json.dumps(test_run_dto.__dict__), verify=False, headers=headers)


def check_auth_token(app):
    auth_token_request = get_auth_token(app)
    if auth_token_request.status_code == 200:
        return auth_token_request.text
    else:
        return "Error"


def schedule_test_on_the_portal(test, app_obj, pod_id):
    response = portal_post(app_obj.config['PORTAL_URL'] + "test/scheduleTestPod/" + pod_id,
                                         test, app_obj)
    return response


def sync_all_tests_with_portal(tests, app_obj):
    dumped_tests = []
    test_schema = TestSchema()
    for test in tests:
        output = test_schema.dump(test)
        dumped_tests.append(output)
    response = portal_post(app_obj.config['PORTAL_URL'] + "test/syncTests", dumped_tests, app_obj)
    return response
