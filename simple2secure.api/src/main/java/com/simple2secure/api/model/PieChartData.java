package com.simple2secure.api.model;

import java.util.List;

public class PieChartData {

	private List<Integer> series;
	
	private List<String> labels;
	
	public PieChartData() {
		
	}
	
	public PieChartData(List<Integer> series, List<String> labels) {
		this.series = series;
		this.labels = labels;
	}

	public List<Integer> getSeries() {
		return series;
	}

	public void setSeries(List<Integer> series) {
		this.series = series;
	}

	public List<String> getLabels() {
		return labels;
	}

	public void setLabels(List<String> labels) {
		this.labels = labels;
	}
}
