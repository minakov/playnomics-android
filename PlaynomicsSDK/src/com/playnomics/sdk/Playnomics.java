package com.playnomics.sdk;

import java.util.Date;

import android.app.Activity;
import android.content.Context;

import com.playnomics.client.AssetClient;
import com.playnomics.client.EventQueue;
import com.playnomics.client.EventWorker;
import com.playnomics.client.FrameDataClient;
import com.playnomics.client.HttpConnectionFactory;
import com.playnomics.client.IEventQueue;
import com.playnomics.client.IEventWorker;
import com.playnomics.events.MilestoneEvent.MilestoneType;
import com.playnomics.messaging.HtmlAdFactory;
import com.playnomics.messaging.MessagingManager;
import com.playnomics.messaging.ui.PlayViewFactory;
import com.playnomics.session.ActivityObserver;
import com.playnomics.session.HeartBeatProducer;
import com.playnomics.session.IActivityObserver;
import com.playnomics.session.IHeartBeatProducer;
import com.playnomics.session.Session;
import com.playnomics.util.AndroidLogger;
import com.playnomics.util.Config;
import com.playnomics.util.ContextWrapper;
import com.playnomics.util.IConfig;
import com.playnomics.util.LogWriter;
import com.playnomics.util.Logger;
import com.playnomics.util.Util;

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
				
				HttpConnectionFactory connectionFactory = new HttpConnectionFactory(logger);
				IConfig config = new Config();
				Playnomics.util = new Util(logger);
				IEventQueue eventQueue = new EventQueue(config, connectionFactory);
				IEventWorker eventWorker = new EventWorker(eventQueue, connectionFactory, logger);
				IActivityObserver activityObserver = new ActivityObserver(util);
				IHeartBeatProducer heartbeatProducer = new HeartBeatProducer(config.getAppRunningIntervalSeconds());
				HtmlAdFactory adFactory = new HtmlAdFactory();
				AssetClient assetClient = new AssetClient(connectionFactory);
				FrameDataClient frameAssetClient = new FrameDataClient(assetClient, config, logger, adFactory);
				
				PlayViewFactory viewFactory = new PlayViewFactory();
				MessagingManager messagingManager = new MessagingManager(config, frameAssetClient, util, logger, viewFactory);
				instance = new Session(config, util, connectionFactory, logger, eventQueue, eventWorker, activityObserver, heartbeatProducer, messagingManager);
			}
			return instance;
		}
	}
	
	public static void start(Context context, long applicationId, String userId){
		Session session = getInstance();
		session.setApplicationId(applicationId);
		session.setUserId(userId);	
		ContextWrapper contextWrapper = new ContextWrapper(context, logger, util);
		session.start(contextWrapper);
	}
	
	public static void start(Context context, long applicationId){
		Session session = getInstance();
		session.setApplicationId(applicationId);
		session.setUserId(null);
		
		ContextWrapper contextWrapper = new ContextWrapper(context, logger, util);
		session.start(contextWrapper);
	}
	
	public static void onActivityResumed(Activity activity){
		Session session = getInstance();
		session.onActivityResumed(activity);
	}
	
	public static void onActivityPaused(Activity activity){
		Session session = getInstance();
		session.onActivityPaused(activity);
	}
	
	public static void transactionInUSD(float priceInUSD, int quantity){
		Session session = getInstance();
		session.transactionInUSD(priceInUSD, quantity);
	}
	
	public static void milestone(MilestoneType milestoneType){
		Session session = getInstance();
		session.milestone(milestoneType);
	}
	
	public static void attributeInstall(String source, String campaign, Date installDateUtc){
		Session session = getInstance();
		session.attributeInstall(source, campaign, installDateUtc);
	}
	
	public static void attributeInstall(String source, String campaign){
		Session session = getInstance();
		session.attributeInstall(source, campaign, null);
	}
	
	public static void attributeInstall(String source){
		Session session = getInstance();
		session.attributeInstall(source, null, null);
	}
	
	public static void preloadFrameIds(String ... frameIds){
		
	}
	
	public static void showFrame(String frameId, IPlaynomicsFrameDelegate delegate){
		
	}
}
