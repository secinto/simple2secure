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

import java.util.concurrent.TimeUnit;

import com.simple2secure.api.dbo.GenericDBObject;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Settings extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = -8942359149793228266L;

	private long accessTokenValidityTime;
	private TimeUnit accessTokenValidityUnit;
	private long accessTokenProbeValidityTime;
	private TimeUnit accessTokenProbeValidityUnit;
	private long accessTokenProbeRestValidityTime;
	private TimeUnit accessTokenProbeRestValidityTimeUnit;

	public Settings(long accessTokenValidityTime, TimeUnit accessTokenValidityUnit, long accessTokenProbeValidityTime,
			TimeUnit accessTokenProbeValidityUnit) {
		this.accessTokenValidityTime = accessTokenValidityTime;
		this.accessTokenValidityUnit = accessTokenValidityUnit;
		this.accessTokenProbeValidityTime = accessTokenProbeValidityTime;
		this.accessTokenProbeValidityUnit = accessTokenProbeValidityUnit;

	}
}
