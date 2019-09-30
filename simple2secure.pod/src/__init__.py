from flask import Flask
from flask_cors import CORS
from src.db.database import db, PodInfo, Test, CompanyLicensePod
from src.db.database_schema import ma, TestSchema
from src.util import rest_utils, file_utils
from src.util.license_utils import parse_license_file, get_license_file
from src.util.util import print_error_message, print_success_message_auth
from celery import Celery

import src.config.config as config_module
import secrets
import requests
import os
import logging



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
        initialize_pod(app)
        authenticate(app)

    return app


def initialize_pod(app):
    with app.app_context():
        pod_info = PodInfo.query.first()

        # If there is not pod_info object in database, generate new pod_id and save object to db
        if pod_info is None:
            app.config['POD_ID'] = secrets.token_urlsafe(20)
            pod_info = PodInfo(app.config['POD_ID'], "")
            db.session.add(pod_info)
            db.session.commit()
            app.logger.info('Generating new pod id: %s', app.config['POD_ID'])
        # if there is a podInfo object in database, set saved pod_id into the app.config[POD_ID] variable
        else:
            app.config['POD_ID'] = pod_info.generated_id
            app.logger.info('Using existing pod id from the database: %s', app.config['POD_ID'])


def authenticate(app):
    try:
        stored_license = CompanyLicensePod.query.first();
        if stored_license is not None:
            app.config['LICENSE_ID'] = stored_license.license_id
        else:
            license_from_file = get_license_file()
            if license_from_file is not None:
                license_file = parse_license_file(license_from_file, app)
                app.config['LICENSE_ID'] = license_file.licenseId
                print_success_message_auth(app)
                app.config['CONNECTED_WITH_PORTAL'] = True
            else:
                app.logger.error('No license file found. Can not activate pod, working in offline mode')
                app.config['CONNECTED_WITH_PORTAL'] = False

        auth_token_obj = rest_utils.get_auth_token_object(app)

        if auth_token_obj.status_code == 200:
            app.config['AUTH_TOKEN'] = auth_token_obj.text

        else:
            app.logger.error('Error occured while activating the pod: %s', print_error_message())
            app.config['CONNECTED_WITH_PORTAL'] = False

    except requests.exceptions.ConnectionError:
        app.logger.error('Error occured while activating the pod: %s', print_error_message())
        # shutdown_server()
