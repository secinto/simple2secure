import enum
import os

from sqlalchemy import create_engine, Column, Text, Boolean, Enum, Float, String, BigInteger, Integer, Date, BLOB
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker, scoped_session

db_uri = 'sqlite:///:memory:'
db_path = os.path.abspath(os.path.relpath('db'))
if not os.path.exists(db_path):
    os.makedirs(db_path)
db_uri = 'sqlite:///{}'.format(db_path + '/pod.db')

db = create_engine(db_uri, echo=False)
Base = declarative_base()


class DeviceStatus(str, enum.Enum):
    ONLINE = 'ONLINE'
    OFFLINE = 'OFFLINE'
    UNKNOWN = 'UNKNOWN'


class DeviceType(str, enum.Enum):
    PROBE = 'PROBE'
    POD = 'POD'
    WWW = 'WWW'
    UNKNOWN = 'UNKNOWN'


class PodInfo(Base):
    __tablename__ = 'pod_info'
    id = Column(Text, primary_key=True)
    authToken = Column(Text)
    connected = Column(Boolean)
    groupId = Column(Text)
    licenseId = Column(Text)
    hashValueService = Column(Text)

    def __init__(self, generated_id):
        self.id = generated_id
        self.connected = False
        self.authToken = ""
        self.groupId = ""
        self.licenseId = ""
        self.hashValueService = ""


class DeviceInfo(Base):
    __tablename__ = 'device_info'
    id = Column(Text, primary_key=True)
    name = Column(Text)
    ipAddress = Column(Text)
    netMask = Column(Text)
    deviceStatus = Column(Enum(DeviceStatus))
    lastOnlineTimestamp = Column(Float)
    type = Column(Enum(DeviceType))
    publiclyAvailable = Column(Boolean)

    def __init__(self, device_id, name, ip_address, net_mask, last_online_timestamp, publicly_available,
                 device_status=DeviceStatus.UNKNOWN,
                 _type=DeviceType.POD):
        self.id = device_id
        self.name = name
        self.ipAddress = ip_address
        self.netMask = net_mask
        self.deviceStatus = device_status
        self.lastOnlineTimestamp = last_online_timestamp
        self.type = _type
        self.publiclyAvailable = publicly_available


class Test(Base):
    __tablename__ = 'test'
    id = Column(Text, primary_key=True)
    podId = Column(Text)
    name = Column(Text)
    testContent = Column(Text)
    lastChangedTimestamp = Column(Text)

    def __init__(self, _id, name, test_content, last_changed_timestamp, pod_id):
        self.id = _id
        self.name = name
        self.podId = pod_id
        self.testContent = test_content
        self.lastChangedTimestamp = last_changed_timestamp

    def __eq__(self, other):
        if not isinstance(other, Test):
            return NotImplemented
        return self.name == other.name and self.podId == other.podId and self.testContent == other.testContent and self.lastChangedTimestamp == other.lastChangedTimestamp

    def to_json(self):
        return {
            'name': self.name,
            'testContent': self.testContent
        }


class TestSequence(Base):
    __tablename__ = 'test_sequence'
    id = Column(Text, primary_key=True)
    podId = Column(Text)
    name = Column(Text)
    testSequenceContent = Column(Text)
    sequenceHash = Column(Text)
    lastChangedTimeStamp = Column(Float)

    def __init__(self, _id, name, sequence_content, last_changed_timestamp, pod_id):
        self.id = _id
        self.name = name
        self.podId = pod_id
        self.testSequenceContent = sequence_content
        self.lastChangedTimeStamp = last_changed_timestamp

    def __eq__(self, other):
        if not isinstance(other, TestSequence):
            return NotImplemented
        return self.id == other.id and self.name == other.name and self.podId == other.podId and \
               self.testSequenceContent == other.testSequenceContent


