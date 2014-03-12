package com.playnomics.android.client;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Set;
import java.util.TreeMap;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.playnomics.android.events.CustomEvent;
import com.playnomics.android.session.GameSessionInfo;
import com.playnomics.android.util.Config;
import com.playnomics.android.util.LargeGeneratedId;
import com.playnomics.android.util.Logger;
import com.playnomics.android.util.UnitTestLogWriter;
import com.playnomics.android.util.Util;


public class EventWorkerTest {
	
	@Mock 
	private HttpURLConnection connectionMock;
	@Mock 
	private HttpConnectionFactory factoryMock;
	
	private EventQueue queue;
	private EventWorker worker;
	private GameSessionInfo sessionInfo;
	private CustomEvent event;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	
		when(factoryMock.startConnectionForUrl(any(String.class))).thenReturn(connectionMock);
		when(factoryMock.buildUrl(any(String.class), any(String.class),
				any(TreeMap.class))).thenCallRealMethod();
		
		Logger logger = new Logger(new UnitTestLogWriter());
		Config config = new Config();
		Util util = new Util(logger);
		queue = new EventQueue(config, factoryMock);
		worker = new EventWorker(queue, factoryMock, logger);
		
		sessionInfo = new GameSessionInfo(1L, "userId", "breadcrumbId", new LargeGeneratedId(10L));
		event = new CustomEvent(config, util, sessionInfo, "my event");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testRequestSuccesses() throws IOException, InterruptedException {
		when(connectionMock.getResponseCode()).thenReturn(200);
		queue.enqueueEvent(event);
		worker.start();
		Thread.sleep(1500);
		worker.stop();
		Set<String> unprocessed = worker.getAllUnprocessedEvents();
		assertTrue("Unprocessed is empty", unprocessed.isEmpty());
	}
	
	@Test()
	public void testRequestThrowsException() throws InterruptedException, IOException{
		when(connectionMock.getResponseCode()).thenThrow(new IOException());
		queue.enqueueEvent(event);
		worker.start();
		Thread.sleep(1500);
		
		assertFalse("Worker has been stopped", worker.isRunning());
		Set<String> unprocessed = worker.getAllUnprocessedEvents();
		assertFalse("Unprocessed is not empty", unprocessed.isEmpty());
	}
}
