package com.simple2secure.portal.controller;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.google.common.base.Strings;
import com.simple2secure.api.config.ConfigItems;
import com.simple2secure.api.dto.CompanyGroupDTO;
import com.simple2secure.api.dto.UserDTO;
import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.CompanyLicense;
import com.simple2secure.api.model.Probe;
import com.simple2secure.api.model.User;
import com.simple2secure.api.model.UserRole;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.repository.ConfigRepository;
import com.simple2secure.portal.repository.DeviceRepository;
import com.simple2secure.portal.repository.EmailConfigurationRepository;
import com.simple2secure.portal.repository.EmailRepository;
import com.simple2secure.portal.repository.GroupRepository;
import com.simple2secure.portal.repository.LicenseRepository;
import com.simple2secure.portal.repository.NetworkReportRepository;
import com.simple2secure.portal.repository.NotificationRepository;
import com.simple2secure.portal.repository.ProcessorRepository;
import com.simple2secure.portal.repository.QueryRepository;
import com.simple2secure.portal.repository.ReportRepository;
import com.simple2secure.portal.repository.RuleRepository;
import com.simple2secure.portal.repository.StepRepository;
import com.simple2secure.portal.repository.TokenRepository;
import com.simple2secure.portal.repository.ToolRepository;
import com.simple2secure.portal.repository.UserRepository;
import com.simple2secure.portal.security.PasswordValidator;
import com.simple2secure.portal.service.MessageByLocaleService;
import com.simple2secure.portal.utils.DataInitialization;
import com.simple2secure.portal.utils.PortalUtils;

@RestController
public class UserController {

	@Autowired
	UserRepository userRepository;

	@Autowired
	ConfigRepository configRepository;

	@Autowired
	QueryRepository queryRepository;

	@Autowired
	StepRepository stepRepository;

	@Autowired
	ToolRepository toolRepository;

	@Autowired
	ProcessorRepository processorRepository;

	@Autowired
	EmailConfigurationRepository emailConfigRepository;

	@Autowired
	EmailRepository emailRepository;

	@Autowired
	DeviceRepository deviceRepository;

	@Autowired
	GroupRepository groupRepository;

	@Autowired
	LicenseRepository licenseRepository;
	
	@Autowired
	ReportRepository reportRepository;
	
	@Autowired
	NetworkReportRepository networkReportRepository;
	
	@Autowired
	NotificationRepository notificationRepository;
	
	@Autowired
	RuleRepository ruleRepository;
	
	@Autowired
	TokenRepository tokenRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	PasswordValidator passwordValidator;

	@Autowired
	MessageByLocaleService messageByLocaleService;

	protected static SecureRandom random = new SecureRandom();

	RestTemplate restTemplate = new RestTemplate();

	public static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");
	public static final Charset UTF_8 = Charset.forName("UTF-8");
	static final Logger log = LoggerFactory.getLogger(UserController.class);

