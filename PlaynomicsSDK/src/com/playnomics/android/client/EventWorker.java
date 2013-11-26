package com.playnomics.android.client;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.playnomics.android.util.Logger;
import com.playnomics.android.util.Logger.LogLevel;

public class EventWorker implements IEventWorker {

	private IHttpConnectionFactory connectionFactory;
	private IEventQueue eventQueue;
	private Logger logger;
	private AtomicBoolean running;

	private Thread thread;
	private ScheduledThreadPoolExecutor scheduler;

	public EventWorker(IEventQueue eventQueue, IHttpConnectionFactory factory,
			Logger logger) {
		this.running = new AtomicBoolean(false);
		this.connectionFactory = factory;
		this.eventQueue = eventQueue;
		this.logger = logger;
	}

	public boolean isRunning(){
		return running.get();
	}
	
	public void start() {
		if (running.getAndSet(true)) {
			// return if we were already running
			return;
		}

		thread = new Thread(new Runnable() {
			public void run() {
				doWork();
			}
		});
		thread.start();
	}

	public void stop() {
		if(scheduler != null){
			scheduler.shutdown();
		}
		
		if (!running.getAndSet(false)) {
			return;
		}

		try {
			thread.join();
		} catch (InterruptedException ex) {
			logger.log(LogLevel.WARNING, ex);
		}
	}

	private void stopWithRestart(){
		if (!running.getAndSet(false)) {
			return;
		}
		
		scheduler = new ScheduledThreadPoolExecutor(1);
		Runnable restartTask = new Runnable() {
			public void run() {
				start();
			}
		};
		scheduler.schedule(restartTask, 2, TimeUnit.MINUTES);
	}
	
	private void doWork() {
		while (running.get()) {
			while (!eventQueue.isEmpty() && running.get()) {
				String url = eventQueue.dequeueEventUrl();

				boolean successful = false;
				HttpURLConnection connection = null;
				try {
					connection = connectionFactory.startConnectionForUrl(url);
					successful = (connection.getResponseCode() == HttpURLConnection.HTTP_OK);
				} catch (IOException ex) {
					logger.log(LogLevel.WARNING, ex,
							"Could not connext to the event API. Shutting down the queue for 2 minutes ...", url);
					eventQueue.enqueueEventUrl(url);
					stopWithRestart();
				} finally {
					if (connection != null) {
						connection.disconnect();
					}
				}

				if (!successful) {
					eventQueue.enqueueEventUrl(url);
					logger.log(LogLevel.WARNING, "Event URL Request failed %s... retrying", url);
				} else {
					logger.log(LogLevel.DEBUG, "Event URL Request succeeded %s", url);
				}
			}

			if(!running.get()){
				return;
			}
			
			try {
				// put this thread to sleep
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				return;
			}
		}
	}

	public Set<String> getAllUnprocessedEvents() {
		HashSet<String> unprocessedEvents = new HashSet<String>();
		while(!eventQueue.isEmpty()){
			unprocessedEvents.add(eventQueue.dequeueEventUrl());
		}
		return unprocessedEvents;
	}
}
