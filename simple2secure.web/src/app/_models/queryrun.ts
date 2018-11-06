import {Generic} from './generic';
import {Timeunit} from './index';

export class QueryRun extends Generic{
  groupId: string;
  name: string;
  always: boolean;
  analysisInterval: number;
  analysisIntervalUnit: Timeunit;
  sqlQuery: string;
}
