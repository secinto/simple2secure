package com.simple2secure.api.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.simple2secure.api.dbo.GenericDBObject;

@Entity
@Table(name = "Config")
public class Config extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = -3153781553493971957L;

	private String probeId;
	private String groupId;
	private int version;
	private String config_supplier;
	private String task_supplier;
	private String stylesheet;
	private boolean use_configured_iface;
	private int interface_number;
	private boolean show_interfaces;
	private String external_address;
	private int connection_timeout;
	private String processing_factory;
	private String communication_factory;
	private int wt_intervall;


	@ManyToOne(cascade = {CascadeType.ALL})
	@JoinColumn(name = "db_config")
	private DBConfig db_config;
	@ManyToOne(cascade = {CascadeType.ALL})
	@JoinColumn(name = "query_config")
	private QueryConfig queries;

	private String baseUrl;

	@OneToMany(cascade = {CascadeType.ALL})
	private List<API> apis;
	
	private boolean isGroupConfiguration;

	public Config() {

	}

	/**
	 *
	 * @param version
	 * @param config_supplier
	 * @param task_supplier
	 * @param stylesheet
	 * @param use_configured_iface
	 * @param interface_number
	 * @param show_interfaces
	 * @param external_address
	 * @param connection_timeout
	 * @param processing_factory
	 * @param communication_factory
	 * @param db_config
	 */
	public Config(String probeId, int version, String config_supplier, String task_supplier, String stylesheet, boolean use_configured_iface,
			int interface_number, boolean show_interfaces, String external_address, int connection_timeout, String processing_factory,
			String communication_factory, int wt_intervall, DBConfig db_config, QueryConfig queries, List<API> apis, String baseUrl) {
		super();
		this.probeId = probeId;
		this.version = version;
		this.config_supplier = config_supplier;
		this.task_supplier = task_supplier;
		this.stylesheet = stylesheet;
		this.use_configured_iface = use_configured_iface;
		this.interface_number = interface_number;
		this.show_interfaces = show_interfaces;
		this.external_address = external_address;
		this.connection_timeout = connection_timeout;
		this.processing_factory = processing_factory;
		this.communication_factory = communication_factory;
		this.wt_intervall = wt_intervall;
		this.db_config = db_config;
		this.queries = queries;
		this.baseUrl = baseUrl;
		this.apis = apis;
	}
	
	/**
	 * 
	 * @param userUUID
	 * @param id
	 * @param version
	 * @param config_supplier
	 * @param task_supplier
	 * @param stylesheet
	 * @param use_configured_iface
	 * @param interface_number
	 * @param show_interfaces
	 * @param external_address
	 * @param connection_timeout
	 * @param processing_factory
	 * @param communication_factory
	 * @param wt_intervall
	 * @param db_config
	 * @param queries
	 * @param apis
	 * @param baseUrl
	 */
	
	public Config(String probeId, String id, int version, String config_supplier, String task_supplier, String stylesheet, boolean use_configured_iface,
			int interface_number, boolean show_interfaces, String external_address, int connection_timeout, String processing_factory,
			String communication_factory, int wt_intervall, DBConfig db_config, QueryConfig queries, List<API> apis, String baseUrl) {
		super();
		this.probeId = probeId;
		this.id = id;
		this.version = version;
		this.config_supplier = config_supplier;
		this.task_supplier = task_supplier;
		this.stylesheet = stylesheet;
		this.use_configured_iface = use_configured_iface;
		this.interface_number = interface_number;
		this.show_interfaces = show_interfaces;
		this.external_address = external_address;
		this.connection_timeout = connection_timeout;
		this.processing_factory = processing_factory;
		this.communication_factory = communication_factory;
		this.wt_intervall = wt_intervall;
		this.db_config = db_config;
		this.queries = queries;
		this.baseUrl = baseUrl;
		this.apis = apis;
	}	

	/**
	 *
	 * @return
	 */

	public int getVersion() {
		return version;
	}

	/**
	 *
	 * @param version
	 */

	public void setVersion(int version) {
		this.version = version;
	}

	/**
	 *
	 * @return
	 */

	public String getConfig_supplier() {
		return config_supplier;
	}

	/**
	 *
	 * @param config_supplier
	 */

	public void setConfig_supplier(String config_supplier) {
		this.config_supplier = config_supplier;
	}

	/**
	 *
	 * @return
	 */

	public String getTask_supplier() {
		return task_supplier;
	}

	/**
	 *
	 * @param task_supplier
	 */

	public void setTask_supplier(String task_supplier) {
		this.task_supplier = task_supplier;
	}

	/**
	 *
	 * @return
	 */

	public String getStylesheet() {
		return stylesheet;
	}

	/**
	 *
	 * @param stylesheet
	 */

	public void setStylesheet(String stylesheet) {
		this.stylesheet = stylesheet;
	}

	/**
	 *
	 * @return
	 */

	public boolean isUse_configured_iface() {
		return use_configured_iface;
	}

	/**
	 *
	 * @param use_configured_iface
	 */

	public void setUse_configured_iface(boolean use_configured_iface) {
		this.use_configured_iface = use_configured_iface;
	}

	/**
	 *
	 * @return
	 */

	public int getInterface_number() {
		return interface_number;
	}

	/**
	 *
	 * @param interface_number
	 */

	public void setInterface_number(int interface_number) {
		this.interface_number = interface_number;
	}

	/**
	 *
	 * @return
	 */

	public boolean isShow_interfaces() {
		return show_interfaces;
	}

	/**
	 *
	 * @param show_interfaces
	 */

	public void setShow_interfaces(boolean show_interfaces) {
		this.show_interfaces = show_interfaces;
	}

	/**
	 *
	 * @return
	 */

	public String getExternal_address() {
		return external_address;
	}

	/**
	 *
	 * @param external_address
	 */

	public void setExternal_address(String external_address) {
		this.external_address = external_address;
	}

	/**
	 *
	 * @return
	 */

	public int getConnection_timeout() {
		return connection_timeout;
	}

	/**
	 *
	 * @param connection_timeout
	 */

	public void setConnection_timeout(int connection_timeout) {
		this.connection_timeout = connection_timeout;
	}

	/**
	 *
	 * @return
	 */

	public String getProcessing_factory() {
		return processing_factory;
	}

	/**
	 *
	 * @param processing_factory
	 */

	public void setProcessing_factory(String processing_factory) {
		this.processing_factory = processing_factory;
	}

	/**
	 *
	 * @return
	 */

	public DBConfig getDb_config() {
		return db_config;
	}

	/**
	 *
	 * @param db_config
	 */

	public void setDb_config(DBConfig db_config) {
		this.db_config = db_config;
	}

	/**
	 *
	 * @return
	 */

	public String getCommunication_factory() {
		return communication_factory;
	}

	/**
	 *
	 * @param communication_factory
	 */

	public void setCommunication_factory(String communication_factory) {
		this.communication_factory = communication_factory;
	}

	public QueryConfig getQueries() {
		return queries;
	}

	public void setQueries(QueryConfig queries) {
		this.queries = queries;
	}

	public String getProbeId() {
		return probeId;
	}

	public void setProbeId(String probeId) {
		this.probeId = probeId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public List<API> getApis() {
		return apis;
	}

	public void setApis(List<API> apis) {
		this.apis = apis;
	}

	public int getWt_intervall() {
		return wt_intervall;
	}

	public void setWt_intervall(int wt_intervall) {
		this.wt_intervall = wt_intervall;
	}

	public boolean isGroupConfiguration() {
		return isGroupConfiguration;
	}

	public void setGroupConfiguration(boolean isGroupConfiguration) {
		this.isGroupConfiguration = isGroupConfiguration;
	}
}
