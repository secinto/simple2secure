import {Base} from './base';

export class Processor extends Base{
  groupId: string;
  processor_class: string;
  groovyProcessor: string;
  analysisInterval: number;
  analysisIntervalUnit: string;
}
