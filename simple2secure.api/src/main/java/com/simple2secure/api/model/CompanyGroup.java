package com.simple2secure.api.model;

import java.util.ArrayList;
import java.util.List;

import com.simple2secure.api.dbo.GenericDBObject;

public class CompanyGroup extends GenericDBObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8637103644388176110L;
	
	private String parentId;
	private String name;
	private String adminGroupId;
		
	List<String> childrenIds = new ArrayList<>();
	
	List<CompanyGroup> children = new ArrayList<>();
	
	private boolean rootGroup;
	
	private boolean standardGroup = false;
	
	private List<String> superUserIds = new ArrayList<>();
	
	public CompanyGroup() {}
	
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
		this.childrenIds.add(companyGroupId);
	}
	
	public void addChildren(CompanyGroup child) {
		this.children.add(child);
	}
	
	public void removeChildrenId(String companyGroupId) {
		this.childrenIds.remove(companyGroupId);
	}
	
	public void removeChild(CompanyGroup child) {
		this.children.remove(child);
	}

	public String getAdminGroupId() {
		return adminGroupId;
	}

	public void setAdminGroupId(String adminGroupId) {
		this.adminGroupId = adminGroupId;
	}

	public boolean isRootGroup() {
		return rootGroup;
	}

	public void setRootGroup(boolean rootGroup) {
		this.rootGroup = rootGroup;
	}

	public void setChildrenIds(List<String> children) {
		this.childrenIds = children;
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

	public List<String> getSuperUserIds() {
		return superUserIds;
	}

	public void setSuperUserIds(List<String> superUserIds) {
		this.superUserIds = superUserIds;
	}
	
	public void addSuperUserId(String superUserId) {
		this.superUserIds.add(superUserId);
	}
	
	public void removeSuperUserId(String superUserId) {
		this.superUserIds.remove(superUserId);
	}
}
