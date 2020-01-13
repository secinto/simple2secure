package com.simple2secure.api.model;

import java.util.List;

public class ChartData {

	private List<String> labels;
	private List<List<Integer>> series;
	private List<Integer> seriesPieChart;
	
	public ChartData() {
		
	}
	
	public ChartData(List<Integer> seriesPieChart) {
		this.seriesPieChart = seriesPieChart;
	}
	
	public ChartData(List<String> labels, List<List<Integer>> series) {
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

	public List<Integer> getSeriesPieChart() {
		return seriesPieChart;
	}

	public void setSeriesPieChart(List<Integer> seriesPieChart) {
		this.seriesPieChart = seriesPieChart;
	}
}
