import {Base} from './base';
import {TestStep} from './testStep';

export class Test extends Base {
	podId = '';
	description = '';
	version = '';
	precondition = new TestStep();
	step = new TestStep();
	postcondition= new TestStep();
}