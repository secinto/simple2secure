import {Generic} from './generic';
import {Probe} from './probe';

export class User extends Generic {
	password: string;
	firstName: string;
	lastName: string;
	email: string;
	zip: string;
	company: string;
	mobile: string;
	myProbes: Probe[];
}
