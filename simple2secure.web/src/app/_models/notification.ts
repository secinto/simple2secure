import {Generic} from './generic';

export class Notification extends Generic {
	content: string;
	contextId: string;
	read: boolean;
}
