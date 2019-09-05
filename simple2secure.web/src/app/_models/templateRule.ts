import {Base} from './base';
import {TemplateCondition} from './templateCondition';
import {TemplateAction} from './templateAction';

export class TemplateRule extends Base{
    name: string;
    description: string;
    contextID: string;
    templateCondition: TemplateCondition;
    templateAction: TemplateAction;
}
