package com.simple2secure.probe.dao;

import java.util.List;

public interface BaseDao<T> {

	/**
	 * Get all objects from table
	 *
	 * @return
	 */
	public List<T> getAll();

	/**
	 * Get Object by ID from table
	 */

	public T getByID(String id);

	/**
	 * Save object to table
	 */
	public void save(T t);

	/**
	 * Delete object from table
	 *
	 * @param t
	 */
	public void delete(T t);

	/**
	 * Checks if object already exists, if yes updates in the the table, if not inserts new one to the table
	 *
	 * @param t
	 */
	public void merge(T t);

	/**
	 * Deletes all the entries in this table.
	 */
	public void deleteAll();
	
	/**
	 *  Finds the object by the field name
	 * @param fieldName
	 * @param value
	 * @return
	 */
	public List<T> findByFieldName(String fieldName, Object value);
}
