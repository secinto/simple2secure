import {RuleParam} from './ruleParam';
import {RuleParamArray} from './ruleParamArray';
import {Base} from './base';

export class TemplateAction extends Base {
    nameTag: string;
    descriptionTag: string;
    // @ts-ignore
    params: RuleParam[];
    // @ts-ignore
    paramArrays: RuleParamArray[];
}
