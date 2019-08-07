import {Base} from './base';
import {Timeunit} from './timeunit';
import {LicensePlan} from './LicensePlan';

export class Settings extends Base{
    accessTokenValidityTime: number;
    accessTokenValidityUnit: Timeunit;
    accessTokenProbeValidityTime: number;
    accessTokenProbeValidityUnit: Timeunit;
    accessTokenProbeRestValidityTime: number;
    accessTokenProbeRestValidityTimeUnit: Timeunit;
}
