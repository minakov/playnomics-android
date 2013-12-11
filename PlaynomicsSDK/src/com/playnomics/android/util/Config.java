package com.playnomics.android.util;

import java.util.ResourceBundle;

public class Config implements IConfig {
	private final String CONFIG_FILE = "PlaynomicsSDKConfig";

	private ResourceBundle bundle;

	public Config() {
		bundle = ResourceBundle.getBundle(CONFIG_FILE);
	}

	public String getSdkVersion() {
		return bundle.getString("sdk.version");
	}

	public String getSdkName() {
		return bundle.getString("sdk.name");
	}


	private String getProdEventsUrl() {
		return bundle.getString("eventsUrl.prod");
	}

	private String getProdMessagingUrl() {
		return bundle.getString("messagingUrl.prod");
	}
	
	private String overrideEventsUrl;
	public void setOverrideEventsUrl(String url) {
		overrideEventsUrl = url;
	}

	public String getEventsUrl() {
		if (!Util.stringIsNullOrEmpty(overrideEventsUrl)) {
			return overrideEventsUrl;
		}
		return getProdEventsUrl();
	}

	private String overrideMessagingUrl;

	public void setOverrideMessagingUrl(String url) {
		overrideMessagingUrl = url;
	}

	public String getMessagingUrl() {
		if (!Util.stringIsNullOrEmpty(overrideMessagingUrl)) {
			return overrideMessagingUrl;
		}
		return getProdMessagingUrl();
	}

	public String getApplicationIdKey() {
		return bundle.getString("eventKeys.applicationIdKey");
	}

	public String getUserIdKey() {
		return bundle.getString("eventKeys.userIdKey");
	}

	public String getAndroidIdKey() {
		return bundle.getString("eventKeys.androidIdKey");
	}

	public String getEventTimeKey() {
		return bundle.getString("eventKeys.eventTimeKey");
	}

	public String getSdkVersionKey() {
		return bundle.getString("eventKeys.sdkVersionKey");
	}

	public String getSdkNameKey() {
		return bundle.getString("eventKeys.sdkNameKey");
	}
	
	public String getTimeZoneOffsetKey() {
		return bundle.getString("eventKeys.timeZoneOffsetKey");
	}

	public String getSequenceKey() {
		return bundle.getString("eventKeys.sequenceKey");
	}

	public String getTouchesKey() {
		return bundle.getString("eventKeys.touchesKey");
	}

	public String getTotalTouchesKey() {
		return bundle.getString("eventKeys.totalTouchesKey");
	}

	public String getKeysPressedKey() {
		return bundle.getString("eventKeys.keysPressedKey");
	}

	public String getTotalKeysPressedKey() {
		return bundle.getString("eventKeys.totalKeysPressedKey");
	}

	public String getSessionStartTimeKey() {
		return bundle.getString("eventKeys.sessionStartTimeKey");
	}

	public String getIntervalMillisecondsKey() {
		return bundle.getString("eventKeys.intervalMillisecondsKey");
	}

	public String getCollectionModeKey() {
		return bundle.getString("eventKeys.collectionModeKey");
	}

	public String getSessionPauseTimeKey() {
		return bundle.getString("eventKeys.sessionPauseTimeKey");
	}

	public String getUserInfoTypeKey() {
		return bundle.getString("eventKeys.userInfoTypeKey");
	}

	public String getUserInfoSourceKey() {
		return bundle.getString("eventKeys.userInfoSourceKey");
	}

	public String getUserInfoCampaignKey() {
		return bundle.getString("eventKeys.userInfoCampaignKey");
	}

	public String getUserInfoInstallDateKey() {
		return bundle.getString("eventKeys.userInfoInstallDateKey");
	}

	public String getUserInfoPushTokenKey() {
		return bundle.getString("eventKeys.userInfoPushTokenKey");
	}

	public String getTransactionIdKey() {
		return bundle.getString("eventKeys.transactionIdKey");
	}

	public String getTransactionTypeKey() {
		return bundle.getString("eventKeys.transactionTypeKey");
	}

	public String getTransactionItemIdKey() {
		return bundle.getString("eventKeys.transactionItemIdKey");
	}

	public String getTransactionQuantityKey() {
		return bundle.getString("eventKeys.transactionQuantityKey");
	}

	public String getTransactionCurrencyTypeFormatKey() {
		return bundle.getString("eventKeys.transactionCurrencyTypeFormatKey");
	}

	public String getTransactionCurrencyValueFormatKey() {
		return bundle.getString("eventKeys.transactionCurrencyValueFormatKey");
	}

	public String getTransactionCurrencyCategoryFormatKey() {
		return bundle
				.getString("eventKeys.transactionCurrencyCategoryFormatKey");
	}

	public String getMilestoneIdKey() {
		return bundle.getString("eventKeys.milestoneIdKey");
	}

	public String getMilestoneNameKey() {
		return bundle.getString("eventKeys.milestoneNameKey");
	}

	public int getAppRunningIntervalSeconds() {
		return Integer.parseInt(bundle.getString("appRunningIntervalSeconds"));
	}

	public int getAppRunningIntervalMilliseconds() {
		return getAppRunningIntervalSeconds() * 1000;
	}
	
	public int getAppPauseTimeoutMinutes(){
		return Integer.parseInt(bundle.getString("appPauseTimeoutMinutes"));
	}

	public int getCollectionMode() {
		return Integer.parseInt(bundle.getString("collectionMode"));
	}

	public String getEventPathUserInfo() {
		return bundle.getString("eventNames.userInfo");
	}

	public String getEventPathMilestone() {
		return bundle.getString("eventNames.milestone");
	}

	public String getEventPathTransaction() {
		return bundle.getString("eventNames.transaction");
	}

	public String getEventPathAppRunning() {
		return bundle.getString("eventNames.appRunning");
	}

	public String getEventPathAppPage() {
		return bundle.getString("eventNames.appPage");
	}

	public String getEventPathAppResume() {
		return bundle.getString("eventNames.appResume");
	}

	public String getEventPathAppStart() {
		return bundle.getString("eventNames.appStart");
	}

	public String getEventPathAppPause() {
		return bundle.getString("eventNames.appPause");
	}

	public String getMessagingPathAds() {
		return bundle.getString("messaging.adsPath");
	}

	public String getMessagingPlacementNameKey() {
		return bundle.getString("messaging.placementNameKey");
	}

	public String getMessagingScreenWidthKey() {
		return bundle.getString("messaging.screenWidthKey");
	}

	public String getMessagingScreenHeightKey() {
		return bundle.getString("messaging.screenHeightKey");
	}

	public String getMessagingLanguageKey(){
		return bundle.getString("messaging.languageKey");
	}

	public String getCacheFileName() {
		return bundle.getString("cacheFileName");
	}
}

