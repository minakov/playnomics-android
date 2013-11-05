package com.playnomics.android.sdk;

import java.util.Date;

import android.app.Activity;
import android.content.Context;

import com.playnomics.android.client.AssetClient;
import com.playnomics.android.client.EventQueue;
import com.playnomics.android.client.EventWorker;
import com.playnomics.android.client.HttpConnectionFactory;
import com.playnomics.android.client.IEventQueue;
import com.playnomics.android.client.IEventWorker;
import com.playnomics.android.client.PlacementDataClient;
import com.playnomics.android.messaging.HtmlAdFactory;
import com.playnomics.android.messaging.MessagingManager;
import com.playnomics.android.messaging.ui.PlayViewFactory;
import com.playnomics.android.session.ActivityObserver;
import com.playnomics.android.session.HeartBeatProducer;
import com.playnomics.android.session.IActivityObserver;
import com.playnomics.android.session.IHeartBeatProducer;
import com.playnomics.android.session.Session;
import com.playnomics.android.util.AndroidLogger;
import com.playnomics.android.util.Config;
import com.playnomics.android.util.ContextWrapper;
import com.playnomics.android.util.IConfig;
import com.playnomics.android.util.LogWriter;
import com.playnomics.android.util.Logger;
import com.playnomics.android.util.Util;

public class Playnomics {
	private static final Object syncLock = new Object();
	private static Session instance;

	private static Logger logger;
	private static Util util;

	private static Session getInstance() {
		synchronized (Playnomics.syncLock) {
			if (instance == null) {
				LogWriter logWriter = new AndroidLogger("PLAYNOMICS");
				Playnomics.logger = new Logger(logWriter);

				HttpConnectionFactory connectionFactory = new HttpConnectionFactory(
						logger);
				IConfig config = new Config();
				Playnomics.util = new Util(logger);
				IEventQueue eventQueue = new EventQueue(config,
						connectionFactory);
				IEventWorker eventWorker = new EventWorker(eventQueue,
						connectionFactory, logger);
				IActivityObserver activityObserver = new ActivityObserver(util);
				IHeartBeatProducer heartbeatProducer = new HeartBeatProducer(
						config.getAppRunningIntervalSeconds());
				HtmlAdFactory adFactory = new HtmlAdFactory();
				AssetClient assetClient = new AssetClient(connectionFactory);
				PlacementDataClient placementDataClient = new PlacementDataClient(
						assetClient, config, logger, adFactory, util);

				PlayViewFactory viewFactory = new PlayViewFactory();
				MessagingManager messagingManager = new MessagingManager(
						config, placementDataClient, util, logger, viewFactory);
				instance = new Session(config, util, connectionFactory, logger,
						eventQueue, eventWorker, activityObserver,
						heartbeatProducer, messagingManager);
			}
			return instance;
		}
	}
	
	public static void setOverrideEventsUrl(String eventsUrl){
		Session session = getInstance();
		session.setOverrideEventsUrl(eventsUrl);
	}

	public static void setOverrideMessagingUrl(String messagingUrl){
		Session session = getInstance();
		session.setOverrideMessagingUrl(messagingUrl);
	}
	
	public static void start(Context context, long applicationId, String userId) {
		Session session = getInstance();
		session.setApplicationId(applicationId);
		session.setUserId(userId);
		ContextWrapper contextWrapper = new ContextWrapper(context, logger,
				util);
		session.start(contextWrapper);
	}

	public static void start(Context context, long applicationId) {
		Session session = getInstance();
		session.setApplicationId(applicationId);
		session.setUserId(null);

		ContextWrapper contextWrapper = new ContextWrapper(context, logger,
				util);
		session.start(contextWrapper);
	}

	public static void onActivityResumed(Activity activity) {
		Session session = getInstance();
		session.onActivityResumed(activity);
	}

	public static void onActivityPaused(Activity activity) {
		Session session = getInstance();
		session.onActivityPaused(activity);
	}

	public static void transactionInUSD(float priceInUSD, int quantity) {
		Session session = getInstance();
		session.transactionInUSD(priceInUSD, quantity);
	}

	public static void customEvent(String customEventName) {
		Session session = getInstance();
		session.customEvent(customEventName);
	}

	public static void attributeInstall(String source, String campaign,
			Date installDateUtc) {
		Session session = getInstance();
		session.attributeInstall(source, campaign, installDateUtc);
	}

	public static void attributeInstall(String source, String campaign) {
		Session session = getInstance();
		session.attributeInstall(source, campaign, null);
	}

	public static void attributeInstall(String source) {
		Session session = getInstance();
		session.attributeInstall(source, null, null);
	}

	public static void preloadPlacements(String... placementNames) {
		Session session = getInstance();
		session.preloadPlacements(placementNames);
	}

	public static void showPlacement(String placementName, Activity activity,
			IPlaynomicsPlacementDelegate delegate) {
		Session session = getInstance();
		session.showPlacement(placementName, activity, delegate);
	}
	
	public static void showPlacement(String placementName, Activity activity) {
		Session session = getInstance();
		session.showPlacement(placementName, activity, null);
	}
	
	public static void hidePlacement(String placementName) {
		Session session = getInstance();
		session.hidePlacement(placementName);
	}
}
