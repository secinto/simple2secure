import {Base} from './base';

export class Context extends Base{
    name: string;
    admins: string[];
    licensePlanId: string;
    currentNumberOfLicenseDownloads: number;
}
