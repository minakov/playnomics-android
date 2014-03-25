package com.playnomics.android.util;

public interface IConfig {

	public String getSdkVersion();

	public String getSdkName();

	public void setOverrideEventsUrl(String eventsUrl);

	public String getEventsUrl();

	public void setOverrideMessagingUrl(String messagingUrl);

	public String getMessagingUrl();

	public void setOverrideApiUrl(String ApiUrl);

	public String getApiUrl();

	public String getApplicationIdKey();

	public String getUserIdKey();

	public String getAndroidIdKey();

	public String getEventTimeKey();

	public String getSdkVersionKey();

	public String getSdkNameKey();

	public String getAppVersionKey();

	public String getDeviceModelKey();

	public String getDeviceMakerKey();

	public String getDeviceOSVersionKey();

	public String getTimeZoneOffsetKey();

	public String getSequenceKey();

	public String getTouchesKey();

	public String getTotalTouchesKey();

	public String getKeysPressedKey();

	public String getTotalKeysPressedKey();

	public String getSessionStartTimeKey();

	public String getIntervalMillisecondsKey();

	public String getCollectionModeKey();

	public String getSessionPauseTimeKey();

	public String getUserInfoGenderKey();

	public String getUserInfoBirthYearKey();

	public String getUserInfoTypeKey();

	public String getUserInfoSourceKey();

	public String getUserInfoCampaignKey();

	public String getUserInfoInstallDateKey();

	public String getUserInfoPushTokenKey();

	public String getTransactionIdKey();

	public String getTransactionTypeKey();

	public String getTransactionItemIdKey();

	public String getTransactionQuantityKey();

	public String getTransactionCurrencyTypeFormatKey();

	public String getTransactionCurrencyValueFormatKey();

	public String getTransactionCurrencyCategoryFormatKey();

	public String getMilestoneNameKey();

	public int getAppRunningIntervalSeconds();

	public int getAppRunningIntervalMilliseconds();
	
	public int getAppPauseTimeoutMinutes();

	public int getCollectionMode();

	public String getEventPathUserInfo();

	public String getEventPathMilestone();

	public String getEventPathTransaction();

	public String getEventPathAppRunning();

	public String getEventPathAppPage();

	public String getEventPathAppResume();

	public String getEventPathAppStart();

	public String getEventPathAppPause();

	public String getMessagingPathAds();

	public String getMessagingPlacementNameKey();

	public String getMessagingScreenWidthKey();

	public String getMessagingScreenHeightKey();
	
	public String getMessagingLanguageKey();
	
	public String getCacheFileName();
	
	public String getUserSegmentsPath();
}