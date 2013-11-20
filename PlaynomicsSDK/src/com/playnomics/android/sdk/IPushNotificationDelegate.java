package com.playnomics.android.sdk;

public interface IPushNotificationDelegate {
	public void onPushRegistrationSuccess(String registrationId);
	
	public void onPushRegistrationFailure();
	
	public void onPushRegistrationFailure(Exception ex);
	
	public void onPushRegistrationFailure(int errorCode);
}
