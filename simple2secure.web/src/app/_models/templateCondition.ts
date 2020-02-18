import {RuleParam} from './ruleParam';
import {RuleParamArray} from './ruleParamArray';
import {Base} from './base';

export class TemplateCondition extends Base{
    nameTag: string;
    descriptionTag: string;
    // @ts-ignore
    params: RuleParam[];
    // @ts-ignore
    paramArrays: RuleParamArray[];
}
