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

import com.simple2secure.api.model.CompanyLicensePrivate;
import com.simple2secure.portal.dao.MongoRepository;

public abstract class LicenseRepository extends MongoRepository<CompanyLicensePrivate> {
	public abstract List<CompanyLicensePrivate> findByGroupId(String groupId);

	public abstract List<CompanyLicensePrivate> findByUserId(String userId);

	public abstract CompanyLicensePrivate findByProbeId(String probeId);

	public abstract CompanyLicensePrivate findByPodId(String podId);

	public abstract List<CompanyLicensePrivate> findByLicenseId(String licenseId);

	public abstract CompanyLicensePrivate findByLicenseIdAndProbeId(String licenseId, String probeId);

	public abstract CompanyLicensePrivate findByLicenseIdAndPodId(String licenseId, String podId);

	public abstract CompanyLicensePrivate findByLicenseAndHostname(String licenseId, String hostname);

	public abstract CompanyLicensePrivate findByAccessToken(String accessToken);

	public abstract CompanyLicensePrivate findByGroupAndUserId(String groupId, String userId);

	public abstract CompanyLicensePrivate findByHostname(String hostname);

	public abstract void deleteByGroupId(String groupId);

	public abstract void deleteByProbeId(String probeId);

	public abstract void deleteByPodId(String podId);

}
