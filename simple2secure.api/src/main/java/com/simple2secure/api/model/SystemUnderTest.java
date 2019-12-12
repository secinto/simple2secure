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

import javax.persistence.Embedded;

import com.simple2secure.api.dbo.GenericDBObject;

public class SystemUnderTest extends GenericDBObject {

    /**
	 * 
	 */
	private static final long serialVersionUID = 5020907897089097628L;

    private String contextId;
    private String endDeviceId;
    @Embedded
    private String endDeviceType;
    private String endDeviceLocation;
    private String name;
	private String ipAdress;
    private String netMask;
    private DeviceStatus deviceStatus;
    private long lastOnlineTimestamp;
    

    public SystemUnderTest(){
    }

    public SystemUnderTest(String contextId, String endDeviceId, String endDeviceType, String endDeviceLocation, 
    		String name,  String ipAdress, String netMask){
        setContextId(contextId);
        setEndDeviceId(endDeviceId);
        setEndDeviceType(endDeviceType);
        setEndDeviceLocation(endDeviceLocation);
        setName(name);
        setIpAdress(ipAdress);
        setNetMask(netMask);
    }
    
    public SystemUnderTest(String groupId, String endDeviceId, String endDeviceType, String endDeviceLocation, 
    		String name,  String ipAdress, String netMask, DeviceStatus deviceStatus, long lastOnlineTimestamp){
    	this(groupId, endDeviceId, endDeviceType, endDeviceLocation, name, ipAdress, netMask);
        setDeviceStatus(deviceStatus);
        setLastOnlineTimestamp(lastOnlineTimestamp);
    }

    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }
    
    public String getEndDeviceId() {
		return endDeviceId;
	}

	public void setEndDeviceId(String endDeviceId) {
		this.endDeviceId = endDeviceId;
	}

    public String getEndDeviceType() {
        return endDeviceType;
    }

    public void setEndDeviceType(String endDeviceType) {
        this.endDeviceType = endDeviceType;
    }

    public String getEndDeviceLocation() {
        return endDeviceLocation;
    }

    public void setEndDeviceLocation(String endDeviceLocation) {
        this.endDeviceLocation = endDeviceLocation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIpAdress() {
		return ipAdress;
	}

	public void setIpAdress(String ipAdress) {
		this.ipAdress = ipAdress;
	}

	public String getNetMask() {
		return netMask;
	}

	public void setNetMask(String netMask) {
		this.netMask = netMask;
	}

	public DeviceStatus getDeviceStatus() {
		return deviceStatus;
	}

	public void setDeviceStatus(DeviceStatus deviceStatus) {
		this.deviceStatus = deviceStatus;
	}

	public long getLastOnlineTimestamp() {
		return lastOnlineTimestamp;
	}

	public void setLastOnlineTimestamp(long lastOnlineTimestamp) {
		this.lastOnlineTimestamp = lastOnlineTimestamp;
	}
}