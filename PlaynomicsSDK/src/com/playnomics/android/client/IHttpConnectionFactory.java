package com.playnomics.android.client;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.TreeMap;

public interface IHttpConnectionFactory {
	HttpURLConnection startConnectionForUrl(String urlString)
			throws IOException;

	public String buildUrl(String url, String path,
			TreeMap<String, Object> queryParameters);
}
