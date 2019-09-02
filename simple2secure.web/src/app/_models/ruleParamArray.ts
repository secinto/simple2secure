import {DataType} from './dataType';

export class RuleParamArray<T> {
    name: string;
    description_en: string;
    description_de: string;
    values: T[];
    type: DataType;
}
