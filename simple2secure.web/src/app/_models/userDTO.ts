import {User} from './user';
import {Probe} from './probe';
import {CompanyGroup} from './companygroup';

export class UserDTO{
    myProfile: User;
    myUsersList: User[];
    myGroups: CompanyGroup[];
    probes: Probe[];
}
