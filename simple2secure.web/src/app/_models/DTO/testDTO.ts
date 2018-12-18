import {TestResult} from '../testResult';
import {TestCase} from '../testCase';

export class TestDTO{
    test: TestCase;
    results: TestResult[];
}