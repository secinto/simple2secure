import {Base} from './base';
import {TestResult} from './testResult';
import {Command} from './command';

export class Test extends Base{
    name: string;
    commands: Command[];
    testResult: TestResult[];
    customTest: boolean;
    createInstance: boolean;
}
