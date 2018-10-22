import { Component} from '@angular/core';
import { ImportService, AlertService} from '../_services/index';

@Component({
    moduleId: module.id,
    templateUrl: 'guiImport.component.html'
})

export class GuiImportComponent {
    file: any;
    error: string;

    constructor(
        private importService: ImportService,
        private alertService: AlertService ) {}


    getFiles(files: any) {
        const empDataFiles: FileList = files.files;
        this.file = empDataFiles[0];
    }

    postfile() {
        if (this.file !== undefined) {
            this.importService.postFormData(this.file).map(responce => {
            }).catch(error => this.error = error);
            setTimeout(() => {
                this.alertService.success('File uploaded successfully!');
            }, 5000);
        } else {
            this.alertService.error(this.error);
        }
    }
}
