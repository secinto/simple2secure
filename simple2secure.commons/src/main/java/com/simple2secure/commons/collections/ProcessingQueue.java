/**
 *********************************************************************
 *
 * Copyright (C) 2019 by secinto GmbH (http://www.secinto.com)
 *
 *********************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 *
 *********************************************************************
 */
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
	 * Pushes a {@link PacketContainer} as last element onto the queue. This is a FIFO type of queue.
	 *
	 * @param packet
	 */
	public void push(T packet) {
		synchronized (processingQueue) {
			processingQueue.add(packet);
			processingQueue.notifyAll();
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
					Thread.currentThread().interrupt();
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
