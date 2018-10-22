/*
 * Copyright (c) 2017 Secinto GmbH This software is the confidential and proprietary information of Secinto GmbH. All rights reserved.
 * Secinto GmbH and its affiliates make no representations or warranties about the suitability of the software, either express or implied,
 * including but not limited to the implied warranties of merchantability, fitness for a particular purpose, or non-infringement. NXP B.V.
 * and its affiliates shall not be liable for any damages suffered by licensee as a result of using, modifying or distributing this software
 * or its derivatives. This copyright notice must appear in all copies of this software.
 */

package com.simple2secure.portal.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.google.common.base.Strings;
import com.simple2secure.api.dto.ConfigDTO;
import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.CompanyLicense;
import com.simple2secure.api.model.Config;
import com.simple2secure.api.model.Probe;
import com.simple2secure.api.model.QueryRun;
import com.simple2secure.api.model.User;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.repository.ConfigRepository;
import com.simple2secure.portal.repository.GroupRepository;
import com.simple2secure.portal.repository.LicenseRepository;
import com.simple2secure.portal.repository.QueryRepository;
import com.simple2secure.portal.repository.UserRepository;
import com.simple2secure.portal.service.MessageByLocaleService;

@RestController
public class ConfigController {

	@Autowired
	ConfigRepository configRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	QueryRepository queryRepository;
	
	@Autowired
	LicenseRepository licenseRepository;
	
	@Autowired
	GroupRepository groupRepository;	
	
    @Autowired
    MessageByLocaleService messageByLocaleService;

	RestTemplate restTemplate = new RestTemplate();
	
	static final Logger log = LoggerFactory.getLogger(ConfigController.class);

	/**
	 * This function updates configuration and automatically after each update the version will be incremented
	 * 
	 * @throws ItemNotFoundRepositoryException
	 */
	@RequestMapping(value = "/api/config", method = RequestMethod.POST, consumes = "application/json")
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<Config> saveConfig(@RequestBody Config config, @RequestHeader("Accept-Language") String locale) throws ItemNotFoundRepositoryException {
		config.setVersion(config.getVersion() + 1);
		this.configRepository.update(config);
		return new ResponseEntity<Config>(config, HttpStatus.OK);
	}
	
