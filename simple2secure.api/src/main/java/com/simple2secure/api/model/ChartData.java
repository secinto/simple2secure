package com.simple2secure.api.model;

import java.util.List;

public class ChartData {

	private List<String> labels;
	private List<List<Integer>> series;
	
	public ChartData() {
		
	}
	
	public ChartData(List<String> labels, List<List<Integer>> series) {
		super();
		this.labels = labels;
		this.series = series;
	}	
	
	public List<String> getLabels() {
		return labels;
	}
	public void setLabels(List<String> labels) {
		this.labels = labels;
	}
	public List<List<Integer>> getSeries() {
		return series;
	}
	public void setSeries(List<List<Integer>> series) {
		this.series = series;
	}
	
	
}
