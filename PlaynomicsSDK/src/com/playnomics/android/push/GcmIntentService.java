package com.playnomics.android.push;

import org.json.JSONException;
import org.json.JSONObject;


import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.playnomics.android.sdk.IGoogleCloudMessageConfig;
import com.playnomics.android.util.Util;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

public class GcmIntentService extends IntentService {
	public static final int NOTIFICATION_ID = 1;
	private final String GCM_MESSAGE_RECEIVED = "com.google.android.c2dm.intent.RECEIVE";
	private final String NOTIFICATION_OPENED = "com.playnomics.android.push.PUSH_OPENED";
	
	private final String PUSH_INTERACTED_URL_KEY = "pushInteractedUrl";
	private final String GCM_MESSAGE_KEY = "message";
	private final String GCM_TITLE_KEY = "title";
	private final String GCM_DATA_KEY = "data";
	
	private IGoogleCloudMessageConfig config;
	
    public GcmIntentService() {
        super("GcmIntentService");
        this.config = GcmManager.config;
    }

    @Override
    protected void onHandleIntent(Intent intent) {    	
    	if(intent.getAction().equals(GCM_MESSAGE_RECEIVED)){
    		Bundle extras = intent.getExtras();
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
            
            String messageType = gcm.getMessageType(intent);
            
            if (!extras.isEmpty()) {
                if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                	String dataString = extras.getString(GCM_DATA_KEY);
                	
                	if(!Util.stringIsNullOrEmpty(dataString)){
                		try {
    						JSONObject json = new JSONObject(dataString);
    						
    						String message = json.getString(GCM_MESSAGE_KEY);
    	                	String title = json.getString(GCM_TITLE_KEY);
    	                	String pushInteractedUrl = json.getString(PUSH_INTERACTED_URL_KEY);
    	                	
    	                	sendNotification(message, title, pushInteractedUrl);
    					} catch (JSONException e) {
    					} catch (Exception ex){
    					}
                	}	
                }
            }
            //Release the wake lock provided by the WakefulBroadcastReceiver.
            GcmBroadcastReceiver.completeWakefulIntent(intent);
    	} else if(intent.getAction().equals(NOTIFICATION_OPENED)) {
    		
    		String pushInteractedUrl = intent.getStringExtra(PUSH_INTERACTED_URL_KEY);
    		GcmManager.onPushNotificationOpened(pushInteractedUrl);
    		//launch the activity in a new task, clearing all activities above this
            //in the stack
    		Intent launch = new Intent(this, config.getNotificationDestination());
            launch.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
            launch.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP);
            
            startActivity(intent);
    	}
    }

    private void sendNotification(String message, String title, String pushInteractedUrl) {
    	NotificationManager notificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
        builder.setContentText(message);
        builder.setContentTitle(title);

        builder.setSmallIcon(config.getNotificationIcon());
        //by default use all of the defaults for vibrations, sounds, and lights
        builder.setDefaults(Notification.DEFAULT_ALL);
        //remove the notification after it has been pressed
        builder.setAutoCancel(true);
        
        Intent openNotification = new Intent(this, this.getClass());
        openNotification.setAction(NOTIFICATION_OPENED);
        openNotification.putExtra(PUSH_INTERACTED_URL_KEY, pushInteractedUrl);
        
        PendingIntent contentIntent = PendingIntent.getBroadcast(this, 0, openNotification, 0);
        builder.setContentIntent(contentIntent);
        
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}