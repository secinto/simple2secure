/**
 *********************************************************************
 *   simple2secure is a cyber risk and information security platform.
 *   Copyright (C) 2019  by secinto GmbH <https://secinto.com>
 *********************************************************************
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of the
 *   License, or (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *********************************************************************
 */
package com.simple2secure.probe.utils;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.api.model.CompanyLicensePublic;
import com.simple2secure.api.model.NetworkReport;
import com.simple2secure.api.model.ProbePacket;
import com.simple2secure.api.model.Processor;
import com.simple2secure.api.model.QueryRun;
import com.simple2secure.api.model.Report;
import com.simple2secure.api.model.Step;
import com.simple2secure.probe.dao.BaseDao;
import com.simple2secure.probe.dao.impl.LicenseDaoImpl;
import com.simple2secure.probe.dao.impl.NetworkReportDaoImpl;
import com.simple2secure.probe.dao.impl.ProbePacketDaoImpl;
import com.simple2secure.probe.dao.impl.ProcessorDaoImpl;
import com.simple2secure.probe.dao.impl.QueryDaoImpl;
import com.simple2secure.probe.dao.impl.ReportDaoImpl;
import com.simple2secure.probe.dao.impl.StepDaoImpl;

public class DBUtil {

	private static Logger log = LoggerFactory.getLogger(DBUtil.class);

	protected int ARRAY_SIZE = 30;

	private static DBUtil instance;

	private LicenseDaoImpl licenseDao;
	private NetworkReportDaoImpl networkReportDao;
	private ProcessorDaoImpl processorDao;
	private ReportDaoImpl reportDao;
	private QueryDaoImpl queryDao;
	private StepDaoImpl stepDao;
	private ProbePacketDaoImpl probePacketDao;
	public static boolean hasDBChanged = false;

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

		if (probePacketDao == null) {
			probePacketDao = new ProbePacketDaoImpl(persistenceUnitName);
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

		if (t instanceof CompanyLicensePublic || t == CompanyLicensePublic.class) {
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

		else if (t instanceof ProbePacket || t == ProbePacket.class) {
			return probePacketDao;
		}
		return null;
	}
}
