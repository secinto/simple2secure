package com.simple2secure.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.ElementCollection;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;

import com.simple2secure.api.dbo.GenericDBObject;

public class User extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = -4145217947046921778L;

	private String firstName;
	private String lastName;
	private String username;
	private String email;
	private String password;
	private String address;
	private String city;
	private String zip;
	private String company;
	private String mobile;
	private String phone;
	private boolean enabled;
	private boolean passwordUpdated;
	
	@Enumerated(EnumType.STRING)
	private UserRole userRole;
	
	private String adminGroupId;
	
	@ElementCollection
	private List<String> myUsers = new ArrayList<>();

	@OneToMany
	private List<Probe> myProbes;

	private String activationToken;
	private boolean activated;
	private String passwordResetToken;
	private long passwordResetExpirationTime;

	public User() {

	}

	public User(String firstName, String lastName, String username, String email, String password, boolean enabled,
			List<Probe> myProbes, UserRole userRole, String activationToken, boolean activated, boolean passwordUpdated) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.username = username;
		this.email = email;
		this.password = password;
		this.enabled = enabled;
		this.myProbes = myProbes;
		this.activationToken = activationToken;
		this.activated = activated;
		this.passwordUpdated = passwordUpdated;
		this.myUsers = new ArrayList<String>();
		this.userRole = userRole;
	}
	
	public User(String email, UserRole userRole) {
		this.email = email;
		this.userRole = userRole;
	}

	/*public User(String uuid, String firstName, String lastName, String username, String email, String password, boolean enabled,
			String userRole, List<Probe> myProbes, String activationToken, boolean activated, boolean passwordUpdated) {
		this(uuid, firstName, lastName, username, email, password, enabled, myProbes, activationToken, activated, passwordUpdated);
		this.userRole = userRole;
		this.myUsers = new ArrayList<String>();
	}*/

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public List<Probe> getMyProbes() {
		return this.myProbes;
	}

	public void setMyProbes(List<Probe> myProbes) {
		this.myProbes = myProbes;
	}

	public void setActivated(boolean activate) {
		this.activated = activate;

	}

	public boolean isActivated() {
		return this.activated;
	}

	public void setActivationToken(String activationLink) {
		this.activationToken = activationLink;
	}

	public String getActivationToken() {
		return this.activationToken;
	}

	public void setPasswordResetExpirationTime(long timestamp) {
		this.passwordResetExpirationTime = timestamp;

	}

	public long getPasswordResetExpirationTime() {
		return this.passwordResetExpirationTime;
	}

	public void setPasswordResetToken(String token) {
		this.passwordResetToken = token;
	}

	public String getPasswordResetToken() {
		return this.passwordResetToken;
	}

	public boolean isPasswordUpdated() {
		return passwordUpdated;
	}

	public void setPasswordUpdated(boolean passwordUpdated) {
		this.passwordUpdated = passwordUpdated;
	}

	public List<String> getMyUsers() {
		return myUsers;
	}

	public void setMyUsers(List<String> myUsers) {
		this.myUsers = myUsers;
	}
	
	public void addMyUser(String userId) {
		this.myUsers.add(userId);
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public UserRole getUserRole() {
		return userRole;
	}

	public void setUserRole(UserRole userRole) {
		this.userRole = userRole;
	}

	public String getAdminGroupId() {
		return adminGroupId;
	}

	public void setAdminGroupId(String adminGroupId) {
		this.adminGroupId = adminGroupId;
	}
	
	
}
