package com.playnomics.android.push;



import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.playnomics.android.sdk.IGoogleCloudMessageConfig;

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
	private final String PUSH_INTERACTED_URL_KEY = "pushInteractedUrl";
	private final String GCM_MESSAGE_KEY = "message";
	private final String GCM_TITLE_KEY = "title";
	
	private IGoogleCloudMessageConfig config;
	
    public GcmIntentService() {
        super("GcmIntentService");
        this.config = GcmManager.config;
    }

    @Override
    protected void onHandleIntent(Intent intent) {    	
    	Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        
        String messageType = gcm.getMessageType(intent);
        
        if (!extras.isEmpty() && GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
			try{	
				String message = extras.getString(GCM_MESSAGE_KEY);
            	String title = extras.getString(GCM_TITLE_KEY);
            	String pushInteractedUrl = extras.getString(PUSH_INTERACTED_URL_KEY);
            	
            	sendNotification(message, title, pushInteractedUrl);
			} catch (Exception ex){
			}
        }
        //Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
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
        
        Intent openNotification = new Intent(this, GcmBroadcastReceiver.class);
        openNotification.setAction(GcmBroadcastReceiver.NOTIFICATION_OPENED);
        openNotification.putExtra(PUSH_INTERACTED_URL_KEY, pushInteractedUrl);
        
        PendingIntent contentIntent = PendingIntent.getBroadcast(this, 0, openNotification, 0);
        builder.setContentIntent(contentIntent);
        
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}