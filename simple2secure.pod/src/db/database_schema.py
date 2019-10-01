from flask_marshmallow import Marshmallow
from src.db.database import TestResult, Test, PodInfo, TestSequence, TestSequenceResult, CompanyLicensePod

ma = Marshmallow()


class CompanyLicensePodSchema(ma.ModelSchema):
    class Meta:
        fields = ("groupId", "licenseId", "podId", "hostname", "configuration")


class TestResultSchema(ma.ModelSchema):
    class Meta:
        model = TestResult


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
