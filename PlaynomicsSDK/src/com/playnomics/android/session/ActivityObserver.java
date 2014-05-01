package com.playnomics.android.session;

import java.util.concurrent.ConcurrentLinkedQueue;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.playnomics.android.util.Util;

/**
 * @author jaredjenkins Tracks the life-cycle of Activities as a proxy for
 *         session state machine, and user interaction with the Activity.
 */
public class ActivityObserver implements IActivityObserver {
	private SessionStateMachine stateMachine;
	private ConcurrentLinkedQueue<Activity> activities;
	private Util util;

	public ActivityObserver(Util util) {
		this.util = util;
		this.activities = new ConcurrentLinkedQueue<Activity>();
	}

	public void setStateMachine(SessionStateMachine stateMachine) {
		this.stateMachine = stateMachine;
	}

	/**
	 * @param activity
	 *            Enqueues the activity and attaches a proxy object on the
	 *            callback so that the SDK can observe and track user
	 *            interaction.
	 */
	public void observeNewActivity(Activity activity,
			final TouchEventHandler handler) {
		util.overrideActivityWindowCallback(activity, handler);
		activities.add(activity);
		stateMachine.resume();
	}

	/**
	 * Dequeues the previously observed activity. When there are no longer any
	 * more activities to observe we assume that the application has been
	 * paused, and can be killed or resumed at some future state.
	 */
	public void forgetLastActivity() {
		Activity activity = activities.remove();
		util.removeWindowCallback(activity);
		mHandler.sendEmptyMessageDelayed(0, 500);
	}

	private Handler mHandler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
			if (activities.isEmpty()) {
				stateMachine.pause();
			}
		}
	};

}
