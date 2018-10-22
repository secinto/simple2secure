import {Generic} from './generic';

export class CompanyGroup extends Generic {
    name: string;
    addedByUserId: string;
    owner: string;
    licenseExpirationDate: string;
    children: CompanyGroup[];
    maxNumberOfLicenseDownloads: number;
    currentNumberOfLicenseDownloads: number;
}
