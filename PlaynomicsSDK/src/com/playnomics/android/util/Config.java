package com.playnomics.android.util;

public class Config implements IConfig {

	public String getSdkVersion() {
		return "${project.version}";
	}

	public String getSdkName() {
		return "aj";
	}


	private String getProdEventsUrl() {
		return "https://e.a.playnomics.net/v1/";
	}

	private String getProdMessagingUrl() {
		return "https://ads.a.playnomics.net/v3/";
	}

	private String getProdApiUrl() {
		return "https://api.a.playnomics.net/v2/";
	}

	private String overrideApiUrl;
	public void setOverrideApiUrl(String url) {
		overrideApiUrl = url;
	}

	public String getApiUrl() {
		if (!Util.stringIsNullOrEmpty(overrideApiUrl)) {
			return overrideApiUrl;
		}
		return getProdApiUrl();
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
		return "a";
	}

	public String getUserIdKey() {
		return "u";
	}

	public String getAndroidIdKey() {
		return "androidId";
	}

	public String getEventTimeKey() {
		return "t";
	}

	public String getAppVersionKey() {
		return "appver";
	}

	public String getDeviceModelKey() {
		return "model";
	}

	public String getDeviceManufacturerKey() {
		return "manufacturer";
	}

	public String getDeviceOSVersionKey() {
		return "osver";
	}

	public String getSdkVersionKey() {
		return "ever";
	}

	public String getSdkNameKey() {
		return "esrc";
	}
	
	public String getTimeZoneOffsetKey() {
		return "z";
	}

	public String getSequenceKey() {
		return "q";
	}

	public String getTouchesKey() {
		return "c";
	}

	public String getTotalTouchesKey() {
		return "e";
	}

	public String getKeysPressedKey() {
		return "k";
	}

	public String getTotalKeysPressedKey() {
		return "l";
	}

	public String getSessionStartTimeKey() {
		return "r";
	}

	public String getIntervalMillisecondsKey() {
		return "d";
	}

	public String getCollectionModeKey() {
		return "m";
	}

	public String getSessionPauseTimeKey() {
		return "p";
	}

	public String getUserInfoGenderKey() {
		return "px";
	}

	public String getUserInfoBirthYearKey() {
		return "pb";
	}

	public String getUserInfoTypeKey() {
		return "pt";
	}

	public String getUserInfoSourceKey() {
		return "po";
	}

	public String getUserInfoCampaignKey() {
		return "pm";
	}

	public String getUserInfoInstallDateKey() {
		return "pi";
	}

	public String getUserInfoPushTokenKey() {
		return "pushTok";
	}

	public String getTransactionIdKey() {
		return "r";
	}

	public String getTransactionTypeKey() {
		return "tt";
	}

	public String getTransactionItemIdKey() {
		return "i";
	}

	public String getTransactionQuantityKey() {
		return "tq";
	}

	public String getTransactionCurrencyTypeFormatKey() {
		return "tc%d";
	}

	public String getTransactionCurrencyValueFormatKey() {
		return "tv%d";
	}

	public String getTransactionCurrencyCategoryFormatKey() {
		return "ta%d";
	}

	public String getMilestoneNameKey() {
		return "mn";
	}

	public int getAppRunningIntervalSeconds() {
		return 60;
	}

	public int getAppRunningIntervalMilliseconds() {
		return getAppRunningIntervalSeconds() * 1000;
	}
	
	public int getAppPauseTimeoutMinutes(){
		return 30;
	}

	public int getCollectionMode() {
		return 7;
	}

	public String getEventPathUserInfo() {
		return "userInfo";
	}

	public String getEventPathMilestone() {
		return "milestone";
	}

	public String getEventPathTransaction() {
		return "transaction";
	}

	public String getEventPathAppRunning() {
		return "appRunning";
	}

	public String getEventPathAppPage() {
		return "appPage";
	}

	public String getEventPathAppResume() {
		return "appResume";
	}

	public String getEventPathAppStart() {
		return "appStart";
	}

	public String getEventPathAppPause() {
		return "appPause";
	}

	public String getMessagingPathAds() {
		return "ads";
	}

	public String getMessagingPlacementNameKey() {
		return "f";
	}

	public String getMessagingScreenWidthKey() {
		return "d";
	}

	public String getMessagingScreenHeightKey() {
		return "c";
	}

	public String getMessagingLanguageKey(){
		return"lang";
	}

	public String getCacheFileName() {
		return "playnomicsEventList";
	}
	
	public String getUserSegmentsPath() {
		return "userSegments";
	}

	private int[] heartBeatIntervalInMinutes = {1, 2, 4, 8, 15};
	public int[] getHeartBeatIntervalInMinutes() {
		return heartBeatIntervalInMinutes;
	}
}

