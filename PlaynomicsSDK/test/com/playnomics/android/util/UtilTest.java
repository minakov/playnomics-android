package com.playnomics.android.util;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.playnomics.android.util.Logger;
import com.playnomics.android.util.Util;

public class UtilTest {
	private Logger logger = new Logger(new UnitTestLogWriter());

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGeneratedLongsAlwaysPositive() {
		Util util = new Util(logger);
		for (int i = 0; i < 1000; i++) {
			long num = util.generatePositiveRandomLong();
			assertTrue(num >= 0);
		}
	}

	@Test
	public void testStringIsEmptyOrNull() {
		assertTrue(Util.stringIsNullOrEmpty(null));
		assertTrue(Util.stringIsNullOrEmpty(""));
		assertFalse(Util.stringIsNullOrEmpty("test"));
	}

}
