package com.simple2secure.api.dbo;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class GenericDBObject implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 7464953476214229002L;

	public static String ID = "_id";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	// @Column(name = "id", updatable = false, nullable = false)
	public String id;

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return this.id;
	}

	@Override
	public boolean equals(Object obj) {
		try {
			return this.id.equals(((GenericDBObject) obj).getId());
		} catch (Exception ex) {
			return false;
		}
	}
}
