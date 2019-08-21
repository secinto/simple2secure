from flask_sqlalchemy import SQLAlchemy
from enum import Enum

db = SQLAlchemy()


class TestResult(db.Model):

    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(50))
    result = db.Column(db.Text)
    testRunId = db.Column(db.String(120))
    hostname = db.Column(db.String(120))
    timestamp = db.Column(db.String(120))
    isSent = db.Column(db.Boolean)

    def __init__(self, name, result, testRunId, hostname, timestamp, is_sent):
        self.name = name
        self.result = result
        self.testRunId = testRunId
        self.hostname = hostname
        self.timestamp = timestamp
        self.isSent = is_sent


class PodInfo(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    generated_id = db.Column(db.Text)
    hash_value_service = db.Column(db.Text)

    def __init__(self, generated_id, hash_value_service):
        self.generated_id = generated_id
        self.hash_value_service = hash_value_service


class Test(db.Model):
    id = db.Column(db.Text, primary_key=True)
    podId = db.Column(db.Text)
    name = db.Column(db.Text)
    test_content = db.Column(db.Text)
    hash_value = db.Column(db.Text)
    lastChangedTimestamp = db.Column(db.BigInteger)

    def __init__(self, name, test_content, hash_value, last_changed_timestamp, pod_id):
        self.name = name
        self.podId = pod_id
        self.test_content = test_content
        self.hash_value = hash_value
        self.lastChangedTimestamp = last_changed_timestamp


class Notification:
    def __init__(self, content):
        self.content = content


class CompanyLicensePod:
    def __init__(self, group_id, license_id, pod_id, hostname, configuration):
        self.groupId = group_id
        self.licenseId = license_id
        self.podId = pod_id
        self.hostname = hostname
        self.configuration = configuration


class TestRun:
    def __init__(self, testId, testName, podId, contextId, testRunType, testContent, testStatus, timestamp):
        self.testId = testId
        self.testName = testName
        self.podId = podId
        self.contextId = contextId
        self.testRunType = testRunType
        self.testContent = testContent
        self.testStatus = testStatus
        self.timestamp = timestamp


class TestStatusDTO:
    def __init__(self, testRunId, testId, testStatus):
        self.testRunId = testRunId
        self.testId = testId
        self.testStatus = testStatus


class NotificationDTO:
    def __init__(self, notification, testRunDTO):
        self.notification = notification
        self.testRunDTO = testRunDTO


class TestStatus(Enum):
    UNKNOWN = 1
    SCHEDULED = 2
    RUNNING = 3
    PLANNED = 4
    EXECUTED = 5


