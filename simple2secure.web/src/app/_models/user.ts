import {Generic} from './generic';
import {Probe} from './probe';
import {UserRole} from './userRole'
import {CompanyGroup} from './companygroup';

export class User extends Generic {
    username: string;
    password: string;
    confirmPassword: string;
    firstName: string;
    lastName: string;
    userRole: UserRole;
    email: string;
    address: string;
    city: string;
    zip: string;
    company: string;
    mobile: string;
    phone: string;
    groupId: string;
    groupName: string;
    myUsersList: User[];
    myProbes: Probe[];
}
