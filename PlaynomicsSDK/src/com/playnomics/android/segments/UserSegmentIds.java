package com.playnomics.android.segments;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.playnomics.android.client.AssetClient;
import com.playnomics.android.client.HttpConnectionFactory;
import com.playnomics.android.client.AssetClient.ResponseStatus;
import com.playnomics.android.sdk.IPlaynomicsSegmentationDelegate;
import com.playnomics.android.session.Session;
import com.playnomics.android.util.IAsyncCall;
import com.playnomics.android.util.IConfig;
import com.playnomics.android.util.Logger;
import com.playnomics.android.util.Util;
import com.playnomics.android.util.Logger.LogLevel;

public class UserSegmentIds implements IAsyncCall {
	private Session session;
	private IConfig config;
	private Logger logger;
	private String status;
	private String description;
	private String error;
	private IPlaynomicsSegmentationDelegate delegate;
	private List<Long> list;

	public UserSegmentIds(Session session, IConfig config, Logger logger, final IPlaynomicsSegmentationDelegate delegate) {
		this.session = session;
		this.config = config;
		this.logger = logger;
		this.delegate = delegate;
	}

	@Override
	public void onBackgroundThread() {
		String apiUrl = config.getApiUrl();
		String userSegmentsPath = config.getUserSegmentsPath();

		TreeMap<String, Object> queryParams = new TreeMap<String, Object>();
		queryParams.put(config.getApplicationIdKey(),
				session.getApplicationId());
		queryParams.put(config.getUserIdKey(), session.getUserId());

		HttpConnectionFactory connectionFactory = new HttpConnectionFactory(
				logger);
		AssetClient assetClient = new AssetClient(connectionFactory, logger);
		AssetClient.AssetResponse jsonResponse = assetClient
				.requestAsset(apiUrl, userSegmentsPath, queryParams);

		if (jsonResponse==null || jsonResponse.getResponseCode()==0) {
			error = "Error";
			description = jsonResponse==null?"No resposne. Connection failed":"Connection failed";
			return;
		}
		try {
			byte[] data = jsonResponse.getData();
			if (data == null || data.length == 0) {
				error = "Error";
				description = "No data in resposne";
				return;
			}

			String jsonData = new String(data, Util.UT8_ENCODING);
			JSONObject json = new JSONObject(jsonData);

			logger.log(LogLevel.VERBOSE, "Received json for segments: %s\n%s", session.getUserId(), jsonData);

			status = json.optString("status");
			error = json.optString("error", null);
			JSONArray segmentArray = json.optJSONArray("segments");
			if (segmentArray != null && segmentArray.length()!=0) {
				list = new ArrayList<Long>();
				for (int i=0; i<segmentArray.length(); i++) {
					String val = segmentArray.getString(i);
					list.add( Long.parseLong(val) );
				}
			}
			if (error==null && jsonResponse.getStatus() != ResponseStatus.SUCCESS) {
				error = "HttpError";
				description = Integer.toString(jsonResponse.getResponseCode());
			}
		} catch (JSONException ex) {
			description = "Json error";
			logger.log(LogLevel.ERROR, ex, "JSONException: Cannot get user segments");
		} catch (UnsupportedEncodingException ex) {
			description = "Json string encoding error";
			logger.log(LogLevel.ERROR, ex, "UnsupportedEncodingException: Cannot get user segments");
		}
	}

	@Override
	public void postExecuteOnUiThread() {
		if (error==null)
			delegate.onFetchedUserSegmentIds(list);
		else
			delegate.onFetchedUserSegmentIdsError(error, description);
	}

	public String getStatus() {
		return status;
	}

	public String getDescription() {
		return description;
	}

	public String getError() {
		return error;
	}

	public List<Long> getList() {
		return list;
	}
	
}
