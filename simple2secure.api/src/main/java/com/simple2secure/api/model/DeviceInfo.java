package com.simple2secure.api.model;

import java.io.Serializable;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.bson.types.ObjectId;

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
@Table(
		name = "deviceInfo")
public class DeviceInfo extends GenericDBObject implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -7168259292146418922L;

	protected @NonNull String name;
	protected String ipAddress;
	protected String netMask;
	protected @Embedded @NonNull DeviceType type;
	protected DeviceStatus deviceStatus = DeviceStatus.UNKNOWN;
	protected long lastOnlineTimestamp;
	protected boolean publiclyAvailable;
	protected String deviceName;

	public DeviceInfo(ObjectId id, String name, DeviceType type, DeviceStatus status, long lastOnlineTimestamp, boolean publiclyAvailable) {
		setId(id);
		setName(name);
		setType(type);
		setDeviceStatus(status);
		setLastOnlineTimestamp(lastOnlineTimestamp);
		setPubliclyAvailable(publiclyAvailable);
	}

}
