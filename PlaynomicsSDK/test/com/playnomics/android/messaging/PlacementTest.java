package com.playnomics.android.messaging;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import android.app.Activity;

import com.playnomics.android.client.AssetClient;
import com.playnomics.android.client.PlacementDataClient;
import com.playnomics.android.client.AssetClient.AssetResponse;
import com.playnomics.android.client.AssetClient.ResponseStatus;
import com.playnomics.android.messaging.HtmlAd;
import com.playnomics.android.messaging.HtmlAdFactory;
import com.playnomics.android.messaging.HtmlCloseButton;
import com.playnomics.android.messaging.NativeCloseButton;
import com.playnomics.android.messaging.Placement;
import com.playnomics.android.messaging.Target;
import com.playnomics.android.messaging.Placement.IPlacementStateObserver;
import com.playnomics.android.messaging.Placement.PlacementState;
import com.playnomics.android.messaging.Target.TargetType;
import com.playnomics.android.messaging.ui.PlayDialog;
import com.playnomics.android.messaging.ui.RenderTaskFactory;
import com.playnomics.android.sdk.IPlaynomicsPlacementDelegate;
import com.playnomics.android.session.ICallbackProcessor;
import com.playnomics.android.session.Session;
import com.playnomics.android.util.Config;
import com.playnomics.android.util.IConfig;
import com.playnomics.android.util.Logger;
import com.playnomics.android.util.UnitTestLogWriter;
import com.playnomics.android.util.Util;
import com.playnomics.android.util.Logger.LogLevel;

public class PlacementTest {

	@Mock
	private Util utilMock;
	@Mock
	private ICallbackProcessor processorMock;
	@Mock
	private Session sessionMock;
	@Mock
	private IPlacementStateObserver observerMock;
	@Mock
	private HtmlAdFactory htmlAdFactory;
	@Mock
	private HtmlAd adMock;
	@Mock
	private Target targetMock;
	
	@Mock
	private AssetClient assetClientMock;
	@Mock
	private AssetResponse jsonAssetResponseMock;
	@Mock
	private AssetResponse imageAssetResponseMock;
	@Mock
	private RenderTaskFactory renderTaskFactoryMock;
	@Mock
	private Activity activityMock;

	@Mock
	private IPlaynomicsPlacementDelegate delegateMock;

	@Mock
	private NativeCloseButton nativeCloseMock;

	@Mock
	private HtmlCloseButton htmlCloseMock;

	@Mock
	private Runnable layoutPlacementTaskMock;
	@Mock
	private Runnable showPlacementTaskMock;

	private PlacementDataClient dataClient;
	private Placement placement;
	private Logger logger;

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

		when(sessionMock.getApplicationId()).thenReturn(1L);
		when(sessionMock.getAndroidId()).thenReturn("deviceId");
		when(sessionMock.getUserId()).thenReturn("userId");

		when(utilMock.getDeviceLanguage()).thenReturn("en");
		
		IConfig config = new Config();

		logger = new Logger(new UnitTestLogWriter());
		logger.setLogLevel(LogLevel.VERBOSE);

		when(
				assetClientMock.requestAsset(any(String.class),
						any(String.class), any(TreeMap.class))).thenReturn(

		jsonAssetResponseMock);

		dataClient = new PlacementDataClient(assetClientMock, config, logger,
				htmlAdFactory, utilMock);
		dataClient.setSession(sessionMock);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSuccessJsonWithHtmlCloseButton() throws IOException,
			InterruptedException, JSONException {
		byte[] data = new byte[1];
		when(jsonAssetResponseMock.getData()).thenReturn(data);
		when(jsonAssetResponseMock.getStatus()).thenReturn(
				ResponseStatus.SUCCESS);

		when(htmlAdFactory.createDataFromBytes(data, "placementName")).thenReturn(adMock);

		when(adMock.getCloseButton()).thenReturn(htmlCloseMock);

		placement = new Placement("placementName", processorMock, utilMock,
				logger, observerMock, renderTaskFactoryMock);
		Thread thread = dataClient.loadPlacementInBackground(placement);
		thread.join();

		verifyPlacementState(PlacementState.LOAD_COMPLETE);
	}

	@Test
	public void testSuccessJsonWithNativeCloseButton() throws IOException,
			InterruptedException, JSONException {
		when(assetClientMock.requestAsset(any(String.class))).thenReturn(
				imageAssetResponseMock);

		byte[] data = new byte[1];
		when(jsonAssetResponseMock.getData()).thenReturn(data);
		when(jsonAssetResponseMock.getStatus()).thenReturn(
				ResponseStatus.SUCCESS);

		when(imageAssetResponseMock.getData()).thenReturn(data);
		when(imageAssetResponseMock.getStatus()).thenReturn(
				ResponseStatus.SUCCESS);

		when(htmlAdFactory.createDataFromBytes(data, "placementName")).thenReturn(adMock);

		when(adMock.getCloseButton()).thenReturn(nativeCloseMock);

		placement = new Placement("placementName", processorMock, utilMock,
				logger, observerMock, renderTaskFactoryMock);
		Thread thread = dataClient.loadPlacementInBackground(placement);
		thread.join();

		verifyPlacementState(PlacementState.LOAD_COMPLETE);
	}

