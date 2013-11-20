package com.playnomics.android.util;

import java.io.File;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.provider.Settings;
import android.view.Window;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.playnomics.android.session.TouchEventHandler;
import com.playnomics.android.session.WindowCallbackProxy;
import com.playnomics.android.util.Logger.LogLevel;

public class Util implements IRandomGenerator {

	public static final TimeZone TIME_ZONE_GMT = TimeZone.getTimeZone("GMT");
	public static final String UT8_ENCODING = "UTF-8";
	public static final String CONTENT_TYPE_HTML = "text/html";

	private Logger logger;

	public Util(Logger logger) {
		this.logger = logger;
	}

	public long generatePositiveRandomLong() {
		Random rand = new Random();
		return Math.abs(rand.nextLong());
	}

	public String getDeviceIdFromContext(Context context) {
		return Settings.Secure.getString(context.getContentResolver(),
				Settings.Secure.ANDROID_ID);
	}

	public int getApplicationVersionFromContext(Context context) {
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo info;
			info = packageManager.getPackageInfo(context.getPackageName(), 0);
			return info.versionCode;
		} catch (NameNotFoundException ex) {
			// according to Google's docs this should never happen
			logger.log(LogLevel.WARNING, ex,
					"Could not obtain the application version from the package manager");
			// in the event of a failure always return a -1
			return -1;
		}
	}

	public void startUriIntent(String uri, Context context) {
		
		try{
			Intent uriIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
			
			PackageManager packageManager = context.getPackageManager();
			List<ResolveInfo> activities = packageManager.queryIntentActivities(uriIntent, 0);
			boolean isIntentSafe = activities.size() > 0;
			
			if(isIntentSafe){
				context.startActivity(uriIntent);
			} else {
				logger.log(LogLevel.WARNING, "Failed to navigate to URI %s. There is nothing to repond to this URI.", uri);
			}
		} catch(Exception ex){
			logger.log(LogLevel.WARNING, "Failed to navigate to URI %s", uri);
			logger.log(LogLevel.WARNING, ex);
		}
	}

	public String getDeviceLanguage() {
		Locale defaultLocale = Locale.getDefault();
		if (defaultLocale == null) {
			return null;
		}
		return defaultLocale.getLanguage();
	}

	public boolean isGooglePlaySdkAvailable(){
		try{
			Class.forName("com.google.android.gms.common.GooglePlayServicesUtil");
			return true;
		} catch(ClassNotFoundException exception){
			logger.log(LogLevel.WARNING, "Google Play Services are not available on this device.");
		}
		return false;
	}
	
	public int getGooglePlayServiceStatus(Context context){
		return GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
	}
	
	public GoogleCloudMessaging getGCMFromContext(Context context){
		return GoogleCloudMessaging.getInstance(context);
	}
	
	public File getContextCacheFile(Context context, String fileName) {
		return new File(context.getCacheDir(), fileName);
	}

	public static boolean stringIsNullOrEmpty(String value) {
		return (value == null || value.isEmpty());
	}

	public static boolean isEmptyObject(JSONObject object) {
		return object.names() == null;
	}

	public static Map<String, Object> getMap(JSONObject object, String key)
			throws JSONException {
		return toMap(object.getJSONObject(key));
	}

	public static Map<String, Object> toMap(JSONObject object)
			throws JSONException {
		Map<String, Object> map = new HashMap<String, Object>();
		@SuppressWarnings("rawtypes")
		Iterator keys = object.keys();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			map.put(key, fromJson(object.get(key)));
		}
		return map;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List toList(JSONArray array) throws JSONException {
		List list = new ArrayList();
		for (int i = 0; i < array.length(); i++) {
			list.add(fromJson(array.get(i)));
		}
		return list;
	}

	private static Object fromJson(Object json) throws JSONException {
		if (json == JSONObject.NULL) {
			return null;
		} else if (json instanceof JSONObject) {
			return toMap((JSONObject) json);
		} else if (json instanceof JSONArray) {
			return toList((JSONArray) json);
		} else {
			return json;
		}
	}

	public void overrideActivityWindowCallback(Activity activity,
			TouchEventHandler handler) {
		Window.Callback currentCallback = activity.getWindow().getCallback();
		activity.getWindow().setCallback(
				WindowCallbackProxy.newCallbackProxyForActivity(
						currentCallback, handler));
	}

	public void removeWindowCallback(Activity activity) {
		Window.Callback callbackProxy = activity.getWindow().getCallback();
		WindowCallbackProxy proxy = (WindowCallbackProxy) Proxy
				.getInvocationHandler(callbackProxy);
		activity.getWindow().setCallback(proxy.getOriginalCallback());
	}

	public void runTaskOnActivityUIThread(Runnable task, Activity activity) {
		activity.runOnUiThread(task);
	}
	
	public Thread startTaskOnBackgroundThread(Runnable task){
		Thread thread = new Thread(task);
		thread.start();
		return thread;
	}
}
