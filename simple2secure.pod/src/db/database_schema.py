from flask_marshmallow import Marshmallow
from src.db.database import TestResult, Test, PodInfo, TestSequence, TestSequenceResult, DeviceInfo

ma = Marshmallow()


class CompanyLicensePublicSchema(ma.ModelSchema):
    class Meta:
        fields = ("groupId", "licenseId", "expirationDate", "deviceId", "accessToken", "deviceIsPod")


class TestResultSchema(ma.ModelSchema):
    class Meta:
        fields = ("result", "hostname", "name", "testRunId", "timestamp")


class PodInfoSchema(ma.ModelSchema):
    class Meta:
        model = PodInfo


class TestSchema(ma.ModelSchema):
    class Meta:
        model = Test


class TestSequenceSchema(ma.ModelSchema):
    class Meta:
        model = TestSequence


class TestSequenceResultSchema(ma.ModelSchema):
    class Meta:
        model = TestSequenceResult


class DeviceInfoSchema(ma.ModelSchema):
    class Meta:
        fields = ["deviceId", "hostName", "ipAddress", "netMask", "deviceStatus", "lastOnlineTimestamp"]
