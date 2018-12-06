import {Generic} from './generic';

export class EmailConfiguration extends Generic{
    contextId: string;
    incomingServer: string;
    incomingPort: string;
    outgoingServer: string;
    outgoingPort: string;
    email: string;
    password: string;
}
