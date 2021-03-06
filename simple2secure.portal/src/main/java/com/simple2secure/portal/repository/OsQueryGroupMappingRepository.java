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
package com.simple2secure.portal.repository;

import java.util.List;

import org.bson.types.ObjectId;

import com.simple2secure.api.model.OsQueryGroupMapping;
import com.simple2secure.portal.dao.MongoRepository;

public abstract class OsQueryGroupMappingRepository extends MongoRepository<OsQueryGroupMapping> {

	public abstract List<OsQueryGroupMapping> findByGroupId(ObjectId groupId);

	public abstract List<OsQueryGroupMapping> findByQueryId(ObjectId queryId);

	public abstract void deleteByGroupId(ObjectId groupId);

	public abstract void deleteByQueryId(ObjectId queryId);

	public abstract List<OsQueryGroupMapping> findByGroupIdAndOSInfo(ObjectId groupId, List<Integer> possibleValues);

	public abstract List<OsQueryGroupMapping> getAllMapingsByGroupIds(List<ObjectId> group_ids, List<Integer> possibleValues);

}
