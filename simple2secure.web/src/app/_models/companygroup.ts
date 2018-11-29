import {Generic} from './generic';

export class CompanyGroup extends Generic {
    name: string;
    contextId: string;
    children: string[];
    rootGroup: boolean;
    standardGroup: boolean;
    superUserIds: string[];
}
