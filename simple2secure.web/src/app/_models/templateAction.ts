import { RuleParam } from './ruleParam';
import { RuleParamArray } from './ruleParamArray';
import { Base } from './base';
import { RuleFactType } from './ruleFactType';

export class TemplateAction extends Base {
    className: string;
    nameTag: string;
    descriptionTag: string;
    ruleFactType: RuleFactType;
    // @ts-ignore
    params: RuleParam[];
    // @ts-ignore
    paramArrays: RuleParamArray[];
}
