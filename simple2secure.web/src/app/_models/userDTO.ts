import {User} from './user';
import {Probe} from './probe';
import {CompanyGroup} from './companygroup';
import {UserRoleDTO} from './userRoleDTO';
import {Context} from './context';

export class UserDTO{
    myProfile: User;
    myUsersList: UserRoleDTO[];
    myGroups: CompanyGroup[];
    myProbes: Probe[];
    myContexts: Context[];
}
