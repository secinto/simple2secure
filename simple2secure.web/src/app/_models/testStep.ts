import {Base} from './base';
import {Command} from './command';

export class TestStep extends Base {
	description = '';
	command = new Command();
}