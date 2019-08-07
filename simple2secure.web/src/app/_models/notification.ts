import {Generic} from './generic';

export class Notification extends Generic {
  userId: string;
  toolId: string;
  name: string;
  content: string;
  read: boolean;
}
