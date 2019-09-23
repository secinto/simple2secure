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

package com.simple2secure.api.model;

import com.simple2secure.api.dbo.GenericDBObject;

public class Notification extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = 3081134740537811621L;
	private String content;
	private String contextId;
	private boolean read;
	private long timestamp;

	public Notification() {
	}

	public Notification(String content, boolean read, String contextId) {
		super();
		this.content = content;
		this.contextId = contextId;
		this.read = read;
	}

	public Notification(String content, boolean read, String contextId, long timestamp) {
		super();
		this.content = content;
		this.contextId = contextId;
		this.read = read;
		this.timestamp = timestamp;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	public String getContextId() {
		return contextId;
	}

	public void setContextId(String contextId) {
		this.contextId = contextId;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}
