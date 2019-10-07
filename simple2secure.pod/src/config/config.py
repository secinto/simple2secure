import urllib3


class Config(object):
    urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)
    SECRET_KEY = "ChangeIt2019!"
    DATABASE_URI = 'sqlite:///:memory:'
    CELERY_BROKER_URL = "redis://localhost:6379"
    CELERY_RESULT_BACKEND = "redis://localhost:6379"
    SQLALCHEMY_DATABASE_URI = ""
    POD_ID = ''
    GROUP_ID = ''
    LICENSE_ID = ''
    PORTAL_URL = 'https://localhost:8443/api/'
    # PORTAL_URL = 'https://simple2secure.info:51001/s2s/api/'
    AUTH_TOKEN = ''
    SQLALCHEMY_TRACK_MODIFICATIONS = False
    CONNECTED_WITH_PORTAL = False
    ACTIVATE_LICENSE = False
    LOG_FILE = 'logs/app.log'
    LOG_LEVEL_NAME = 'DEBUG'
    LOG_FORMAT = '%(asctime)-15s %(message)s'


class ProductionConfig(Config):
    DEBUG = False
    TESTING = False
    DATABASE_URI = 'mysql://user@localhost/foo'
    CELERY_BROKER_URL = "redis://redis:6379"
    CELERY_RESULT_BACKEND = "redis://redis:6379"


class DevelopmentConfig(Config):
    DEBUG = True
    Testing = True
