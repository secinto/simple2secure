import {Probe} from '../probe';
import {CompanyGroup} from '../companygroup';
import {UserRoleDTO} from './userRoleDTO';
import {Context} from '../context';
import {UserInfo} from '../userInfo';

export class UserDTO{
    myProfile: UserInfo;
    myUsersList: UserRoleDTO[];
    myGroups: CompanyGroup[];
    myProbes: Probe[];
    myContexts: Context[];
    assignedGroups: string[];
}
