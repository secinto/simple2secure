import { Injectable } from '@angular/core';
import {Http, Response, URLSearchParams, Headers, RequestOptions} from '@angular/http';
import { Observable } from 'rxjs/Rx';
import 'rxjs/add/operator/toPromise';
import { environment } from '../../environments/environment';

@Injectable()
export class ImportService {
  constructor(private http: Http) { }

	postFormData(file: File) {
	  return Observable.fromPromise(new Promise((resolve, reject) => {
	  const formData: any = new FormData();
	  const xhr = new XMLHttpRequest();
	  const currentUser = JSON.parse(localStorage.getItem('currentUser'));
	  formData.append('file', file, file.name);

	  xhr.onreadystatechange = function () {
		if (xhr.readyState === 4) {
		  if (xhr.status === 200) {
			resolve(xhr.response);
		  } else {
			reject(xhr.response);
		  }
		}
	  };
	  xhr.open('POST', environment.apiEndpoint + 'gui/upload/' + currentUser.userID, true);
	  xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
	  xhr.send(formData);
	  }));
	}

    getAll() {
        const headers = new Headers({'Authorization': localStorage.getItem('token')});
        const options = new RequestOptions({ headers: headers});
        return this.http.get(environment.apiEndpoint + 'gui', options).map((response: Response) => response.json());
    }
}
