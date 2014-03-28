package com.playnomics.android.session;

import java.util.concurrent.atomic.AtomicBoolean;

import com.playnomics.android.util.IConfig;

import android.os.Handler;
import android.os.Message;

public class HeartBeatProducer implements IHeartBeatProducer {
	private static final int MSG_HEART_BEAT = 0xBEA1; 

	private AtomicBoolean started;
	private HeartBeatHandler heartBeatHandler;
	private int delayIndex = 0;
	private int[] heartBeatIntervals;

	public HeartBeatProducer() {
		this.started = new AtomicBoolean(false);
	}

	public HeartBeatProducer(IConfig config) {
		this.started = new AtomicBoolean(false);
		this.heartBeatIntervals = config.getHeartBeatIntervalInMinutes();
	}

	public void start(final HeartBeatHandler handler) {
		if (started.getAndSet(true)) {
			return;
		}
		heartBeatHandler = handler;
		delayIndex = 0;
		setHeartBeatTimer(heartBeatIntervals[delayIndex]);
	}

	public void stop() {
		if (!started.getAndSet(false)) {
			return;
		}
		mHandler.removeMessages(MSG_HEART_BEAT);
	}

	public boolean isRunningForLongTime() {
		return (delayIndex!=0);
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
    	int delayInMillSeconds = heartBeatIntervals[delayIndex]*60*1000;
    	setHeartBeatTimer(getNextDelayInMinutes());
    	heartBeatHandler.onHeartBeat(delayInMillSeconds);
    }
 
	private void setHeartBeatTimer(long delayInMinutes) {
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_HEART_BEAT), delayInMinutes*60*1000); 
    }

	private int getNextDelayInMinutes() {
		int nextIndex = delayIndex + 1;
		if (nextIndex<heartBeatIntervals.length) {
			delayIndex = nextIndex;
	    }
		return heartBeatIntervals[delayIndex];
	}

}
