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

import java.util.concurrent.TimeUnit;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TestObjWeb {

	@JsonSerialize(
			using = ToStringSerializer.class)
	private ObjectId podId;

	@JsonSerialize(
			using = ToStringSerializer.class)
	private ObjectId testId;

	private String hostname;
	private boolean active;
	private String name;
	private TestContent test_content;
	private boolean scheduled;
	private long scheduledTime;
	private TimeUnit scheduledTimeUnit;

	public TestObjWeb(String name, TestContent test_content) {
		super();
		this.name = name;
		this.test_content = test_content;
	}
}
