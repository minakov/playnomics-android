package com.playnomics.android.messaging;

import java.util.concurrent.ConcurrentHashMap;

import android.app.Activity;

import com.playnomics.android.client.PlacementDataClient;
import com.playnomics.android.messaging.Placement.IPlacementStateObserver;
import com.playnomics.android.messaging.Placement.PlacementState;
import com.playnomics.android.messaging.ui.IPlayViewFactory;
import com.playnomics.android.messaging.ui.RenderTaskFactory;
import com.playnomics.android.sdk.IPlacementDelegate;
import com.playnomics.android.sdk.IPlaynomicsPlacementDelegate;
import com.playnomics.android.sdk.IPlaynomicsPlacementRawDelegate;
import com.playnomics.android.session.ICallbackProcessor;
import com.playnomics.android.session.Session;
import com.playnomics.android.util.IConfig;
import com.playnomics.android.util.Logger;
import com.playnomics.android.util.Util;

public class MessagingManager implements IPlacementStateObserver {
	private Util util;
	private ConcurrentHashMap<String, Placement> placementsByName;
	private ConcurrentHashMap<String, Placement> placementsByActivityName;
	private PlacementDataClient placementDataClient;
	private Logger logger;
	private ICallbackProcessor callbackProcessor;
	private RenderTaskFactory renderTaskFactory;

	public void setSession(Session session) {
		this.callbackProcessor = session;
		this.placementDataClient.setSession(session);
	}

	public MessagingManager(IConfig config,
			PlacementDataClient placementDataClient, Util util, Logger logger,
			IPlayViewFactory viewFactory) {
		this.placementDataClient = placementDataClient;
		this.util = util;
		this.placementsByName = new ConcurrentHashMap<String, Placement>();
		this.placementsByActivityName = new ConcurrentHashMap<String, Placement>();
		this.logger = logger;
		this.renderTaskFactory = new RenderTaskFactory(viewFactory, logger);
	}

	public void preloadPlacements(String[] placementNames) {
		for (String placementName : placementNames) {
			if(placementName != null){
				getOrAddPlacement(placementName);
			}
		}
	}

	public void showPlacement(String placementName, Activity activity,
			IPlacementDelegate delegate) {
		if(placementName == null){
			return;
		}
		Placement placement = getOrAddPlacement(placementName);
	
		if(placement.getState() == PlacementState.NOT_LOADED || placement.getState() == PlacementState.LOAD_FAILED){
			placement.setState(PlacementState.LOAD_STARTED);
			placementDataClient.loadPlacementInBackground(placement);
		}
		
		placement.show(activity, delegate);
	}
	
	private Placement getOrAddPlacement(String placementName) {
		Placement placement;
		if (!placementsByName.containsKey(placementName)) {
			placement = new Placement(placementName, callbackProcessor, util,
					logger, this, renderTaskFactory);
			placement.setState(PlacementState.LOAD_STARTED);
			placementDataClient.loadPlacementInBackground(placement);
			placementsByName.put(placementName, placement);
		} else {
			placement = placementsByName.get(placementName);
		}
		return placement;
	}

	public void hidePlacement(String placementName) {
		if (placementsByName.containsKey(placementName)) {
			Placement placement = placementsByName.get(placementName);
			placement.hide();
		}
	}

	public void onPlacementShown(Activity activity, Placement placement) {
		placementsByActivityName.put(getKeyForActivity(activity), placement);
	}

	public void onPlacementDisposed(Activity activity, Placement placement) {
		placement.setState(PlacementState.LOAD_STARTED);
		placementDataClient.loadPlacementInBackground(placement);
		placementsByActivityName.remove(getKeyForActivity(activity));
	}

	public void onActivityResumed(Activity activity) {
		String key = getKeyForActivity(activity);
		if (placementsByActivityName.containsKey(key)) {
			Placement placement = placementsByActivityName.get(key);
			placement.attachActivity(activity);
		}
	}

	public void onActivityPaused(Activity activity) {
		String key = getKeyForActivity(activity);
		if (placementsByActivityName.containsKey(key)) {
			Placement placement = placementsByActivityName.get(key);
			placement.detachActivity();
		}
	}

	private String getKeyForActivity(Activity activity) {
		return activity.getClass().getName();
	}
}
