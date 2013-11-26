package com.playnomics.android.events;

import java.util.Date;

import com.playnomics.android.session.GameSessionInfo;
import com.playnomics.android.util.IConfig;

public class UserInfoEvent extends ExplicitEvent {

	public UserInfoEvent(IConfig config, GameSessionInfo sessionInfo,
			String source, String campaign, Date installDate) {
		super(config, sessionInfo);
		appendParameter(config.getUserInfoSourceKey(), source);
		appendParameter(config.getUserInfoCampaignKey(), campaign);
		// date should be in EPOCH format

		if (installDate != null) {
			appendParameter(config.getUserInfoInstallDateKey(),
					installDate.getTime());
		}
		appendParameter(config.getUserInfoTypeKey(), "update");
	}

	public UserInfoEvent(IConfig config, GameSessionInfo sessionInfo,
			String pushRegistrationId) {
		super(config, sessionInfo);
		appendParameter(config.getUserInfoPushTokenKey(), pushRegistrationId);
		appendParameter(config.getUserInfoTypeKey(), "update");
	}

	@Override
	public String getUrlPath() {
		return config.getEventPathUserInfo();
	}
}
