package com.simple2secure.api.dto;

import java.util.List;

import com.simple2secure.api.model.Pod;
import com.simple2secure.api.model.Test;

public class PodDTO {

	private Pod pod;

	private List<Test> test;

	public PodDTO() {
	}

	public PodDTO(Pod pod, List<Test> test) {
		this.pod = pod;
		this.test = test;
	}

	public Pod getPod() {
		return pod;
	}

	public void setPod(Pod pod) {
		this.pod = pod;
	}

	public List<Test> getTest() {
		return test;
	}

	public void setTest(List<Test> test) {
		this.test = test;
	}

}
