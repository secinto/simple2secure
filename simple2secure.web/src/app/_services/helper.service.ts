import {Injectable} from '@angular/core';
import {TestRunDTO} from '../_models/DTO/testRunDTO';

@Injectable()
export class HelperService {

	getEnumValue(value: any){
		if (value == 'MANUAL_POD'){
			return 'MANUAL POD';
		}
		else if (value == 'MANUAL_PORTAL'){
			return 'MANUAL PORTAL';
		}
		else{
			return 'AUTOMATIC PORTAL';
		}
	}

	getTestStatusByTestResult(testRunDTO: TestRunDTO){
		if(testRunDTO.testResult == null){
			return 'UNKNOWN';
		}
		else{
			return testRunDTO.testRun.testStatus;
		}
	}
}
