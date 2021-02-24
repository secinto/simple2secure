import logging
import os

from celery import Celery

import src.config.config as config_module
from src.celery.start_celery import run_worker
from src.db.database import db, PodInfo, Test
from src.db.database_schema import TestSchema
from src.util.auth_utils import authenticate
from src.util.db_utils import update, get_pod
from src.util.rest_utils import check_portal_alive
from src.util.test_utils import sync_tests
from src.util.util import print_error_message, check_command_params, init_logger


def create_app(argv):
    return entrypoint(argv, 'app')


def create_celery_app(app):
    # Initialize Celery

    if app.USE_CELERY_IN_DOCKER:
        celery = Celery('celery_tasks', broker=config_module.ProductionConfig.CELERY_BROKER_URL,
                        backend=config_module.ProductionConfig.CELERY_BROKER_URL)
    else:
        celery = Celery('celery_tasks', broker=config_module.DevelopmentConfig.CELERY_BROKER_URL,
                        backend=config_module.DevelopmentConfig.CELERY_BROKER_URL)

    celery.conf.broker_url = app.CELERY_BROKER_URL
    celery.conf.result_backend = app.CELERY_RESULT_BACKEND

    celery.finalize()
    return celery


def entrypoint(argv, mode='app'):
    os.environ.setdefault('FORKED_BY_MULTIPROCESSING', '1')

    app = config_module.DevelopmentConfig

    # Check command line arguments and initialize logger, thereafter loggers can be used
    if mode != 'app':
        init_logger(app)
        log = logging.getLogger('celery.init')
    else:
        check_command_params(argv, app)
        log = logging.getLogger('pod.init')

    log.info('===============================================================')
    log.info('=====================      STARTING      ======================')
    log.info('===============================================================')

    # DB, marshmallow and Celery initialization
    log.info("Initialization of DB and Marshmallow")
    log.info("Creating tables in the DB if not existent")

    if not app.POD_ID:
        log.info('Obtaining the POD info')
        pod_info = get_pod()
        app.POD_ID = pod_info.id

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
