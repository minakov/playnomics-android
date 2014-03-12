package com.playnomics.android.sdk;

import java.util.List;

public abstract interface IPlaynomicsSegmentationDelegate {	
	public void onFetchedUserSegmentIds(List<Long> segmentationIds);
	public void onFetchedUserSegmentIdsError(String error, String description);
}
