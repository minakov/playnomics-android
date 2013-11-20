package com.playnomics;

import java.util.Date;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.playnomics.android.sdk.IGoogleCloudMessageConfig;
import com.playnomics.android.sdk.IPushNotificationDelegate;
import com.playnomics.android.sdk.Playnomics;
import com.playnomics.android.util.Logger.LogLevel;

public class PlaynomicsTestAppActivity 
	extends Activity 
	implements IGoogleCloudMessageConfig, IPushNotificationDelegate {
	
	private static boolean preloaded = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		final long applicationId = 2143315484923938870L;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		Playnomics.setLogLevel(LogLevel.VERBOSE);
		//Playnomics.setTestMode(false);
		Playnomics.start(this, applicationId);
		Playnomics.enablePushNotifications(this, this);
		
		if(!preloaded){
			//only preload once
			Playnomics.preloadPlacements("44841d6a2bcec8c9", "a40893b36c6ddb32", "67dbfcad37eccbf9", "5bc049bb66ffc121", "e45c59f627043701");
			preloaded = true;
		}
	}
	
	@Override
	protected void onResume() {
		Playnomics.onActivityResumed(this);
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		Playnomics.onActivityPaused(this);
		super.onPause();
	}

	public void onUserInfoClick(View view) {
		String source = "source";
		String campaign = "campaign";
		GregorianCalendar cal = new GregorianCalendar();
		Date installDate = cal.getTime();
		
		Playnomics.attributeInstall(source, campaign, installDate);
	}
	
	public void onTransactionClick(View view) {
		float price = 0.99f;
		int quantity = 1;
		Playnomics.transactionInUSD(price, quantity);
	}

	public void onMilestoneClick(View view) {
		String eventName = "my event";
		Playnomics.customEvent(eventName);
	}

	public void onHttpClick(View view){
		setupPlacement("44841d6a2bcec8c9");
	}
	
	public void onJsonClick(View view){
		setupPlacement("a40893b36c6ddb32");
	}
	
	public void onNullTargetClick(View view){
		setupPlacement("67dbfcad37eccbf9");
	}
	
	public void onNoAdsClick(View view){
		setupPlacement("5bc049bb66ffc121");
	}
	
	public void onThirdPartyAdClick(View view){
 		setupPlacement("e45c59f627043701");
	}
	
	private void setupPlacement (String placementName){
		RichDataFrameDelegate delegate = new RichDataFrameDelegate(placementName, getApplicationContext());
		Playnomics.showPlacement(placementName, this, delegate);
	}

	@Override
	public Class<?> getNotificationDestination() {
		return PlaynomicsTestAppActivity.class;
	}

	@Override
	public int getNotificationIcon() {
		return R.drawable.ic_launcher;
	}

	@Override
	public String getSenderId() {
		return "463115531919";
	}

	@Override
	public void onPushRegistrationSuccess(String registrationId) {
		Log.d(this.getClass().getName(), String.format("Registered device %s", registrationId));
	}

	@Override
	public void onPushRegistrationFailure() {
		Log.e(this.getClass().getName(), "Failed to register device");
	}

	@Override
	public void onPushRegistrationFailure(Exception ex) {
		Log.e(this.getClass().getName(), "Failed to register device", ex);
	}

	@Override
	public void onPushRegistrationFailure(int errorCode) {
		final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
		Log.e(this.getClass().getName(), "Failed to register device, GooglePlayServices is out of date");
		
		if (GooglePlayServicesUtil.isUserRecoverableError(errorCode)) {
            GooglePlayServicesUtil.getErrorDialog(errorCode, this,
                    PLAY_SERVICES_RESOLUTION_REQUEST).show();
        }
		
	}
}