import {UserRole} from './userRole';
import {UserRegistrationType} from './userRegistrationType';

export class UserRegistration{
    email: string;
    password: string;
    userRole: UserRole;
    groupIds: string[];
    addedByUserId: string;
    registrationType: UserRegistrationType;
}