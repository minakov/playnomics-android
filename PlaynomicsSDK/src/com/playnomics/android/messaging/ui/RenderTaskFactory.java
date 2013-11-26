package com.playnomics.android.messaging.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.playnomics.android.messaging.HtmlAd;
import com.playnomics.android.messaging.NativeCloseButton;
import com.playnomics.android.messaging.Placement;
import com.playnomics.android.messaging.Placement.IPlacementStateObserver;
import com.playnomics.android.messaging.ui.PlayViewFactory.IImageViewHandler;
import com.playnomics.android.messaging.ui.PlayWebView.IPlayWebViewHandler;
import com.playnomics.android.util.Logger;
import com.playnomics.android.util.Logger.LogLevel;

public class RenderTaskFactory {

	private IPlayViewFactory viewFactory;
	private Logger logger;

	public RenderTaskFactory(IPlayViewFactory viewFactory, Logger logger) {
		this.viewFactory = viewFactory;
		this.logger = logger;
	}

	public Runnable createLayoutPlacementTask(final Placement placement,
			final HtmlAd htmlAd, final Activity activity,
			final IPlayWebViewHandler handler,
			final IImageViewHandler imageViewHandler,
			final IPlacementStateObserver observer) {

		return new Runnable() {
			public void run() {
				try {
					PlayWebView webView = viewFactory.createPlayWebView(
							activity, htmlAd.getHtmlContent(),
							 handler, logger);
					
					PlayDialog dialog;
					if (htmlAd.getCloseButton() instanceof NativeCloseButton) {
						NativeCloseButton closeButton = (NativeCloseButton) htmlAd
								.getCloseButton();

						ImageView closeButtonView = viewFactory
								.createImageView(activity, imageViewHandler);

						byte[] imageData = closeButton.getImageData();
						Bitmap bitmap = BitmapFactory.decodeByteArray(
								imageData, 0, imageData.length);
						closeButtonView.setImageBitmap(bitmap);
						dialog = viewFactory.createPlayDialog(activity,
								webView, observer, closeButtonView, placement);
					} else {
						dialog = viewFactory.createPlayDialog(activity,
								webView, observer, placement);
					}
					
					placement.setDialog(dialog);
 				} catch (Exception ex) {
					logger.log(LogLevel.WARNING,
							"The placement %s cannot be rendered",
							placement.getPlacementName());
					logger.log(LogLevel.WARNING, ex);
				}
			}
		};
	}
	
	public Runnable createShowPlacementTask(final PlayDialog dialog){
		return new Runnable() {
			public void run() {
				dialog.show();
			}
		};
	}

	public Runnable createHidePlacementTask(final PlayDialog dialog) {
		return new Runnable() {
			public void run() {
				dialog.dismiss();
			}
		};
	}
}
