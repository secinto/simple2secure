from marshmallow import Schema, fields

from src.db.database import PodInfo


class CompanyLicensePublicSchema(Schema):
    id = fields.Str()
    groupId = fields.Str()
    licenseId = fields.Str()
    expirationDate = fields.Str()
    deviceId = fields.Str()
    accessToken = fields.Str()
    refreshToken = fields.Str()
    deviceIsPod = fields.Boolean()


class TestResultSchema(Schema):
    class Meta:
        fields = ("result", "hostname", "name", "testName", "testRunId", "deviceId", "timestamp", "isSent")


class PodInfoSchema(Schema):
    class Meta:
        model = PodInfo


class TestSchema(Schema):
    id = fields.Str()
    podId = fields.Str()
    name = fields.Str()
    testContent = fields.Str()
    lastChangedTimestamp = fields.Str()


class TestSequenceSchema(Schema):
    id = fields.Str()
    podId = fields.Str()
    name = fields.Str()
    testSequenceContent = fields.Str()
    lastChangedTimeStamp = fields.Float()


class TestSequenceResultSchema(Schema):
    class Meta:
        fields = ("id", "sequenceRunId", "sequenceId", "podId", "sequenceName", "sequenceResult", "timestamp")


class DeviceInfoSchema(Schema):
    class Meta:
        fields = ["id", "name", "ipAddress", "netMask", "deviceStatus", "lastOnlineTimestamp", "type", "publiclyAvailable"]


class TestSequenceStepResultSchema(Schema):
    class Meta:
        fields = ("id", "sequenceRunId", "testId", "podId", "testResult", "timestamp")
