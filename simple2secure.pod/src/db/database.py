from flask_sqlalchemy import SQLAlchemy
from enum import Enum

db = SQLAlchemy()


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


class CompanyLicensePod(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    group_id = db.Column(db.Text)
    license_id = db.Column(db.Text)
    pod_id = db.Column(db.Text)
    hostname = db.Column(db.BigInteger)
    configuration = db.Column(db.Text)

    def __init__(self, group_id, license_id, pod_id, hostname, configuration):
        self.groupId = group_id
        self.licenseId = license_id
        self.podId = pod_id
        self.hostname = hostname
        self.configuration = configuration


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


class TestStatus(Enum):
    UNKNOWN = 1
    SCHEDULED = 2
    RUNNING = 3
    PLANNED = 4
    EXECUTED = 5
