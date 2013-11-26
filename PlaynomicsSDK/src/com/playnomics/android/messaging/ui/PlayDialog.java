package com.playnomics.android.messaging.ui;
import com.playnomics.android.messaging.Placement;
import com.playnomics.android.messaging.Placement.IPlacementStateObserver;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class PlayDialog extends Dialog {
	private PlayWebView webView;
	private ImageView nativeCloseButton;
	private Activity activity;
	private IPlacementStateObserver observer;
	private Placement placement;
	
	public PlayDialog(Activity activity, PlayWebView webView, final IPlacementStateObserver observer, Placement placement) {
		super(activity);
		this.placement = placement;
		this.webView = webView;
		this.activity = activity;
		this.observer = observer;
	}
	
	public PlayDialog(Activity activity, PlayWebView webView, final IPlacementStateObserver observer, ImageView nativeCloseButton, Placement placement) {
		this(activity, webView, observer, placement);
		this.nativeCloseButton = nativeCloseButton;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, 
				LayoutParams.FILL_PARENT);
		addContentView(webView, layoutParams);
		
		if(nativeCloseButton != null){
			RelativeLayout.LayoutParams params = new LayoutParams(nativeCloseButton.getWidth(), nativeCloseButton.getHeight());
			params.addRule(RelativeLayout.ALIGN_PARENT_TOP | RelativeLayout.ALIGN_PARENT_RIGHT);
			addContentView(nativeCloseButton, params);
		}
		
		//do this after we add the content so the content fills the entire screen
		Window window = getWindow();
		window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		//make the dialog window fullscreen
		window.setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.FILL_PARENT);
		setCanceledOnTouchOutside(false);
		//can't be close by the back button
		setCancelable(false);
		
		observer.onPlacementShown(activity, placement);
	}
}
