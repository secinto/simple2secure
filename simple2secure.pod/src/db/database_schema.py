from flask_marshmallow import Marshmallow
from src.db.database import TestResult, Test, PodInfo, TestSequence, TestSequenceResult

ma = Marshmallow()


class CompanyLicensePublicSchema(ma.ModelSchema):
    class Meta:
        fields = ("groupId", "licenseId", "expirationDate", "deviceId", "hostname", "accessToken", "deviceIsPod")


class TestResultSchema(ma.ModelSchema):
    class Meta:
        fields = ("result", "hostname", "name", "testRunId", "timestamp")
        #model = TestResult


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
