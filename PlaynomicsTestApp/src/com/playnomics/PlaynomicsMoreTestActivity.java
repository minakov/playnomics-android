package com.playnomics;

import com.playnomics.android.sdk.Playnomics;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.view.View;

public class PlaynomicsMoreTestActivity extends Activity {
	private static final int PICTURE_RESULT = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.more_layout);
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

	public void onLaunchBrowser(View view){
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
		startActivity(browserIntent);
	}

	public void onLaunchCamera(View view){
		Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);   
        this.startActivityForResult(camera, PICTURE_RESULT);
	}

	public void onSendNotification(View view){
		// prepare intent which is triggered if the
		// notification is selected

		Intent intent = new Intent(this, PlaynomicsTestAppActivity.class);
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

		// build notification
		// the addAction re-use the same intent to keep the example short
		NotificationCompat.Builder builder =  
	            new NotificationCompat.Builder(this)  
	            .setSmallIcon(R.drawable.ic_launcher)  
	            .setContentTitle("Playnomics Sample App Notification test")  
	            .setContentText("This is a test notification")
	            .setContentIntent(pIntent);		    

		NotificationManager notificationManager = 
		  (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		notificationManager.notify(0, builder.build()); 
	}

}
