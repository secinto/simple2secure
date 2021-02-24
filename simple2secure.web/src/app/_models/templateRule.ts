import { Base } from './base';
import { TemplateCondition } from './templateCondition';
import { TemplateAction } from './templateAction';

export class TemplateRule extends Base {
    name: string;
    description: string;
    contextID: string;
    limit: number;
    conditionExpression: string;
    templateConditions: TemplateCondition[];
    templateActions: TemplateAction[];
}
