import {Base} from './base';
import {Parameter} from './parameter';

export class Command extends Base {
	command: string;
	parameters: Parameter[];
}
