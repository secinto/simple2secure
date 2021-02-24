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
import java.util.Map;

import org.bson.types.ObjectId;

import com.simple2secure.api.model.CompanyLicensePrivate;
import com.simple2secure.portal.dao.MongoRepository;

public abstract class LicenseRepository extends MongoRepository<CompanyLicensePrivate> {
	public abstract List<CompanyLicensePrivate> findAllByGroupId(ObjectId groupId);

	public abstract List<CompanyLicensePrivate> findByUserId(ObjectId userId);

	public abstract CompanyLicensePrivate findByDeviceId(ObjectId deviceId);

	public abstract List<CompanyLicensePrivate> findByDeviceStatusOnline();

	public abstract List<CompanyLicensePrivate> findByLicenseId(ObjectId licenseId);

	public abstract CompanyLicensePrivate findByLicenseIdAndDeviceId(ObjectId licenseId, ObjectId deviceId);

	public abstract CompanyLicensePrivate findByLicenseAndHostname(ObjectId licenseId, String hostname);

	public abstract CompanyLicensePrivate findByAccessToken(String accessToken);

	public abstract CompanyLicensePrivate findByGroupAndUserId(ObjectId groupId, ObjectId userId);

	public abstract CompanyLicensePrivate findByHostname(String hostname);

	public abstract void deleteByGroupId(ObjectId groupId);

	public abstract void deleteByDeviceId(ObjectId deviceId);

	public abstract Map<String, Object> findByGroupIdsPaged(List<ObjectId> groupIds, int page, int size);

	public abstract List<CompanyLicensePrivate> findByGroupIds(List<ObjectId> groupIds);

	public abstract Map<String, Object> getDevicesByGroupIdPagination(ObjectId groupId, int page, int size, String filter);

}