	/**
	 * this function returns config by config id
	 *
	 * @param id
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/api/configs/{id}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<Config> getConfigByID(@PathVariable("id") String id, @RequestHeader("Accept-Language") String locale) {
		Config config = this.configRepository.find(id);
		if(config == null) {
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("configuration_not_found", locale)), HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<Config>(config, HttpStatus.OK);
	}	
	
	/**
	 * This function returns configuration for the specified probe
	 *
	 * @param user_id
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/api/config/{probeId}", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('PROBE')")
	public ResponseEntity<Config> getConfigByProbeId(@PathVariable("probeId") String probeId, @RequestHeader("Accept-Language") String locale) {
		if(Strings.isNullOrEmpty(probeId)) {
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("configuration_not_found", locale)), HttpStatus.NOT_FOUND);
		}
		else {
			Config config = this.configRepository.findByProbeId(probeId);
			if(config != null) {
				return new ResponseEntity<Config>(config, HttpStatus.OK);
			}
			else {
				return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("configuration_not_found", locale)), HttpStatus.NOT_FOUND);
			}
		}
	}	
	
	/**
	 * This function returns configuration for the specified user
	 *
	 * @param user_id
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/api/config/dto/{type}/{userId}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<ConfigDTO>> getConfigDtoByUserUUID(@PathVariable("type") String type, @PathVariable("userId") String userId, 
			@RequestHeader("Accept-Language") String locale) {
		if(!Strings.isNullOrEmpty(type)) {
			List<ConfigDTO> configurations = new ArrayList<>();
			
			//Return the configuration for the probes
			if(type.equals("probe")) {
				List<CompanyLicense> licenses = this.licenseRepository.findByUserId(userId);
				if(licenses != null) {
					for(CompanyLicense license: licenses) {
						if(license != null) {
							//only activated probes will be shown
							if(license.isActivated()) {
								if(!Strings.isNullOrEmpty(license.getGroupId())) {
									Config config = this.configRepository.findByProbeId(license.getProbeId());
									
									if(config != null) {
										String groupName = "";
										if(!Strings.isNullOrEmpty(config.getGroupId())) {
											CompanyGroup group = groupRepository.find(config.getGroupId());
											
											if(group != null) {
												groupName = group.getName();
												configurations.add(new ConfigDTO(groupName, config, true));
											}
										}
									}
								}								
							}

						}
					}
				}
				else {
					return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("configuration_not_found", locale)), HttpStatus.NOT_FOUND);
				}
			}
			
			//Return the configuration for the groups
			else {
				User user = userRepository.find(userId);
				
				if(user != null) {
					if(!Strings.isNullOrEmpty(user.getGroupId())) {
						CompanyGroup group = groupRepository.find(user.getGroupId());
						if(group != null) {
							Config config = configRepository.findByGroupId(user.getGroupId());
							if(config != null) {
								configurations.add(new ConfigDTO(group.getName(), config, false));
							}
						}
					}
				}
				
				List<CompanyGroup> groups = groupRepository.findByOwnerId(userId);
				if(groups != null) {
					for(CompanyGroup group: groups) {
						if(group != null) {
							Config config = this.configRepository.findByGroupId(group.getId());
							
							if(config != null) {
								configurations.add(new ConfigDTO(group.getName(), config, true));
							}
						}
					}
				}
				
				if(configurations.isEmpty()) {
					return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("configuration_not_found", locale)), HttpStatus.NOT_FOUND);
				}
			}
			
			return new ResponseEntity<List<ConfigDTO>>(configurations, HttpStatus.OK);
			
		}
		else {
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("configuration_not_found", locale)), HttpStatus.NOT_FOUND);
		}		
	}
	
	/**
	 * This function updates the current probe configuration from the current group configuration
	 * 
	 * @throws ItemNotFoundRepositoryException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/api/config/update/group", method = RequestMethod.POST, consumes = "application/json")
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<Config> applyGroupConfig(@RequestBody Config config, @RequestHeader("Accept-Language") String locale) throws ItemNotFoundRepositoryException {

		if(config != null) {
			if(!Strings.isNullOrEmpty(config.getGroupId())) {
				Config groupConfig = configRepository.findByGroupId(config.getGroupId());
				if(groupConfig != null) {
					Config tempConfig = groupConfig;
					tempConfig.setId(config.getId());
					tempConfig.setVersion(config.getVersion() + 1);
					tempConfig.setProbeId(config.getProbeId());
					tempConfig.setGroupConfiguration(config.isGroupConfiguration());
					configRepository.update(tempConfig);	
					return new ResponseEntity<Config>(tempConfig, HttpStatus.OK);
				}
				else {
					log.error("Group Configuration not found");
					return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_updating_configuration", locale)), HttpStatus.NOT_FOUND);					
				}
			}
			else {
				log.error("Group Id cannot be null");
				return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_updating_configuration", locale)), HttpStatus.NOT_FOUND);
			}
		}
		return new ResponseEntity<Config>(config, HttpStatus.OK);
	}

	/**
	 * This function updates or saves new Query config into the database
	 * @throws ItemNotFoundRepositoryException 
	 */
	@RequestMapping(value = "/api/config/query", method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<QueryRun> updateQuery(@RequestBody QueryRun query, @RequestHeader("Accept-Language") String locale) throws ItemNotFoundRepositoryException {

		if (!Strings.isNullOrEmpty(query.getId())) {
			
			this.queryRepository.update(query);
			return new ResponseEntity<QueryRun>(query, HttpStatus.OK);
		}

		else {
			if(!Strings.isNullOrEmpty(query.getGroupId())) {
				query.setGroupQueryRun(true);
			}
			else {
				query.setGroupQueryRun(false);
			}
			
			this.queryRepository.save(query);
			return new ResponseEntity<QueryRun>(query, HttpStatus.OK);
		}

	}
	
	/**
	 * This function updates the current probe configuration from the current group configuration
	 * 
	 * @throws ItemNotFoundRepositoryException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/api/config/query/update/group", method = RequestMethod.POST, consumes = "application/json")
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<Probe> applyGroupOSQueryConfig(@RequestBody Probe probe, @RequestHeader("Accept-Language") String locale) throws ItemNotFoundRepositoryException {

		if(probe != null) {
			if(!Strings.isNullOrEmpty(probe.getProbeId())) {
				CompanyLicense license = licenseRepository.findByProbeId(probe.getProbeId());
				
				if(license != null) {
					if(!Strings.isNullOrEmpty(license.getGroupId())) {
						
						//Delete current probe queries
						List<QueryRun> probeQueries = queryRepository.findByProbeId(probe.getProbeId(), true);
						if(probeQueries != null) {
							for(QueryRun query : probeQueries) {
								queryRepository.delete(query);
							}
						}
						else {
							log.debug("Nothing to delete for this probe!");
						}
						
						//Copy group queries
						List<QueryRun> groupQueries = queryRepository.findByGroupId(license.getGroupId(), true, true);
						
						if(groupQueries != null) {
							for(QueryRun query : groupQueries) {
								query.setId(null);
								query.setProbeId(probe.getProbeId());
								query.setGroupQueryRun(false);
								
								queryRepository.save(query);
								
							}
						}
						else {
							log.debug("No queries found for this group!");
						}
						
						return new ResponseEntity<Probe>(probe, HttpStatus.OK);
					}
					else {
						log.error("Group id not found in the license object");
						return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("error_while_update_queryrun", locale)), HttpStatus.NOT_FOUND);
					}
				}
				else {
					log.error("License for the provided probeId does not exist!");
					return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("error_while_update_queryrun", locale)), HttpStatus.NOT_FOUND);
				}
			}
			else {
				log.error("ProbeId cannot be null or empty");
				return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("error_while_update_queryrun", locale)), HttpStatus.NOT_FOUND);
			}
		}
		else {
			log.error("Probe object cannot be null");
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("error_while_update_queryrun", locale)), HttpStatus.NOT_FOUND);
		}
	}	
	
	/**
	 * This function returns all users from the user repository
	 */
	@RequestMapping(value = "/api/config/query/{queryId}", method = RequestMethod.DELETE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<?> deleteConfig(@PathVariable("queryId") String queryId, @RequestHeader("Accept-Language") String locale) {
		QueryRun queryRun = queryRepository.find(queryId);
		if (queryRun == null) {
			return new ResponseEntity<>(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_deleting_queryrun",
					ObjectUtils.toObjectArray(queryId), locale)), HttpStatus.NOT_FOUND);
		} else {
			queryRepository.delete(queryRun);
			return new ResponseEntity<>(queryRun, HttpStatus.OK);
		}
	}	

