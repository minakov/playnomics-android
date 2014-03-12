package com.playnomics.android.util;

public interface IAsyncCall {
	public void onBackgroundThread();
	public void postExecuteOnUiThread();
}
