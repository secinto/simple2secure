import {Base} from './base';
import {TestStep} from './testStep';

export class Test extends Base {
	podId: string;
	description: string;
	version: string;
	precondition: TestStep;
	step: TestStep;
	postcondition: TestStep;
}