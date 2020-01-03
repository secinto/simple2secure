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

import java.util.List;

import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.Context;
import com.simple2secure.api.model.Device;
import com.simple2secure.api.model.UserInfo;

public class UserDTO {

	private List<UserRoleDTO> myUsersList;
	private List<CompanyGroup> myGroups;
	private List<Context> myContexts;
	private List<String> assignedGroups;
	private UserInfo myProfile;

	public UserDTO(UserInfo user, List<UserRoleDTO> myUsers, List<CompanyGroup> myGroups, List<Context> myContexts,
			List<String> assignedGroups) {
		myProfile = user;
		myUsersList = myUsers;
		this.myGroups = myGroups;
		this.myContexts = myContexts;
		this.assignedGroups = assignedGroups;
	}

	public List<UserRoleDTO> getMyUsersList() {
		return myUsersList;
	}

	public void setMyUsersList(List<UserRoleDTO> myUsersList) {
		this.myUsersList = myUsersList;
	}

	public UserInfo getMyProfile() {
		return myProfile;
	}

	public void setMyProfile(UserInfo myProfile) {
		this.myProfile = myProfile;
	}

	public List<CompanyGroup> getMyGroups() {
		return myGroups;
	}

	public void setMyGroups(List<CompanyGroup> myGroups) {
		this.myGroups = myGroups;
	}

	public List<Context> getMyContexts() {
		return myContexts;
	}

	public void setMyContexts(List<Context> myContexts) {
		this.myContexts = myContexts;
	}

	public List<String> getAssignedGroups() {
		return assignedGroups;
	}

	public void setAssignedGroups(List<String> assignedGroups) {
		this.assignedGroups = assignedGroups;
	}
}
