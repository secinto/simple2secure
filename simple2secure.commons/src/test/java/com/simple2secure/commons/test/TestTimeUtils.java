package com.simple2secure.commons.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Date;

import org.junit.jupiter.api.Test;

import com.simple2secure.commons.time.TimeUtils;

public class TestTimeUtils {

	@Test
	public void testParseDate() throws Exception {
		Date parsedDate = TimeUtils.parseDate(TimeUtils.REPORT_DATE_FORMAT, "Wed Oct 16 16:37:23 CEST 2019");
		assertNotNull(parsedDate);
		String formatedDate = TimeUtils.formatDate(TimeUtils.SIMPLE_DATE_FORMAT, parsedDate);
		assertNotNull(formatedDate);
		assertEquals("10/16/2019", formatedDate);
	}
}
