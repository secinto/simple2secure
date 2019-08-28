import {DataType} from './dataType';
import {ConditionParam} from './conditionParam';
import {ConditionParamArray} from './conditionParamArray';

export class TemplateCondition {
    name: string;
    description_en: string;
    description_de: string;
    // @ts-ignore
    params: ConditionParam[];
    // @ts-ignore
    paramArrays: ConditionParamArray[];
}
