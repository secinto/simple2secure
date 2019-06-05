import {Base} from './base';
import {TestResultObj} from './testResultObj';

export class TestResult extends Base {
	name: string;
	licenseId: string;
	groupId: string;
	hostname: string;
	result: TestResultObj;
	timestamp: number;
}
