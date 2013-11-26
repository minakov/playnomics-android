package com.playnomics.android.client;

import java.io.UnsupportedEncodingException;

import com.playnomics.android.events.PlaynomicsEvent;

public interface IEventQueue {

	public void enqueueEvent(PlaynomicsEvent event)
			throws UnsupportedEncodingException;

	public void enqueueEventUrl(String eventUrl);

	public boolean isEmpty();

	public String dequeueEventUrl();
}