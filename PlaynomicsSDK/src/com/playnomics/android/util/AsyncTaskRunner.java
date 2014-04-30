package com.playnomics.android.util;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.playnomics.android.util.IAsyncCall;

public class AsyncTaskRunner {
	private IAsyncCall asyncCall = null;
	private InternalAsyncTask asyncTask = null;

	public AsyncTaskRunner(final IAsyncCall asyncCall) {
		this.asyncCall = asyncCall;
		this.asyncTask = new InternalAsyncTask();
	}

	public void execute() {
		if (Looper.myLooper() != Looper.getMainLooper()) {
			Handler handler = new Handler(Looper.getMainLooper()) {
				@Override
				public void handleMessage(Message msg) {
					asyncTask.execute();
				}
			};
			handler.sendEmptyMessage(0);
			return;
		}
		asyncTask.execute();
	}

	public void cancel(boolean mayInterruptIfRunning) {
		asyncTask.cancel(mayInterruptIfRunning);
	}

	private class InternalAsyncTask extends AsyncTask<String, String, String> {
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
}
