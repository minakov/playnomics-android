package com.playnomics.android.session;

import java.util.concurrent.atomic.AtomicBoolean;

import android.os.Handler;
import android.os.Message;

public class HeartBeatProducer implements IHeartBeatProducer {
	private static final int MSG_HEART_BEAT = 0xBEA1; 
	private static final long HEART_BEAT_DELAY_IN_MILL = 60*1000;
	private static final long MAX_HEART_BEAT_DELAY_IN_MILL = HEART_BEAT_DELAY_IN_MILL*15;

	private long startIntervalInMillSeconds = HEART_BEAT_DELAY_IN_MILL;
	private long heartbeatIntervalInMillSeconds = HEART_BEAT_DELAY_IN_MILL;
	private AtomicBoolean started;
	private HeartBeatHandler heartBeatHandler;

	public HeartBeatProducer() {
		this.started = new AtomicBoolean(false);
	}

	public HeartBeatProducer(long startIntervalInMillSeconds) {
		this.started = new AtomicBoolean(false);
		this.startIntervalInMillSeconds = startIntervalInMillSeconds;
	}

	public void start(final HeartBeatHandler handler) {
		if (started.getAndSet(true)) {
			return;
		}
		heartBeatHandler = handler;
		setHeartBeatTimer(startIntervalInMillSeconds);
	}

	public void stop() {
		if (!started.getAndSet(false)) {
			return;
		}
		mHandler.removeMessages(MSG_HEART_BEAT);
	}

	public boolean isRunningForLongTime() {
		return (heartbeatIntervalInMillSeconds!=startIntervalInMillSeconds);
	}

    private Handler mHandler = new Handler() { 
        @Override 
        public void handleMessage(Message msg) { 
            switch (msg.what) { 
                case MSG_HEART_BEAT: { 
                	HandleHeartBeat();
                    break;  
                } 
                default: 
                	super.handleMessage(msg); 
            }
        }
    };

    private void HandleHeartBeat() {
    	setHeartBeatTimer(getNextEventTimeDelay());
    	heartBeatHandler.onHeartBeat(heartbeatIntervalInMillSeconds);
    }
 
	private void setHeartBeatTimer(long delay) {
		heartbeatIntervalInMillSeconds = delay;
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_HEART_BEAT), heartbeatIntervalInMillSeconds); 
    } 
 
	private long getNextEventTimeDelay() {
	    if (heartbeatIntervalInMillSeconds>=MAX_HEART_BEAT_DELAY_IN_MILL)
	        return heartbeatIntervalInMillSeconds;

	    long delay = (heartbeatIntervalInMillSeconds*2);
	    if (delay<MAX_HEART_BEAT_DELAY_IN_MILL)
	        return delay;

	    return MAX_HEART_BEAT_DELAY_IN_MILL;
	}

}
