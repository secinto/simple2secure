package com.simple2secure.api.model;

import java.util.List;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.simple2secure.api.dbo.GenericDBObject;
import com.simple2secure.api.dto.TestSUTDataInput;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TestSequence extends GenericDBObject {

	private static final long serialVersionUID = -914338716345452064L;

	@JsonSerialize(
			using = ToStringSerializer.class)
	private ObjectId podId;

	private String name;
	private List<TestSUTDataInput> tests;
	private long lastChangedTimeStamp;

	public TestSequence(ObjectId podId, String name, List<TestSUTDataInput> tests) {
		this.podId = podId;
		this.name = name;
		this.tests = tests;
	}
}
