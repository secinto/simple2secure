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
	 * Finds the object by the field name
	 *
	 * @param fieldName
	 * @param value
	 * @return
	 */
	public List<T> findByFieldName(String fieldName, Object value);

	/**
	 * Returns the specified page number for the field name.
	 *
	 * @param filedName
	 * @param value
	 * @param lastPageNumber
	 * @return
	 */
	public List<T> findByFieldNamePaging(String filedName, Object value, int lastPageNumber);

	/**
	 * Returns the amount of pages required to obtain all results.
	 *
	 * @param filedName
	 * @param value
	 * @return
	 */
	public int getLastPageNumberByFieldName(String filedName, Object value);

}
