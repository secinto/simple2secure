import {Timeunit} from './timeunit';
import {Generic} from './generic';

export class LicensePlan extends Generic{
    name: string;
    validity: number;
    validityUnit: Timeunit;
    maxNumberOfDownloads: number;
}
