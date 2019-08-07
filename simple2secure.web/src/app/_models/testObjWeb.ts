import {TestContent} from './testContent';
import {Timeunit} from './timeunit';

export class TestObjWeb {
	podId = '';
	testId = '';
	hostname = '';
	name = '';
	test_content = new TestContent();
	scheduled: boolean;
	scheduledTime: number;
	scheduledTimeUnit: Timeunit;
}