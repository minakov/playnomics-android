package com.playnomics.android.client;

import java.io.UnsupportedEncodingException;
import java.util.Set;

import com.playnomics.android.events.PlaynomicsEvent;

public class StubEventWorker implements IEventWorker {
	private IEventQueue eventQueue;

	@Override
	public boolean isRunning() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

	private Set<String> unprocessedUrls;

	public void setAllUnprocessedEvents(Set<String> urls) {
		unprocessedUrls = urls;
	}

	@Override
	public Set<String> getAllUnprocessedEvents() {
		return unprocessedUrls;
	}

	@Override
	public void enqueueEvent(PlaynomicsEvent event)
			throws UnsupportedEncodingException {
		eventQueue.enqueueEvent(event);
	}

	@Override
	public void enqueueEventUrl(String eventUrl) {
		eventQueue.enqueueEventUrl(eventUrl);
	}

	public void setEventQueue(IEventQueue eventQueue) {
		this.eventQueue = eventQueue;
	}
}
