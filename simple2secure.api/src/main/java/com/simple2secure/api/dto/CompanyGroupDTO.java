package com.simple2secure.api.dto;

import com.simple2secure.api.model.CompanyGroup;

public class CompanyGroupDTO {
	
	private CompanyGroup group;
	
	private boolean owner;
	
	public CompanyGroupDTO(CompanyGroup group, boolean owner) {
		this.group = group;
		this.owner = owner;
	}

	public CompanyGroup getGroup() {
		return group;
	}

	public void setGroup(CompanyGroup group) {
		this.group = group;
	}

	public boolean isOwner() {
		return owner;
	}

	public void setOwner(boolean owner) {
		this.owner = owner;
	}
}
