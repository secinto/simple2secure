import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router} from '@angular/router';
import {JwtHelper} from 'angular2-jwt';
import {Context, ContextDTO} from '../_models';

@Injectable()
export class RoleGuard implements CanActivate {
    context: ContextDTO;
    constructor(public router: Router) {}
    canActivate(route: ActivatedRouteSnapshot): boolean {
        const expectedRole = route.data.expectedRole;

        this.context = JSON.parse(localStorage.getItem('context'));

        if (this.context.userRole.toString() !== expectedRole) {
            this.router.navigate(['']);
            return false;
        }


        return true;
    }
}