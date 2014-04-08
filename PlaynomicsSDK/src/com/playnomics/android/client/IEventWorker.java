package com.playnomics.android.client;

import java.io.UnsupportedEncodingException;
import java.util.Set;

import com.playnomics.android.events.PlaynomicsEvent;

public interface IEventWorker {

	public boolean isRunning();
	
	public void start();

	public void stop();

	public Set<String> getAllUnprocessedEvents();

	public void enqueueEvent(PlaynomicsEvent event)
			throws UnsupportedEncodingException;

	public void enqueueEventUrl(String eventUrl);

}