	/**
	 * This function is used to add new user. For each user added it sets the
	 * configuration, queries, processors and steps from the default configuration
	 * .json files which are placed on the server
	 * 
	 * @throws ItemNotFoundRepositoryException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	@RequestMapping(value = "/api/users/{type}", method = RequestMethod.POST)
	public ResponseEntity<User> insertUser(@RequestBody User user, @PathVariable("type") String type,
			@RequestHeader("Accept-Language") String locale) throws ItemNotFoundRepositoryException, IOException, URISyntaxException {

		return addNewUser(user, type, locale);
	}

	/**
	 *
	 * @param user
	 * @return
	 * @throws ItemNotFoundRepositoryException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	@RequestMapping(value = "/api/register/{type}", method = RequestMethod.POST, consumes = "application/json")
	public ResponseEntity<User> registerUser(@RequestBody User user, @PathVariable("type") String type,
			@RequestHeader("Accept-Language") String locale) throws ItemNotFoundRepositoryException, IOException, URISyntaxException {

		return addNewUser(user, type, locale);
	}

	/**
	 *
	 * @param user
	 * @return
	 * @throws ItemNotFoundRepositoryException
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/api/users/activate/updatePassword/{authenticationToken}", method = RequestMethod.POST)
	public ResponseEntity<User> updateUserPasswordFirstLogin(@PathVariable("authenticationToken") String authenticationToken,
			@RequestBody String password, @RequestHeader("Accept-Language") String locale)
			throws ItemNotFoundRepositoryException, URISyntaxException, IOException {

		if (!Strings.isNullOrEmpty(password) && !Strings.isNullOrEmpty(authenticationToken)) {
			User user = userRepository.findByActivationToken(authenticationToken);

			if (user != null) {
				user.setActivated(true);
				user.setPassword(password);
				return addNewUser(user, ConfigItems.type_password_update, locale);
			} else {
				return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_user_not_found", locale)),
						HttpStatus.NOT_FOUND);
			}

		} else {
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("unknown_error_occured", locale)),
					HttpStatus.NOT_FOUND);
		}

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/api/users/activate/{activationToken}", method = RequestMethod.GET)
	public ResponseEntity<InputStreamResource> activateUser(@PathVariable("activationToken") String activationToken,
			@RequestHeader("Accept-Language") String locale) throws ItemNotFoundRepositoryException, URISyntaxException, IOException {
		User user = userRepository.findByActivationToken(activationToken);

		if (user != null) {

			if (!user.isActivated()) {
				if (user.isPasswordUpdated()) {
					user.setActivated(true);
					userRepository.update(user);
				}

				URI url = new URI(ConfigItems.BASE_URL_WEB + "/#/account/activate/" + activationToken);

				if (!user.isPasswordUpdated()) {
					url = new URI(ConfigItems.BASE_URL_WEB + "/#/account/updatePassword/" + activationToken);
				}

				HttpHeaders httpHeaders = new HttpHeaders();
				httpHeaders.setLocation(url);
				return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
			} else {
				URI url = new URI(ConfigItems.BASE_URL_WEB + "/#/account/activated/");
				HttpHeaders httpHeaders = new HttpHeaders();
				httpHeaders.setLocation(url);
				return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
			}

		} else {
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("activation_token_not_valid", locale)),
					HttpStatus.NOT_FOUND);
		}
	}

	/**
	 *
	 * @param user
	 * @return
	 * @throws ItemNotFoundRepositoryException
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/api/users/sendResetPasswordEmail", method = RequestMethod.POST)
	public ResponseEntity<User> sendResetPasswordEmail(@RequestBody String email, @RequestHeader("Accept-Language") String locale)
			throws ItemNotFoundRepositoryException, IOException {

		if (!Strings.isNullOrEmpty(email)) {
			User user = userRepository.findByEmailOnlyActivated(email);
			if (user != null) {
				user.setPasswordResetToken(PortalUtils.generateToken());
				user.setPasswordResetExpirationTime(System.currentTimeMillis() + ConfigItems.expiration_time_password_reset);

				userRepository.update(user);

				// Add template for the email content and retrieve it from the database!
				String emailContent = "In order to change your password please click on the link below. \n\n Password reset link: "
						+ ConfigItems.BASE_URL + "/api/users/resetPassword/" + user.getPasswordResetToken();

				if (PortalUtils.sendEmail(user, emailContent, ConfigItems.email_subject_pr)) {
					return new ResponseEntity<User>(user, HttpStatus.OK);
				} else {
					return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("error_while_sending_email", locale)),
							HttpStatus.NOT_FOUND);
				}
			} else {
				return new ResponseEntity(
						new CustomErrorType(messageByLocaleService.getMessage("user_with_provided_email_not_exists", locale)),
						HttpStatus.NOT_FOUND);
			}
		} else {
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("unknown_error_occured", locale)),
					HttpStatus.NOT_FOUND);
		}
	}

	/**
	 *
	 * @param user
	 * @return
	 * @throws URISyntaxException
	 */
	@RequestMapping(value = "/api/users/resetPassword/{token}", method = RequestMethod.GET)
	public ResponseEntity<User> showChangePasswordPage(@PathVariable("token") String token, @RequestHeader("Accept-Language") String locale)
			throws URISyntaxException {
		URI url = new URI(ConfigItems.BASE_URL_WEB + "/#/resetPassword/" + token);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setLocation(url);
		return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
	}

