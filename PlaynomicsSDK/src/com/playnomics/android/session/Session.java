package com.playnomics.android.session;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.Activity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.playnomics.android.client.IEventQueue;
import com.playnomics.android.client.IEventWorker;
import com.playnomics.android.client.IHttpConnectionFactory;
import com.playnomics.android.client.SegmentationClient;
import com.playnomics.android.events.AppPageEvent;
import com.playnomics.android.events.AppPauseEvent;
import com.playnomics.android.events.AppResumeEvent;
import com.playnomics.android.events.AppRunningEvent;
import com.playnomics.android.events.AppStartEvent;
import com.playnomics.android.events.CustomEvent;
import com.playnomics.android.events.ImplicitEvent;
import com.playnomics.android.events.TransactionEvent;
import com.playnomics.android.events.UserInfoEvent;
import com.playnomics.android.messaging.MessagingManager;
import com.playnomics.android.push.GcmManager;
import com.playnomics.android.push.GcmManager.ICloudMessagingHandler;
import com.playnomics.android.sdk.IGoogleCloudMessageConfig;
import com.playnomics.android.sdk.IPlacementDelegate;
import com.playnomics.android.sdk.IPlaynomicsSegmentationDelegate;
import com.playnomics.android.sdk.IPushConfig;
import com.playnomics.android.sdk.IPushNotificationDelegate;
import com.playnomics.android.util.CacheFile;
import com.playnomics.android.util.CacheFile.ICacheFileHandler;
import com.playnomics.android.util.ContextWrapper;
import com.playnomics.android.util.EventTime;
import com.playnomics.android.util.IConfig;
import com.playnomics.android.util.LargeGeneratedId;
import com.playnomics.android.util.Logger;
import com.playnomics.android.util.Logger.LogLevel;
import com.playnomics.android.util.Util;

