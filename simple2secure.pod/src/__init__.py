import getopt
import logging
import os
import sys

import requests
from celery import Celery
from flask import Flask
from flask_cors import CORS

import src.config.config as config_module
from src.db.database import db, PodInfo, Test
from src.db.database_schema import ma, TestSchema
from src.util import db_utils
from src.util.license_utils import get_license, get_pod
from src.util.rest_utils import authenticate_pod, check_portal_alive
from src.util.test_utils import get_tests, sync_tests
from src.util.util import print_error_message, shutdown_server


def create_app(argv):
    return entrypoint(argv, 'app')


def create_celery_app(app):
    # Initialize Celery
    celery = Celery('celery_tasks', broker=config_module.DevelopmentConfig.CELERY_BROKER_URL,
                    backend=config_module.DevelopmentConfig.CELERY_BROKER_URL)
    celery.conf.broker_url = app.config['CELERY_BROKER_URL']
    celery.conf.result_backend = app.config['CELERY_RESULT_BACKEND']

    class ContextTask(celery.Task):
        def __call__(self, *args, **kwargs):
            with app.app_context():
                return self.run(*args, **kwargs)

    celery.Task = ContextTask
    celery.finalize()
    return celery


def entrypoint(argv, mode='app'):
    os.environ.setdefault('FORKED_BY_MULTIPROCESSING', '1')

    app = Flask(__name__)
    app.config.from_object(config_module.DevelopmentConfig)

    CORS(app)

    # DB, marshmallow and Celery initialization
    db.init_app(app)
    ma.init_app(app)

    activate = False

    argumentsList = argv[1:]

    try:
        opts, args = getopt.getopt(argumentsList, "ha:", ["activate="])
    except getopt.GetoptError:
        print('app.py -a <True/False>')
        sys.exit(2)

    for opt, arg in opts:
        if opt == '-h':
            print('app.py -a <True/False>')
            sys.exit()
        elif opt in ("-a", "-activate"):
            activate = True

    with app.app_context():
        db.create_all()
        db.session.commit()

    if mode == 'app':
        logging.basicConfig(filename='logs/app.log', level=logging.INFO)
        check_portal_alive(app)
        get_pod(app)
        authenticate(app, activate)
        init_tests(app)

    return app


def authenticate(app, activate=False):
    with app.app_context():
        try:
            stored_license = get_license(app, True)
            if stored_license is not None and stored_license.licenseId != 'NO_ID':
                app.config['LICENSE_ID'] = stored_license.licenseId
            else:
                raise RuntimeError('NO license stored and no license ZIP file available from file system under '
                                   'static/license')

            if activate or not stored_license.activated:
                authenticate_pod(app, stored_license)

        except requests.exceptions.ConnectionError as ce:
            app.logger.error('Error occurred while activating the pod: %s', print_error_message())
            raise RuntimeError('Activating pod on portal did not work: %s', ce)
        except RuntimeError as re:
            app.config['CONNECTED_WITH_PORTAL'] = False
            app.logger.error('Error occurred while starting the pod: %s', re)


def init_tests(app):
    sync_tests(app)
