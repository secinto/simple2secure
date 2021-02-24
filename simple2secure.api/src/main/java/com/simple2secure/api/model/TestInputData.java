package com.simple2secure.api.model;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.simple2secure.api.dbo.GenericDBObject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TestInputData extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = -2737068568498630401L;

	@JsonSerialize(
			using = ToStringSerializer.class)
	private ObjectId testId;
	private String data;
	private String name;

}
