package com.simple2secure.commons.collections;

import java.util.ArrayDeque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessingQueue<T> {
	private static Logger log = LoggerFactory.getLogger(ProcessingQueue.class);

	private ArrayDeque<T> processingQueue;

	public ProcessingQueue() {
		processingQueue = new ArrayDeque<T>();
	}

	/**
	 * Pushes a {@link PacketContainer} as last element onto the queue. This is a
	 * FIFO type of queue.
	 *
	 * @param packet
	 */
	public void push(T packet) {
		synchronized (processingQueue) {
			processingQueue.add(packet);
			processingQueue.notify();
		}
	}

	/**
	 * Obtains the first element of the queue, blocks until an element is available.
	 *
	 * @return
	 */
	public T pop() {
		synchronized (processingQueue) {
			while (processingQueue.isEmpty()) {
				try {
					processingQueue.wait();
				} catch (InterruptedException e) {
					log.error("Processing queue interrupted. Reason {}", e);
				}
			}
			return processingQueue.poll();
		}
	}

	/**
	 * Checks whether the queue has an element available.
	 *
	 * @return
	 */
	public synchronized boolean hasElement() {
		return !processingQueue.isEmpty();
	}

	public synchronized int size() {
		return processingQueue.size();
	}
}
