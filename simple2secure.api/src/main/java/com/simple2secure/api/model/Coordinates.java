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

public class Coordinates {
	private Double srclatitude;
	private Double srclongitude;
	private Double destlatitude;
	private Double destlongitude;

	public Coordinates() {

	}

	public Coordinates(Double srclatitude, Double srclongitude, Double destlatitude, Double destlongitude) {
		this.srclatitude = srclatitude;
		this.srclongitude = srclongitude;
		this.destlatitude = destlatitude;
		this.destlongitude = destlongitude;
	}

	public Double getSrclatitude() {
		return srclatitude;
	}

	public void setSrclatitude(Double srclatitude) {
		this.srclatitude = srclatitude;
	}

	public Double getSrclongitude() {
		return srclongitude;
	}

	public void setSrclongitude(Double srclongitude) {
		this.srclongitude = srclongitude;
	}

	public Double getDestlatitude() {
		return destlatitude;
	}

	public void setDestlatitude(Double destlatitude) {
		this.destlatitude = destlatitude;
	}

	public Double getDestlongitude() {
		return destlongitude;
	}

	public void setDestlongitude(Double destlongitude) {
		this.destlongitude = destlongitude;
	}
}
