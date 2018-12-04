import {User} from './user';
import {Probe} from './probe';
import {CompanyGroup} from './companygroup';
import {UserRoleDTO} from './userRoleDTO';

export class UserDTO{
    myProfile: User;
    myUsersList: UserRoleDTO[];
    myGroups: CompanyGroup[];
    probes: Probe[];
}
