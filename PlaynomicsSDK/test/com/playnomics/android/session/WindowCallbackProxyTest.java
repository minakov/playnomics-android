package com.playnomics.android.session;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.playnomics.android.session.TouchEventHandler;
import com.playnomics.android.session.WindowCallbackProxy;
import com.playnomics.android.util.Logger;
import com.playnomics.android.util.UnitTestLogWriter;

import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.view.accessibility.AccessibilityEvent;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Window.class, MotionEvent.class })
public class WindowCallbackProxyTest {

	private Logger logger;
	private static int touchCount = 0;
	private static Window.Callback stubCallback;
	private MotionEvent motionEventMock;
	@Mock
	private TouchEventHandler handlerMock;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		stubCallback = new Window.Callback() {
			public void onWindowFocusChanged(boolean arg0) {
			}

			public void onWindowAttributesChanged(LayoutParams arg0) {
			}

			public boolean onSearchRequested() {
				return false;
			}

			public boolean onPreparePanel(int arg0, View arg1, Menu arg2) {
				return false;
			}

			public void onPanelClosed(int arg0, Menu arg1) {
			}

			public boolean onMenuOpened(int arg0, Menu arg1) {
				return false;
			}

			public boolean onMenuItemSelected(int arg0, MenuItem arg1) {
				return false;
			}

			public void onDetachedFromWindow() {

			}

			public View onCreatePanelView(int arg0) {
				return null;
			}

			public boolean onCreatePanelMenu(int arg0, Menu arg1) {
				return false;
			}

			public void onContentChanged() {
			}

			public void onAttachedToWindow() {
			}

			public boolean dispatchTrackballEvent(MotionEvent arg0) {
				return false;
			}

			public boolean dispatchTouchEvent(MotionEvent arg0) {
				touchCount++;
				return false;
			}

			public boolean dispatchPopulateAccessibilityEvent(
					AccessibilityEvent arg0) {
				return false;
			}

			public boolean dispatchKeyEvent(KeyEvent arg0) {
				return false;
			}
		};
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		motionEventMock = PowerMockito.mock(MotionEvent.class);
		
		touchCount = 0;
		logger = new Logger(new UnitTestLogWriter());
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testTouchSingleEventDownReceived() {
		Mockito.when(motionEventMock.getActionMasked()).thenReturn(
				MotionEvent.ACTION_DOWN);

		stubCallback = WindowCallbackProxy.newCallbackProxyForActivity(
				stubCallback, handlerMock, logger);
		stubCallback.dispatchTouchEvent(motionEventMock);

		assertEquals("Touch event received in original callback", 1, touchCount);
		Mockito.verify(handlerMock).onTouchEventReceived();
	}

	@Test
	public void testTouchMultiTouchDownReceived() {
		Mockito.when(motionEventMock.getActionMasked()).thenReturn(
				MotionEvent.ACTION_POINTER_DOWN);

		stubCallback = WindowCallbackProxy.newCallbackProxyForActivity(
				stubCallback, handlerMock, logger);
		stubCallback.dispatchTouchEvent(motionEventMock);

		assertEquals("Touch event received in original callback", 1, touchCount);
		Mockito.verify(handlerMock).onTouchEventReceived();
	}

	@Test
	public void testTouchTouchUpNotReceived() {
		Mockito.when(motionEventMock.getActionMasked()).thenReturn(
				MotionEvent.ACTION_UP);

		stubCallback = WindowCallbackProxy.newCallbackProxyForActivity(
				stubCallback, handlerMock, logger);
		stubCallback.dispatchTouchEvent(motionEventMock);

		assertEquals("Touch event received in original callback", 1, touchCount);
		Mockito.verify(handlerMock, Mockito.never()).onTouchEventReceived();
	}
}
