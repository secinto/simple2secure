import {Generic} from './generic';

export class CompanyGroup extends Generic {
    name: string;
    adminGroupId: string;
    children: string[];
    rootGroup: boolean;
    standardGroup: boolean;
    superUserIds: string[];
}