class TestResult(Base):
    __tablename__ = 'test_result'
    id = Column(Integer, primary_key=True)
    name = Column(String(50))
    result = Column(BLOB)
    deviceId = Column(Text)
    testRunId = Column(String(120))
    testName = Column(String(120))
    hostname = Column(String(120))
    timestamp = Column(String(120))
    isSent = Column(Boolean)

    def __init__(self, name, result, test_run_id, device_id, test_name, hostname, timestamp, is_sent):
        self.name = name
        self.result = result
        self.testRunId = test_run_id
        self.deviceId = device_id
        self.testName = test_name
        self.hostname = hostname
        self.timestamp = timestamp
        self.isSent = is_sent

    def as_dict(self):
        return {c.name: getattr(self, c.name) for c in self.__table__.columns}


class TestSequenceResult(Base):
    __tablename__ = "test_sequence_result"
    id = Column(Text, primary_key=True)
    sequenceRunId = Column(Text)
    sequenceId = Column(Text)
    podId = Column(Text)
    sequenceName = Column(Text)
    sequenceResult = Column(Text)
    timestamp = Column(BigInteger)

    def __init__(self, _id, sequence_run_id, sequence_id, pod_id, sequence_name, sequence_result, time_stamp):
        self.id = _id
        self.sequenceRunId = sequence_run_id
        self.sequenceId = sequence_id
        self.podId = pod_id
        self.sequenceName = sequence_name
        self.sequenceResult = sequence_result
        self.timestamp = time_stamp


class TestSequenceStepResult(Base):
    __tablename__ = "test_sequence_step_result"
    id = Column(Text, primary_key=True)
    sequenceRunId = Column(Text)
    testId = Column(Text)
    podId = Column(Text)
    testResult = Column(Text)
    timestamp = Column(BigInteger)
    isSent = Column(Boolean)

    def __init__(self, _id, sequence_run_id, test_id, pod_id, test_result, time_stamp, is_sent):
        self.id = _id
        self.sequenceRunId = sequence_run_id
        self.testId = test_id
        self.podId = pod_id
        self.testResult = test_result
        self.timestamp = time_stamp
        self.isSent = is_sent


class CompanyLicensePublic(Base):
    __tablename__ = 'company_license_public'
    id = Column(Text, primary_key=True)
    groupId = Column(Text)
    licenseId = Column(Text)
    deviceId = Column(Text)
    accessToken = Column(Text)
    refreshToken = Column(Text)
    activated = Column(Boolean)
    expirationDate = Column(Date)

    def __init__(self, _id, group_id, license_id, pod_id, expiration_date):
        self.id = _id
        self.groupId = group_id
        self.licenseId = license_id
        self.deviceId = pod_id
        self.expirationDate = expiration_date
        self.activated = False
        self.accessToken = ""
        self.refreshToken = ""

    def as_dict(self):
        return {c.name: getattr(self, c.name) for c in self.__table__.columns}


class Notification:

    def __init__(self, content):
        self.content = content


class TestRun:
    def __init__(self, test_id, test_name, pod_id, context_id, test_run_type, test_content, test_status, timestamp):
        self.testId = test_id
        self.testName = test_name
        self.podId = pod_id
        self.contextId = context_id
        self.testRunType = test_run_type
        self.testContent = test_content
        self.testStatus = test_status
        self.timestamp = timestamp


class TestStatusDTO:
    def __init__(self, test_run_id, test_id, test_status):
        self.testRunId = test_run_id
        self.testId = test_id
        self.testStatus = test_status


class NotificationDTO:
    def __init__(self, notification, test_run_dto):
        self.notification = notification
        self.testRunDTO = test_run_dto


class ResponseError:
    def __init__(self, error_message):
        self.errorMessage = error_message


class TestStatus(Enum):
    UNKNOWN = 1
    SCHEDULED = 2
    RUNNING = 3
    PLANNED = 4
    EXECUTED = 5


Base.metadata.create_all(db)
Session = scoped_session(sessionmaker(bind=db, expire_on_commit=False))
