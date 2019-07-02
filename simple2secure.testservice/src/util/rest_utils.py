import requests
from src.util import file_utils
from flask import json
from datetime import datetime


def get_auth_token(app):
    headers = {'Content-Type': 'application/json', 'Accept-Language': 'en-EN'}
    return requests.post(app.config['PORTAL_URL'] + "license/activatePod",
                         data=json.dumps(file_utils.parse_license_file(file_utils.get_license_file(), app).__dict__),
                         verify=False,
                         headers=headers).text


def portal_post(url, data, app):
    print("Token before sending" + app.config['AUTH_TOKEN'])
    if not app.config['AUTH_TOKEN']:
        app.config['AUTH_TOKEN'] = get_auth_token(app)

    print("TOKEN BEFORE AFTER SENDING" + app.config['AUTH_TOKEN'])
    headers = {'Content-Type': 'application/json', 'Accept-Language': 'en-EN', 'Authorization': "Bearer " +
                                                                                                app.config['AUTH_TOKEN']}
    requests.post(url, data=json.dumps(data), verify=False, headers=headers)


def portal_get(url, app):

    if not app.config['AUTH_TOKEN']:
        app.config['AUTH_TOKEN'] = get_auth_token(app)
    # print(" * Auth Token before posting function: " + app.config['AUTH_TOKEN'])
    headers = {'Content-Type': 'application/json', 'Accept-Language': 'en-EN', 'Authorization': "Bearer " +
                                                                                                app.config['AUTH_TOKEN']}
    data_request = requests.get(url, verify=False, headers=headers).text
    data_array = json.loads(data_request)

    return data_array


def portal_post_celery(url, data, auth_token):
    headers = {'Content-Type': 'application/json', 'Accept-Language': 'en-EN', 'Authorization': "Bearer " + auth_token}
    requests.post(url, data=json.dumps(data), verify=False, headers=headers)


def send_notification(test_id, content, app):
    # url = app.config['PORTAL_URL'] + "notification"
    # timestamp = datetime.now().timestamp() * 1000
    # notification = Notification(test_id, content, timestamp)
    # portal_post(url, notification.__dict__, app)
    print("send notification")
