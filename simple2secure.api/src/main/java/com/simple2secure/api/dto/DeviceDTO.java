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

package com.simple2secure.api.dto;

import java.util.List;

import org.bson.types.ObjectId;

import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.CompanyLicensePublic;
import com.simple2secure.api.model.Device;
import com.simple2secure.api.model.DeviceInfo;
import com.simple2secure.api.model.TestObjWeb;
import com.simple2secure.api.model.TestSequence;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DeviceDTO {

	private ObjectId _id;
	private String name;
	private ObjectId licensePlanId;
	private int currentNumberOfLicenseDownloads;
	private String _class;
	private CompanyGroup groups;
	private CompanyLicensePublic licenses;
	private DeviceInfo deviceInfos;
	
}