	/**
	 *
	 * @param user
	 * @return
	 * @throws ItemNotFoundRepositoryException
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/api/users/updatePassword/{token}", method = RequestMethod.POST)
	public ResponseEntity<User> updateUserPassword(@PathVariable("token") String token, @RequestBody String password,
			@RequestHeader("Accept-Language") String locale) throws ItemNotFoundRepositoryException, URISyntaxException, IOException {

		if (!Strings.isNullOrEmpty(password) && !Strings.isNullOrEmpty(token)) {
			User user = userRepository.findByPasswordResetToken(token);

			if (user != null) {
				if (user.getPasswordResetExpirationTime() >= System.currentTimeMillis()) {

					if (user.getPassword().equals(passwordEncoder.encode(password))) {
						return new ResponseEntity(
								new CustomErrorType(messageByLocaleService.getMessage("password_must_be_different", locale)),
								HttpStatus.NOT_FOUND);
					}

					user.setPassword(passwordEncoder.encode(password));
					user.setPasswordResetToken(token);
					userRepository.update(user);

					String emailContent = messageByLocaleService.getMessage("password_changed_email_content", locale);
					PortalUtils.sendEmail(user, emailContent, ConfigItems.email_subjct_pcs);

					URI url = new URI(ConfigItems.BASE_URL_WEB);
					HttpHeaders httpHeaders = new HttpHeaders();
					httpHeaders.setLocation(url);
					return new ResponseEntity<>(httpHeaders, HttpStatus.OK);
				} else {
					return new ResponseEntity(
							new CustomErrorType(messageByLocaleService.getMessage("password_reset_token_expired", locale)),
							HttpStatus.NOT_FOUND);
				}
			} else {
				return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_token_already_used", locale)),
						HttpStatus.NOT_FOUND);
			}

		} else {
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("unknown_error_occured", locale)),
					HttpStatus.NOT_FOUND);
		}

	}

	/**
	 * This function returns all users from the user repository
	 */
	@RequestMapping(value = "/api/users", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<List<User>> getUsers(@RequestHeader("Accept-Language") String locale) {
		List<User> userList = userRepository.findAll();
		return new ResponseEntity<List<User>>(userList, HttpStatus.OK);
	}

	/**
	 * This function finds and returns user according to the user id
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/api/users/{id}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<UserDTO> getUserByID(@PathVariable("id") String id, @RequestHeader("Accept-Language") String locale) {

		User user = userRepository.findByUserID(id);
		List<User> myUsers = new ArrayList<>();
		if (user != null) {

			/* Set user users from the userRepository by the addedByUserId */
			List<String> myUserIds = user.getMyUsers();
			if (myUserIds == null) {
				myUserIds = new ArrayList<>();
			} else {
				for (String userId : myUserIds) {
					User myUser = userRepository.findByUserID(userId);
					if (myUser != null) {
						myUsers.add(myUser);
					}

				}
			}

			/* Set user groups from the groupRepository */
			List<CompanyGroup> myGroups = groupRepository.findByOwnerId(id);
			
			if(myGroups == null) {
				myGroups = new ArrayList<>();
			}
			
			if(!Strings.isNullOrEmpty(user.getGroupId())) {
				CompanyGroup group = groupRepository.find(user.getGroupId());
				if(group != null) {
					myGroups.add(group);
				}
			}
			
			/* Set user probes from the licenses */
			List<CompanyLicense> licenses = licenseRepository.findByUserId(id);
			List<Probe> myProbes = new ArrayList<Probe>();
			if (licenses != null) {
				for (CompanyLicense license : licenses) {
					//Retrieve only activated probes
					if(license.isActivated()) {
						CompanyGroup group = groupRepository.find(license.getGroupId());
						if(group != null) {						
							Probe probe = new Probe(license.getProbeId(), group.getName(), license.isActivated());
							myProbes.add(probe);	
						}
					}
				}
			}

			user.setMyProbes(myProbes);

			UserDTO userDTO = new UserDTO(user, myUsers, myGroups);
			return new ResponseEntity<UserDTO>(userDTO, HttpStatus.OK);
		}

