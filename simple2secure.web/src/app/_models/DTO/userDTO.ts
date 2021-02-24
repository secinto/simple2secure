/**
 *********************************************************************
 *   simple2secure is a cyber risk and information security platform.
 *   Copyright (C) 2019  by secinto GmbH <https://secinto.com>
 *********************************************************************
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of the
 *   License, or (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *********************************************************************
 */

import { Probe } from '../probe';
import { CompanyGroup } from '../companygroup';
import { UserRoleDTO } from './userRoleDTO';
import { Context } from '../context';
import { UserInfo } from '../userInfo';
import { Device } from '../device';

export class UserDTO {
	myProfile: UserInfo;
	myUsersList: UserRoleDTO[];
	myGroups: CompanyGroup[];
	myDevices: Device[];
	myContexts: Context[];
	assignedGroups: string[];
}
