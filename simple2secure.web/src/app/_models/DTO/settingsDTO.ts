import {Settings} from '../settings';
import {LicensePlan} from '../LicensePlan';

export class SettingsDTO{
    public settings: Settings;
    public licensePlan: LicensePlan[];
}
