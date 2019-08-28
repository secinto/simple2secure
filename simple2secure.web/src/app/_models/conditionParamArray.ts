import {DataType} from './dataType';

export class ConditionParamArray<T> {
    name: string;
    description_en: string;
    description_de: string;
    values: T[];
    type: DataType;
}
