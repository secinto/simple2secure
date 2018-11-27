package com.simple2secure.probe.utils;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.api.model.CompanyLicensePublic;
import com.simple2secure.api.model.Config;
import com.simple2secure.api.model.NetworkReport;
import com.simple2secure.api.model.Processor;
import com.simple2secure.api.model.QueryRun;
import com.simple2secure.api.model.Report;
import com.simple2secure.api.model.Step;
import com.simple2secure.probe.dao.BaseDao;
import com.simple2secure.probe.dao.impl.ConfigDaoImpl;
import com.simple2secure.probe.dao.impl.LicenseDaoImpl;
import com.simple2secure.probe.dao.impl.NetworkReportDaoImpl;
import com.simple2secure.probe.dao.impl.ProcessorDaoImpl;
import com.simple2secure.probe.dao.impl.QueryDaoImpl;
import com.simple2secure.probe.dao.impl.ReportDaoImpl;
import com.simple2secure.probe.dao.impl.StepDaoImpl;

public class DBUtil {

	private static Logger log = LoggerFactory.getLogger(DBUtil.class);

	protected int ARRAY_SIZE = 30;

	private static DBUtil instance;

	private ConfigDaoImpl configDao;
	private LicenseDaoImpl licenseDao;
	private NetworkReportDaoImpl networkReportDao;
	private ProcessorDaoImpl processorDao;
	private ReportDaoImpl reportDao;
	private QueryDaoImpl queryDao;
	private StepDaoImpl stepDao;

	public static DBUtil getInstance() throws IllegalArgumentException {
		return getInstance(null);
	}

	public static DBUtil getInstance(String persistenceUnitName) {
		if (instance == null) {
			instance = new DBUtil(persistenceUnitName);
		}
		return instance;
	}

	private DBUtil(String persistenceUnitName) {
		if (configDao == null) {
			configDao = new ConfigDaoImpl(persistenceUnitName);
		}

		if (licenseDao == null) {
			licenseDao = new LicenseDaoImpl(persistenceUnitName);
		}

		if (networkReportDao == null) {
			networkReportDao = new NetworkReportDaoImpl(persistenceUnitName);
		}

		if (processorDao == null) {
			processorDao = new ProcessorDaoImpl(persistenceUnitName);
		}

		if (reportDao == null) {
			reportDao = new ReportDaoImpl(persistenceUnitName);
		}

		if (queryDao == null) {
			queryDao = new QueryDaoImpl(persistenceUnitName);
		}

		if (stepDao == null) {
			stepDao = new StepDaoImpl(persistenceUnitName);
		}

		if (log.isDebugEnabled()) {
			log.debug("Database successfully instantiated");
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public synchronized void save(Object t) {
		BaseDao dao = getDao(t);
		if (dao != null) {
			dao.save(t);
		}
		if (log.isDebugEnabled()) {
			log.debug("Object of type {} successfully stored in database", t.getClass().getName());
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public synchronized void merge(Object t) {
		BaseDao dao = getDao(t);
		if (dao != null) {
			dao.merge(t);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public synchronized void delete(Object t) {
		BaseDao dao = getDao(t);
		if (dao != null) {
			dao.delete(t);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public synchronized <T> List<T> findByFieldName(String fieldName, Object value, Object t) {
		List<T> queryObjects = new ArrayList<>();

		BaseDao dao = getDao(t);
		if (dao != null) {
			queryObjects = dao.findByFieldName(fieldName, value);
		}

		return queryObjects;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public synchronized <T> List<T> findAll(Object t) {
		List<T> queryObjects = new ArrayList<>();

		BaseDao dao = getDao(t);
		if (dao != null) {
			queryObjects = dao.getAll();
		}
		return queryObjects;
	}

	@SuppressWarnings("rawtypes")
	public synchronized void clearDB(Object t) {
		BaseDao dao = getDao(t);
		if (dao != null) {
			dao.deleteAll();
		}
	}

	@SuppressWarnings("rawtypes")
	private BaseDao getDao(Object t) {
		if (t instanceof Config || t == Config.class) {
			return configDao;
		}

		else if (t instanceof CompanyLicensePublic || t == CompanyLicensePublic.class) {
			return licenseDao;
		}

		else if (t instanceof NetworkReport || t == NetworkReport.class) {
			return networkReportDao;
		}

		else if (t instanceof Processor || t == Processor.class) {
			return processorDao;
		}

		else if (t instanceof Report || t == Report.class) {
			return reportDao;
		}

		else if (t instanceof QueryRun || t == QueryRun.class) {
			return queryDao;
		}

		else if (t instanceof Step || t == Step.class) {
			return stepDao;
		}
		return null;
	}
}
