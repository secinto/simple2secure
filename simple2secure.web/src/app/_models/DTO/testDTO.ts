import {TestCaseResult} from '../testCaseResult';
import {TestCase} from '../testCase';

export class TestDTO{
    test: TestCase;
    results: TestCaseResult[];
}