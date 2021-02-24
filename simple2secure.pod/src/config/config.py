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
    # APIs License
    API_LICENSE_RENEW_TOKEN = PORTAL_URL + "license/renewAuthentication"
    API_LICENSE_ACTIVATE = PORTAL_URL + "license/authenticate"
    # APIs Device
    API_DEVICE_SCHEDULED_TESTS = PORTAL_URL + "devices/scheduledTests/{deviceId}"
    API_DEVICE_UPDATE_INFO = PORTAL_URL + "devices/update"
    # APIs Notification
    API_NOTIFICATION_SAVE = PORTAL_URL + "notification/{deviceId}"
    API_SEQUENCE_UPDATE_STATUS = PORTAL_URL + "sequence/update/status/{sequenceId}"
    # APIs Service
    API_SERVICE = PORTAL_URL + "service"
    # APIs Sequence
    API_SEQUENCE_SAVE_RESULT = PORTAL_URL + "sequence/save/sequencerunresult"
    API_SEQUENCE_SAVE_STEP_RESULT = PORTAL_URL + "sequence/save/sequencestepresult"
    API_SEQUENCE_SCHEDULED_SEQUENCES = PORTAL_URL + "sequence/scheduledSequence/{deviceId}"
    # APIs Test
    API_TEST_SAVE_RESULT = PORTAL_URL + "test/saveTestResult"
    API_TEST_UPDATE_STATUS = PORTAL_URL + "test/updateTestStatus"
    API_TEST_SCHEDULE = PORTAL_URL + "test/scheduleTestPod/{deviceId}"
    API_TEST_SYNC_POD = PORTAL_URL + "test/syncTest"
    API_TEST_SYNC = PORTAL_URL + "test/syncTests/{deviceId}"
    API_TEST_BY_ID = PORTAL_URL + "test/byTestId/{testId}"


class ProductionConfig(Config):
    DEBUG = False
    TESTING = False
    # DATABASE_URI = 'mysql://user@localhost/foo'
    CELERY_BROKER_URL = "redis://redis:6379"
    CELERY_RESULT_BACKEND = "redis://redis:6379"


class DevelopmentConfig(Config):
    DEBUG = False
    Testing = True
