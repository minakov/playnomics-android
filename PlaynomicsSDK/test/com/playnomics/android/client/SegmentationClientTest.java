package com.playnomics.android.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
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

import com.playnomics.android.client.AssetClient.AssetResponse;
import com.playnomics.android.client.AssetClient.ResponseStatus;
import com.playnomics.android.segments.UserSegmentIds;
import com.playnomics.android.sdk.IPlaynomicsSegmentationDelegate;
import com.playnomics.android.session.Session;
import com.playnomics.android.util.Config;
import com.playnomics.android.util.IConfig;
import com.playnomics.android.util.Logger;
import com.playnomics.android.util.UnitTestLogWriter;
import com.playnomics.android.util.Util;
import com.playnomics.android.util.Logger.LogLevel;

public class SegmentationClientTest {

	@Mock
	private Util utilMock;
	@Mock
	private Session sessionMock;
	
	@Mock
	private AssetClient assetClientMock;
	@Mock
	private AssetResponse jsonAssetResponseMock;
	@Mock
	private Activity activityMock;

	@Mock
	private IPlaynomicsSegmentationDelegate delegateMock;

	private Logger logger;
	private IConfig config;

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
		
		config = new Config();

		logger = new Logger(new UnitTestLogWriter());
		logger.setLogLevel(LogLevel.VERBOSE);

		when(
				assetClientMock.requestAsset(any(String.class),
						any(String.class), any(TreeMap.class))).thenReturn(

		jsonAssetResponseMock);

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSuccessJson() throws IOException,
			InterruptedException, JSONException {

		byte[] data = "{\"status\": \"Ok\", \"segments\": [17, 1790, 3636, 5087, 5088]}".getBytes("UTF-8");
		System.out.println(data.toString());
		when(jsonAssetResponseMock.getData()).thenReturn(data);
		when(jsonAssetResponseMock.getStatus()).thenReturn(
				ResponseStatus.SUCCESS);
		when(jsonAssetResponseMock.getResponseCode()).thenReturn(200);
		
		when(
				assetClientMock.requestAsset(any(String.class),
						any(String.class), any(TreeMap.class))).thenReturn(

		jsonAssetResponseMock);

		UserSegmentIds userSegmentIds = new UserSegmentIds(sessionMock, config, logger, delegateMock);
		userSegmentIds.onBackgroundThread();
	}

	@Test
	public void testErrorJson() throws IOException,
			InterruptedException, JSONException {
		byte[] data = new byte[1];
		when(jsonAssetResponseMock.getData()).thenReturn(data);
		when(jsonAssetResponseMock.getStatus()).thenReturn(
				ResponseStatus.FAILURE);

		UserSegmentIds userSegmentIds = new UserSegmentIds(sessionMock, config, logger, delegateMock);
		userSegmentIds.onBackgroundThread();
		/*assertEquals("Segementation json not loaded ","Error",
				userSegmentIds.getError());*/
	}

}
