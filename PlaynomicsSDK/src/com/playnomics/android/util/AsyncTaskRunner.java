package com.playnomics.android.util;

import android.os.AsyncTask;

import com.playnomics.android.util.IAsyncCall;

public class AsyncTaskRunner  extends AsyncTask<String, String, String> {
	private IAsyncCall asyncCall = null;

	public AsyncTaskRunner(final IAsyncCall asyncCall) {
		super();
		this.asyncCall = asyncCall;
	}

	@Override
	protected String doInBackground(String... arg0) {
		asyncCall.onBackgroundThread();
		return null;
	}
	
	@Override
	protected void onPostExecute(String s) {
		asyncCall.postExecuteOnUiThread();
	}
}
