import datetime
import json

from flask import request

from src.db.database import Test


def shutdown_server():
    func = request.environ.get('werkzeug.server.shutdown')
    if func is None:
        raise RuntimeError('Not running with the Werkzeug Server')
    func()


def get_current_timestamp():
    timestamp = datetime.now().timestamp() * 1000
    return timestamp


def generate_test_object(sync_test):
    sync_test_json = json.loads(sync_test)
    test = Test(sync_test_json["name"], sync_test_json["test_content"], sync_test_json["hash_value"],
                sync_test_json["lastChangedTimestamp"], sync_test_json["podId"])
    test.id = sync_test_json["id"]
    return test


def generate_test_object_from_json(sync_test_json):
    test = Test(sync_test_json["name"], sync_test_json["test_content"], sync_test_json["hash_value"],
                sync_test_json["lastChangedTimestamp"], sync_test_json["podId"])
    test.id = sync_test_json["id"]
    return test


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
    message = "----------------------------------------------\n" \
              "----------------INITIALIZATION----------------\n" \
              "----------------------------------------------\n" \
              "--       Extracting the pod license         --\n" \
              "----------------------------------------------\n" \
              "-- * Pod License Id : " + app.config['LICENSE_ID'] + "\n" \
                                                                    "-- * Pod Group Id : " + app.config[
                  'GROUP_ID'] + "\n" \
                                "-- * Pod Id : " + app.config['POD_ID'] + "\n" \
                                                                          "----------------------------------------------\n" \
                                                                          "----------------------------------------------\n" \
                                                                          "--          ACTIVATING THE LICENSE          --\n" \
                                                                          "----------------------------------------------\n" \
                                                                          "-- * Auth Token : " + app.config[
                  'AUTH_TOKEN'] + "\n" \
                                  "----------------------------------------------\n" \
                                  "----------------------------------------------\n" \
                                  "---------------INITIALIZATION END-------------\n" \
                                  "----------------------------------------------\n"

    app.logger.info('Pod Informaticn: %s', message)
