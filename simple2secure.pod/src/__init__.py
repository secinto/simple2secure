import json

from flask import Flask
from flask_cors import CORS
from src.db.database import db, PodInfo, Test, CompanyLicensePod
from src.db.database_schema import ma, TestSchema
from celery import Celery

import src.config.config as config_module
import requests
import os
import logging

from src.util.license_utils import get_license, get_pod
from src.util.rest_utils import get_auth_token_object
from src.util.util import print_error_message


def create_app():
    return entrypoint('app')


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


def entrypoint(mode='app'):
    os.environ.setdefault('FORKED_BY_MULTIPROCESSING', '1')

    app = Flask(__name__)
    app.config.from_object(config_module.DevelopmentConfig)

    CORS(app)

    # DB, marshmallow and Celery initialization
    db.init_app(app)
    ma.init_app(app)

    with app.app_context():
        db.create_all()
        db.session.commit()

    if mode == 'app':
        logging.basicConfig(filename='logs/app.log', level=logging.INFO)
        get_pod(app)
        authenticate(app)

    return app


def authenticate(app):
    with app.app_context():
        try:
            stored_license = get_license(app);
            if stored_license is not None:
                app.config['LICENSE_ID'] = stored_license.license_id

            auth_token_obj = get_auth_token_object(json.dumps(stored_license.__dict__), app)

            if auth_token_obj.status_code == 200:
                app.config['AUTH_TOKEN'] = auth_token_obj.text
                app.config['CONNECTED_WITH_PORTAL'] = True
            else:
                app.logger.error('Error occured while activating the pod: %s', print_error_message())
                app.config['CONNECTED_WITH_PORTAL'] = False

        except requests.exceptions.ConnectionError:
            app.logger.error('Error occured while activating the pod: %s', print_error_message())
            app.config['CONNECTED_WITH_PORTAL'] = False
            # shutdown_server()
