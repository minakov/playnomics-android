package com.playnomics.android.client;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.playnomics.android.events.PlaynomicsEvent;
import com.playnomics.android.util.IConfig;
import com.playnomics.android.util.Util;

public class EventQueue implements IEventQueue {
	private IConfig config;
	private IHttpConnectionFactory factory;
	private ConcurrentLinkedQueue<String> eventUrlQueue;

	public EventQueue(IConfig config, IHttpConnectionFactory builder) {
		this.config = config;
		eventUrlQueue = new ConcurrentLinkedQueue<String>();
		this.factory = builder;
	}
	
	public void enqueueEvent(PlaynomicsEvent event) {
		String eventUrl = factory.buildUrl(config.getEventsUrl(),
				event.getUrlPath(), event.getEventParameters());
		enqueueEventUrl(eventUrl);
	}

	public void enqueueEventUrl(String eventUrl) {
		if (!Util.stringIsNullOrEmpty(eventUrl)) {
			eventUrlQueue.add(eventUrl);
		}
	}

	public boolean isEmpty() {
		return eventUrlQueue.isEmpty();
	}

	public String dequeueEventUrl() {
		return eventUrlQueue.remove();
	}
}