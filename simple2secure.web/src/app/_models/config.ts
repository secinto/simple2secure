import {Generic} from './generic';

export class Config extends Generic {
  version: number;
  showInterfaces: boolean;
    externalAddress: string;
    processingFactory: string;
  bpfFilter: string;
}
