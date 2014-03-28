package com.playnomics.android.events;

import com.playnomics.android.session.GameSessionInfo;
import com.playnomics.android.util.*;

public class AppPauseEvent extends ImplicitEvent {

	public AppPauseEvent(IConfig config, GameSessionInfo sessionInfo,
			LargeGeneratedId instanceId, EventTime sessionStartTime,
			int sequenceNumber, int touches, int totalTouches,
			int delayInMinutes) {
		super(config, sessionInfo, instanceId);

		final int updateTimeIntervalMilliseconds = delayInMinutes*60*1000;
		final int keysPressed = 0;
		final int totalKeysPressed = 0;
		final int collectionMode = config.getCollectionMode();

		appendParameter(config.getSequenceKey(), sequenceNumber);
		appendParameter(config.getTouchesKey(), touches);
		appendParameter(config.getTotalTouchesKey(), totalTouches);
		appendParameter(config.getSessionStartTimeKey(), sessionStartTime);
		appendParameter(config.getKeysPressedKey(), keysPressed);
		appendParameter(config.getTotalKeysPressedKey(), totalKeysPressed);
		appendParameter(config.getIntervalMillisecondsKey(),
				updateTimeIntervalMilliseconds);
		appendParameter(config.getCollectionModeKey(), collectionMode);
	}

	@Override
	public String getUrlPath() {
		return config.getEventPathAppPause();
	}
}
