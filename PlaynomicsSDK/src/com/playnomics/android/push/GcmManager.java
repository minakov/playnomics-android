package com.playnomics.android.push;

import android.content.Context;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.playnomics.android.sdk.IGoogleCloudMessageConfig;
import com.playnomics.android.util.AsyncTaskRunner;
import com.playnomics.android.util.IAsyncCall;
import com.playnomics.android.util.Logger;
import com.playnomics.android.util.Util;

public class GcmManager {
	
	public interface ICloudMessagingHandler{
		public void onDeviceRegistered(String registrationId);
		public void onDeviceRegistrationFailed(Exception exception);
		public void onPushNotificationInteracted(String pushInteractedUrlFormat);
	}
	
	private Logger logger;
	private Util util;
	private static ICloudMessagingHandler messagingHandler;
	
	static IGoogleCloudMessageConfig config;
	
	public GcmManager(Logger logger, Util util, 
			ICloudMessagingHandler messagingHandler, 
			IGoogleCloudMessageConfig provider){
		this.logger = logger;
		this.util = util;
		GcmManager.messagingHandler = messagingHandler;
		GcmManager.config = provider;
	}

	private class AyncRegistrationGCM implements IAsyncCall {
		private String registrationId;
		private Exception exception;
		private Context context;

		public AyncRegistrationGCM(final Context context) {
			this.context = context;
		}

		@Override
		public void onBackgroundThread() {
			try{
				GoogleCloudMessaging gcm = util.getGCMFromContext(context);
				registrationId = gcm.register(config.getSenderId());
			} catch(Exception ex){
				exception = ex;
			}
		}

		@Override
		public void postExecuteOnUiThread() {
			if (exception==null) {
				messagingHandler.onDeviceRegistered(registrationId);
			} else {
				messagingHandler.onDeviceRegistrationFailed(exception);
			}
		}
	}

	public void preformRegistration(final Context context) {
		AsyncTaskRunner pushRegistrationTask = new AsyncTaskRunner(new AyncRegistrationGCM(context));
		pushRegistrationTask.execute();
	}

	static void onPushNotificationOpened(String pushInteractedUrl){
		messagingHandler.onPushNotificationInteracted(pushInteractedUrl);
	}
}
