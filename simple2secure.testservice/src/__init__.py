from flask import Flask
from flask_cors import CORS
from src.db.database import db, PodInfo, Test
from src.db.database_schema import ma, TestSchema
from src.util import rest_utils, file_utils
from celery import Celery
import src.config.config as config_module
import secrets
import requests
import os


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
        init(app)

    return app


def init(app):
    with app.app_context():
        pod_info = PodInfo.query.first()
        # If there is not pod_info object in database, generate new pod_id and save object to db
        if pod_info is None:
            app.config['POD_ID'] = secrets.token_urlsafe(20)
            pod_info = PodInfo(app.config['POD_ID'], "")
            db.session.add(pod_info)
            db.session.commit()
        # if there is a podInfo object in database, set saved pod_id into the app.config[POD_ID] variable
        else:
            app.config['POD_ID'] = pod_info.generated_id

        try:
            auth_token_obj = rest_utils.get_auth_token_object(app)

            if auth_token_obj.status_code == 200:
                app.config['AUTH_TOKEN'] = auth_token_obj.text
                license_from_file = file_utils.get_license_file()
                license_file = file_utils.parse_license_file(license_from_file, app)
                app.config['LICENSE_ID'] = license_file.licenseId

            else:
                print(rest_utils.print_error_message())
                # shutdown_server()

        except requests.exceptions.ConnectionError:
                print(rest_utils.print_error_message())
                # shutdown_server()



