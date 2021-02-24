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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.simple2secure.api.dbo.GenericDBObject;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ContextUserAuthentication extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = -1144793004982781563L;

	private @NonNull String userId;

	@JsonSerialize(
			using = ToStringSerializer.class)
	private @NonNull ObjectId contextId;
	private boolean ownContext;
	
	private List<String> licenseIds;

	@Enumerated(EnumType.STRING)
	private UserRole userRole;

	public ContextUserAuthentication(String userId, ObjectId contextId, UserRole userRole, boolean ownContext) {
		this.userId = userId;
		this.contextId = contextId;
		this.userRole = userRole;
		this.ownContext = ownContext;
	}

}
