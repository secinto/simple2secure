import datetime

from flask import request


def shutdown_server():
    func = request.environ.get('werkzeug.server.shutdown')
    if func is None:
        raise RuntimeError('Not running with the Werkzeug Server')
    func()


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