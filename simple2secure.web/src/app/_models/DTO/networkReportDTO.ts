import {NetworkReport} from '../networkReport';
import {Coordinates} from '../coordinates';

export class NetworkReportDTO extends NetworkReport{
	coordinates: Coordinates[];
}
