import {Base} from './base';
import {Command} from './command';
import {Rule} from './rule';

export class TestStep extends Base {
	description: string;
	command: Command;
	condition: Rule;
}