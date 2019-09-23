import {RuleParam} from './ruleParam';
import {RuleParamArray} from './ruleParamArray';
import {Base} from './base';

export class TemplateAction extends Base {
    name: string;
    description_en: string;
    description_de: string;
    // @ts-ignore
    params: RuleParam[];
    // @ts-ignore
    paramArrays: RuleParamArray[];
}
