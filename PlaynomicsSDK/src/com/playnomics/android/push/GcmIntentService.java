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
        
        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
            	String message = extras.getString("message");
            	String title = extras.getString("title");
            	sendNotification(message, title);
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String message, String title) {
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
        
        //launch the activity in a new task, clearing all activities above this
        //in the stack
        Intent launch = new Intent(this, config.getNotificationDestination());
        launch.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
        launch.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP);
        
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
        		launch, 0);
        builder.setContentIntent(contentIntent);
        
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}