package com.playnomics.android.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import com.playnomics.android.events.PlaynomicsEvent;
import com.playnomics.android.util.IConfig;
import com.playnomics.android.util.Logger;
import com.playnomics.android.util.Logger.LogLevel;

public class EventWorker implements IEventWorker {

	private IHttpConnectionFactory connectionFactory;
	private IEventQueue eventQueue;
	private HandlerThread handerThread;
	private Handler handler;
	private AtomicBoolean running;
	private AtomicBoolean startComplete;
	private IConfig config;
	private Logger logger;
	private HttpURLConnection connection;

	private static final int MSG_START_QUE = 1;
	private static final int MSG_EMPTY_WAIT_QUE = 2;
	private static final int MSG_NEW_EVENT = 3;

	public EventWorker(IConfig config, IHttpConnectionFactory factory,
			Logger logger) {
		this.running = new AtomicBoolean(false);
		this.connectionFactory = factory;
		this.config = config;
		this.logger = logger;
		this.eventQueue = new EventQueue(config, connectionFactory);
		this.startComplete = new AtomicBoolean(false);
	}

	public boolean isRunning(){
		return running.get();
	}

	public void start() {
		if (running.getAndSet(true)) {
			// return if we were already running
			return;
		}

		handerThread = createHandlerThread();
		handerThread.start();
	}

	public void stop() {
		if (!running.getAndSet(false)) {
			return;
		}

		startComplete.set(false);
		handerThread.quit();
		handerThread.interrupt();
	}


	private HandlerThread createHandlerThread() {
		return new HandlerThread("EventWorker") {
			@Override
			protected void onLooperPrepared() {
				super.onLooperPrepared();
				handler = new Handler(getLooper()) {
					public void handleMessage(Message message) {
						// Handle the message on the thread associated to the given looper.
						dataHandler(message.what);
					}
				};
				handler.sendEmptyMessage(MSG_START_QUE);
				startComplete.set(true);
			}
		};
	}

	private void dataHandler(int msg) {
		if (!running.get())
			return;

		String url = dequeueEventUrl();
		if (url==null) {
			if (running.get())
				handler.sendEmptyMessageDelayed(MSG_EMPTY_WAIT_QUE, config.getQueEmptyTimeoutInMilliseconds());
			return;
		}
		if (!running.get()) {
			enqueueEventUrl(url);
			return;
		}
		handler.sendEmptyMessage(MSG_NEW_EVENT);

		int responseCode = dispatchData(url);
		if (responseCode==HttpURLConnection.HTTP_OK) {
			logger.log(LogLevel.DEBUG, "Event URL Request succeeded %s", url);
		} else {
			// HTTP error retry sending again later.
			enqueueEventUrl(url);
			if (responseCode==0 && running.get()) {	// Connection failure error
				try {
					// Wait and try later
					logger.log(LogLevel.WARNING,
							"Could not connext to the event API. Shutting down the queue for 2 minutes ...", url);
					Thread.sleep(config.getQueHttpErrorTimeoutInSeconds());
				} catch (InterruptedException e) {
				}
			}
		}
	}

	private int dispatchData(String url) {
		try {
			connection = connectionFactory.startConnectionForUrl(url);
			if (connection!=null)
				return connection.getResponseCode();
		} catch (IOException ex) {
			logger.log(LogLevel.WARNING, ex,
					"Could not connext to the event API.", url);
		} catch (NullPointerException ex) {
			logger.log(LogLevel.WARNING, ex,
					"Could not connext to the event API.", url);
		} finally {
			if (connection != null) {
				connection.disconnect();
				connection = null;
			}
		}
		return 0;
	}

	private String dequeueEventUrl() {
		if (!eventQueue.isEmpty()) {
			try {
				return eventQueue.dequeueEventUrl();
			} catch(java.util.NoSuchElementException ex) {
			}
		}
		return null;
	}

	private boolean isReadyAndQueEmpty() {
		return (startComplete.get() && eventQueue.isEmpty());
	}

	public void enqueueEvent(PlaynomicsEvent event)
			throws UnsupportedEncodingException {
		boolean isReadyAndQueEmpty = isReadyAndQueEmpty();

		eventQueue.enqueueEvent(event);

		if (isReadyAndQueEmpty)
			handler.sendEmptyMessage(MSG_NEW_EVENT); // Ping the handler
	}

	public void enqueueEventUrl(String url) {
		boolean isReadyAndQueEmpty = isReadyAndQueEmpty();

		eventQueue.enqueueEventUrl(url);

		if (isReadyAndQueEmpty)
			handler.sendEmptyMessage(MSG_NEW_EVENT); // Ping the handler
	}

	public Set<String> getAllUnprocessedEvents() {
		HashSet<String> unprocessedEvents = new HashSet<String>();
		while(!eventQueue.isEmpty()){
			unprocessedEvents.add(eventQueue.dequeueEventUrl());
		}
		return unprocessedEvents;
	}
}
