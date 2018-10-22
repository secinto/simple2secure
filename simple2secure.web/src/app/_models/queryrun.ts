import {Generic} from './generic';
import {Timeunit} from './index';

export class QueryRun extends Generic{
  probeId: string;
  groupId: string;
  name: string;
  always: boolean;
  analysisInterval: number;
  analysisIntervalUnit: Timeunit;
  sqlQuery: string;
  isGroupQueryRun: boolean;
}
