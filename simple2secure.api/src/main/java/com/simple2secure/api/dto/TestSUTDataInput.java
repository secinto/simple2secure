package com.simple2secure.api.dto;

import com.simple2secure.api.model.SystemUnderTest;
import com.simple2secure.api.model.TestInputData;
import com.simple2secure.api.model.TestObjWeb;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TestSUTDataInput {

	private SystemUnderTest sut;
	private TestInputData inputData;
	private TestObjWeb test;

}
