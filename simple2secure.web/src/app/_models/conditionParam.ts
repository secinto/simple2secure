import {DataType} from './dataType';

export class ConditionParam<T> {
    name: string;
    description_en: string;
    description_de: string;
    value: T;
    type: DataType;
}
