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
package com.simple2secure.service.interfaces;

public interface Engine {

	/**
	 * Returns the status of the engine if it is either running or stopped.
	 *
	 * @return
	 */
	public boolean isStopped();

	/**
	 * Blocking process which initializes and starts all required tasks, threads and processes and returns the status of the start operation
	 * afterwards. This can't be called from any of the scheduled tasks since they are itself controlled by this engine.
	 *
	 * @return
	 */
	public boolean start();

	/**
	 * Blocking process which stops and deletes all required tasks, threads and processes and returns the status of the stop operation
	 * afterwards. This can't be called from any of the scheduled tasks since they are itself controlled by this engine.
	 *
	 * @return
	 */
	public boolean stop();

	/**
	 * Returns the name of the engine.
	 *
	 * @return
	 */
	public String getName();
}
