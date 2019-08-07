import {Settings} from '../settings';
import {LicensePlan} from '../LicensePlan';
import {TestMacro} from '../TestMacro';

export class SettingsDTO {
	public settings: Settings;
	public licensePlan: LicensePlan[];
	public testMacroList: TestMacro[];
}
