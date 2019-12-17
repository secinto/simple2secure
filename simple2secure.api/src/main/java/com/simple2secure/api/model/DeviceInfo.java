package com.simple2secure.api.model;

import javax.persistence.Embedded;

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
public class DeviceInfo extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = -7168259292146418922L;

	protected @NonNull String name;
	protected @NonNull String deviceId;
	protected @NonNull String ipAddress;
	protected @NonNull String netMask;
	protected @Embedded @NonNull DeviceType type;
	protected DeviceStatus deviceStatus = DeviceStatus.UNKNOWN;
	protected long lastOnlineTimestamp;

}
