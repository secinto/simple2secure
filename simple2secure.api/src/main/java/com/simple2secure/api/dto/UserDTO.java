/*
 * Copyright (c) 2017 Secinto GmbH This software is the confidential and proprietary information of Secinto GmbH. All rights reserved.
 * Secinto GmbH and its affiliates make no representations or warranties about the suitability of the software, either express or implied,
 * including but not limited to the implied warranties of merchantability, fitness for a particular purpose, or non-infringement. NXP B.V.
 * and its affiliates shall not be liable for any damages suffered by licensee as a result of using, modifying or distributing this software
 * or its derivatives. This copyright notice must appear in all copies of this software.
 */

package com.simple2secure.api.dto;

import java.util.List;

import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.Context;
import com.simple2secure.api.model.Probe;
import com.simple2secure.api.model.User;

public class UserDTO {

	private List<UserRoleDTO> myUsersList;
	private List<CompanyGroup> myGroups;
	private List<Probe> myProbes;
	private List<Context> myContexts;
	private User myProfile;

	public UserDTO(User user, List<UserRoleDTO> myUsers, List<CompanyGroup> myGroups, List<Probe> myProbes, List<Context> myContexts) {
		myProfile = user;
		myUsersList = myUsers;
		this.myGroups = myGroups;
		this.myProbes = myProbes;
		this.myContexts = myContexts;
	}

	public List<UserRoleDTO> getMyUsersList() {
		return myUsersList;
	}

	public void setMyUsersList(List<UserRoleDTO> myUsersList) {
		this.myUsersList = myUsersList;
	}

	public User getMyProfile() {
		return myProfile;
	}

	public void setMyProfile(User myProfile) {
		this.myProfile = myProfile;
	}

	public List<CompanyGroup> getMyGroups() {
		return myGroups;
	}

	public void setMyGroups(List<CompanyGroup> myGroups) {
		this.myGroups = myGroups;
	}

	public List<Probe> getMyProbes() {
		return myProbes;
	}

	public void setMyProbes(List<Probe> myProbes) {
		this.myProbes = myProbes;
	}

	public List<Context> getMyContexts() {
		return myContexts;
	}

	public void setMyContexts(List<Context> myContexts) {
		this.myContexts = myContexts;
	}
}
