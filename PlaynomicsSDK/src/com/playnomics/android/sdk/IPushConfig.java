package com.playnomics.android.sdk;

public abstract interface IPushConfig {
	public Class<?> getNotificationDestination();
	public int getNotificationIcon();
}