public class Session implements SessionStateMachine, TouchEventHandler,
		HeartBeatHandler, ICallbackProcessor, ICloudMessagingHandler {
	// session
	private SessionState sessionState;

	private final Object syncLock = new Object();
	public SessionState getSessionState() {
		synchronized (syncLock) {
			return sessionState;
		}
	}
	
	private void setSessionState(SessionState sessionState){
		synchronized (syncLock) {
			this.sessionState = sessionState;
		}
	}

	private Logger logger;
	private IEventWorker eventWorker;
	private IEventQueue eventQueue;
	private Util util;
	private IConfig config;
	private ContextWrapper contextWrapper;
	private IActivityObserver observer;
	private IHeartBeatProducer producer;
	private MessagingManager messagingManager;
	private CacheFile cacheFile;
	//push notifications
	
	private String unprocessedRegistrationId;
	private String unprocessedPushInteractionUrl;
	
	private IPushNotificationDelegate notificationDelegate;
	
	//session data
	private Long applicationId;

	public Long getApplicationId() {
		return applicationId;
	}

	private String userId;

	public String getUserId(){
		return userId;
	}
	
	public String getAndroidId() {
		return androidId;
	}

	private String androidId;

	private LargeGeneratedId sessionId;

	LargeGeneratedId getSessionId() {
		return sessionId;
	}

	private LargeGeneratedId instanceId;

	LargeGeneratedId getInstanceId() {
		return instanceId;
	}

	private EventTime sessionStartTime;

	EventTime getSessionStartTime() {
		return sessionStartTime;
	}

	private EventTime sessionPauseTime;

	EventTime getSessionPauseTime() {
		return sessionPauseTime;
	}

	private AtomicInteger sequence;
	private AtomicInteger touchEvents;
	private AtomicInteger allTouchEvents;	
	
	public void setOverrideEventsUrl(String eventsUrl){
		config.setOverrideEventsUrl(eventsUrl);
	}
	
	public void setOverrideMessagingUrl(String messagingUrl){
		config.setOverrideMessagingUrl(messagingUrl);
	}
	
	public void setLogLevel(LogLevel logLevel){
		logger.setLogLevel(logLevel);
	}
	
	public Session(IConfig config, Util util,
			IHttpConnectionFactory connectionFactory, Logger logger,
			IEventQueue eventQueue, IEventWorker eventWorker,
			IActivityObserver activityObserver, IHeartBeatProducer producer,
			MessagingManager messagingManager, CacheFile cacheFile) {
		this.logger = logger;
		this.sessionState = SessionState.NOT_STARTED;
		this.util = util;
		this.config = config;
		this.eventQueue = eventQueue;
		this.eventWorker = eventWorker;
		this.observer = activityObserver;
		this.producer = producer;
		this.messagingManager = messagingManager;
		// this is done to make Session more testable
		// we want MessagingManager to be mocked out when we start the session
		this.messagingManager.setSession(this);
		this.cacheFile = cacheFile;
	}
	
	public void enablePushNotifications(IPushConfig pushConfig, IPushNotificationDelegate notificationDelegate){
		try{		
			this.notificationDelegate = notificationDelegate;
			
			if(pushConfig instanceof IGoogleCloudMessageConfig){			
				IGoogleCloudMessageConfig gcmConfig = (IGoogleCloudMessageConfig) pushConfig;
				if(util.isGooglePlaySdkAvailable()){
					//Google Cloud Messaging;
					GcmManager manager = new GcmManager(logger, util, this, gcmConfig);
					
					if(contextWrapper.pushSettingsOutdated()){
						//settings are out-dated, so we need to get a new registration ID
						int resultCode = util.getGooglePlayServiceStatus(contextWrapper.getContext());
						
						if (resultCode != ConnectionResult.SUCCESS) {
					    	if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
					    		logger.log(LogLevel.WARNING, "Google Play Services are not up-to-date on this device.");
					    		notificationDelegate.onPushRegistrationFailure(resultCode);
					        } else {
					            logger.log(LogLevel.ERROR, "Google Play Services are not available on this device.");
					            notificationDelegate.onPushRegistrationFailure();
					        }
					    	return;
					    }
						
						Runnable pushRegistrationTask = manager.createRegistrationTask(contextWrapper.getContext());
						util.startTaskOnBackgroundThread(pushRegistrationTask);
					}
				}
			}
		} catch(Exception ex){
			logger.log(LogLevel.ERROR, ex, "Could not enable push notifications");
		}
	}
	
	public void start(ContextWrapper contextWrapper, Long applicationId, String userId) {
		try {
			if (getSessionState() == SessionState.STARTED || getSessionState() == SessionState.PAUSED) {
				return;
			}
			
			this.applicationId = applicationId;
			if (this.applicationId == null) {
				throw new NullPointerException("Application ID must be set");
			}
			
			this.userId = userId;

			setSessionState(SessionState.STARTED);
			this.contextWrapper = contextWrapper;
			
			androidId = util.getDeviceIdFromContext(contextWrapper.getContext());
			
			if (Util.stringIsNullOrEmpty(this.userId)) {
				this.userId = androidId;
			}

			sequence = new AtomicInteger(1);
			touchEvents = new AtomicInteger(0);
			allTouchEvents = new AtomicInteger(0);

			// send appRunning or appPage
			LargeGeneratedId lastSessionId = contextWrapper
					.getPreviousSessionId();

			EventTime lastEventTime = contextWrapper.getLastEventTime();
			GregorianCalendar threeMinutesAgo = new GregorianCalendar(
					Util.TIME_ZONE_GMT);
			threeMinutesAgo.add(Calendar.MINUTE, -3);

			boolean sessionLapsed = lastEventTime == null || 
					(lastEventTime != null && lastEventTime.compareTo(threeMinutesAgo) < 0) || 
					lastSessionId == null;

			ImplicitEvent implicitEvent;
			if (sessionLapsed) {
				sessionId = new LargeGeneratedId(util);
				instanceId = sessionId;

				implicitEvent = new AppStartEvent(config, getSessionInfo(),
						instanceId);
				sessionStartTime = implicitEvent.getEventTime();
				contextWrapper.setLastSessionStartTime(sessionStartTime);
				contextWrapper.setPreviousSessionId(sessionId);
			} else {
				sessionId = lastSessionId;
				instanceId = new LargeGeneratedId(util);
				implicitEvent = new AppPageEvent(config, getSessionInfo(),
						instanceId);
				sessionStartTime = contextWrapper.getLastSessionStartTime();
			}

			eventQueue.enqueueEvent(implicitEvent);

			lookForVersionChange();

			if(unprocessedRegistrationId != null){
				//if we received a push registration ID 
				//before we could start the session, send this registration ID
				//to the API
				onDeviceRegistered(unprocessedRegistrationId);
				unprocessedRegistrationId = null;
			}

			if(unprocessedPushInteractionUrl != null){
				//if we received a push notification before the session was started,
				//so process it now
				onPushNotificationInteracted(unprocessedPushInteractionUrl);
				unprocessedPushInteractionUrl = null;
			}
			
			eventWorker.start();
			producer.start(this);
			observer.setStateMachine(this);
			
			cacheFile.setContext(contextWrapper.getContext());			
		} catch (Exception ex) {
			logger.log(LogLevel.ERROR, ex, "Could not start session");
			setSessionState(SessionState.NOT_STARTED);
		}
	}

	public void pause() {
		try {
			if (sessionState != SessionState.STARTED || applicationId == null) {
				return;
			}
			if (producer.isRunningForLongTime()) {
				// running for a long time queue appRunning
				queueAppRunningEvent();
			}
			producer.stop();

			sessionPauseTime = new EventTime();
			AppPauseEvent event = new AppPauseEvent(config, getSessionInfo(),
					instanceId, sessionStartTime, sequence.get(),
					touchEvents.get(), allTouchEvents.get());
			sequence.incrementAndGet();
			eventQueue.enqueueEvent(event);
			eventWorker.stop();

			Set<String> unprocessUrls = eventWorker.getAllUnprocessedEvents();
			Runnable writeTask = cacheFile.writeSetToFile(unprocessUrls);
			util.startTaskOnBackgroundThread(writeTask);
			
			setSessionState(SessionState.PAUSED);
		} catch (Exception ex) {
			logger.log(LogLevel.ERROR, ex, "Could not pause the session");
		}
	}

	public void resume() {
		try {
			if (getSessionState() != SessionState.PAUSED || applicationId == null) {
				return;
			}

			GregorianCalendar thirtyMinutesAgo = new GregorianCalendar(
					Util.TIME_ZONE_GMT);
			thirtyMinutesAgo.add(Calendar.MINUTE, -config.getAppPauseTimeoutMinutes());
			
			if(sessionPauseTime.compareTo(thirtyMinutesAgo) < 0){
				//the session has been paused for longer than 30 minutes
				//we should treat this as a completely new session
				setSessionState(SessionState.NOT_STARTED);
				start(contextWrapper, applicationId, userId);
				return;
			}
			
			AppResumeEvent event = new AppResumeEvent(config, getSessionInfo(),
					instanceId, sessionStartTime, sessionPauseTime,
					sequence.get());
			eventQueue.enqueueEvent(event);
			eventWorker.start();
			producer.start(this);
			
			ICacheFileHandler handler = new ICacheFileHandler() {
				public void onReadSetComplete(Set<String> data) {
					if(data != null){
						for(String url : data){
							eventQueue.enqueueEventUrl(url);
						}
					}
				}
			};
			
			Runnable readTask = cacheFile.readSetFromFile(handler);
			util.startTaskOnBackgroundThread(readTask);

			setSessionState(SessionState.STARTED);
		} catch (Exception ex) {
			logger.log(LogLevel.ERROR, ex, "Could not resume the session");
		}
	}

	public void queueAppRunningEvent() {
		try {
			sequence.incrementAndGet();
			AppRunningEvent event = new AppRunningEvent(config,
					getSessionInfo(), instanceId, sessionStartTime,
					sequence.get(), touchEvents.get(), allTouchEvents.get());
			eventQueue.enqueueEvent(event);
			//reset the touch events
			touchEvents.set(0);
			//update the last event time
			contextWrapper.setLastEventTime(event.getEventTime());
		} catch (UnsupportedEncodingException exception) {
			logger.log(LogLevel.ERROR, exception, "Could not log appRunning");
		}
	}

	public void onHeartBeat(long heartbeatIntervalInMillSeconds) {
		queueAppRunningEvent();
	}

	public void onTouchEventReceived() {
		touchEvents.incrementAndGet();
		allTouchEvents.incrementAndGet();
	}

	private GameSessionInfo getSessionInfo() {
		return new GameSessionInfo(applicationId, userId, androidId, sessionId);
	}

	private void assertSessionStarted(String name) {
		if (!(getSessionState() == SessionState.STARTED || getSessionState() == SessionState.PAUSED)) {
			throw new IllegalStateException("Session must be started " + name);
		}
	}

	// explicit events
	public void transactionInUSD(float priceInUSD, int quantity) {
		try {
			assertSessionStarted("transactionInUSD");
			TransactionEvent event = new TransactionEvent(config, util,
					getSessionInfo(), quantity, priceInUSD);
			eventQueue.enqueueEvent(event);
		} catch (Exception ex) {
			logger.log(LogLevel.ERROR, ex, "Could not send transaction");
		}
	}

	public void attributeInstall(String source, String campaign,
			Date installDate) {
		try {
			assertSessionStarted("attributeInstall");
			UserInfoEvent event = new UserInfoEvent(config, getSessionInfo(),
					source, campaign, installDate);
			eventQueue.enqueueEvent(event);
		} catch (Exception ex) {
			logger.log(LogLevel.ERROR, ex,
					"Could not send install attribution information");
		}
	}

	public void customEvent(String customEventName) {
		try {
			assertSessionStarted("customEvent");
			CustomEvent event = new CustomEvent(config, util, getSessionInfo(),
					customEventName);
			eventQueue.enqueueEvent(event);
		} catch (Exception ex) {
			logger.log(LogLevel.ERROR, ex, "Could not send custom event");
		}
	}

	// activity pause/resume
	public void onActivityResumed(Activity activity) {
		try {
			assertSessionStarted("onActivityResumed");
			observer.observeNewActivity(activity, this);
			messagingManager.onActivityResumed(activity);
		} catch (Exception ex) {
			logger.log(LogLevel.ERROR, ex, "Could not attach activity");
		}
	}

	public void onActivityPaused(Activity activity) {
		try {
			assertSessionStarted("onActivityPaused");
			observer.forgetLastActivity();
			messagingManager.onActivityPaused(activity);
		} catch (Exception ex) {
			logger.log(LogLevel.ERROR, ex, "Could not detach activity");
		}
	}

	public void processUrlCallback(String url) {
		eventQueue.enqueueEventUrl(url);
	}
	
	public void fetchUserSegmentIds(final IPlaynomicsSegmentationDelegate delegate) {
		SegmentationClient segmentationClient = new SegmentationClient(Session.this, config, logger);
		segmentationClient.fetchUserSegmentIds(delegate);
	}

	public void setUserGender(String gender) {
		try {
			assertSessionStarted("setUserGender");
			UserInfoEvent event = new UserInfoEvent(config,
					getSessionInfo());
			event.setGender(gender);
			eventQueue.enqueueEvent(event);
		} catch (Exception ex) {
			logger.log(LogLevel.ERROR, ex, "Could not send user gender");
		}
	}

	public void setUserBirthYear(int year) {
		try {
			assertSessionStarted("setUserBirthYear");
			UserInfoEvent event = new UserInfoEvent(config,
					getSessionInfo());
			event.setBirthYear(year);
			eventQueue.enqueueEvent(event);
		} catch (Exception ex) {
			logger.log(LogLevel.ERROR, ex, "Could not send user birth year");
		}
	}

	/* Messaging */
	public void preloadPlacements(String[] placementNames){
		try{
			assertSessionStarted("preloadPlacements");
			messagingManager.preloadPlacements(placementNames);
		} catch (Exception ex) {
			logger.log(LogLevel.ERROR, ex, "Could not preload placements");
		}
	}
	
	public void showPlacement(String placementName, Activity activity, IPlacementDelegate delegate){
		try{
			assertSessionStarted("showPlacement");
			messagingManager.showPlacement(placementName, activity, delegate);
		} catch (Exception ex) {
			logger.log(LogLevel.ERROR, ex, "Could not preload placements");
		}
	}
	
	public void hidePlacement(String placementName){
		try{
			assertSessionStarted("hidePlacement");
			messagingManager.hidePlacement(placementName);
		} catch (Exception ex) {
			logger.log(LogLevel.ERROR, ex, "Could not preload placements");
		}
	}

	@Override
	public void onDeviceRegistered(String registrationId) {
		try {
			
			if(sessionState == SessionState.NOT_STARTED){
				//if the session has not started, then just save the registration ID for later
				//we can't register yet, because the userId and applicationId are null
				unprocessedRegistrationId = registrationId;
				return;
			}
			
			logger.log(LogLevel.DEBUG, "Received pushRegistrationId: %s", registrationId);
			contextWrapper.setPushRegistrationId(registrationId);
			notificationDelegate.onPushRegistrationSuccess(registrationId);
			
			UserInfoEvent userInfoEvent = new UserInfoEvent(config, getSessionInfo(), registrationId);
			eventQueue.enqueueEvent(userInfoEvent);
			
		} catch (UnsupportedEncodingException ex) {
			logger.log(LogLevel.ERROR, ex, "Could not send pushRegistrationId to server %s", registrationId);
		}
	}

	@Override
	public void onDeviceRegistrationFailed(Exception exception) {
		logger.log(LogLevel.WARNING, exception, "Failed to get a registration ID");
		notificationDelegate.onPushRegistrationFailure(exception);
	}
	
	@Override
	public void onPushNotificationInteracted(String pushInteractedUrl){
		if(sessionState == SessionState.NOT_STARTED){
			unprocessedPushInteractionUrl = pushInteractedUrl;
		} else {
			StringBuilder builder = new StringBuilder(pushInteractedUrl);
			builder.append(String.format("&a=%d", applicationId));
			builder.append(String.format("&u=%s", userId));
			builder.append(String.format("&androidId=%s", getAndroidId()));
			builder.append(String.format("&pt=%s", contextWrapper.getPushRegistrationId()));
			processUrlCallback(builder.toString());
		}
	}

	private void lookForVersionChange() {
		boolean isAppVersionChanged = contextWrapper.isAppVersionChanged();
		boolean isAndroidVersionChanged = contextWrapper.isAndroidVersionChanged();
		if (isAppVersionChanged || isAndroidVersionChanged) {
			UserInfoEvent userInfoEvent = new UserInfoEvent(config, getSessionInfo());
			userInfoEvent.setAppVersion(contextWrapper.getApplicationVersionName());
			userInfoEvent.setDeviceModel(Util.getDeviceModel());
			userInfoEvent.setDeviceManufacturer(Util.getDeviceManufacturer());
			userInfoEvent.setDeviceOSVersion(Util.getAndroidOSVersion());
			try {
				eventQueue.enqueueEvent(userInfoEvent);
			} catch (UnsupportedEncodingException e) {
			}
		}
	}
}
