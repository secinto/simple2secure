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
package com.simple2secure.probe.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.bson.types.ObjectId;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.probe.dao.BaseDao;

public abstract class BaseDaoImpl<T> implements BaseDao<T> {

	/** The Constant log. */
	static final Logger log = LoggerFactory.getLogger(BaseDaoImpl.class);

	/** The entity class. */
	protected Class<T> entityClass;

	protected static String PERSISTENCE_UNIT_NAME = "s2s";
	protected static int pageSize = 10;
	private static EntityManagerFactory factory;

	/**
	 * Creates the EntityManager using the built-in persistence unit name.
	 */
	protected static void createEntityManager() {
		if (factory == null) {
			factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		} else {
			if (!factory.getProperties().get("hibernate.ejb.persistenceUnitName").toString().equals(PERSISTENCE_UNIT_NAME)) {
				factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
			}
		}
	}

	/**
	 * Initializes the EntityManager using the provided persistence unit name.
	 *
	 * @param persistenceUnitName
	 *          The persistence unit which should be used to initialize the EntityManager.
	 */
	public static void init(String persistenceUnitName) {
		PERSISTENCE_UNIT_NAME = persistenceUnitName;
		init();
	}

	/**
	 * Initializes the EntityManager using the default persistence unit name. If another unit name should be used function
	 * {@link #init(String)} must be used.
	 */
	public static void init() {
		createEntityManager();
	}

	/**
	 * Returns the entity manager
	 *
	 * @return
	 */
	public EntityManager getEntityManager() {
		return factory.createEntityManager();
	}

	public Session getHibernateSession() {
		Session session = getEntityManager().unwrap(Session.class);
		return session;
	}

	/**
	 * Returns all objects for the given class
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<T> getAll() {
		// EntityManager em = getEntityManager();
		return getEntityManager().createQuery("from " + this.entityClass.getName()).getResultList();
	}

	/**
	 * Returns object by id
	 */
	@Override
	public T getByID(ObjectId id) {
		return getEntityManager().find(this.entityClass, id);
	}

	/**
	 * Saves object into the database
	 */
	@Override
	public void save(T t) {
		EntityManager em = getEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		em.persist(t);
		tx.commit();
	}

	/**
	 * Checks if object already exists, if yes updates in the the database, if not inserts new one to the database
	 */
	@Override
	public void merge(T t) {
		EntityManager em = getEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		em.merge(t);
		tx.commit();
	}

	/**
	 * Deletes an object from database
	 */
	@Override
	public void delete(T t) {
		EntityManager em = getEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		em.remove(em.contains(t) ? t : em.merge(t));
		tx.commit();
	}

	@Override
	public void deleteAll() {
		List<T> entities = getAll();
		for (T entity : entities) {
			delete(entity);
		}

	}

	/**
	 * Find list by field name
	 *
	 * @param fieldName
	 * @param value
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<T> findByFieldName(String fieldName, Object value) {
		Query query = getEntityManager().createQuery(getQuery(fieldName)).setParameter(fieldName, value);
		return query.getResultList();
	}

	/**
	 * Find list by field name
	 *
	 * @param fieldName
	 * @param value
	 * @return
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<T> findByFieldNamePaging(String fieldName, Object value, int lastPageNumber) {
		Query query = getEntityManager().createQuery(getQuery(fieldName)).setParameter(fieldName, value);
		query.setFirstResult((lastPageNumber - 1) * pageSize);
		query.setMaxResults(pageSize);
		return query.getResultList();
	}

	@Override
	public int getLastPageNumberByFieldName(String fieldName, Object value) {
		Query countQuery = getEntityManager().createQuery(getCountQuery(fieldName)).setParameter(fieldName, value);
		Long countResults = (Long) countQuery.getSingleResult();
		int lastPageNumber = (int) (Math.ceil(countResults / pageSize));
		return lastPageNumber;
	}

	/**
	 * Gets the query.
	 *
	 * @param fieldName
	 *          the field name
	 * @return the query
	 */
	private String getQuery(String fieldName) {
		String query = "from " + this.entityClass.getName() + " t " + "where t." + fieldName + " = :" + fieldName;
		return query;
	}

	private String getCountQuery(String fieldName) {
		String query = "Select count (t) from " + this.entityClass.getName() + " t " + "where t." + fieldName + " = :" + fieldName;
		return query;
	}
}