	@Test
	public void testSuccessJsonRequestWithNativeFailure() throws IOException,
			InterruptedException, JSONException {
		when(assetClientMock.requestAsset(any(String.class))).thenReturn(
				imageAssetResponseMock);

		byte[] data = new byte[1];
		when(jsonAssetResponseMock.getData()).thenReturn(data);
		when(jsonAssetResponseMock.getStatus()).thenReturn(
				ResponseStatus.SUCCESS);

		when(imageAssetResponseMock.getStatus()).thenReturn(
				ResponseStatus.FAILURE);

		when(htmlAdFactory.createDataFromBytes(data, "placementName")).thenReturn(adMock);

		when(adMock.getCloseButton()).thenReturn(nativeCloseMock);

		placement = new Placement("placementName", processorMock, utilMock,
				logger, observerMock, renderTaskFactoryMock);
		Thread thread = dataClient.loadPlacementInBackground(placement);
		thread.join();

		verifyPlacementState(PlacementState.LOAD_FAILED);
	}

	@Test
	public void testFailedJsonRequest() throws IOException,
			InterruptedException {
		when(jsonAssetResponseMock.getStatus()).thenReturn(
				ResponseStatus.FAILURE);

		placement = new Placement("placementName", processorMock, utilMock,
				logger, observerMock, renderTaskFactoryMock);
		Thread thread = dataClient.loadPlacementInBackground(placement);
		thread.join();
		verifyPlacementState(PlacementState.LOAD_FAILED);
	}

	@Test
	public void testFailEncodingException() throws IOException,
			InterruptedException, UnsupportedEncodingException, JSONException {
		byte[] data = new byte[1];
		when(jsonAssetResponseMock.getData()).thenReturn(data);
		when(jsonAssetResponseMock.getStatus()).thenReturn(
				ResponseStatus.SUCCESS);

		when(htmlAdFactory.createDataFromBytes(data, "placementName")).thenThrow(
				new UnsupportedEncodingException("Failed"));

		placement = new Placement("placementName", processorMock, utilMock,
				logger, observerMock, renderTaskFactoryMock);
		Thread thread = dataClient.loadPlacementInBackground(placement);
		thread.join();
		
		verifyPlacementState(PlacementState.LOAD_FAILED);
	}

	@Test
	public void testFailJSONException() throws IOException,
			InterruptedException, UnsupportedEncodingException, JSONException {
		byte[] data = new byte[1];
		when(jsonAssetResponseMock.getData()).thenReturn(data);
		when(jsonAssetResponseMock.getStatus()).thenReturn(
				ResponseStatus.SUCCESS);

		when(htmlAdFactory.createDataFromBytes(data, "placementName")).thenThrow(
				new JSONException("Failed"));

		placement = new Placement("placementName", processorMock, utilMock,
				logger, observerMock, renderTaskFactoryMock);
		Thread thread = dataClient.loadPlacementInBackground(placement);
		thread.join();
		
		verifyPlacementState(PlacementState.LOAD_FAILED);
	}

	private void verifyPlacementState(PlacementState state) {
		if (state == PlacementState.LOAD_COMPLETE) {
			when(
					renderTaskFactoryMock.createLayoutPlacementTask(placement,
							adMock, activityMock, placement, placement, observerMock))
					.thenReturn(layoutPlacementTaskMock);
			
			assertEquals("Placement loaded", PlacementState.LOAD_COMPLETE,
					placement.getState());

			placement.show(activityMock, delegateMock);
			verify(utilMock).runTaskOnActivityUIThread(layoutPlacementTaskMock, activityMock);
		} else if (state == PlacementState.LOAD_FAILED) {
			assertEquals("Placement not loaded", PlacementState.LOAD_FAILED,
					placement.getState());

			placement.show(activityMock, delegateMock);
			verify(utilMock, Mockito.never()).runTaskOnActivityUIThread(layoutPlacementTaskMock, activityMock);
			verify(delegateMock).onRenderFailed();
		}
	}
	
	@Test
	public void testWebViewLoadSuccess() throws IOException, InterruptedException, JSONException{
		testSuccessJsonWithHtmlCloseButton();
		when(renderTaskFactoryMock.createShowPlacementTask(any(PlayDialog.class))).thenReturn(showPlacementTaskMock);
		
		String impressionUrl = "http://impressionUrl";
		Map<String, Object> data = new HashMap<String, Object>();
		
		when(adMock.getImpressionUrl()).thenReturn(impressionUrl);
		when(adMock.getTarget()).thenReturn(targetMock);
		when(targetMock.getTargetData()).thenReturn(data);
		
		placement.onLoadComplete();
		//simulate a rotation
		placement.onLoadComplete();
		
		verify(utilMock, Mockito.atLeast(2)).runTaskOnActivityUIThread(showPlacementTaskMock, activityMock);
		verify(processorMock).processUrlCallback(impressionUrl);
		verify(delegateMock).onShow(data);
	}
	
