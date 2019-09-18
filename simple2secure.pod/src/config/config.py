import urllib3


class Config(object):

    urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)
    TESTING = False
    SECRET_KEY = "ChangeIt2019!"
    DATABASE_URI = 'sqlite:///:memory:'
    CELERY_BROKER_URL = "redis://localhost:6379"
    CELERY_RESULT_BACKEND = "redis://localhost:6379"
    SQLALCHEMY_DATABASE_URI = "sqlite:///pod.sqlite3"
    POD_ID = ''
    GROUP_ID = ''
    LICENSE_ID = ''
    #PORTAL_URL = 'https://localhost:8443/api/'
    PORTAL_URL = 'https://144.76.93.104:51001/s2s/api/'
    AUTH_TOKEN = ''
    SQLALCHEMY_TRACK_MODIFICATIONS = False


class ProductionConfig(Config):
    DEBUG = False
    DATABASE_URI = 'mysql://user@localhost/foo'
    CELERY_BROKER_URL = "redis://redis:6379"
    CELERY_RESULT_BACKEND = "redis://redis:6379"


class DevelopmentConfig(Config):
    DEBUG = True

