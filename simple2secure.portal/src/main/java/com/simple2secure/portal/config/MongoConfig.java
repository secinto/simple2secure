package com.simple2secure.portal.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.authentication.UserCredentials;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.simple2secure.portal.dao.ExtendedMongoTemplate;

@Configuration
@PropertySource(value = { "classpath:application.properties" })
@EnableMongoRepositories
@Profile("prod")
public class MongoConfig extends AbstractMongoConfiguration {

	@Value("${spring.data.mongodb.host}")
	private String host;

	@Value("${spring.data.mongodb.port}")
	private Integer port;

	@Value("${spring.data.mongodb.database}")
	private String database;

	@SuppressWarnings("deprecation")
	@Bean
	public MongoDbFactory mongoDbFactory() throws Exception {
		MongoClient mongoClient = new MongoClient(this.host, this.port);
		UserCredentials userCredentials = new UserCredentials("", "");
		return new SimpleMongoDbFactory(mongoClient, this.database, userCredentials);
	}

	@Bean
	public ExtendedMongoTemplate mongoTemplate() throws Exception {
		ExtendedMongoTemplate mongoTemplate = new ExtendedMongoTemplate(mongoDbFactory());
		return mongoTemplate;
	}

	@Override
	protected String getDatabaseName() {
		return this.database;
	}

	@Override
	public Mongo mongo() throws Exception {
		return new MongoClient(this.host, this.port);
	}

}
