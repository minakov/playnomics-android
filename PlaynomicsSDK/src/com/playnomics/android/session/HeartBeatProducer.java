package com.playnomics.android.session;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class HeartBeatProducer implements IHeartBeatProducer {
	private ScheduledThreadPoolExecutor hearbeatSchedule;
	private long heartbeatIntervalSeconds;

	private AtomicBoolean started;

	public HeartBeatProducer(long heartbeatIntervalSeconds) {
		this.started = new AtomicBoolean(false);
		this.heartbeatIntervalSeconds = heartbeatIntervalSeconds;
	}

	public void start(final HeartBeatHandler handler) {
		if (started.getAndSet(true)) {
			return;
		}
		hearbeatSchedule = new ScheduledThreadPoolExecutor(1);
		hearbeatSchedule
				.scheduleAtFixedRate(new Runnable() {
					public void run() {
						handler.onHeartBeat(heartbeatIntervalSeconds);
					}
				}, heartbeatIntervalSeconds, heartbeatIntervalSeconds,
						TimeUnit.SECONDS);
	}

	public void stop() {
		if (!started.getAndSet(false)) {
			return;
		}
		hearbeatSchedule.shutdown();
	}
}
