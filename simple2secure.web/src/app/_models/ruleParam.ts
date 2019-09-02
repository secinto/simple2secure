import {DataType} from './dataType';

export class RuleParam<T> {
    name: string;
    description_en: string;
    description_de: string;
    value: T;
    type: DataType;
}
