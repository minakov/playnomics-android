package com.playnomics.android.session;

public interface IHeartBeatProducer {

	public void start(final HeartBeatHandler handler);

	public void stop();

	public boolean isRunningForLongTime();
}