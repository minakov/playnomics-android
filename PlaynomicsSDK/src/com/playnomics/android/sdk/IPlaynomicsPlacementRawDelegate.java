package com.playnomics.android.sdk;


public interface IPlaynomicsPlacementRawDelegate extends IPlacementDelegate {
	void onShow(String rawJsonData);

	void onTouch(String rawJsonData);

	void onClose(String rawJsonData);
}
