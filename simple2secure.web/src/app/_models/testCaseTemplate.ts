import {Base} from './base';
import {Command} from './command';

export class TestCaseTemplate extends Base{
    name: string;
    toolId: string;
    commands: Command[];
}
