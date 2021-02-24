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

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;

import com.simple2secure.api.dto.OsQueryReportDTO;
import com.simple2secure.api.model.OsQueryReport;
import com.simple2secure.portal.dao.MongoRepository;

public abstract class OsQueryReportRepository extends MongoRepository<OsQueryReport> {
	public abstract List<OsQueryReport> getReportsByDeviceId(ObjectId deviceId);

	public abstract List<OsQueryReport> getReportsByName(String name, int page, int size);

	public abstract List<OsQueryReport> getReportsByDeviceAndName(ObjectId deviceId, String name, int page, int size);

	public abstract List<OsQueryReport> getLastReportsFromTimeStampAndName(Date timestamp, String name);

	public abstract long getPagesForReportsByName(String name);

	public abstract long getPagesForReportsByDeviceAndName(ObjectId deviceId, String name);

	public abstract OsQueryReportDTO getReportsByDeviceIdWithPagination(List<ObjectId> deviceIds, int page, int size, String filter);

	public abstract void deleteByDeviceId(ObjectId deviceId);

	public abstract List<OsQueryReport> getSearchQueryByDeviceIds(String searchQuery, List<ObjectId> deviceIds);

	public abstract List<OsQueryReport> getReportsByDeviceId(List<ObjectId> deviceIds);
}
