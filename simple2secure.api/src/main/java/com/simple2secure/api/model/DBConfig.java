package com.simple2secure.api.model;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.simple2secure.api.dbo.GenericDBObject;

@Entity
@Table(name = "DBConfig")
public class DBConfig extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = -770860586822644469L;
	private String location;
	private String dbURI;
	private String write_user;
	private String write_password;
	private String read_user;
	private String read_password;
	private int time_slot_size;

	public DBConfig() {
	}

	/**
	 *
	 * @param location
	 * @param dbURI
	 * @param write_user
	 * @param write_password
	 * @param read_user
	 * @param read_password
	 * @param time_slot_size
	 */
	public DBConfig(String location, String dbURI, String write_user, String write_password, String read_user, String read_password,
			int time_slot_size) {
		super();
		this.location = location;
		this.dbURI = dbURI;
		this.write_user = write_user;
		this.write_password = write_password;
		this.read_user = read_user;
		this.read_password = read_password;
		this.time_slot_size = time_slot_size;
	}

	/**
	 *
	 * @return
	 */
	public String getLocation() {
		return location;
	}

	/**
	 *
	 * @param location
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 *
	 * @return
	 */
	public String getDbURI() {
		return dbURI;
	}

	/**
	 *
	 * @param dbURI
	 */
	public void setDbURI(String dbURI) {
		this.dbURI = dbURI;
	}

	/**
	 *
	 * @return
	 */
	public String getWrite_user() {
		return write_user;
	}

	/**
	 *
	 * @param write_user
	 */
	public void setWrite_user(String write_user) {
		this.write_user = write_user;
	}

	/**
	 *
	 * @return
	 */
	public String getWrite_password() {
		return write_password;
	}

	public void setWrite_password(String write_password) {
		this.write_password = write_password;
	}

	/**
	 *
	 * @return
	 */
	public String getRead_user() {
		return read_user;
	}

	/**
	 *
	 * @param read_user
	 */
	public void setRead_user(String read_user) {
		this.read_user = read_user;
	}

	/**
	 *
	 * @return
	 */
	public String getRead_password() {
		return read_password;
	}

	/**
	 *
	 * @param read_password
	 */
	public void setRead_password(String read_password) {
		this.read_password = read_password;
	}

	/**
	 *
	 * @return
	 */
	public int getTime_slot_size() {
		return time_slot_size;
	}

	/**
	 *
	 * @param time_slot_size
	 */
	public void setTime_slot_size(int time_slot_size) {
		this.time_slot_size = time_slot_size;
	}

}
