import {User} from './user';
import {UserRole} from './userRole';

export class UserRoleDTO{
    user: User;
    userRole: UserRole;
    groupIds: string[];
}
