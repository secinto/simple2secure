import {Base} from './base';

export class Rule extends Base {
	name: string;
	description: string;
	contextID: string;
	groovyCode: string;
}
