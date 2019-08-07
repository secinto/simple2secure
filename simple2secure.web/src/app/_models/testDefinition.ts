import {TestStep} from './testStep';

export class TestDefinition{
	description = '';
	version = '';
	precondition = new TestStep();
	step = new TestStep();
	postcondition= new TestStep();
}