	@Test
	public void testWebViewLoadFailed() throws IOException, InterruptedException, JSONException{
		testSuccessJsonWithHtmlCloseButton();
		placement.onLoadFailure(1);	
		assertEquals("Placement not loaded", PlacementState.LOAD_FAILED,
				placement.getState());
		verify(delegateMock).onRenderFailed();
	}
	
	@Test
	public void testWebViewTouchTargetData() throws IOException, InterruptedException, JSONException{
		testSuccessJsonWithHtmlCloseButton();
		
		String clickUrl = "http://clickUrl";
		String clickLink = "pn://clickLink";
		
		when(adMock.getClickUrl()).thenReturn(clickUrl);
		when(adMock.getClickLink()).thenReturn(clickLink);
		when(adMock.getTarget()).thenReturn(targetMock);
		
		Map<String, Object> data = new HashMap<String, Object>();
		when(targetMock.getTargetData()).thenReturn(data);
		when(targetMock.getTargetType()).thenReturn(TargetType.DATA);
	
		placement.onUrlLoading(clickLink);
		
		verify(processorMock).processUrlCallback(clickUrl);
		verify(delegateMock).onTouch(data);
	}
	
	@Test
	public void testWebViewTouchTargetUrl() throws IOException, InterruptedException, JSONException{
		testSuccessJsonWithHtmlCloseButton();
		
		String clickUrl = "http://clickUrl";
		String clickLink = "pn://clickLink";
		String targetUrl = "http://targetUrl";
		
		when(adMock.getClickUrl()).thenReturn(clickUrl);
		when(adMock.getClickLink()).thenReturn(clickLink);
		when(adMock.getTarget()).thenReturn(targetMock);
		when(adMock.getTarget()).thenReturn(targetMock);
		
		when(targetMock.getTargetUrl()).thenReturn(targetUrl);
		when(targetMock.getTargetType()).thenReturn(TargetType.URL);
		
		placement.onUrlLoading(clickLink);

		verify(processorMock).processUrlCallback(clickUrl);
		verify(utilMock).startUriIntent(targetUrl, activityMock);
	}
	
	@Test
	public void testWebViewTouchNullTarget() throws IOException, InterruptedException, JSONException{
		testSuccessJsonWithHtmlCloseButton();
		
		String clickUrl = "http://clickUrl";
		String clickLink = "pn://clickLink";
		String targetUrl = null;
		
		when(adMock.getClickUrl()).thenReturn(clickUrl);
		when(adMock.getClickLink()).thenReturn(clickLink);
		when(adMock.getTarget()).thenReturn(targetMock);

		when(targetMock.getTargetUrl()).thenReturn(targetUrl);
		when(targetMock.getTargetType()).thenReturn(TargetType.URL);
		
		placement.onUrlLoading(clickLink);
		
		verify(processorMock).processUrlCallback(clickUrl);
		verify(utilMock, Mockito.never()).startUriIntent(targetUrl,
				activityMock);
	}
	
	@Test
	public void testWebViewTouchCloseLink() throws IOException, InterruptedException, JSONException{
		testSuccessJsonWithHtmlCloseButton();
		
		String closeLink = "pn://close";
		String closeUrl = "http://closeUrl";
		when(adMock.getCloseUrl()).thenReturn(closeUrl);
		when(htmlCloseMock.getCloseLink()).thenReturn(closeLink);
		
		when(adMock.getTarget()).thenReturn(targetMock);
		
		Map<String, Object> data = new HashMap<String, Object>();
		when(targetMock.getTargetData()).thenReturn(data);
		when(targetMock.getTargetType()).thenReturn(TargetType.DATA);
		
		placement.onUrlLoading(closeLink);

		verify(processorMock).processUrlCallback(closeUrl);
		verify(delegateMock).onClose(data);
	}
	
	@Test
	public void testNativeCloseButtonTouched() throws IOException, InterruptedException, JSONException{
		testSuccessJsonWithNativeCloseButton();
		
		String closeUrl = "http://closeUrl";
		when(adMock.getCloseUrl()).thenReturn(closeUrl);
		
		when(adMock.getTarget()).thenReturn(targetMock);
		
		Map<String, Object> data = new HashMap<String, Object>();
		when(targetMock.getTargetData()).thenReturn(data);
		when(targetMock.getTargetType()).thenReturn(TargetType.DATA);
		
		placement.onTouch();
		
		verify(processorMock).processUrlCallback(closeUrl);
		verify(delegateMock).onClose(data);
	}
}