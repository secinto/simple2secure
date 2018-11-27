import {Generic} from './generic';

export class NetworkReport extends Generic{
  probeId: string;
  startTime: string;
  processorName: string;
  content: Map<String, String>;
  stringContent: string;
  sent: boolean;
}
