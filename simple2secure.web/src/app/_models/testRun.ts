import {Base} from './base';
import {TestRunType} from './testRunType';
import {TestStatus} from './testStatus';

export class TestRun extends Base {
	testId: string;
	testName: string;
	podId: string;
	contextId: string;
	executed: boolean;
	testRunType: TestRunType;
	testContent: string;
	testStatus: TestStatus;
}