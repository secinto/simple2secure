import logging
import os

import requests
from celery import Celery
from flask import Flask
from flask_cors import CORS

import src.config.config as config_module
from src.celery.start_celery import run_worker
from src.db.database import db, PodInfo, Test
from src.db.database_schema import ma, TestSchema
from src.util import db_utils
from src.util.db_utils import init_db
from src.util.license_utils import get_license, get_pod
from src.util.rest_utils import authenticate_pod, check_portal_alive
from src.util.test_utils import get_tests, sync_tests
from src.util.util import print_error_message, shutdown_server, check_command_params, init_logger


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

    activate = False

    # Check command line arguments and initialize logger, thereafter loggers can be used
    if mode == 'app':
        check_command_params(argv, app)
        init_logger(app)

    CORS(app)

    log = logging.getLogger('pod.init')

    if not app.config['SQLALCHEMY_DATABASE_URI']:
        db_path = os.path.abspath(os.path.relpath('db'))
        if not os.path.exists(db_path):
            os.makedirs(db_path)

        db_uri = 'sqlite:///{}'.format(db_path + '/pod.db')

        log.info('Database URI {}\r\n'.format(db_uri))

        app.config['SQLALCHEMY_DATABASE_URI'] = db_uri

    # DB, marshmallow and Celery initialization
    log.info("Initialization of DB and Marshmallow")
    db.init_app(app)
    ma.init_app(app)

    with app.app_context():
        log.info("Creating tables in the DB if not existent")
        init_db(app)

    if mode == 'app':
        log.info('Check connection to PORTAL')
        check_portal_alive(app)
        log.info('Obtaining the POD info')
        get_pod(app)
        log.info('Authenticating the POD against the PORTAL')
        authenticate(app, activate)
        log.info('Initializing tests - verifying if the DB contains the latest version')
        sync_tests(app)
        log.info('Initialize and run celery worker')
        #run_worker()

    return app


def authenticate(app, activate=False):
    with app.app_context():
        try:
            log = logging.getLogger('pod.init')

            log.info('Obtaining the license')
            stored_license = get_license(app, True)
            if stored_license is not None and stored_license.licenseId != 'NO_ID':
                log.info('License is available and contains a license ID')
                app.config['LICENSE_ID'] = stored_license.licenseId
            else:
                log.info('No license available either from the DB or the file system')
                raise RuntimeError('NO license stored and no license ZIP file available from file system under '
                                   'static/license')

            if activate or not stored_license.activated:
                log.info('License will be activated on the PORTAL, obtaining a new auth token')
                authenticate_pod(app, stored_license)

        except requests.exceptions.ConnectionError as ce:
            raise RuntimeError('Activating POD on PORTAL did not work: %s', ce.strerror)
        except RuntimeError as re:
            app.config['CONNECTED_WITH_PORTAL'] = False
            log.error('Error occurred while starting the POD: %s', re)
