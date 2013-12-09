package com.playnomics.android.sdk;

import java.util.Map;

public interface IPlaynomicsPlacementDelegate extends IPlacementDelegate {
	void onShow(Map<String, Object> jsonData);

	void onTouch(Map<String, Object> jsonData);

	void onClose(Map<String, Object> jsonData);
}