	/**
	 * This function returns the query config for the specified user
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER', 'PROBE')")
	@RequestMapping(value = "/api/config/query/{probeId}/{select_all}", method = RequestMethod.GET)
	public ResponseEntity<List<QueryRun>> getQueriesByUserID(@PathVariable("probeId") String probeId,
			@PathVariable("select_all") boolean select_all, @RequestHeader("Accept-Language") String locale) {
		
		List<QueryRun> queryConfig = this.queryRepository.findByProbeId(probeId, select_all);
		if(queryConfig == null) {
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("error_while_getting_queryrun", locale)), HttpStatus.NOT_FOUND);			
		}
		
		return new ResponseEntity<List<QueryRun>>(queryConfig, HttpStatus.OK);
	}
	
	/**
	 * This function returns query config by the id
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/api/config/query/group/{groupId}/{select_all}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<QueryRun>> getQueriesByGroupId(@PathVariable("groupId") String groupId, @PathVariable("select_all") boolean select_all, @RequestHeader("Accept-Language") String locale) {
		List<QueryRun> queryConfig = this.queryRepository.findByGroupId(groupId, select_all, true);
		
		if(queryConfig == null) {
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("queryrun_not_found", locale)), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<List<QueryRun>>(queryConfig, HttpStatus.OK);
	}	

	/**
	 * This function returns query config by the id
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/api/config/query/{id}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<QueryRun> getQueryByID(@PathVariable("id") String id, @RequestHeader("Accept-Language") String locale) {
		QueryRun queryConfig = this.queryRepository.find(id);
		
		if(queryConfig == null) {
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("queryrun_not_found", locale)), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<QueryRun>(queryConfig, HttpStatus.OK);
	}
}
