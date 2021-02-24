import { DataType } from './dataType';

export class RuleParamArray<T> {
    nameTag: string;
    descriptionTag: string;
    values: T[];
    type: DataType;
}
