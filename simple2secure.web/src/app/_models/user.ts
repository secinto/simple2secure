import {Generic} from './generic';
import {Probe} from './probe';

export class User extends Generic {
    username: string;
    password: string;
    confirmPassword: string;
    firstName: string;
    lastName: string;
    email: string;
    address: string;
    city: string;
    zip: string;
    company: string;
    mobile: string;
    phone: string;
    groupId: string;
    myProbes: Probe[];
}
