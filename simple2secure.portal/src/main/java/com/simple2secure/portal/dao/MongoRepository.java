package com.simple2secure.portal.dao;

import java.util.List;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.mongodb.DBObject;
import com.simple2secure.api.dbo.GenericDBObject;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;

/**
 * Provides generic methods for Mongo DB repositories. Call _init() from the concrete repository after collectionName, collectionIndexName
 * and objectIdName have been assigned.
 *
 * @author pschosteritsch
 *
 * @param <T>
 *          the concrete database object which is managed by the repository. Has to be derived from BasicDBObject
 */
@Repository
public abstract class MongoRepository<T extends GenericDBObject> {

	static final Logger log = LoggerFactory.getLogger(MongoRepository.class);

	@Autowired
	protected ExtendedMongoTemplate mongoTemplate;

	/**
	 * Mongo collection name for objects.
	 */
	protected String collectionName;

	protected Class<T> className;

	/**
	 * Inserts a new item into the repository and the database
	 *
	 * @param item
	 *          the item to insert
	 */
	public void save(T item) {
		//ObjectId objId = new ObjectId();	
		DBObject dbObj = this.mongoTemplate.convert(item);
		//dbObj.put(GenericDBObject.ID, objId);
		this.mongoTemplate.save(dbObj, this.collectionName);

		//return objId;
	}
	
	/**
	 * Inserts a new item into the repository and the database and returns objectId
	 *
	 * @param item
	 *          the item to insert
	 * @return 
	 */
	public ObjectId saveAndReturnId(T item) {
		ObjectId objId = new ObjectId();	
		DBObject dbObj = this.mongoTemplate.convert(item);
		dbObj.put(GenericDBObject.ID, objId);
		this.mongoTemplate.save(dbObj, this.collectionName);
		return objId;
	}

	/**
	 * Removes an item from the repository and the database If there is no such item the method simply returns
	 *
	 * @param item
	 */
	public void delete(T item) {
		String objId = item.getId();
		this.mongoTemplate.remove(Query.query(Criteria.where(GenericDBObject.ID).is(objId)), this.collectionName);
	}

	/**
	 * Removes an item from the repository and the database If there is no such item the method simply returns
	 *
	 * @param item
	 */
	public void delete(DBObject item) {
		String objId = (String) item.get(GenericDBObject.ID);
		this.mongoTemplate.remove(Query.query(Criteria.where(GenericDBObject.ID).is(objId)), this.collectionName);
	}

	public void deleteAll() {
		this.mongoTemplate.dropCollection(this.collectionName);
		this.mongoTemplate.createCollection(this.collectionName);
	}

	/**
	 * Updates the properties of an existing item in the repository and the database
	 *
	 * @param item
	 *          the item to update
	 * @throws ItemNotFoundRepositoryException
	 *           if there is no such item
	 */
	public void update(T item) throws ItemNotFoundRepositoryException {
		String objId = item.getId();

		List<? extends GenericDBObject> items = this.mongoTemplate.find(Query.query(Criteria.where(GenericDBObject.ID).is(objId)),
				item.getClass(), this.collectionName);

		if (items == null) {
			throw new ItemNotFoundRepositoryException();
		}

		if (items.size() <= 0) {
			throw new ItemNotFoundRepositoryException();
		}

		this.delete(item);
		this.mongoTemplate.save(item, this.collectionName);
	}

	/**
	 * Finds an item in the repository and the database by its object identifier
	 *
	 * @param itemId
	 *          the string identifying the item
	 * @return the item mapped to the identifier
	 * @throws ItemNotFoundRepositoryException
	 *           if there is no such item
	 */
	public T find(String itemId) {
		String objId = itemId;

		List<T> items = this.mongoTemplate.find(Query.query(Criteria.where(GenericDBObject.ID).is(objId)), this.className, this.collectionName);

		if (items != null && items.size() > 0) {
			return items.get(0);
		}

		return null;
	}

	/**
	 *
	 * @param itemId
	 * @param _class
	 * @return
	 * @throws ItemNotFoundRepositoryException
	 */
	public List<T> findAll() {

		List<T> items = this.mongoTemplate.findAll(this.className, this.collectionName);

		if (items != null && items.size() > 0) {
			return items;
		}

		return null;
	}

}
