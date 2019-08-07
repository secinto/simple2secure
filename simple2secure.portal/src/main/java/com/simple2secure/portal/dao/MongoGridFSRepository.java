package com.simple2secure.portal.dao;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Repository;

import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.simple2secure.api.dbo.GenericDBObject;

@Repository
public class MongoGridFSRepository {

	@Autowired
	private GridFsTemplate gridFsTemplate;

	/**
	 * Saves the contents of a file to the GridFS
	 *
	 * @param inputStream
	 *          the stream to read the file content from
	 *
	 * @return the object representing the saved file
	 */
	public ObjectId save(InputStream inputStream, String filename) {
		return gridFsTemplate.store(inputStream, filename);
	}

	/**
	 * Returns a stored file found by its Id
	 *
	 * @param id
	 *          the object identifier of the file to find
	 *
	 * @return the object representing the file or null if the file was not found
	 */
	public GridFSFile getById(String id) {
		return gridFsTemplate.findOne(new Query(Criteria.where(GenericDBObject.ID).is(id)));
	}

	/**
	 * Returns all files which match the regular expression of a filename
	 *
	 * @param filename
	 *          the regular expression of the file name to match
	 *
	 * @return a list of objects representing the found files
	 */
	public List<GridFSFile> getByFilename(String filename) {
		return gridFsTemplate.find(new Query(Criteria.where("filename").regex(filename))).into(new ArrayList<GridFSFile>()); //$NON-NLS-1$
	}

	/**
	 * TODO: fix me Returns all files which match given meta data
	 *
	 * @param metaData
	 *          the meta data to match
	 * @return a list of file matching the meta data
	 */
	public List<GridFSFile> getByMetadata(DBObject metaData) {
		Query searchQuery = new Query();

		for (String key : metaData.keySet()) {
			searchQuery.addCriteria(Criteria.where("metadata." + key).is(metaData.get(key))); //$NON-NLS-1$
		}
		return gridFsTemplate.find(searchQuery).into(new ArrayList<GridFSFile>());
	}

	/**
	 * Removes a stored file found by its Id
	 *
	 * @param id
	 *          the object identifier of the file to remove
	 */
	public void delete(ObjectId id) {
		gridFsTemplate.delete(new Query(Criteria.where(GenericDBObject.ID).is(id)));
	}

	/**
	 * Removes a stored file
	 *
	 * @param file
	 *          the file to remove
	 */
	public void delete(GridFSFile file) {
		this.delete(file.getObjectId());
	}

	public void deleteAll() {
		List<GridFSFile> allFiles = gridFsTemplate.find(new Query(Criteria.where("length").gt(new Integer(0)))) //$NON-NLS-1$
				.into(new ArrayList<GridFSFile>());

		for (GridFSFile file : allFiles) {
			this.delete(file);
		}
	}
}
