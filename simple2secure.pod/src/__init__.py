import logging
import os

from celery import Celery
from flask import Flask
from flask_cors import CORS

import src.config.config as config_module
from src.celery.start_celery import run_worker
from src.db.database import db, PodInfo, Test
from src.db.database_schema import ma, TestSchema
from src.util.db_utils import init_db, update, get_pod
from src.util.util import print_error_message, shutdown_server, check_command_params, init_logger
from src.util.auth_utils import authenticate
from src.util.rest_utils import check_portal_alive
from src.util.test_utils import get_tests, sync_tests


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

    # Check command line arguments and initialize logger, thereafter loggers can be used
    if mode != 'app':
        init_logger(app)

    if mode == 'app':
        check_command_params(argv, app)
        log = logging.getLogger('pod.init')
    else:
        log = logging.getLogger('celery.init')

    CORS(app)

    if not app.config['SQLALCHEMY_DATABASE_URI']:
        db_path = os.path.abspath(os.path.relpath('db'))
        if not os.path.exists(db_path):
            os.makedirs(db_path)
        db_uri = 'sqlite:///{}'.format(db_path + '/pod.db')
        log.info('Database URI {}'.format(db_uri))
        app.config['SQLALCHEMY_DATABASE_URI'] = db_uri

    with app.app_context():
        # DB, marshmallow and Celery initialization
        log.info("Initialization of DB and Marshmallow")
        db.init_app(app)
        ma.init_app(app)
        log.info("Creating tables in the DB if not existent")
        init_db()

    if not app.config['POD_ID']:
        log.info('Obtaining the POD info')
        podInfo = get_pod(app)
        app.config['POD_ID'] = podInfo.generated_id

    if mode == 'app':
        log.info('Check connection to PORTAL')
        check_portal_alive(app)
        log.info('Authenticating the POD against the PORTAL')
        authenticate(app)
        log.info('Initializing tests - verifying if the DB contains the latest version')
        sync_tests(app)
        log.info('Initialize and run celery worker')
        run_worker()

    return app
