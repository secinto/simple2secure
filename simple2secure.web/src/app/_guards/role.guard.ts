import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router} from '@angular/router';
import {JwtHelper} from 'angular2-jwt';

@Injectable()
export class RoleGuard implements CanActivate {
    jwtHelper: JwtHelper = new JwtHelper();
    constructor(public router: Router) {}
    canActivate(route: ActivatedRouteSnapshot): boolean {
        const expectedRole = route.data.expectedRole;

        const token = localStorage.getItem('token');
        // decode the token to get its payload
        const tokenPayload = this.jwtHelper.decodeToken(token);
        if (tokenPayload.userRole !== expectedRole) {
            this.router.navigate(['']);
            return false;
        }
        return true;
    }
}