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
package com.simple2secure.probe.logging;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import ch.qos.logback.core.OutputStreamAppender;

public class GUIAppender<E> extends OutputStreamAppender<E> {

	private static final DelegatingOutputStream DELEGATING_OUTPUT_STREAM = new DelegatingOutputStream(null);

	@Override
	public void start() {
		setOutputStream(DELEGATING_OUTPUT_STREAM);
		super.start();
	}

	public static void setStaticOutputStream(OutputStream outputStream) {
		DELEGATING_OUTPUT_STREAM.setOutputStream(outputStream);
	}

	private static class DelegatingOutputStream extends FilterOutputStream {

		public DelegatingOutputStream(OutputStream out) {
			super(new OutputStream() {
				@Override
				public void write(int b) throws IOException {
				}
			});
		}

		void setOutputStream(OutputStream outputStream) {
			out = outputStream;
		}
	}

}
