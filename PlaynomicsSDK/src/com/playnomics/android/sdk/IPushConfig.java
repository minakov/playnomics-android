package com.playnomics.android.sdk;

public abstract interface IPushConfig {

    /*
	 * Provides the SDK with a destination to send the user when they interact with
	 * your push notification. This activity's intent will be flagged as FLAG_ACTIVITY_NEW_TASK and
     * FLAG_ACTIVITY_CLEAR_TOP, so that a new version of this activity will be launched.
     *
     * @result Returns an Class for a derivative of Activity.
	 */
	public Class<?> getNotificationDestination();

    /*
     * Provides the SDK with the icon that should be shown adjacent to message in the Notification
     * Manager.
     */
	public int getNotificationIcon();
}
