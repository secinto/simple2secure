package com.simple2secure.api.model;

import com.simple2secure.api.dbo.GenericDBObject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Widget extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = -3052204613738700648L;

	private String name;
	private String description;
	private String tag;
	private String bgClass;
	private String icon;
	private String api;
}
