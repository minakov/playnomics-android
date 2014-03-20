package com.playnomics.android.session;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

import com.playnomics.android.session.HeartBeatHandler;
import com.playnomics.android.session.HeartBeatProducer;
import com.playnomics.android.util.Config;

public class HeartBeatProducerTest {

	@Mock
	private HeartBeatHandler handler;

	private HeartBeatProducer producer;
	private int intervalSeconds;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		intervalSeconds = 1;
		//producer = new HeartBeatProducer(intervalSeconds);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testProducer() throws InterruptedException {
/*		producer.start(handler);
		Thread.sleep((long) (intervalSeconds * 1.5 * 1000));
		producer.stop();
		verify(handler, Mockito.atLeastOnce()).onHeartBeat(intervalSeconds);*/
	}
}
