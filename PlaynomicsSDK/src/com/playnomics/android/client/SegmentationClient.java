package com.playnomics.android.client;


import android.os.AsyncTask;

import com.playnomics.android.sdk.IPlaynomicsSegmentationDelegate;
import com.playnomics.android.segments.UserSegmentIds;
import com.playnomics.android.session.Session;
import com.playnomics.android.util.IAsyncCall;
import com.playnomics.android.util.AsyncTaskRunner;
import com.playnomics.android.util.IConfig;
import com.playnomics.android.util.Logger;

public class SegmentationClient {
	private Session session;
	private IConfig config;
	private Logger logger;

	public SegmentationClient(Session session, IConfig config, Logger logger) {
		super();
		this.session = session;
		this.config = config;
		this.logger = logger;
	}

	public void fetchUserSegmentIds(final IPlaynomicsSegmentationDelegate delegate) {
		AsyncTaskRunner fetchUserSegmentTask = new AsyncTaskRunner(new UserSegmentIds(session, config, logger, delegate));
		fetchUserSegmentTask.execute();
	}
}
