package com.playnomics.android.messaging;

import android.app.Activity;

import com.playnomics.android.messaging.Target.TargetType;
import com.playnomics.android.messaging.ui.PlayDialog;
import com.playnomics.android.messaging.ui.PlayViewFactory.IImageViewHandler;
import com.playnomics.android.messaging.ui.PlayWebView;
import com.playnomics.android.messaging.ui.RenderTaskFactory;
import com.playnomics.android.sdk.IPlacementDelegate;
import com.playnomics.android.sdk.IPlaynomicsPlacementDelegate;
import com.playnomics.android.sdk.IPlaynomicsPlacementRawDelegate;
import com.playnomics.android.session.ICallbackProcessor;
import com.playnomics.android.util.Logger;
import com.playnomics.android.util.Logger.LogLevel;
import com.playnomics.android.util.Util;

public class Placement implements PlayWebView.IPlayWebViewHandler, IImageViewHandler {

	public interface IPlacementStateObserver {
		void onPlacementShown(Activity activity, Placement placement);

		void onPlacementDisposed(Activity activity, Placement placement);
	}

	public enum PlacementState {
		NOT_LOADED, LOAD_STARTED, LOAD_COMPLETE, LOAD_FAILED
	}

	private boolean shouldRender;

	
	private boolean impressionLogged;

	private Util util;
	private IPlacementDelegate delegate;
	private Activity activity;
	private ICallbackProcessor callbackProcessor;
	private Logger logger;
	private IPlacementStateObserver observer;
	private RenderTaskFactory renderTaskFactory;

	private PlayDialog dialog;

	private String placementName;

	public String getPlacementName() {
		return placementName;
	}

	private Object sycnLockState = new Object();
	private PlacementState state;

	public PlacementState getState() {
		synchronized(sycnLockState){
			return state;
		}
	}

	public void setState(PlacementState state) {
		synchronized(sycnLockState){
			this.state = state;
		}
	}

	public void setDialog(PlayDialog dialog) {
		this.dialog = dialog;
	}

	private boolean hasNativeCloseButton() {
		return htmlAd != null
				&& htmlAd.getCloseButton() instanceof NativeCloseButton;
	}

	private HtmlAd htmlAd;

	public Placement(String placementName,
			ICallbackProcessor callbackProcessor, Util util, Logger logger,
			IPlacementStateObserver observer,
			RenderTaskFactory renderTaskFactory) {
		this.observer = observer;
		this.placementName = placementName;
		this.state = PlacementState.NOT_LOADED;
		this.callbackProcessor = callbackProcessor;
		this.util = util;
		this.logger = logger;
		this.renderTaskFactory = renderTaskFactory;
	}

	public void updatePlacementData(HtmlAd htmlAd) {
		//do this first
		setState(PlacementState.LOAD_COMPLETE);
		this.htmlAd = htmlAd;
		impressionLogged = false;
		if (shouldRender) {
			loadWebView();
		}
	}

	public void show(final Activity activity,
			IPlacementDelegate delegate) {
		
		logger.log(LogLevel.DEBUG, "Showing on THREAD %d", Thread.currentThread().getId());
		this.delegate = delegate;
		this.activity = activity;
		
		shouldRender = true;
		if (getState() == PlacementState.LOAD_COMPLETE) {
			loadWebView();
		} else if (getState() == PlacementState.LOAD_FAILED) {
			if (delegate != null) {
				delegate.onRenderFailed();
			}
		}
	}

	private void loadWebView() {
		if (!(shouldRender && getState() == PlacementState.LOAD_COMPLETE)) {
			return;
		}

		Runnable renderTask = renderTaskFactory.createLayoutPlacementTask(this,
				htmlAd, activity, this, this, observer);
		// make sure we run this task on the UI thread
		util.runTaskOnActivityUIThread(renderTask, activity);
	}

	public void hide() {
		//do this first for synchronization
		setState(PlacementState.NOT_LOADED);
		removeFromView();
		shouldRender = false;
		observer.onPlacementDisposed(activity, this);
	}
	
	private void removeFromView(){
		if (dialog != null) {
			Runnable hideTask = renderTaskFactory
					.createHidePlacementTask(dialog);
			util.runTaskOnActivityUIThread(hideTask, activity);
			dialog = null;
		}
	}

	public void onLoadFailure(int errorCode) {
		onLoadFailure();
	}

	private void onLoadFailure() {
		setState(PlacementState.LOAD_FAILED);
		if (delegate != null) {
			delegate.onRenderFailed();
		}
	}

	public void onLoadComplete() {
		setState(PlacementState.LOAD_COMPLETE);
		
		Runnable showTask = renderTaskFactory.createShowPlacementTask(dialog);
		util.runTaskOnActivityUIThread(showTask, activity);
		
		if (!impressionLogged) {
			impressionLogged = true;
			callbackProcessor.processUrlCallback(htmlAd.getImpressionUrl());
		
			if (delegate != null) {
				if(delegate instanceof IPlaynomicsPlacementDelegate){
					((IPlaynomicsPlacementDelegate) delegate).onShow(htmlAd.getTarget().getTargetData());
				} else if(delegate instanceof IPlaynomicsPlacementRawDelegate){
					((IPlaynomicsPlacementRawDelegate) delegate).onShow(htmlAd.getTarget().getTargetDataJson());
				}
			}
		}
	}

	public void onUrlLoading(String url) {
		hide();
		if (!Util.stringIsNullOrEmpty(url)) {
			if (!hasNativeCloseButton()) {
				HtmlCloseButton htmlClose = (HtmlCloseButton) htmlAd
						.getCloseButton();
				if (url.equals(htmlClose.getCloseLink())) {
					onAdClosed(true);
					return;
				}
			}

			onAdTouched();
			if (!url.equals(htmlAd.getClickLink())) {
				util.startUriIntent(url, activity);
			}
		}
	}

	private void onAdTouched() {
		callbackProcessor.processUrlCallback(htmlAd.getClickUrl());
		Target target = htmlAd.getTarget();

		if (target.getTargetType() == TargetType.URL
				&& !Util.stringIsNullOrEmpty(target.getTargetUrl())) {
			util.startUriIntent(target.getTargetUrl(), activity);
		}

		if (delegate != null) {
			if(delegate instanceof IPlaynomicsPlacementDelegate){
				((IPlaynomicsPlacementDelegate) delegate).onTouch(htmlAd.getTarget().getTargetData());
			} else if(delegate instanceof IPlaynomicsPlacementRawDelegate){
				((IPlaynomicsPlacementRawDelegate) delegate).onTouch(htmlAd.getTarget().getTargetDataJson());
			}
		}
	}

	private void onAdClosed(boolean closedByUser) {
		if (closedByUser) {
			callbackProcessor.processUrlCallback(htmlAd.getCloseUrl());
		}
		if (delegate != null) {
			if(delegate instanceof IPlaynomicsPlacementDelegate){
				((IPlaynomicsPlacementDelegate) delegate).onClose(htmlAd.getTarget().getTargetData());
			} else if(delegate instanceof IPlaynomicsPlacementRawDelegate){
				((IPlaynomicsPlacementRawDelegate) delegate).onClose(htmlAd.getTarget().getTargetDataJson());
			}
		}
	}

	public void attachActivity(Activity activity) {
		this.activity = activity;
		loadWebView();
	}

	public void detachActivity() {
		removeFromView();
	}

	public void onTouch() {
		hide();
		onAdClosed(true);
	}
}