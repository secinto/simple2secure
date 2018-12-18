import {Tool} from '../tool';
import {TestDTO} from './testDTO';
import {TestCaseTemplate} from '../testCaseTemplate';

export class ToolDTO{

    tool: Tool;
    tests: TestDTO[];
    templates: TestCaseTemplate[];
}