		else {
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_user_not_found", locale)),
					HttpStatus.NOT_FOUND);
		}

	}

	/**
	 * This function deletes configuration and user according to the user id
	 *
	 * @param userId
	 * @return
	 * @throws ItemNotFoundRepositoryException 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/api/users/{id}", method = RequestMethod.DELETE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<User> deleteUser(@PathVariable("id") String userId, @RequestHeader("Accept-Language") String locale) 
			throws ItemNotFoundRepositoryException {
		boolean hasPrivilegedUser = false;
		if(!Strings.isNullOrEmpty(userId)) {
			
			User user = userRepository.find(userId);
			
			if(user != null) {
				//If user added users check if contains privileged(ADMIN or SUPERUSER)
				if(user.getMyUsers() != null) {
					hasPrivilegedUser = containsPrivilegedUser(user.getMyUsers());
					
					//If user contains privileged user(ADMIN or SUPERUSER) - copy groups and own users to one of them
					if(hasPrivilegedUser) {
						User privilegedUser = getPrivilegedUser(user.getMyUsers());
						
						if(privilegedUser != null) {
							privilegedUser = copyMyUsersToPrivilegedUser(privilegedUser, user.getMyUsers());
							copyMyGroupsToPrivilegedUser(privilegedUser, user);
							userRepository.update(privilegedUser);
							User addedByUser = userRepository.findAddedByUser(user.getId());
							if(addedByUser != null) {
								updateAddedByUser(addedByUser, user.getId(), privilegedUser.getId());
							}
							deleteUserDependencies(user, false, false);
						}					
					}
					else {
						deleteUserDependencies(user, true, true);
					}
				}
				else {
					deleteUserDependencies(user, true, true);
				}
				
				userRepository.delete(user);
				
				return new ResponseEntity<User>(user, HttpStatus.OK);			
			}
			else {
				return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_deleting_user", locale)),
						HttpStatus.NOT_FOUND);				
			}
		}
		else {			
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_deleting_user", locale)),
					HttpStatus.NOT_FOUND);
		}
	}
	
	
	/**
	 * This function updates the myUsers List for the user which added the user which is currently being deleted and
	 * new privileged user will be added to this list
	 * @param addedByUser
	 * @param oldUserId
	 * @param newUserId
	 */
	private void updateAddedByUser(User addedByUser, String oldUserId, String newUserId) {
		if(addedByUser != null) {
			if(addedByUser.getMyUsers() != null) {
				addedByUser.getMyUsers().remove(oldUserId);
				if(!Strings.isNullOrEmpty(newUserId)) {
					addedByUser.addMyUser(newUserId);
				}
				try {
					userRepository.update(addedByUser);
				} catch (ItemNotFoundRepositoryException e) {
					log.error("User not found");
				}
			}
		}
	}
	
	/**
	 * This function copies the groups from the user which is being deleted to the privileged one
	 * @param privilegedUser
	 * @param user
	 */
	private void copyMyGroupsToPrivilegedUser(User privilegedUser, User user) {
		List<CompanyGroup> groups = groupRepository.findByOwnerId(user.getId());
		
		if(groups != null) {
			for(CompanyGroup group : groups) {
				group.setAddedByUserId(privilegedUser.getId());
				group.setOwner(privilegedUser.getEmail());
				try {
					groupRepository.update(group);
				} catch (ItemNotFoundRepositoryException e) {
					log.error("Group not found");
				}
			}
		}
	}
	
	/**
	 * This function copies the added users from the user which is being deleted to the privileged one
	 * @param privilegedUser
	 * @param myUsers
	 * @return
	 */
	private User copyMyUsersToPrivilegedUser(User privilegedUser, List<String> myUsers) {
		for(String myUser : myUsers) {
			if(!myUser.equals(privilegedUser.getId())) {
				privilegedUser.addMyUser(myUser);
			}
		}
		return privilegedUser;
	}
	
	/**
	 * This function returns the privileged user from the list of myUsers
	 * @param myUsers
	 * @return
	 */
	private User getPrivilegedUser(List<String> myUsers) {
		User adminUser = null;
		User superUser = null;
		if(myUsers != null) {
			for(String myUserId : myUsers) {
				User myUser = userRepository.find(myUserId);
				if(myUser != null) {
					if(myUser.getUserRole().equals(UserRole.ADMIN)) {
						adminUser = myUser;
					}
					else if(myUser.getUserRole().equals(UserRole.SUPERUSER)) {
						superUser = myUser;
					}
				}
			}
			
			if(adminUser != null) {
				return adminUser;
			}
			else if(superUser != null) {
				return superUser;
			}
			else {
				return null;
			}
		}
		return null;
	}
	
	/**
	 * This function checks if the user which is being deleted contains a privileged user (ADMIN or SUPERUSER)
	 * @param myUsers
	 * @return
	 */
	private boolean containsPrivilegedUser(List<String> myUsers) {
		if(myUsers != null) {
			for(String myUserId : myUsers) {
				User myUser = userRepository.find(myUserId);
				if(myUser != null) {
					if(myUser.getUserRole().equals(UserRole.ADMIN) || myUser.getUserRole().equals(UserRole.SUPERUSER)) {
						return true;
					}
				}
				
			}
		}
		return false;
	}
	
	/**
	 * This function deletes all user dependencies. Flag deleteMyUsers is used to distinguish if the
	 * users from the myUsers list should be deleted or not
	 * @param user
	 * @param deleteMyUsers
	 */
	private void deleteUserDependencies(User user, boolean deleteMyUsers, boolean updateMyUsers) {
		
		if(deleteMyUsers) {
			if(user.getMyUsers() != null) {
				for(String myUserId : user.getMyUsers()) {
					User myUser = userRepository.find(myUserId);
					
					if(myUser != null) {
						deleteUserDependencies(user, false, false);
					}
				}
			}		
		}
		
		if(updateMyUsers) {
			User addedByUser = userRepository.findAddedByUser(user.getId());
			if(addedByUser != null) {
				updateAddedByUser(addedByUser, user.getId(), null);
			}
		}
		
		List<CompanyGroup> groups = groupRepository.findByOwnerId(user.getId());
		
		if(groups != null) {
			for(CompanyGroup group : groups) {
				licenseRepository.deleteByGroupId(group.getId());
				groupRepository.delete(group);
			}
		}
		ruleRepository.deleteByUserId(user.getId());
		toolRepository.deleteByUserID(user.getId());
		emailConfigRepository.deleteByUserId(user.getId());
		emailRepository.deleteByUserId(user.getId());
		reportRepository.deleteByUserId(user.getId());
		networkReportRepository.deleteByUserId(user.getId());
		notificationRepository.deleteByUserId(user.getId());
		tokenRepository.deleteByUserId(user.getId());
	}

	/**
	 * This function returns all devices according to the user id
	 */
	@RequestMapping(value = "/api/users/devices/{userID}")
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<Probe>> getProbesByUserID(@PathVariable("userID") String userId,
			@RequestHeader("Accept-Language") String locale) {

		List<Probe> probes = new ArrayList<Probe>();

		List<CompanyLicense> licenses = licenseRepository.findByUserId(userId);

		if (licenses != null) {
			for (CompanyLicense license : licenses) {
				//Retrieve only activated probes
				if(license.isActivated()) {
					CompanyGroup group = groupRepository.find(license.getGroupId());
					if(group != null) {
						Probe probe = new Probe(license.getProbeId(), group.getName(), license.isActivated());
						probes.add(probe);					
					}					
				}

			}
		}
		return new ResponseEntity<List<Probe>>(probes, HttpStatus.OK);
	}

	/**
	 * This function returns all groups according to the user id
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/api/users/groups/{userID}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<CompanyGroupDTO>> getGroupsByUserID(@PathVariable("userID") String userId,
			@RequestHeader("Accept-Language") String locale) {

		if(!Strings.isNullOrEmpty(userId)) {
			
			List<CompanyGroupDTO> groupDTO = new ArrayList<>();
			
			List<CompanyGroup> groups = groupRepository.findByOwnerId(userId);
			
			if(groups != null) {
				for(CompanyGroup group : groups) {
					groupDTO.add(new CompanyGroupDTO(group, true));
				}
			}
			
			User user = userRepository.find(userId);
			
			if(user != null) {
				if(!Strings.isNullOrEmpty(user.getGroupId())) {
					CompanyGroup group = groupRepository.find(user.getGroupId());
					if(group != null) {
						groupDTO.add(new CompanyGroupDTO(group, false));
					}
				}
			}

			return new ResponseEntity<List<CompanyGroupDTO>>(groupDTO, HttpStatus.OK);
		}
		else {
			return new ResponseEntity(
					new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_retrieving_group", locale)),
					HttpStatus.NOT_FOUND);			
		}

	}

	/**
	 * This function add new group to the group repository
	 * 
	 * @param group
	 * @param locale
	 * @return
	 * @throws ItemNotFoundRepositoryException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/api/users/group", method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<CompanyGroup> addGroup(@RequestBody CompanyGroup group, @RequestHeader("Accept-Language") String locale)
			throws ItemNotFoundRepositoryException {
		if (group != null) {
			User user = userRepository.findByUserID(group.getAddedByUserId());
			if (Strings.isNullOrEmpty(group.getId())) {			
				if(user != null) {
					group.setOwner(user.getEmail());
					ObjectId groupId = groupRepository.saveAndReturnId(group);
					
					if(Strings.isNullOrEmpty(groupId.toString())) {
						return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_saving_group", locale)),
								HttpStatus.NOT_FOUND);
					}
					else {
						DataInitialization.addDefaultGroupConfiguration(groupId.toString());
						DataInitialization.addDefaultGroupQueries(groupId.toString());
						DataInitialization.addDefaultGroupProcessors(groupId.toString());
						DataInitialization.addDefaultGroupSteps(groupId.toString());
					}
					
				}
				else {
					return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_saving_group", locale)),
							HttpStatus.NOT_FOUND);
				}
				
			} else {
				if(user != null) {
					if(Strings.isNullOrEmpty(group.getOwner())) {
						group.setOwner(user.getEmail());
					}
				}
				groupRepository.update(group);
			}

			return new ResponseEntity<CompanyGroup>(group, HttpStatus.OK);
		} else {
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_saving_group", locale)),
					HttpStatus.NOT_FOUND);
		}
	}	

	/**
	 * This function returns all users from the user repository
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/api/users/group/{groupID}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<CompanyGroup> getGroup(@PathVariable("groupID") String groupId, @RequestHeader("Accept-Language") String locale) {
		CompanyGroup group = groupRepository.find(groupId);
		if (group == null) {
			return new ResponseEntity(
					new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_retrieving_group", locale)),
					HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<CompanyGroup>(group, HttpStatus.OK);
		}

	}

	/**
	 * This function returns all users from the user repository
	 */
	@RequestMapping(value = "/api/users/group/{groupID}", method = RequestMethod.DELETE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<?> deleteGroup(@PathVariable("groupID") String groupId, @RequestHeader("Accept-Language") String locale) {
		//When the group is deleted we have also to delete the processors, configuration, etc.
		CompanyGroup group = groupRepository.find(groupId);
		if (group == null) {
			return new ResponseEntity<>(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_retrieving_group",
					ObjectUtils.toObjectArray(groupId), locale)), HttpStatus.NOT_FOUND);
		} else {
			
			//Delete the group configurations
			configRepository.deleteByGroupId(groupId);
			
			//Delete the group steps
			stepRepository.deleteByGroupId(groupId);
			
			//Delete the group processors
			processorRepository.deleteByGroupId(groupId);
			
			//Delete all licenses and all probe reports which were created
			List<CompanyLicense> licenses = licenseRepository.findByGroupId(groupId);
			
			if(licenses != null) {
				for(CompanyLicense license : licenses) {
					if(!Strings.isNullOrEmpty(license.getProbeId())) {
						reportRepository.deleteByProbeId(license.getProbeId());
						networkReportRepository.deleteByProbeId(license.getProbeId());						
					}
					licenseRepository.delete(license);
				}
			}
			
			//Remove the groups which are assigned to the users			
			userRepository.removeAsssignedGroup(groupId);
			
			//Remove OSQuery configuration
			queryRepository.deleteByGroupId(groupId);
			
			//Remove Group
			groupRepository.delete(group);
			
			return new ResponseEntity<>(group, HttpStatus.OK);
		}
	}

	/**
	 * This function returns all users from the user repository
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/api/users/group/user/{userId}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<List<CompanyGroup>> getGroupsByUserId(@PathVariable("userId") String userId,
			@RequestHeader("Accept-Language") String locale) {
		List<CompanyGroup> groups = groupRepository.findByOwnerId(userId);
		if (groups.isEmpty() || groups == null) {
			return new ResponseEntity(
					new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_retrieving_group", locale)),
					HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<List<CompanyGroup>>(groups, HttpStatus.OK);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private ResponseEntity<User> addNewUser(User user, String type, String locale)
			throws ItemNotFoundRepositoryException, IOException, URISyntaxException {

		/*
		 * TO-DO - if the email changes - check if the email already exists - send new
		 * activation email to the user
		 */
		if (user != null) {

			String addedByUserId = "";
			if (type.contains(ConfigItems.type_add_by_user)) {
				addedByUserId = type.substring(type.indexOf("-") + 1);
			}

			if (type.equals(ConfigItems.type_email)) {
				String randomPass = PortalUtils.alphaNumericString(15);
				user.setPassword(randomPass);
				user.setUsername(user.getEmail());
			} else {
				if (!type.equals(ConfigItems.type_password_update)) {
					if(!type.contains(ConfigItems.type_add_by_user)) {
						user.setPasswordUpdated(true);
					}					
				}

			}

			if (!type.equals(ConfigItems.type_update_user_info)) {
				if(!type.contains(ConfigItems.type_add_by_user)) {
					Errors errors = new BeanPropertyBindingResult(user, "user");

					passwordValidator.validate(user, errors);

					if (errors.hasErrors()) {

						String error = errors.getAllErrors().get(0).getDefaultMessage();

						return new ResponseEntity(new CustomErrorType(error), HttpStatus.NOT_FOUND);
					}
				}
			}

			if (type.equals(ConfigItems.type_password_update)) {
				user.setPassword(passwordEncoder.encode(user.getPassword()));
				user.setPasswordUpdated(true);
			}
			
			if(user.getUserRole() == null) {
				if(user.getEmail().contains("@secinto.com")) {
					user.setUserRole(UserRole.SUPERADMIN);
				}
				else {
					user.setUserRole(UserRole.SUPERUSER);
				}						
			}
			
			if(!Strings.isNullOrEmpty(user.getGroupId())) {
				CompanyGroup group = groupRepository.find(user.getGroupId());
				
				if(group != null) {
					user.setGroupName(group.getName());
				}
				
			}

			if (user.getId() != null) {
				userRepository.update(user);

				if (type.equals(ConfigItems.type_password_update)) {
					URI url = new URI(ConfigItems.BASE_URL_WEB + "/#/account/activate/" + user.getActivationToken());
					HttpHeaders httpHeaders = new HttpHeaders();
					httpHeaders.setLocation(url);
					return new ResponseEntity<>(user, httpHeaders, HttpStatus.OK);
				} else {
					return new ResponseEntity<User>(user, HttpStatus.OK);
				}

			} else {
				User queryUser = userRepository.findByEmail(user.getEmail());
				if (queryUser == null) {

					String activationToken = PortalUtils.generateToken();
					user.setEnabled(true);
					if(!type.contains(ConfigItems.type_add_by_user)) {
						user.setPassword(passwordEncoder.encode(user.getPassword()));
					}					
					user.setActivationToken(activationToken);
					user.setActivated(false);

					if (!Strings.isNullOrEmpty(user.getEmail())) {
						
						userRepository.save(user);
						// ObjectId userID = this.userRepository.save(user);

						String userID = userRepository.findByEmail(user.getEmail()).getId();
						
						if(user.getUserRole() != UserRole.USER && !type.contains(ConfigItems.type_add_by_user)) {
							DataInitialization.addDefaultGroup(userID, user.getEmail());
						}
						
						if (type.contains(ConfigItems.type_add_by_user)) {
							if (Strings.isNullOrEmpty(addedByUserId)) {
								userRepository.delete(user);
								return new ResponseEntity(
										new CustomErrorType(messageByLocaleService.getMessage("error_no_added_by_userId", locale)),
										HttpStatus.NOT_FOUND);
							} else {
								User addedByUser = userRepository.findByUserID(addedByUserId);
								if (addedByUser == null) {
									userRepository.delete(user);
									return new ResponseEntity(
											new CustomErrorType(messageByLocaleService.getMessage("error_no_added_by_userId", locale)),
											HttpStatus.NOT_FOUND);
								} else {
									List<String> addedByUserIds = addedByUser.getMyUsers();
									if (addedByUserIds == null) {
										addedByUserIds = new ArrayList<String>();
									}
									addedByUserIds.add(userID);
									addedByUser.setMyUsers(addedByUserIds);
									userRepository.update(addedByUser);
								}
							}
						}

						if (userID != null) {
							
							String emailContent = messageByLocaleService.getMessage("registration_email_content", locale)
										+ ConfigItems.BASE_URL + "/api/users/activate/" + user.getActivationToken();

							if (!PortalUtils.sendEmail(user, emailContent, ConfigItems.email_subject_al)) {
								return new ResponseEntity(
										new CustomErrorType(messageByLocaleService.getMessage("error_while_sending_email", locale)),
										HttpStatus.NOT_FOUND);

							} 
						} else {
							return new ResponseEntity(
									new CustomErrorType(messageByLocaleService.getMessage("error_while_saving_user", locale)),
									HttpStatus.NOT_FOUND);
						}
					} else {
						return new ResponseEntity(
								new CustomErrorType(messageByLocaleService.getMessage("error_user_cannot_be_empty", locale)),
								HttpStatus.NOT_FOUND);
					}
				} else {
					return new ResponseEntity(
							new CustomErrorType(messageByLocaleService.getMessage("user_with_provided_email_already_exists", locale)),
							HttpStatus.NOT_FOUND);
				}
			}

		} else {
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("unknown_error_occured", locale)),
					HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<User>(user, HttpStatus.OK);
	}
}
