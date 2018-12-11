import {UserRole} from '../userRole';
import {UserInfo} from '../userInfo';

export class UserRoleDTO{
    user: UserInfo;
    userRole: UserRole;
    groupIds: string[];
}
