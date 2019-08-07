import {Generic} from './generic';

export class Email extends Generic{
    userUUID: string;
    configID: string;
    number: number;
    subject: string;
    from: string;
    text: string;
    receivedDate: string;
}
