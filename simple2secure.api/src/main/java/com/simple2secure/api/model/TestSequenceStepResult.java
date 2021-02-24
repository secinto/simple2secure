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
@NoArgsConstructor
@AllArgsConstructor
public class TestSequenceStepResult extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = 1743955225338552917L;

	@JsonSerialize(
			using = ToStringSerializer.class)
	private ObjectId sequenceRunId;

	@JsonSerialize(
			using = ToStringSerializer.class)
	private ObjectId testId;

	@JsonSerialize(
			using = ToStringSerializer.class)
	private ObjectId podId;

	private String testResult;

	private long timestamp;

}
