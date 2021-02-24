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

package com.simple2secure.api.dto;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.simple2secure.api.model.RuleFactType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RuleMappingDTO {
	
	@JsonSerialize(using=ToStringSerializer.class)
	private ObjectId contextId;
	
	@JsonSerialize(using=ToStringSerializer.class)
	private ObjectId ruleId;
	/*
	 * mappedObjectIds is a list of ObjectsIds which can be interpreted by the ruleFactType
	 * e.g. if ruleFactType is a EMAIL the mappedObjectIs are emailConfigurationsIds,
	 * e.g. if ruleFactType is a OSQUERYRESULT the mappedObjectIds are probeIds
	 */
	@JsonSerialize(using=ToStringSerializer.class)
	private List<ObjectId> mappedObjectIds = new ArrayList<>();
	
	@JsonSerialize(using=ToStringSerializer.class)
	private RuleFactType ruleFactType;

}
