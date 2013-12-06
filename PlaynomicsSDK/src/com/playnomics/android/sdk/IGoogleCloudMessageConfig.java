package com.playnomics.android.sdk;

public interface IGoogleCloudMessageConfig extends IPushConfig {
	/*
     * Provides the SDK with your Google API project's Project ID. This is ID is associated
     * with the private key that your provide to Playnomics.

     * @returns Your Google API Project's Project ID
     */
    public String getSenderId();
}
