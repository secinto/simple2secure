import {Base} from './base';
import {TestStep} from './testStep';
import {Timeunit} from './timeunit';

export class Test extends Base {
	podId = '';
	name = '';
	description = '';
	version = '';
	precondition = new TestStep();
	step = new TestStep();
	postcondition= new TestStep();
	scheduled: boolean;
	scheduledTime: number;
	scheduledTimeUnit: Timeunit;
}