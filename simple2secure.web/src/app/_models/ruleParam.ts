import {DataType} from './dataType';

export class RuleParam<T> {
    nameTag: string;
    descriptionTag: string;
    value: T;
    type: DataType;
}
