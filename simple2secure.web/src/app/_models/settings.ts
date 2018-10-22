import {Base} from './base';
import {Timeunit} from './timeunit';

export class Settings extends Base{
    accessTokenValidityTime: number;
    accessTokenValidityUnit: Timeunit;
    accessTokenProbeValidityTime: number;
    accessTokenProbeValidityUnit: Timeunit;
    accessTokenProbeRestValidityTime: number;
    accessTokenProbeRestValidityTimeUnit: Timeunit;
}
