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

	public UserInfoEvent(IConfig config, GameSessionInfo sessionInfo,
			String key, String value) {
		super(config, sessionInfo);
		appendParameter(key, value);
		appendParameter(config.getUserInfoTypeKey(), "update");
	}

	public UserInfoEvent(IConfig config, GameSessionInfo sessionInfo) {
		super(config, sessionInfo);
		appendParameter(config.getUserInfoTypeKey(), "update");
	}

	public void setGender(String gender) {
		if (gender!=null)
			appendParameter(config.getUserInfoGenderKey(), gender);
	}

	public void setBirthYear(int birthYear) {
		if (birthYear>0)
			appendParameter(config.getUserInfoBirthYearKey(), birthYear);
	}

	public void setAppVersion(String appVersion) {
		if (appVersion!=null)
			appendParameter(config.getAppVersionKey(), appVersion);
	}

	public void setDeviceModel(String deviceModel) {
		if (deviceModel!=null)
			appendParameter(config.getDeviceModelKey(), deviceModel);
	}

	public void setDeviceMaker(String maker) {
		if (maker!=null)
			appendParameter(config.getDeviceMakerKey(), maker);
	}

	public void setDeviceOSVersion(String deviceOSVersion) {
		if (deviceOSVersion!=null)
			appendParameter(config.getDeviceOSVersionKey(), deviceOSVersion);
	}

	@Override
	public String getUrlPath() {
		return config.getEventPathUserInfo();
	}
}
