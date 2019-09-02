import {Base} from './base';
import {TemplateCondition} from './templateCondition';
import {TemplateAction} from './templateAction';

export class TemplateRule extends Base{
    name: string;
    description_en: string;
    description_de: string;
    contextID: string;
    templateConditions: TemplateCondition[];
    templateActions: TemplateAction[];
}
