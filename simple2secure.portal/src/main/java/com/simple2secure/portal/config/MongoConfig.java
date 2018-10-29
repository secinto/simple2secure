package com.simple2secure.portal.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.MongoClient;
import com.simple2secure.portal.dao.ExtendedMongoTemplate;
import com.simple2secure.portal.utils.YamlPropertySourceFactory;

@Configuration
@PropertySource(factory = YamlPropertySourceFactory.class, value = "classpath:application.yml")
@EnableMongoRepositories
public class MongoConfig extends AbstractMongoConfiguration {

	@Value("${spring.data.mongodb.host}")
	private String host;

	@Value("${spring.data.mongodb.port}")
	private Integer port;

	@Value("${spring.data.mongodb.database}")
	private String database;

	@Override
	protected String getDatabaseName() {
		return database;
	}

	@Override
	public MongoClient mongoClient() {
		return new MongoClient(host, port);
	}

	@Override
	@Bean
	public MongoDbFactory mongoDbFactory() {
		MongoClient mongoClient = new MongoClient(host, port);
		return new SimpleMongoDbFactory(mongoClient, database);
	}

	@Override
	@Bean
	public ExtendedMongoTemplate mongoTemplate() throws Exception {
		ExtendedMongoTemplate mongoTemplate = new ExtendedMongoTemplate(mongoDbFactory());
		return mongoTemplate;
	}
}
