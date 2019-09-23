import {Email} from '../email';
import {EmailConfiguration} from '../emailconfig';

export class EmailConfigurationDTO {
	public configuration: EmailConfiguration;
	public emails: Email[];
}
