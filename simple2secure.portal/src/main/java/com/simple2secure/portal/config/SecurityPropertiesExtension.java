package com.simple2secure.portal.config;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties("security.authz")
class SecurityPropertiesExtension {

	Map<String, List<String>> roleHierarchy = new LinkedHashMap<>();

	Map<String, List<String>> permissions = new LinkedHashMap<>();
}
