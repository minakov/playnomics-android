package com.playnomics.session;

import static org.mockito.Mockito.verify;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import android.app.Activity;

import com.playnomics.util.Util;

public class ActivityObserverTest {

	@Mock
	private Activity activityMock;
	@Mock
	private TouchEventHandler handlerMock;
	@Mock
	private SessionStateMachine stateMachineMock;
	@Mock
	private Util utilMock;
	
	private ActivityObserver observer;
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		observer = new ActivityObserver(utilMock);
		observer.setStateMachine(stateMachineMock); 
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSingleActivityLifecycle() {
		observer.observeNewActivity(activityMock, handlerMock);
		verify(stateMachineMock).resume();
	
		observer.forgetLastActivity();
		verify(stateMachineMock).pause();
	}
	
	@Test
	public void testMultipleActivitiesLifecycle() {
		observer.observeNewActivity(activityMock, handlerMock);
		observer.observeNewActivity(activityMock, handlerMock);
		verify(stateMachineMock, Mockito.atLeast(2)).resume();
		observer.forgetLastActivity();
		verify(stateMachineMock, Mockito.never()).pause();
	}
}
