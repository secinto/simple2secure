package com.simple2secure.api.dto;

import java.util.List;

import com.simple2secure.api.model.TestSequence;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TestSequenceDTO {

	private TestSequence sequence;
	private List<TestWebDTO> tests;
}
