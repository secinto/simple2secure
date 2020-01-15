import {Component, EventEmitter, Input, Output} from '@angular/core';
import {BaseWidget} from "./basewidget.component";
import {environment} from "../../environments/environment";
import {saveAs as importedSaveAs} from 'file-saver';

@Component({
    selector: 'app-download',
    templateUrl: './download-widget.component.html',
    styleUrls: ['./widgets.scss']
})
export class DownloadWidgetComponent extends BaseWidget{
    @Input() bgClass: string;
    @Input() icon: string;
    @Input() count: number;
    @Input() name: string;
    @Input() data: any[];
    @Input() propertiesId: string;
    @Output() event: EventEmitter<any> = new EventEmitter();
    loading = false;
    selectedGroup = "Standard";
    downloadGroup: any;

    downloadLicense(){
        this.loading = true;
        this.downloadGroup = this.getGroupByName(this.selectedGroup);

        if(this.downloadGroup){
            this.httpService.getFile(environment.apiEndpoint + 'license/' + this.downloadGroup.id)
                .subscribe(
                    data => {
                        importedSaveAs(data, 'license-' + this.downloadGroup.id + '.zip');
                        this.loading = false;
                    },
                    error => {
                        this.alertService.error('Error occured while downloading license');
                        this.loading = false;
                    });
        }
        else{
            this.alertService.error('Error occured while downloading license');
            this.loading = false;
        }     
    }

    getGroupByName(name: string){
        for (let item of this.data) {
            if(item.name === name){
                return item;
            }
        }
        this.downloadGroup = null;
        return null;
    }
}
