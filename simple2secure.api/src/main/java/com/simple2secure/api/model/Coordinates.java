package com.simple2secure.api.model;

public class Coordinates {
	private Double srclatitude;
	private Double srclongitude;
	private Double destlatitude;
	private Double destlongitude;

	public Coordinates() {

	}

	public Coordinates(Double srclatitude, Double srclongitude, Double destlatitude, Double destlongitude) {
		this.srclatitude = srclatitude;
		this.srclongitude = srclongitude;
		this.destlatitude = destlatitude;
		this.destlongitude = destlongitude;
	}

	public Double getSrclatitude() {
		return srclatitude;
	}

	public void setSrclatitude(Double srclatitude) {
		this.srclatitude = srclatitude;
	}

	public Double getSrclongitude() {
		return srclongitude;
	}

	public void setSrclongitude(Double srclongitude) {
		this.srclongitude = srclongitude;
	}

	public Double getDestlatitude() {
		return destlatitude;
	}

	public void setDestlatitude(Double destlatitude) {
		this.destlatitude = destlatitude;
	}

	public Double getDestlongitude() {
		return destlongitude;
	}

	public void setDestlongitude(Double destlongitude) {
		this.destlongitude = destlongitude;
	}
}
