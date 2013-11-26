package com.playnomics.android.util;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.playnomics.android.util.Config;
import com.playnomics.android.util.IConfig;

public class ConfigTest {
	IConfig config;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		config = new Config();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testProductionEventsUrl() {
		assertEquals("Production Events URL", "https://e.a.playnomics.net/v1/",
				config.getEventsUrl());
	}

	@Test
	public void testOverrideEventsUrl() {
		config.setOverrideEventsUrl("https://e.c.playnomics.net/v1/");
		assertEquals("Production Events URL", "https://e.c.playnomics.net/v1/",
				config.getEventsUrl());
	}

	@Test
	public void testProductionMessagingUrl() {
		assertEquals("Production Messaging URL",
				"https://ads.a.playnomics.net/v3/", config.getMessagingUrl());
	}
	
	@Test
	public void testOverrideMessagingUrl() {
		config.setOverrideMessagingUrl("https://ads.c.playnomics.net/v1/");
		assertEquals("Production Events URL",
				"https://ads.c.playnomics.net/v1/", config.getMessagingUrl());
	}
}
