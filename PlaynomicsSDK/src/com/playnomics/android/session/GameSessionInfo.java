package com.playnomics.android.session;

import com.playnomics.android.util.*;

/**
 * @author jaredjenkins Encapsulates general information about the session for
 *         PlaynomicsEvents.
 */
public class GameSessionInfo {
	private Long applicationId;
	private String userId;
	private String androidId;
	private LargeGeneratedId sessionId;

	public GameSessionInfo(Long applicationId, String userId,
			String androidId, LargeGeneratedId sessionId) {
		this.applicationId = applicationId;
		this.userId = userId;
		this.androidId = androidId;
		this.sessionId = sessionId;
	}

	public Long getApplicationId() {
		return applicationId;
	}

	public String getUserId() {
		return userId;
	}

	public String getAndroidId() {
		return androidId;
	}

	public LargeGeneratedId getSessionId() {
		return sessionId;
	}
}
