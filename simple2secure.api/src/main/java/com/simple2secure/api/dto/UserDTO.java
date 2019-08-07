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
import com.simple2secure.api.model.User;

public class UserDTO{

	private List<User> myUsersList;
	private List<CompanyGroup> myGroups;
	private User myProfile;


	public UserDTO(User user, List<User> myUsers, List<CompanyGroup> myGroups) {
		this.myProfile = user;
		this.myUsersList = myUsers;
		this.myGroups = myGroups;
	}

	public List<User> getMyUsersList() {
		return myUsersList;
	}

	public void setMyUsersList(List<User> myUsersList) {
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
}
