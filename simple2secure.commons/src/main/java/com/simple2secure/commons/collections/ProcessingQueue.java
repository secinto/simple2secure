/**
 *********************************************************************
 *   simple2secure is a cyber risk and information security platform.
 *   Copyright (C) 2019  by secinto GmbH <https://secinto.com>
 *********************************************************************
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of the
 *   License, or (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
		processingQueue = new ArrayDeque<>();
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
