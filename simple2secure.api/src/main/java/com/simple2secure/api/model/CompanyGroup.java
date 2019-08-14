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

import com.simple2secure.api.dbo.GenericDBObject;

public class CompanyGroup extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = 8637103644388176110L;

	private String parentId;
	private String name;
	private String contextId;

	List<String> childrenIds = new ArrayList<>();

	List<CompanyGroup> children = new ArrayList<>();

	private boolean rootGroup;

	private boolean standardGroup = false;

	public CompanyGroup() {
	}

	public CompanyGroup(String name, List<String> childrenIds) {
		this.name = name;
		this.childrenIds = childrenIds;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getChildrenIds() {
		return childrenIds;
	}

	public void addChildrenId(String companyGroupId) {
		childrenIds.add(companyGroupId);
	}

	public void addChildren(CompanyGroup child) {
		children.add(child);
	}

	public void removeChildrenId(String companyGroupId) {
		childrenIds.remove(companyGroupId);
	}

	public void removeChild(CompanyGroup child) {
		children.remove(child);
	}

	public String getContextId() {
		return contextId;
	}

	public void setContextId(String contextId) {
		this.contextId = contextId;
	}

	public boolean isRootGroup() {
		return rootGroup;
	}

	public void setRootGroup(boolean rootGroup) {
		this.rootGroup = rootGroup;
	}

	public void setChildrenIds(List<String> children) {
		childrenIds = children;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public List<CompanyGroup> getChildren() {
		return children;
	}

	public void setChildren(List<CompanyGroup> children) {
		this.children = children;
	}

	public boolean isStandardGroup() {
		return standardGroup;
	}

	public void setStandardGroup(boolean standardGroup) {
		this.standardGroup = standardGroup;
	}
}
