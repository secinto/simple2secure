package com.simple2secure.test.portal.user;

import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import com.simple2secure.portal.Simple2SecurePortal;

@RunWith(SpringRunner.class)
@ComponentScan(basePackages = ("com.simple2secure.test.portal"))
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes= {Simple2SecurePortal.class, EmbeddedMongoAutoConfiguration.class, MongoAutoConfiguration.class})

public class UserRegisterTest {

}
