import {Email} from '../email';
import {EmailConfiguration} from '../emailconfig';
import {FrontendRule} from '../frontendRule';

export class EmailConfigurationDTO {
	public configuration: EmailConfiguration;
	public emails: Email[];
	public rules: FrontendRule[];
}