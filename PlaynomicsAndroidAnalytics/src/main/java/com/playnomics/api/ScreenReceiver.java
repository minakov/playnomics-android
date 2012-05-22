package com.playnomics.api;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

class ScreenReceiver extends BroadcastReceiver {
	
	private static String TAG = ScreenReceiver.class.getSimpleName();
	public boolean screenOff;
	
	@Override
	public void onReceive(Context context, Intent intent) {
	
		if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
			screenOff = true;
			PlaynomicsSession.pause();
			Log.i(TAG, "SCREEN TURNED OFF");
		} else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
			screenOff = false;
			Log.i(TAG, "SCREEN TURNED ON");
		}
	}
}