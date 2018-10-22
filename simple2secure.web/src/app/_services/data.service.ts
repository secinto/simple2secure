import { Injectable } from '@angular/core';
import {CompanyGroup} from '../_models';

@Injectable()
export class DataService {
    data: any;

    get() {
        // information lost on page refresh
        // return this.data

        // information stored in local storage
        return JSON.parse(localStorage.getItem('data'));
    }

    set(data: any){

        // information lost on page refresh
        // this.data = data;

        // information stored in local storage
        localStorage.setItem('data', JSON.stringify(data));
    }

    getCurrentUser(){
        return JSON.parse(localStorage.getItem('currentUser'));
    }

    setProbe(data: any){
        localStorage.setItem('probe', JSON.stringify(data));
    }

    static setTool(data: any){
        localStorage.setItem('tool', JSON.stringify(data));
    }

    static getTool(){
        return JSON.parse(localStorage.getItem('tool'));
    }

    static getProbe(){
        return JSON.parse(localStorage.getItem('probe'));
    }

    setGroups(data: CompanyGroup[]){
        localStorage.setItem('groups', JSON.stringify(data));
    }

    getGroups(){
        return JSON.parse(localStorage.getItem('groups'));
    }
}
