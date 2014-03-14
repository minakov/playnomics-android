package com.playnomics.android.util;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.playnomics.android.util.ContextWrapper;
import com.playnomics.android.util.EventTime;
import com.playnomics.android.util.LargeGeneratedId;
import com.playnomics.android.util.Logger;
import com.playnomics.android.util.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class ContextWrapperTest {

	@Mock
	private SharedPreferences preferencesMock;

	@Mock
	private Editor editorMock;

	@Mock
	private Context contextMock;
	@Mock
	private Logger loggerMock;
	@Mock
	private Util utilMock;

	private ContextWrapper contextWrapper;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		when(
				contextMock.getSharedPreferences(eq(ContextWrapper.CACHE_NAME),
						anyInt())).thenReturn(preferencesMock);
		when(preferencesMock.edit()).thenReturn(editorMock);

		contextWrapper = new ContextWrapper(contextMock, loggerMock, utilMock);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetPreviousSessionId() {
		when(
				preferencesMock.getLong(ContextWrapper.SESSION_ID_KEY,
						ContextWrapper.DEFAULT_CACHE_VALUE)).thenReturn(10L);
		LargeGeneratedId previousSessionId = contextWrapper
				.getPreviousSessionId();
		assertEquals("Session ID is restored", 10L, previousSessionId.getId());
	}

	@Test
	public void testGetPreviousSessionIdNull() {
		when(
				preferencesMock.getLong(ContextWrapper.SESSION_ID_KEY,
						ContextWrapper.DEFAULT_CACHE_VALUE)).thenReturn(
				(long) ContextWrapper.DEFAULT_CACHE_VALUE);
		LargeGeneratedId previousSessionId = contextWrapper
				.getPreviousSessionId();
		assertNull("Session ID is null", previousSessionId);
	}

	@Test
	public void testSetPreviousSessionId() {
		LargeGeneratedId sessionId = new LargeGeneratedId(10L);
		contextWrapper.setPreviousSessionId(sessionId);
		verify(editorMock).putLong(ContextWrapper.SESSION_ID_KEY,
				sessionId.getId());
		verify(editorMock).commit();
	}

	@Test
	public void testGetLastEventTime() {
		when(
				preferencesMock.getLong(
						ContextWrapper.LAST_EVENT_TIME_CACHE_KEY,
						ContextWrapper.DEFAULT_CACHE_VALUE)).thenReturn(0L);
		EventTime lastEventTime = contextWrapper.getLastEventTime();
		assertEquals("Last Event Time is restored", 0L,
				lastEventTime.getTimeInMillis());
	}

	@Test
	public void testGetLastEventTimeNull() {
		when(
				preferencesMock.getLong(
						ContextWrapper.LAST_EVENT_TIME_CACHE_KEY,
						ContextWrapper.DEFAULT_CACHE_VALUE)).thenReturn(
				(long) ContextWrapper.DEFAULT_CACHE_VALUE);
		EventTime lastEventTime = contextWrapper.getLastEventTime();
		assertNull("Last Event Time is null", lastEventTime);
	}

	@Test
	public void testSetLastEventTime() {
		EventTime lastEventTime = new EventTime();
		contextWrapper.setLastEventTime(lastEventTime);
		verify(editorMock).putLong(ContextWrapper.LAST_EVENT_TIME_CACHE_KEY,
				lastEventTime.getTimeInMillis());
		verify(editorMock).commit();
	}

	@Test
	public void testGetLastSessionStartTimeTime() {
		when(
				preferencesMock.getLong(
						ContextWrapper.SESSION_START_TIME_CACHE_KEY,
						ContextWrapper.DEFAULT_CACHE_VALUE)).thenReturn(0L);
		EventTime startTime = contextWrapper.getLastSessionStartTime();
		assertEquals("Start Time is restored", 0L, startTime.getTimeInMillis());
	}

	@Test
	public void testGetLastSessionStartTimeNull() {
		when(
				preferencesMock.getLong(
						ContextWrapper.SESSION_START_TIME_CACHE_KEY,
						ContextWrapper.DEFAULT_CACHE_VALUE)).thenReturn(
				(long) ContextWrapper.DEFAULT_CACHE_VALUE);
		EventTime startTime = contextWrapper.getLastSessionStartTime();
		assertNull("Start Time is null", startTime);
	}

	@Test
	public void testSetLastSessionStartTime() {
		EventTime startTime = new EventTime();
		contextWrapper.setLastSessionStartTime(startTime);
		verify(editorMock).putLong(ContextWrapper.SESSION_START_TIME_CACHE_KEY,
				startTime.getTimeInMillis());
		verify(editorMock).commit();
	}

	@Test
	public void testGetPushId() {
		String pushId = "pushId";
		when(preferencesMock.getString(ContextWrapper.PUSH_ID_CACHE_KEY, null))
				.thenReturn(pushId);
		String result = contextWrapper.getPushRegistrationId();
		assertEquals("Push ID is restored", pushId, result);
	}

	@Test
	public void testSetPushId() {
		String pushId = "pushId";
		contextWrapper.setPushRegistrationId(pushId);
		verify(editorMock).putString(ContextWrapper.PUSH_ID_CACHE_KEY, pushId);
		verify(editorMock).commit();
	}

	@Test
	public void testSynchronizeNoChanges() {
		int oldVersion = 1;
		int currentVersion = 1;

		when(utilMock.getApplicationVersionFromContext(contextMock))
				.thenReturn(currentVersion);
		when(
				preferencesMock.getInt(ContextWrapper.APP_VERSION_CACHE_KEY,
						ContextWrapper.DEFAULT_CACHE_VALUE)).thenReturn(
				oldVersion);

		assertFalse("No changes to device settings",
				contextWrapper.isAppVersionChanged());
	}

	@Test
	public void testSynchronizeChanges() {
		int oldVersion = 1;
		int currentVersion = 2;

		when(utilMock.getApplicationVersionFromContext(contextMock))
				.thenReturn(currentVersion);
		when(
				preferencesMock.getInt(ContextWrapper.APP_VERSION_CACHE_KEY,
						ContextWrapper.DEFAULT_CACHE_VALUE)).thenReturn(
				oldVersion);
		// push id is reset
		assertTrue("Changes to device settings",
				contextWrapper.isAppVersionChanged());
		// the push ID is cleared
		verify(editorMock).putString(ContextWrapper.PUSH_ID_CACHE_KEY, null);
		// app version is set
		verify(editorMock).putInt(ContextWrapper.APP_VERSION_CACHE_KEY,
				currentVersion);
		verify(editorMock, Mockito.atLeastOnce()).commit();
	}
}
