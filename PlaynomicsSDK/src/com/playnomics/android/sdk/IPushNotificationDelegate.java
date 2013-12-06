package com.playnomics.android.sdk;

public interface IPushNotificationDelegate {
	/*
	 * Called when the user has been successfully registered for push notifications. 
	 * 
	 * @param registrationId The SDK automatically tracks this ID. This is provided for your own logging and debugging.
	 */
	public void onPushRegistrationSuccess(String registrationId);
	
	/*
	 * Called when the SDK could not successfully register for push notifications. Google Play Services is not 
	 * installed on the device.
	 */
	public void onPushRegistrationFailure();
	
	/*
	 * Called when the SDK could not successfully register for push notifications. This maybe due to network connectivity
	 * at the time we attempted to register this device.
	 *
	 * @param ex Root cause of registration failure.
	 */
	public void onPushRegistrationFailure(Exception ex);
	
	/*
	 * Called  when the SDK could not successfully register for push notifications. This likely due to the 
	 * user's device having an out-dated version of Google Play Services.
	 *
	 * @param Error code received when registering for push notifications.
	 */
	public void onPushRegistrationFailure(int errorCode);
}
