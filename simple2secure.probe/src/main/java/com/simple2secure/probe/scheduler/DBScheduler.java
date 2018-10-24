package com.simple2secure.probe.scheduler;

import java.util.TimerTask;

import com.simple2secure.commons.collections.ProcessingQueue;

public class DBScheduler extends TimerTask {

	private ProcessingQueue<Object> dbProcessingQueue;

	public DBScheduler(ProcessingQueue<Object> dbProcessingQueue) {

	}

	@Override
	public void run() {
		// List<Object> objects = DBUtil.getInstance().getObjects();
		// if (objects.size() >= ARRAY_SIZE) {
		// for (int index = 0; index < objects.size(); index++) {
		// Object object = objects.get(index);
		// save(object);
		// removeObject(object);
		// }
		// }
	}

}
