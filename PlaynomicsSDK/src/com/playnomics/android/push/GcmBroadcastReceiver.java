package com.playnomics.android.push;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;


public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
	static final String GCM_MESSAGE_RECEIVED = "com.google.android.c2dm.intent.RECEIVE";
	static final String NOTIFICATION_OPENED = "com.playnomics.android.push.PUSH_OPENED";
	static final String PUSH_INTERACTED_URL_KEY = "pushInteractedUrl";
	static final String GCM_MESSAGE_KEY = "message";
	static final String GCM_TITLE_KEY = "title";
	
	@Override
	public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(GCM_MESSAGE_RECEIVED)){
        	 // Explicitly specify that GcmIntentService will handle the intent.
            ComponentName comp = new ComponentName(context.getPackageName(),
                    GcmIntentService.class.getName());
            // Start the service, keeping the device awake while it is launching.
        	startWakefulService(context, intent.setComponent(comp));
        	setResultCode(Activity.RESULT_OK);
        } else if(intent.getAction().equals(NOTIFICATION_OPENED)) {
        	String pushInteractedUrl = intent.getStringExtra(PUSH_INTERACTED_URL_KEY);
    		GcmManager.onPushNotificationOpened(pushInteractedUrl);
    		//launch the activity in a new task, clearing all activities above this
            //in the stack
    		Intent launch = new Intent(context, GcmManager.config.getNotificationDestination());
            launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            launch.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(launch);
        }
    }
}
