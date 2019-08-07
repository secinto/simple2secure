import {Base} from './base';
import {Parameter} from './parameter';

export class Command extends Base {
	parameter = new Parameter();
	executable = '';
}
