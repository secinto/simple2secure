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

package com.simple2secure.api.model;

import com.simple2secure.api.dbo.GenericDBObject;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
public abstract class SystemUnderTest extends GenericDBObject {

	/**
	*
	*/
	private static final long serialVersionUID = 5020907897089097628L;

	private @NonNull String contextId;
	private @NonNull String deviceId;
	private @NonNull String name;
//	private String ipAdress;
//	private String netMask;
//	private String uri;
//	@Embedded
//	private @NonNull SystemType systemType;
//	private String deviceLocation;
//	private DeviceStatus deviceStatus;
//	private long lastOnlineTimestamp;

}