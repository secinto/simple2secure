package com.simple2secure.api.dto;

import java.util.List;

import com.simple2secure.api.model.Pod;
import com.simple2secure.api.model.TestObjWeb;

public class PodDTO {

	private Pod pod;

	private List<TestObjWeb> test;

	public PodDTO() {
	}

	public PodDTO(Pod pod, List<TestObjWeb> test) {
		this.pod = pod;
		this.test = test;
	}

	public Pod getPod() {
		return pod;
	}

	public void setPod(Pod pod) {
		this.pod = pod;
	}

	public List<TestObjWeb> getTest() {
		return test;
	}

	public void setTest(List<TestObjWeb> test) {
		this.test = test;
	}

}
