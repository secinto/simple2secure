import urllib3


class Config(object):
    urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)
    SECRET_KEY = "ChangeIt2019!"
    DATABASE_URI = 'sqlite:///:memory:'
    CELERY_BROKER_URL = "redis://localhost:6379"
    CELERY_RESULT_BACKEND = "redis://localhost:6379"
    SQLALCHEMY_DATABASE_URI = ""
    POD_ID = ''
    PORTAL_URL = 'https://localhost:8443/api/v1/'
    # PORTAL_URL = 'https://simple2secure.info:51001/s2s/api/v1/'
    IS_CELERY = False
    SQLALCHEMY_TRACK_MODIFICATIONS = False
    CONNECTED_WITH_PORTAL = False
    LICENSE_FOLDER = 'static/license'
    LOG_FILE = 'logs/app.log'
    LOG_LEVEL_NAME = 'INFO'
    LOG_FORMAT = '[%(asctime)s-%(name)s-%(levelname)s-%(relativeCreated)6d-%(threadName)s] %(message)s'
    LOG_FORMAT_CH = '[%(asctime)s-%(name)s-%(levelname)s-%(threadName)s] %(message)s'
    USE_CELERY_IN_DOCKER = False


class ProductionConfig(Config):
    DEBUG = False
    TESTING = False
    # DATABASE_URI = 'mysql://user@localhost/foo'
    CELERY_BROKER_URL = "redis://redis:6379"
    CELERY_RESULT_BACKEND = "redis://redis:6379"


class DevelopmentConfig(Config):
    DEBUG = False
    Testing = True
