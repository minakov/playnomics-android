package com.playnomics.android.client;

import java.util.TreeMap;

import com.playnomics.android.client.AssetClient.ResponseStatus;
import com.playnomics.android.messaging.HtmlAd;
import com.playnomics.android.messaging.HtmlAdFactory;
import com.playnomics.android.messaging.NativeCloseButton;
import com.playnomics.android.messaging.Placement;
import com.playnomics.android.messaging.Placement.PlacementState;
import com.playnomics.android.session.Session;
import com.playnomics.android.util.IConfig;
import com.playnomics.android.util.Logger;
import com.playnomics.android.util.Logger.LogLevel;
import com.playnomics.android.util.Util;

public class PlacementDataClient {
	private IConfig config;
	private Session session;
	private Logger logger;
	private HtmlAdFactory adFactory;
	private AssetClient assetClient;
	private Util util;

	public void setSession(Session session) {
		this.session = session;
	}

	public PlacementDataClient(AssetClient assetClient, IConfig config,
			Logger logger, HtmlAdFactory adFactory, Util util) {
		this.logger = logger;
		this.config = config;
		this.adFactory = adFactory;
		this.assetClient = assetClient;
		this.util = util;
	}

	public Thread loadPlacementInBackground(final Placement placement) {
		Runnable task = new Runnable() {
			public void run() {
				String messagingApiUrl = config.getMessagingUrl();

				String adsPath = config.getMessagingPathAds();

				TreeMap<String, Object> queryParams = new TreeMap<String, Object>();
				queryParams.put(config.getMessagingPlacementNameKey(),
						placement.getPlacementName());
				queryParams.put(config.getAndroidIdKey(),
						session.getAndroidId());
				queryParams.put(config.getMessagingLanguageKey(),
						util.getDeviceLanguage());
				queryParams.put(config.getApplicationIdKey(),
						session.getApplicationId());
				queryParams.put(config.getUserIdKey(), session.getUserId());
				queryParams.put(config.getSdkNameKey(), config.getSdkName());
				queryParams.put(config.getSdkVersionKey(),
						config.getSdkVersion());

				AssetClient.AssetResponse jsonResponse = assetClient
						.requestAsset(messagingApiUrl, adsPath, queryParams);

				if (jsonResponse.getStatus() == ResponseStatus.SUCCESS) {
					try {
						HtmlAd htmlAd = adFactory.createDataFromBytes(
								jsonResponse.getData(),
								placement.getPlacementName());

						if (htmlAd.getCloseButton() instanceof NativeCloseButton) {
							NativeCloseButton closeButton = (NativeCloseButton) htmlAd
									.getCloseButton();
							AssetClient.AssetResponse imageResponse = assetClient
									.requestAsset(closeButton.getImageUrl());
							if (imageResponse.getStatus() == ResponseStatus.SUCCESS) {
								closeButton.setImageData(imageResponse
										.getData());
								placement.updatePlacementData(htmlAd);
							} else {
								placement.setState(PlacementState.LOAD_FAILED);
							}
						} else {
							placement.updatePlacementData(htmlAd);
						}
					} catch (Exception ex) {
						logger.log(LogLevel.WARNING, ex,
								"Could not fetch message for placement");
						placement.setState(PlacementState.LOAD_FAILED);
					}
				} else {
					placement.setState(PlacementState.LOAD_FAILED);
				}
			}
		};
		Thread thread = new Thread(task);
		thread.start();
		return thread;
	}
}
