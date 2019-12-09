/*
 * Copyright (c) 2017 Secinto GmbH This software is the confidential and proprietary information of Secinto GmbH. All rights reserved.
 * Secinto GmbH and its affiliates make no representations or warranties about the suitability of the software, either express or implied,
 * including but not limited to the implied warranties of merchantability, fitness for a particular purpose, or non-infringement. NXP B.V.
 * and its affiliates shall not be liable for any damages suffered by licensee as a result of using, modifying or distributing this software
 * or its derivatives. This copyright notice must appear in all copies of this software.
 */

package com.simple2secure.api.dto;

import java.util.List;

import com.simple2secure.api.model.QueryCategory;
import com.simple2secure.api.model.QueryRun;

public class QueryDTO {

	private QueryCategory category;
	private List<QueryRun> queries;

	public QueryDTO() {

	}

	public QueryDTO(QueryCategory category, List<QueryRun> queries) {
		super();
		this.category = category;
		this.queries = queries;
	}

	public QueryCategory getCategory() {
		return category;
	}

	public void setCategory(QueryCategory category) {
		this.category = category;
	}

	public List<QueryRun> getQueries() {
		return queries;
	}

	public void setQueries(List<QueryRun> queries) {
		this.queries = queries;
	}

	
}
