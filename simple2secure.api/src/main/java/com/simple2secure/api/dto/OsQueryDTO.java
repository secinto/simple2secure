/*
 * Copyright (c) 2017 Secinto GmbH This software is the confidential and proprietary information of Secinto GmbH. All rights reserved.
 * Secinto GmbH and its affiliates make no representations or warranties about the suitability of the software, either express or implied,
 * including but not limited to the implied warranties of merchantability, fitness for a particular purpose, or non-infringement. NXP B.V.
 * and its affiliates shall not be liable for any damages suffered by licensee as a result of using, modifying or distributing this software
 * or its derivatives. This copyright notice must appear in all copies of this software.
 */

package com.simple2secure.api.dto;

import java.util.List;

import com.simple2secure.api.model.OsQueryCategory;
import com.simple2secure.api.model.OsQuery;

public class OsQueryDTO {

	private OsQueryCategory category;
	private List<OsQuery> queries;

	public OsQueryDTO() {

	}

	public OsQueryDTO(OsQueryCategory category, List<OsQuery> queries) {
		super();
		this.category = category;
		this.queries = queries;
	}

	public OsQueryCategory getCategory() {
		return category;
	}

	public void setCategory(OsQueryCategory category) {
		this.category = category;
	}

	public List<OsQuery> getQueries() {
		return queries;
	}

	public void setQueries(List<OsQuery> queries) {
		this.queries = queries;
	}

	
}
