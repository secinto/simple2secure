package com.simple2secure.api.dto;

import java.util.List;

import com.simple2secure.api.model.SystemUnderTest;
import com.simple2secure.api.model.TestInputData;
import com.simple2secure.api.model.TestObjWeb;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TestWebDTO {
	private TestObjWeb test;
	private List<SystemUnderTest> suts;
	private List<TestInputData> inputData;
}
