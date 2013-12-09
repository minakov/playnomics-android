package com.playnomics.android.messaging;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.playnomics.android.messaging.Position.PositionType;
import com.playnomics.android.messaging.Target.TargetType;
import com.playnomics.android.util.Logger;
import com.playnomics.android.util.Logger.LogLevel;
import com.playnomics.android.util.Util;

public class HtmlAdFactory {
	
	private Logger logger;
	
	public HtmlAdFactory(Logger logger){
		this.logger = logger;
	}
	
	public HtmlAd createDataFromBytes(byte[] data, String placementName)
			throws UnsupportedEncodingException, JSONException {
		if (data == null || data.length == 0) {
			return null;
		}

		String jsonData = new String(data, Util.UT8_ENCODING);
		JSONObject json = new JSONObject(jsonData);
	
		logger.log(LogLevel.VERBOSE, "Received json for placement: %s\n%s", placementName, jsonData);
		
		JSONArray adArray = json.getJSONArray("ads");

		if (adArray == null || adArray.length() == 0) {
			return null;
		}

		JSONObject firstAdData = adArray.getJSONObject(0);

		Position position = getPositionFromResponse(firstAdData);
		Target target = getTargetFromResponse(firstAdData);
		CloseButton closeButton = getCloseButtonFromResponse(firstAdData);
		String clickUrl = cleanJSONString(firstAdData, "clickUrl", false);
		String clickLink = cleanJSONString(firstAdData, "clickLink", true);
		String impressionUrl = cleanJSONString(firstAdData, "impressionUrl",
				false);
		String closeUrl = cleanJSONString(firstAdData, "closeUrl", false);
		String htmlContent = cleanJSONString(firstAdData, "htmlContent", false);
		return new HtmlAd(position, closeButton, target, clickLink, clickUrl,
				impressionUrl, closeUrl, htmlContent);
	}

	private Position getPositionFromResponse(JSONObject response)
			throws JSONException {
		JSONObject positionData = response.getJSONObject("position");
		String positionTypeString = cleanJSONString(positionData,
				"positionType", false);
		if (positionTypeString.equals("fullscreen")) {
			return new Position(PositionType.FULLSCREEN);
		}
		throw new JSONException(String.format("Unsupported positionType: %s",
				positionTypeString));
	}

	private Target getTargetFromResponse(JSONObject response)
			throws JSONException {
		JSONObject targetJSONData = response.getJSONObject("target");
		String targetTypeString = cleanJSONString(targetJSONData, "targetType",
				false);
		TargetType targetType = toTargetType(targetTypeString);

		if (targetType == TargetType.URL) {
			String targetUrl = cleanJSONString(targetJSONData, "targetUrl",
					true);
			return new Target(TargetType.URL, targetUrl);
		}

		if (targetType == TargetType.DATA) {
			String targetDataJSON = targetJSONData
					.optString("targetData", null);
			Map<String, Object> targetData = null;
			if (targetDataJSON != null) {
				JSONObject targetDataJson = new JSONObject(targetDataJSON);
				targetData = Util.toMap(targetDataJson);
			}
			return new Target(TargetType.DATA, targetData, targetDataJSON);
		}

		if (targetType == TargetType.EXTERNAL) {
			return new Target(TargetType.EXTERNAL);
		}
		throw new JSONException("Could not deserialize the target information");
	}

	private CloseButton getCloseButtonFromResponse(JSONObject response)
			throws JSONException {

		JSONObject closeButtonJSONData = response.getJSONObject("closeButton");

		String closeButtonType = closeButtonJSONData
				.getString("closeButtonType");

		if (closeButtonType.equals("native")) {
			String closeImageUrl = closeButtonJSONData
					.getString("closeImageUrl");
			Integer height = closeButtonJSONData.getInt("height");
			Integer width = closeButtonJSONData.getInt("width");
			return new NativeCloseButton(height, width, closeImageUrl);
		} else if (closeButtonType.equals("html")) {
			String closeLink = closeButtonJSONData.getString("closeLink");
			return new HtmlCloseButton(closeLink);
		}
		throw new JSONException(String.format(
				"Unsupported closeButtonType: %s", closeButtonType));
	}

	private TargetType toTargetType(String targetTypeString)
			throws JSONException {
		if (targetTypeString == null) {
			throw new JSONException("targetTypeString is null");
		}

		targetTypeString = targetTypeString.toLowerCase();

		if (targetTypeString.equals("url")) {
			return TargetType.URL;
		} else if (targetTypeString.equals("external")) {
			return TargetType.EXTERNAL;
		} else if (targetTypeString.equals("data")) {
			return TargetType.DATA;
		}
		throw new JSONException(String.format("Unsupported targetType: %s",
				targetTypeString));
	}

	private String cleanJSONString(JSONObject json, String key, boolean optional)
			throws JSONException {
		if (!json.has(key) || json.get(key) == JSONObject.NULL) {
			return null;
		}

		if (optional) {
			return json.optString(key, null);
		}
		return json.getString(key);
	}
}
