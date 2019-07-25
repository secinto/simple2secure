import requests
from src.util import file_utils
from flask import json
from datetime import datetime


def get_auth_token(app):
    with app.app_context():
        headers = {'Content-Type': 'application/json', 'Accept-Language': 'en-EN'}
        return requests.post(app.config['PORTAL_URL'] + "license/activatePod",
                             data=json.dumps(file_utils.parse_license_file(file_utils.get_license_file(), app).__dict__),
                             verify=False,
                             headers=headers).text


def get_auth_token_object(app):
    with app.app_context():
        headers = {'Content-Type': 'application/json', 'Accept-Language': 'en-EN'}
        return requests.post(app.config['PORTAL_URL'] + "license/activatePod",
                             data=json.dumps(file_utils.parse_license_file(file_utils.get_license_file(), app).__dict__),
                             verify=False,
                             headers=headers)


def portal_post(url, data, app):
    with app.app_context():
        print("Token before sending" + app.config['AUTH_TOKEN'])
        if not app.config['AUTH_TOKEN']:
            app.config['AUTH_TOKEN'] = get_auth_token(app)

        print("TOKEN BEFORE AFTER SENDING" + app.config['AUTH_TOKEN'])
        headers = {'Content-Type': 'application/json', 'Accept-Language': 'en-EN', 'Authorization': "Bearer " +
                                                                                                    app.config['AUTH_TOKEN']}
        requests.post(url, data=json.dumps(data), verify=False, headers=headers)


def portal_get(url, app):
    with app.app_context():
        if not app.config['AUTH_TOKEN']:
            app.config['AUTH_TOKEN'] = get_auth_token(app)
        # print(" * Auth Token before posting function: " + app.config['AUTH_TOKEN'])
        headers = {'Content-Type': 'application/json', 'Accept-Language': 'en-EN', 'Authorization': "Bearer " +
                                                                                                    app.config['AUTH_TOKEN']}
        data_request = requests.get(url, verify=False, headers=headers)

        return data_request


def portal_post_celery(url, data, auth_token):
    headers = {'Content-Type': 'application/json', 'Accept-Language': 'en-EN', 'Authorization': "Bearer " + auth_token}
    requests.post(url, data=json.dumps(data), verify=False, headers=headers)


def portal_post_test(url, data, app):
    with app.app_context():
        print("Token before sending" + app.config['AUTH_TOKEN'])
        if not app.config['AUTH_TOKEN']:
            app.config['AUTH_TOKEN'] = get_auth_token(app)

        print("TOKEN BEFORE AFTER SENDING" + app.config['AUTH_TOKEN'])
        headers = {'Content-Type': 'application/json', 'Accept-Language': 'en-EN', 'Authorization': "Bearer " +
                                                                                                    app.config['AUTH_TOKEN']}
        return requests.post(url, data=json.dumps(data), verify=False, headers=headers).text


def send_notification(test_id, content, app):
    # url = app.config['PORTAL_URL'] + "notification"
    # timestamp = datetime.now().timestamp() * 1000
    # notification = Notification(test_id, content, timestamp)
    # portal_post(url, notification.__dict__, app)
    print("send notification")


def check_auth_token(app):
    auth_token_request = get_auth_token(app)
    if auth_token_request.status_code == 200:
        return auth_token_request.text
    else:
        return "Error"


def get_current_timestamp():
    timestamp = datetime.now().timestamp() * 1000
    return timestamp


def print_error_message():
    return "----------------------------------------------\n" \
           "----------------------------------------------\n" \
           "--                                          --\n" \
           "--!!!Error occured - portal not reachable!!!--\n" \
           "--                                          --\n" \
           "--********POD HAS NOT BEEN ACTIVATED********--\n" \
           "----------------------------------------------" \
           "----------------------------------------------"


def print_success_message_auth(app):
    return "----------------------------------------------\n" \
           "----------------INITIALIZATION----------------\n" \
           "----------------------------------------------\n" \
           "--       Extracting the pod license         --\n" \
           "----------------------------------------------\n" \
           "-- * Pod License Id : " + app.config['LICENSE_ID'] + "\n" \
           "-- * Pod Group Id : " + app.config['GROUP_ID'] + "\n" \
           "-- * Pod Id : " + app.config['POD_ID'] + "\n" \
           "----------------------------------------------\n" \
           "----------------------------------------------\n" \
           "--          ACTIVATING THE LICENSE          --\n" \
           "----------------------------------------------\n" \
           "-- * Auth Token : " + app.config['AUTH_TOKEN'] + "\n" \
           "----------------------------------------------\n" \
           "----------------------------------------------\n" \
           "---------------INITIALIZATION END-------------\n" \
           "----------------------------------------------\n"


