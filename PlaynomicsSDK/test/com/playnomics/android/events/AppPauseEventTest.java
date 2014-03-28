package com.playnomics.android.events;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.playnomics.android.events.AppPauseEvent;
import com.playnomics.android.session.GameSessionInfo;
import com.playnomics.android.util.Config;
import com.playnomics.android.util.EventTime;
import com.playnomics.android.util.IConfig;
import com.playnomics.android.util.LargeGeneratedId;
import com.playnomics.android.util.Util;

public class AppPauseEventTest extends PlaynomicsEventTest {

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
	public void testAppPause() {
		Util util = new Util(logger);
		LargeGeneratedId instanceId = new LargeGeneratedId(util);
		GameSessionInfo sessionInfo = getGameSessionInfo();
		IConfig config = new Config();
		// 1 minute ago
		Calendar calendar = new GregorianCalendar();
		calendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND) - 60);
		EventTime startTime = new EventTime(calendar.getTimeInMillis());

		int sequenceNumber = 10;
		int touches = 5;
		int totalTouches = 15;

		int keysPressed = 0;
		int totalKeysPressed = 0;
		int delayInMinutes = 1;

		AppPauseEvent event = new AppPauseEvent(config, sessionInfo,
				instanceId, startTime, sequenceNumber, touches, totalTouches, delayInMinutes);
		testCommonEventParameters(config, event, sessionInfo);

		Map<String, Object> params = event.getEventParameters();
		assertEquals("Insance ID is set", instanceId, params.get("i"));
		assertEquals("Sequence is set", sequenceNumber, params.get("q"));
		assertEquals("Touches is set", touches, params.get("c"));
		assertEquals("Total Touches is set", totalTouches, params.get("e"));
		assertEquals("Keys pressed is set", keysPressed, params.get("k"));
		assertEquals("Total keys pressed is set", totalKeysPressed,
				params.get("l"));
		assertEquals("Session start time is set", startTime, params.get("r"));
		assertEquals("Capture mode is set", config.getCollectionMode(),
				params.get("m"));
		assertEquals("Interval is set in millseconds",
				delayInMinutes*60*1000, params.get("d"));
	}
}
