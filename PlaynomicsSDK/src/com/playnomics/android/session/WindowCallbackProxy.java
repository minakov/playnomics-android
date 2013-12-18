package com.playnomics.android.session;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.playnomics.android.util.Logger;
import com.playnomics.android.util.Logger.LogLevel;

import android.view.MotionEvent;
import android.view.Window;

/**
 * @author jaredjenkins Uses a dynamic proxy to safely intercept the UI events
 *         for an Activity's Window Callback.
 */
public class WindowCallbackProxy implements InvocationHandler {

	private Logger logger;
	private Window.Callback callback;
	private TouchEventHandler eventHandler;

	public Window.Callback getOriginalCallback() {
		return callback;
	}

	public static Window.Callback newCallbackProxyForActivity(
			Window.Callback callback, TouchEventHandler eventHandler, Logger logger) {
		Object proxy = java.lang.reflect.Proxy.newProxyInstance(
				callback.getClass().getClassLoader(), 
				new Class[]{ Window.Callback.class}, 
				new WindowCallbackProxy(callback, eventHandler, logger));
		
		return (Window.Callback) proxy;
	}

	private WindowCallbackProxy(Window.Callback callback,
			TouchEventHandler eventHandler, Logger logger) {
		this.callback = callback;
		this.eventHandler = eventHandler;
		this.logger = logger;
	}

	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {

		Object result;
		if (method.getName().equals("dispatchTouchEvent")) {
			if (args != null && args.length > 0) {
				Object event = args[0];

				if (event != null
						&& event instanceof MotionEvent
						&& (((MotionEvent) event).getActionMasked() == MotionEvent.ACTION_DOWN || ((MotionEvent) event)
								.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN)) {
					logger.log(LogLevel.VERBOSE, "Touch event received.");
					eventHandler.onTouchEventReceived();
				}
			}
		}
		// invoke the method as normal
		result = method.invoke(callback, args);
		return result;
	}

}
