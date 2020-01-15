package com.simple2secure.api.model;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;

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
@Entity
@Table(name = "DeviceInfo")
public class DeviceInfo extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = -7168259292146418922L;

	protected @NonNull String name;
	protected @NonNull String deviceId;
	protected String ipAddress;
	protected String netMask;
	protected @Embedded @NonNull DeviceType type;
	protected DeviceStatus deviceStatus = DeviceStatus.UNKNOWN;
	protected long lastOnlineTimestamp;

}
