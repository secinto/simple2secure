package com.simple2secure.test.db;

import java.io.File;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.simple2secure.api.config.ConfigItems;
import com.simple2secure.api.model.Config;
import com.simple2secure.commons.file.FileUtil;
import com.simple2secure.probe.utils.JsonUtils;
import com.simple2secure.test.TestBase;

public class TestDBInitialisation extends TestBase {

	@Before
	public void setUp() {
		this.initialize();
	}

	@After
	public void tearDown() {
		//this.cleanup();
	}

	@Test
	public void testDBInitializationNormal() throws Exception {
		log.debug("Starting DB initialization");
		/*List<Config> configObjects = getConfigDao().getAll();
		if (configObjects == null) {
			log.info("No config objects found as expected.!");
		}
		Assert.assertNotNull(configObjects);
		Assert.assertEquals(configObjects.size(), 0);*/
		log.debug("Finished DB initialization");
	}

	@Test
	public void testStoreConfigObject() throws Exception {
		log.debug("Reading JSON configuration");

		File potentialConfig = FileUtil.getFile("src/test/resources/" + ConfigItems.CONFIG_JSON_LOCATION);
		Assert.assertNotNull(potentialConfig);
		/*if (potentialConfig != null && potentialConfig.exists()) {
			Config currentConfig = JsonUtils.readConfigFromFile(potentialConfig);
			Assert.assertNotNull(currentConfig);
			getConfigDao().save(currentConfig);

			Config obtainedConfig = getConfigDao().getByID(currentConfig.getId());

			Assert.assertEquals(currentConfig, obtainedConfig);
		}*/
	}

}
