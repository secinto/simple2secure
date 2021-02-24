package com.simple2secure.api.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChartData {

	private List<String> labels;
	private List<List<Integer>> series;
	private List<Integer> seriesPieChart;

	public ChartData(List<Integer> seriesPieChart) {
		this.seriesPieChart = seriesPieChart;
	}

	public ChartData(List<String> labels, List<List<Integer>> series) {
		this.labels = labels;
		this.series = series;
	}
}
