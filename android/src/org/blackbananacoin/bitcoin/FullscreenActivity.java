/*
 * Copyright 2013 Y12STUDIO
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.blackbananacoin.bitcoin;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.EnumMap;

import org.blackbananacoin.bitcoin.util.AddressBlockChainWatcher;
import org.blackbananacoin.bitcoin.util.DownloadExchange;
import org.blackbananacoin.bitcoin.util.SystemUiHider;
import org.blackbananacoin.bitcoin.util.UI;
import org.blackbananacoin.common.bitcoin.Bitcoins;
import org.blackbananacoin.common.json.TwdBit;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class FullscreenActivity extends Activity {
	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = true;

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

	/**
	 * If set, will toggle the system UI visibility upon interaction. Otherwise,
	 * will show the system UI visibility upon interaction.
	 */
	private static final boolean TOGGLE_ON_CLICK = true;

	/**
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

	/**
	 * The instance of the {@link SystemUiHider} for this activity.
	 */
	private SystemUiHider mSystemUiHider;

	private Handler _handler = new Handler();

	private DownloadExchange dl = new DownloadExchange();
	private AddressBlockChainWatcher addrWatcher = new AddressBlockChainWatcher();
	private QrCodeEncoder qrcodeEncoder = new QrCodeEncoder();

	private Runnable runForDownlaodInfo = new Runnable() {

		public void run() {
			try {
				inRunDownloadExchange();
			} catch (Exception ex) {

			} finally {
				_handler.postDelayed(this, UI.TimeDownloadInterval);
			}
		}
	};

	private Runnable runForRefreshInfo = new Runnable() {

		public void run() {
			try {
				inRunRefresh();
			} catch (Exception ex) {

			} finally {
				_handler.postDelayed(this, UI.TimeRefreshInterval);
			}
		}
	};

	public void inRunDownloadExchange() {
		TwdBit twdbit = dl.getExchange();
		checkNotNull(twdbit);
		updateExchange(twdbit);
	}

	protected void inRunRefresh() {
		if (lastUpdateTime > 0) {
			long diffs = (System.currentTimeMillis() - lastUpdateTime) / 1000;
			int min = (int) (diffs / 60);
			int sec = (int) (diffs % 60);
			tvUpdateStatus.setText(String.format("%s分%s秒前更新", min, sec));
		} else {
			tvUpdateStatus.setText("首次更新");
		}
	}

	OnClickListener clDebug = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// runDownloadExchange();
			addrWatcher.createWebsocket();
		}
	};

	private ImageView imgQrBcAddr;

	private TextView tvAmount;
	private TextView tvUpdateStatus;
	private long lastUpdateTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_fullscreen);

		final View controlsView = findViewById(R.id.fullscreen_content_controls);
		final View contentView = findViewById(R.id.fullscreen_content);
		tvMbtcTwd = (TextView) findViewById(R.id.tvBtcTwdInfo);
		tvAmount = (TextView) findViewById(R.id.tvAmount);
		tvUpdateStatus = (TextView) findViewById(R.id.tvUpdateStatus);

		imgQr1 = (ImageView) findViewById(R.id.imgQr1);
		imgQrBcAddr = (ImageView) findViewById(R.id.imgQrBcAddr);
		// imgQr1.setOnClickListener(clDebug);

		// Set up an instance of SystemUiHider to control the system UI for
		// this activity.
		mSystemUiHider = SystemUiHider.getInstance(this, contentView,
				HIDER_FLAGS);
		mSystemUiHider.setup();
		mSystemUiHider
				.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
					// Cached values.
					int mControlsHeight;
					int mShortAnimTime;

					@Override
					@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
					public void onVisibilityChange(boolean visible) {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
							// If the ViewPropertyAnimator API is available
							// (Honeycomb MR2 and later), use it to animate the
							// in-layout UI controls at the bottom of the
							// screen.
							if (mControlsHeight == 0) {
								mControlsHeight = controlsView.getHeight();
							}
							if (mShortAnimTime == 0) {
								mShortAnimTime = getResources().getInteger(
										android.R.integer.config_shortAnimTime);
							}
							controlsView
									.animate()
									.translationY(visible ? 0 : mControlsHeight)
									.setDuration(mShortAnimTime);
						} else {
							// If the ViewPropertyAnimator APIs aren't
							// available, simply show or hide the in-layout UI
							// controls.
							controlsView.setVisibility(visible ? View.VISIBLE
									: View.GONE);
						}

						if (visible && AUTO_HIDE) {
							// Schedule a hide().
							delayedHide(AUTO_HIDE_DELAY_MILLIS);
						}
					}
				});

		// Set up the user interaction to manually show or hide the system UI.
		contentView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (TOGGLE_ON_CLICK) {
					mSystemUiHider.toggle();
				} else {
					mSystemUiHider.show();
				}
			}
		});

		// Upon interacting with UI controls, delay any scheduled hide()
		// operations to prevent the jarring behavior of controls going away
		// while interacting with the UI.
		findViewById(R.id.dummy_button).setOnTouchListener(
				mDelayHideTouchListener);

		_handler.postDelayed(runForDownlaodInfo, 1000);
		_handler.postDelayed(runForRefreshInfo, 5000);

		updateQrCodeBlockchainAddrQuery();
	}

	private void updateQrCodeBlockchainAddrQuery() {
		String content = UI.BC_URL_ADDR_PREFIX + UI.BITCOIN_ADDR_TEST;
		int dimention = 500;
		int width = 500;
		int height = 500;
		try {
			int[] pixels = qrcodeEncoder.getPixels(content, dimention);
			Bitmap bitmap = Bitmap.createBitmap(width, height,
					Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
			imgQrBcAddr.setImageBitmap(bitmap);
		} catch (WriterException e) {
			e.printStackTrace();
		}

	}

	protected void updateExchange(TwdBit twdbit) {

		tvUpdateStatus.setText("匯率更新中..");
		checkNotNull(twdbit);

		tvMbtcTwd.setText(UI.DFMT_INT.format(twdbit.getBtctwd()));
		double amount = (UI.TWD_SRV / twdbit.getBtctwd())
				* (1 + (UI.FEE_RATE_SRV_PERCENT / 100f));

		// update qr code

		String amountFormat = String.format("%.8f", amount);

		tvAmount.setText(amountFormat + " BTC(內含3％手續費)");

		// update status
		lastUpdateTime = System.currentTimeMillis();
		inRunRefresh();
		updatePriceQrCode(amount);
	}

	private void updatePriceQrCode(double amount) {
		String content = Bitcoins.buildUri(UI.BITCOIN_ADDR_TEST, amount);
		int dimention = 500;
		int width = 500;
		int height = 500;
		try {
			int[] pixels = qrcodeEncoder.getPixels(content, dimention);
			Bitmap bitmap = Bitmap.createBitmap(width, height,
					Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
			imgQr1.setImageBitmap(bitmap);
		} catch (WriterException e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		delayedHide(100);
	}

	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (AUTO_HIDE) {
				delayedHide(AUTO_HIDE_DELAY_MILLIS);
			}
			return false;
		}
	};

	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			mSystemUiHider.hide();
		}
	};

	private TextView tvMbtcTwd;

	private ImageView imgQr1;

	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}

	private void t(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_0:
			// some numpad show 144.
			t("0");
			break;
		case KeyEvent.KEYCODE_1:
			// some numpad show 145.
			t("1");
			break;
		case KeyEvent.KEYCODE_2:
			t("2");
			break;
		case KeyEvent.KEYCODE_ENTER:
			// some numpad show 160.
			t("ENTER");
			break;
		default:
			break;
		}
		t("" + keyCode);
		return true;
	}
}
