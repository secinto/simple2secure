import {Base} from './base';
import {TestCase} from './testCase';

export class Tool extends Base{
    generatedName = String;
    tests: TestCase[];
}
