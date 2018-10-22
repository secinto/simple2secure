package com.simple2secure.probe.network;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public abstract class PacketProcessor {

	private String name;

	public String DATE_FORMAT = "EEE, yyyy-MM-dd hh:mm:ss.SSS";

	final long ONE_MINUTE_IN_MILLIS = 60000;

	public SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

	private Map<String, String> options;

	protected PacketContainer packet;

	public PacketProcessor(String name, Map<String, String> options) {
		if (options != null) {
			this.options = options;
		}
		this.name = name;
	}

	public void initialize(PacketContainer packet) {
		this.packet = packet;
	}

	public PacketContainer process() {
		PacketContainer container = processPacket();
		performAnalysis();
		return container;
	}

	public abstract PacketContainer processPacket();

	public abstract void performAnalysis();

	public String getName() {
		return name;
	}

	public Map<String, String> getOptions() {
		return options;
	}

	public boolean isAnalysisTimeExpired(long analysisInterval, Date analysisStartTime) throws ParseException {

		Date date = new Date();

		long currentDateInMS = date.getTime();

		long analysisStartDateInMS = analysisStartTime.getTime();

		long analysisEndDateinMS = analysisStartDateInMS + (analysisInterval * ONE_MINUTE_IN_MILLIS);

		if (currentDateInMS <= analysisEndDateinMS) {
			return false;
		} else {
			return true;
		}
	}

	public long getAnalysisIntervalInMinutes(long intervalTime, TimeUnit intervalUnit) {
		long analysisInterval = 0;

		if (intervalUnit == TimeUnit.HOURS) {
			analysisInterval = intervalTime * 60;
		} else if (intervalUnit == TimeUnit.DAYS) {
			analysisInterval = intervalTime * 60 * 24;
		} else {
			analysisInterval = intervalTime;
		}
		return analysisInterval;
	}
}
