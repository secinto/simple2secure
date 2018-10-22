package com.simple2secure.portal.dao;

import java.io.InputStream;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.stereotype.Repository;

import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;
import com.simple2secure.api.dbo.GenericDBObject;

@Repository
public class MongoGridFSRepository {

	@Autowired
	private GridFsOperations gridOperation;

	/**
	 * Saves the contents of a file to the GridFS
	 *
	 * @param inputStream
	 *          the stream to read the file content from
	 *
	 * @return the object representing the saved file
	 */
	public GridFSFile save(InputStream inputStream, String filename) {
		GridFSFile file = this.gridOperation.store(inputStream, filename);
		return file;
	}

	/**
	 * Returns a stored file found by its Id
	 *
	 * @param id
	 *          the object identifier of the file to find
	 *
	 * @return the object representing the file or null if the file was not found
	 */
	public GridFSDBFile getById(String id) {
		return this.getById(new ObjectId(id));
	}

	/**
	 * Returns a stored file found by its Id
	 *
	 * @param id
	 *          the object identifier of the file to find
	 *
	 * @return the object representing the file or null if the file was not found
	 */
	public GridFSDBFile getById(ObjectId id) {
		return this.gridOperation.findOne(new Query(Criteria.where(GenericDBObject.ID).is(id))); // $NON-NLS-1$
	}

	/**
	 * Returns all files which match the regular expression of a filename
	 *
	 * @param filename
	 *          the regular expression of the file name to match
	 *
	 * @return a list of objects representing the found files
	 */
	public List<GridFSDBFile> getByFilename(String filename) {
		return this.gridOperation
				.find(new Query(Criteria.where(/*MessageCodeUtil.getMessageCodeMessage(MessageCodeGeneral.mongo_filename)*/ "filename").regex(filename))); // $NON-NLS-1$
	}

	/**
	 * TODO: fix me Returns all files which match given meta data
	 *
	 * @param metaData
	 *          the meta data to match
	 * @return a list of file matching the meta data
	 */
	public List<GridFSDBFile> getByMetadata(DBObject metaData) {
		Query searchQuery = new Query();

		for (String key : metaData.keySet()) {
			searchQuery.addCriteria(
					Criteria.where(/*MessageCodeUtil.getMessageCodeMessage(MessageCodeGeneral.mongo_metadata)*/"metadata" + key).is(metaData.get(key))); // $NON-NLS-1$
		}
		return this.gridOperation.find(searchQuery);
	}

	/**
	 * Removes a stored file found by its Id
	 *
	 * @param id
	 *          the object identifier of the file to remove
	 */
	public void delete(ObjectId id) {
		this.gridOperation.delete(new Query(Criteria.where(GenericDBObject.ID).is(id)));
	}

	/**
	 * Removes a stored file
	 *
	 * @param file
	 *          the file to remove
	 */
	public void delete(GridFSDBFile file) {
		this.delete((ObjectId) file.getId());
	}

	public void deleteAll() {
		List<GridFSDBFile> allFiles = this.gridOperation
				.find(new Query(Criteria.where(/*MessageCodeUtil.getMessageCodeMessage(MessageCodeGeneral.mongo_length)*/"mongo_length").gt(new Integer(0)))); // $NON-NLS-1$

		for (GridFSDBFile file : allFiles) {
			this.delete(file);
		}
	}
}
