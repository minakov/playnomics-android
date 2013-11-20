package com.playnomics.android.push;

import android.content.Context;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.playnomics.android.sdk.IGoogleCloudMessageConfig;
import com.playnomics.android.util.Logger;
import com.playnomics.android.util.Util;

public class GcmManager {
	
	public interface ICloudMessagingHandler{
		public void onDeviceRegistered(String registrationId);
		public void onDeviceRegistrationFailed(Exception exception);
	}
	
	private Logger logger;
	private Util util;
	private ICloudMessagingHandler messagingHandler;
	private String senderId;
	
	public static IGoogleCloudMessageConfig config;
	
	public GcmManager(Logger logger, Util util, 
			ICloudMessagingHandler messagingHandler, 
			IGoogleCloudMessageConfig provider){
		this.logger = logger;
		this.util = util;
		this.messagingHandler = messagingHandler;
		GcmManager.config = provider;
	}
	
	public Runnable createRegistrationTask(final Context context){
		return new Runnable(){
			@Override
			public void run() {
				try{
					GoogleCloudMessaging gcm = util.getGCMFromContext(context);
					String registrationId = gcm.register(senderId);
					messagingHandler.onDeviceRegistered(registrationId);
				} catch(Exception ex){
					messagingHandler.onDeviceRegistrationFailed(ex);
				}
			}
		};
	}
}
