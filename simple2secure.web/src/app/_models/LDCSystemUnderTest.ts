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
 
 import { SystemUnderTest } from './systemUnderTest';
 import { Protocol } from './protocol';
 
 
 export class LDCSystemUnderTest extends SystemUnderTest {
	ipAddress: string;
	port: string;
	protocol: Protocol;
	constructor(sut){
		super();
		this.id = sut.id;
		this.contextId = sut.contextId;
		this.deviceId = sut.deviceId;
		this.name = sut.name;
		this.ipAddress = sut.ipAddress;
		this.port = sut.port;
		this.protocol = sut.protocol;
	}
}