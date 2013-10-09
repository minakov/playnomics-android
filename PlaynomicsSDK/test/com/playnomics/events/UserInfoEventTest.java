package com.playnomics.events;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.playnomics.events.MilestoneEvent.MilestoneType;
import com.playnomics.session.GameSessionInfo;
import com.playnomics.util.Config;
import com.playnomics.util.Util;

public class UserInfoEventTest extends PlaynomicsEventTest {

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
	public void testUserInfoAttribution() {
		GameSessionInfo sessionInfo = getGameSessionInfo();
		Config config = new Config();
		
		String source = "source";
		String campaign = "campaign";
		Date installDate = new Date(new GregorianCalendar(Util.TIME_ZONE_GMT).get(Calendar.DATE));
		
		UserInfoEvent event = new UserInfoEvent(config, sessionInfo, source, campaign, installDate);
		testCommonEventParameters(config, event, sessionInfo);

		Map<String, Object> params = event.getEventParameters();
		assertEquals("Source is set", source, params.get("po"));
		assertEquals("Campaign is set", campaign, params.get("pm"));
		assertEquals("Install Date is set", installDate.getTime(), params.get("pi"));
	}

	@Test
	public void testUserInfoDevice(){
		GameSessionInfo sessionInfo = getGameSessionInfo();
		Config config = new Config();
		
		String pushRegistrationId = "pushId";
		String deviceId = "deviceId";
		
		UserInfoEvent event = new UserInfoEvent(config, sessionInfo, pushRegistrationId, deviceId);
		testCommonEventParameters(config, event, sessionInfo);

		Map<String, Object> params = event.getEventParameters();
		assertEquals("Device ID is set", deviceId, params.get("deviceId"));
		assertEquals("Push ID is set", pushRegistrationId, params.get("pushTok"));
	}
}
