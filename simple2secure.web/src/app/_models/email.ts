import {Generic} from './generic';

export class Email extends Generic{
    contextId: string;
    configId: string;
    number: number;
    subject: string;
    from: string;
    text: string;
    receivedDate: string;
}
