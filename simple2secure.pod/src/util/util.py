import getopt
import logging
import sys

from datetime import datetime

log = logging.getLogger('pod.util')


def get_current_timestamp():
    timestamp = datetime.now().timestamp() * 1000
    return timestamp


def get_date_from_string(date_string):
    return datetime.strptime(date_string, '%m/%d/%Y').date()


def check_command_params(argv, app):
    arguments_list = argv[1:]
    log.info(arguments_list)
    try:
        opts, args = getopt.getopt(arguments_list, "hd", ["help", "docker"])
    except getopt.GetoptError:
        log.error("Some error")
        print('app.py -a <True/False>')
        sys.exit(2)

    for opt, arg in opts:
        if opt == '-h':
            print('app.py -a <True/False>')
            sys.exit()
        elif opt in ("-o", "--docker"):
            app.USE_CELERY_IN_DOCKER = True
        else:
            log.info("Found unknown option {}".format(opt))


def init_logger(app):
    logging.basicConfig(filename=app.LOG_FILE,
                        level=logging.getLevelName(app.LOG_LEVEL_NAME),
                        format=app.LOG_FORMAT)
    ch = logging.StreamHandler()
    ch.setLevel(logging.INFO)
    ch.setFormatter(logging.Formatter(app.LOG_FORMAT_CH))
    logging.getLogger().addHandler(ch)


def print_error_message():
    return "-----------------------------------------------\n" \
           "-----------------------------------------------\n" \
           "--                                           --\n" \
           "--!!!Error occurred - portal not reachable!!!--\n" \
           "--                                           --\n" \
           "--*********POD HAS NOT BEEN ACTIVATED********--\n" \
           "-----------------------------------------------" \
           "-----------------------------------------------"


def print_success_message_auth(app):
    message = "----------------------------------------------\n" \
              "----------------INITIALIZATION----------------\n" \
              "----------------------------------------------\n" \
              "--       Extracting the pod license         --\n" \
              "----------------------------------------------\n" \
              "-- * Pod License Id : " + app.LICENSE_ID + "\n" \
                                                                    "-- * Pod Group Id : " + app.GROUP_ID + "\n" \
                                "-- * Pod Id : " + app.POD_ID + "\n" \
                                                                          "----------------------------------------------\n" \
                                                                          "----------------------------------------------\n" \
                                                                          "--          ACTIVATING THE LICENSE          --\n" \
                                                                          "----------------------------------------------\n" \
                                                                          "-- * Auth Token : " + app.AUTH_TOKEN + "\n" \
                                  "----------------------------------------------\n" \
                                  "----------------------------------------------\n" \
                                  "---------------INITIALIZATION END-------------\n" \
                                  "----------------------------------------------\n"

    app.logger.info('Pod information: %s', message)
