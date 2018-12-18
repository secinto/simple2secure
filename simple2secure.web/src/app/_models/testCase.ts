import {Base} from './base';
import {Command} from './command';

export class Test extends Base{
    name: string;
    toolId: string;
    commands: Command[];
}
