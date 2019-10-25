import json

from flask_sqlalchemy import SQLAlchemy
from enum import Enum

db = SQLAlchemy()


class PodInfo(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    generated_id = db.Column(db.Text)
    authToken = db.Column(db.Text)
    connected = db.Column(db.Boolean)
    groupId = db.Column(db.Text)
    licenseId = db.Column(db.Text)
    hash_value_service = db.Column(db.Text)

    def __init__(self, generated_id):
        self.generated_id = generated_id
        self.connected = False
        self.authToken = ""
        self.groupId = ""
        self.licenseId = ""
        self.hash_value_service = ""

class Test(db.Model):
    id = db.Column(db.Integer, primary_key=True)
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


class TestSequence(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    podId = db.Column(db.Text)
    name = db.Column(db.Text)
    sequence_content = db.Column(db.Text)
    hash_value = db.Column(db.Text)
    lastChangedTimestamp = db.Column(db.BigInteger)

    def __init__(self, name, sequence_content, hash_value, last_changed_timestamp, pod_id):
        self.name = name
        self.podId = pod_id
        self.sequence_content = sequence_content
        self.hash_value = hash_value
        self.lastChangedTimestamp = last_changed_timestamp


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

    def as_dict(self):
        return {c.name: getattr(self, c.name) for c in self.__table__.columns}


class TestSequenceResult(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    podId = db.Column(db.Text)
    sequence_name = db.Column(db.Text)
    sequence_result = db.Column(db.Text)
    time_stamp = db.Column(db.BigInteger)

    def __init__(self, pod_id, sequence_name, sequence_result, time_stamp):
        self.podId = pod_id
        self.sequence_name = sequence_name
        self.sequence_result = sequence_result
        self.time_stamp = time_stamp


class CompanyLicensePublic(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    groupId = db.Column(db.Text)
    licenseId = db.Column(db.Text)
    deviceId = db.Column(db.Text)
    accessToken = db.Column(db.Text)
    hostname = db.Column(db.Text)
    activated = db.Column(db.Boolean)
    expirationDate = db.Column(db.Date)
    deviceIsPod = db.Column(db.Boolean)

    def __init__(self, group_id, license_id, pod_id, expiration_date, hostname):
        self.groupId = group_id
        self.licenseId = license_id
        self.deviceId = pod_id
        self.expirationDate = expiration_date
        self.hostname = hostname
        self.activated = False
        self.accessToken = ""
        self.deviceIsPod = True

    def as_dict(self):
        return {c.name: getattr(self, c.name) for c in self.__table__.columns}


class Notification:
    def __init__(self, content):
        self.content = content


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


class ResponseError:
    def __init__(self, errorMessage):
        self.errorMessage = errorMessage


class TestStatus(Enum):
    UNKNOWN = 1
    SCHEDULED = 2
    RUNNING = 3
    PLANNED = 4
    EXECUTED = 5
