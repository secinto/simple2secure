import {Base} from './base';

export class Processor extends Base{
  probeId: string;
  groupId: string;
  processor_class: string;
  groovyProcessor: string;
  analysisInterval: number;
  analysisIntervalUnit: string;
}
