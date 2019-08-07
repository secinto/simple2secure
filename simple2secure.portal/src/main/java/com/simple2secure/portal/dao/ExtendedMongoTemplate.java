/**
 *
 */
package com.simple2secure.portal.dao;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.ReflectionUtils;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

/**
 * Extension of the {@link org.springframework.data.mongodb.core.MongoTemplate} implementing additional finder methods (e.g. by example, by
 * native query).
 *
 * @author skraxberger
 *
 */
public class ExtendedMongoTemplate extends MongoTemplate {

	static final Logger log = LoggerFactory.getLogger(ExtendedMongoTemplate.class);

	/**
	 * @param mongoClient
	 * @param databaseName
	 */
	public ExtendedMongoTemplate(MongoClient mongoClient, String databaseName) {
		super(mongoClient, databaseName);
	}

	/**
	 * Constructor used for a basic template configuration
	 *
	 * @param mongoDbFactory
	 */
	public ExtendedMongoTemplate(MongoDbFactory mongoDbFactory) {
		super(mongoDbFactory);
	}

	/**
	 * Constructor used for a basic template configuration.
	 *
	 * @param mongoDbFactory
	 * @param mongoConverter
	 */
	public ExtendedMongoTemplate(MongoDbFactory mongoDbFactory, MongoConverter mongoConverter) {
		super(mongoDbFactory, mongoConverter);
	}

	/**
	 * Convert a domain object to {@link DBObject} by the spring data mapping subsystem.
	 *
	 * @param <T>
	 * @param object
	 * @return
	 */
	public <T> DBObject convert(T object) {
		BasicDBObject dbDoc = new BasicDBObject();

		MongoConverter mongoConverter = getMongoConverter();

		mongoConverter.write(object, dbDoc);
		return dbDoc;
	}

	private MongoConverter getMongoConverter() {
		Field f = ReflectionUtils.findField(super.getClass(),
				"mongoConverter"/* MessageCodeUtil.getMessageCodeMessage(MessageCodeGeneral.mongo_converted) */); // $NON-NLS-1$
		ReflectionUtils.makeAccessible(f);
		return (MongoConverter) ReflectionUtils.getField(f, this);
	}

	private String determineCollection(Class<?> entityClass) {
		Method m = ReflectionUtils.findMethod(super.getClass(), /*
																														 * MessageCodeUtil.getMessageCodeMessage(
																														 * MessageCodeGeneral.mongo_collection_name_determiner)
																														 */"mongo_collection_name_determiner", // $NON-NLS-1$
				Class.class);
		ReflectionUtils.makeAccessible(m);
		return (String) ReflectionUtils.invokeMethod(m, this, entityClass);
	}

	/**
	 * Finder by "native" mongo db query. <br>
	 * ATTENTION findOne finds the first, no matter if others are following!
	 *
	 * @param <T>
	 * @param entityClass
	 * @param query
	 * @return
	 */
	public <T> T findOneByNativeQuery(Class<T> entityClass, Query query) {
		String collectionName = determineCollection(entityClass);
		return findOne(query, entityClass, collectionName);
	}

	/**
	 * Finder by "native" mongo db query.
	 *
	 * @param <T>
	 * @param entityClass
	 * @param query
	 * @return
	 */
	public <T> List<T> findByNativeQuery(Class<T> entityClass, Query query) {
		String collectionName = determineCollection(entityClass);
		return find(query, entityClass, collectionName);
	}
}
