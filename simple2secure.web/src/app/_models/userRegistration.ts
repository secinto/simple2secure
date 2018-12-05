import {UserRole} from './userRole';
import {UserRegistrationType} from './userRegistrationType';
import {Generic} from './generic';

export class UserRegistration extends Generic{
    email: string;
    password: string;
    addedByUserId: string;
    userRole: UserRole;
    currentContextId: string;
    groupIds: string[];
    registrationType: UserRegistrationType;
}