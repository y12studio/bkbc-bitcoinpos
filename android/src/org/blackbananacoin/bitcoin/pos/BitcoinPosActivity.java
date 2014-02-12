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
package org.blackbananacoin.bitcoin.pos;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;
import java.util.Date;

import org.blackbananacoin.bitcoinpos.lib.FSMBitcoinPos;
import org.blackbananacoin.bitcoinpos.lib.FSMBitcoinPos.FsmEvent;
import org.blackbananacoin.bitcoinpos.lib.FSMBitcoinPos.FsmState;
import org.blackbananacoin.bitcoinpos.lib.FSMBitcoinPos.StateMachineUi;
import org.blackbananacoin.bitcoinpos.lib.SquirrelTests.FSMEvent;
import org.blackbananacoin.bitcoinpos.lib.SquirrelTests.FSMState;
import org.blackbananacoin.bitcoinpos.lib.SquirrelTests.StateMachineSample;
import org.blackbananacoin.common.bitcoin.Bitcoins;
import org.blackbananacoin.common.json.BcApiSingleAddrTx;
import org.blackbananacoin.common.json.BcApiSingleAddrTxItem;
import org.blackbananacoin.common.json.BcApiSingleAddress;
import org.blackbananacoin.common.json.BcApiSingleAddressBuilder;
import org.blackbananacoin.common.json.TwdBit;
import org.squirrelframework.foundation.fsm.StateMachineBuilderFactory;
import org.squirrelframework.foundation.fsm.UntypedStateMachine;
import org.squirrelframework.foundation.fsm.UntypedStateMachineBuilder;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class BitcoinPosActivity extends Activity {
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

	private DownloadBkbcEx dlBkbcEx = new DownloadBkbcEx();
	private DownloadBcTx dlBcTx = new DownloadBcTx();
	private AddressBlockChainWatcher addrWatcher = new AddressBlockChainWatcher();
	private QrCodeEncoder qrcodeEncoder = new QrCodeEncoder();
	private BcApiSingleAddressBuilder builderBcTx = new BcApiSingleAddressBuilder();

	private SoundPool spool;
	private int soundID;
	private int debugRunCount = 0;

	private Runnable runForDownlaodInfo = new Runnable() {

		public void run() {
			try {
				inRunDownloadBkbcEx();
			} catch (Exception ex) {

			} finally {
				_handler.postDelayed(this, UI.TimeDownloadInterval);
			}
		}
	};

	private Runnable runForRefreshInfo = new Runnable() {

		int count = 0;

		public void run() {
			try {
				inRunRefresh();
			} catch (Exception ex) {

			} finally {
				_handler.postDelayed(this, UI.TimeRefreshInterval);
			}
		}
	};

	private UiState uiState;

	public void inRunDownloadBkbcEx() {
		TwdBit twdbit = dlBkbcEx.getExchange();
		checkNotNull(twdbit);
		uiState.setLastTwdBit(twdbit);
		updatePriceQrCode();
	}

	private void turnOnBkbcExQr() {
		if (!lyBkbcEx.isShown()) {
			lyBcApiTxCheck.setVisibility(View.GONE);
			lyBkbcEx.setVisibility(View.VISIBLE);
			lyMidBitcoinCat.setVisibility(View.VISIBLE);
		}

		if (runForBcTxCheck != null) {
			_handler.removeCallbacks(runForBcTxCheck);
			runForBcTxCheck = null;
		}
	}

	private void initBcTxCheckArea() {
		lyBcApiTxResult2.setVisibility(View.INVISIBLE);
		tvBcTxCheckAddr.setText("-");
		tvBcTxCheckAmount.setText("-");
		tvBcTxCheckAddr2.setText("-");
		tvBcTxCheckAmount2.setText("-");
		tvBcTxCheckTime.setText("-");
		tvBcTxCheckHr.setText("-");
		tvBcTxCheckHr2.setText("-");
		tvBcTxCheckWaitDesc.setText("-");
		tvBcTxCheckWaitDesc.setVisibility(View.VISIBLE);
		tvBcTxCheckTitle.setText("交易查詢開始..");
	}

	private void turnOnBcTxCheckArea() {
		if (!lyBcApiTxCheck.isShown()) {
			lyBcApiTxCheck.setVisibility(View.VISIBLE);
			lyBkbcEx.setVisibility(View.GONE);
			initBcTxCheckArea();
			lyMidBitcoinCat.setVisibility(View.GONE);
		}
	}

	/**
	 * https://github.com/y12studio/bkbc-bitcoinpos/issues/11
	 * 
	 * @param lastTx
	 * @return
	 */
	@Deprecated
	private boolean isLastTxShowUpByUnixTime(BcApiSingleAddrTx lastTx) {
		boolean r = false;
		// first check time
		long unixTimeSec = lastTx.getUnixTime();
		// only show less 600 secs
		long unixTimeNow = System.currentTimeMillis() / 1000L;
		// long dateTimeNow = new Date().getTime();
		long diffSec = unixTimeNow - unixTimeSec;
		UI.logv("[LastTx]=" + lastTx);
		UI.logv("LastTx time diff = " + diffSec + " now = " + unixTimeNow);
		if (diffSec > 0 && diffSec < uiState.getSecondsForTxCheck()) {
			r = true;
		}
		return r;
	}

	private boolean isLastTxShowUpByDiff(BcApiSingleAddrTx lastTx) {
		boolean r = false;
		BcTxCheckResult br = uiState.getLastBcTxCheckResult();
		if (br != null && br.getLastBcApiTx() != null) {
			BcApiSingleAddrTx oldTx = br.getLastBcApiTx();
			UI.logv("[CheckDiff] oldTx=" + oldTx);
			if (oldTx.getUnixTime() != lastTx.getUnixTime()) {
				r = true;
			}
		} else {
			r = true;
		}
		UI.log("[CheckDiff] result=" + r + "/ new tx=" + lastTx);
		return r;
	}

	private BcTxCheckResult handleLastTxResult(BcApiSingleAddrTx lastTx,
			BcApiSingleAddrTx last2Tx, int checkRunCount) {
		BcTxCheckResult result = new BcTxCheckResult();
		checkNotNull(lastTx);
		UI.logv(lastTx.toString());
		BcApiSingleAddrTxItem itemIn = lastTx.getFirstTxInputItem();
		BcApiSingleAddrTxItem itemOut = lastTx.getTxOutputItem(uiState
				.getBitcoinAddrShop());
		checkNotNull(itemIn);
		checkNotNull(itemOut);
		turnOnBcTxCheckArea();
		result.setFoundDiff(isLastTxShowUpByDiff(lastTx));
		result.setItemInput(itemIn);
		result.setItemOutput(itemOut);
		result.setLastBcApiTx(lastTx);

		if (last2Tx != null) {
			result.setLastBcApi2Tx(last2Tx);
			BcApiSingleAddrTxItem item2In = last2Tx.getFirstTxInputItem();
			BcApiSingleAddrTxItem item2Out = last2Tx.getTxOutputItem(uiState
					.getBitcoinAddrShop());
			result.setItem2Input(item2In);
			result.setItem2Output(item2Out);
		}

		result.setCheckCount(checkRunCount);
		this.uiState.setLastTxCheckTime(System.currentTimeMillis());
		this.uiState.setLastBcTxCheckResult(result);
		return result;
	}

	public void updateBcTxNextRequestTxt(int sec) {
		tvBcTxCheckWaitDesc.setText("下次查詢:" + sec + "秒");
	}

	@Deprecated
	private void updateNtxChange(BcApiSingleAddrTx lastTx) {
		// play sound
		playSound();
		// show tx list
		turnOnBcTxCheckArea();
		// fill tx content
		BcApiSingleAddrTxItem item = lastTx.getFirstTxInputItem();
		double btc = item.getValue() / Bitcoins.COIN;
		String btcStr = String.format("%.8f BTC", btc);
		String addr = item.getAddr();
		UI.logv("last input addr=" + addr + "/btc=" + btcStr);
		tvBcTxCheckAddr.setText(addr.substring(0, 5) + "**"
				+ addr.substring(addr.length() - 5));
		tvBcTxCheckAmount.setText(btcStr);
	}

	protected void inRunRefresh() {
		tvTime.setText(UI.TFMT.format(new Date()));
		if (uiState.getLastUpdateExTime() > 0) {
			long nextTime = uiState.getLastUpdateExTime()
					+ UI.TimeDownloadInterval;
			long diffs = (nextTime - System.currentTimeMillis()) / 1000;
			int min = (int) (diffs / 60);
			int sec = (int) (diffs % 60);
			tvUpdateStatus.setText(String.format("%d分%d秒後更新", min, sec));
		} else {
			tvUpdateStatus.setText("首次更新");
		}
		if (lyBcApiTxCheck.isShown()) {
			if (uiState.isAutoTurnOnBkbcExQrArea()) {
				UI.logv("[AutoTurnOnBkbcExQrArea]");
				turnOnBkbcExQr();
			}

			if (tvBcTxCheckWaitDesc.isShown()
					&& uiState.getLastTxCheckTime() > 0) {
				// update wait desc
				long nextUpdate = uiState.getLastTxCheckTime()
						+ uiState.getTimeBcTxVerifyMs();
				long diffs = (nextUpdate - System.currentTimeMillis()) / 1000;
				updateBcTxNextRequestTxt((int) diffs);
			}
		}
	}

	private void playSound() {
		AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		float volume = (float) audioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		android.util.Log.v(
				"SOUND",
				"[" + volume + "]["
						+ spool.play(soundID, volume, volume, 1, 0, 1f) + "]");
	}

	private void testWebsocket() {
		// runDownloadExchange();
		addrWatcher.createWebsocket();
	}

	private ImageView imgQrBcAddr;
	private ImageView imgQrWebSite;

	private TextView tvAmount;
	private TextView tvBcTxCheckAddr;
	private TextView tvHelpBar;
	private TextView tvShopBtcAddr;
	private TextView tvBcTxCheckHr;
	private TextView tvBcTxCheckHr2;
	private TextView tvBcTxCheckAddr2;
	private TextView tvBcTxCheckAmount;
	private TextView tvBcTxCheckAmount2;
	private TextView tvBcTxCheckTitle;
	private TextView tvBcTxCheckWaitDesc;
	private TextView tvBcTxCheckTime;
	private TextView tvUpdateStatus;
	private View lyBkbcEx;
	private View lyBcApiTxCheck;
	private View lyHomePriceQr;
	private View lyBcApiTxResult2;
	private View lyMidBitcoinCat;

	private TextView tvTime;

	private TextView tvShopName;
	private TextView tvProductName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullscreen);
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);

		uiState = new UiState();
		float density = getResources().getDisplayMetrics().density;
		UI.logv("Screen Density : " + density);

		final View controlsView = findViewById(R.id.fullscreen_content_controls);
		final View contentView = findViewById(R.id.fullscreen_content);
		tvShopName = (TextView) findViewById(R.id.tvShopName);
		tvProductName = (TextView) findViewById(R.id.tvProductName);

		lyBcApiTxCheck = findViewById(R.id.lyBcTxCheckInfo);
		lyHomePriceQr = findViewById(R.id.lyHomePriceQr);
		lyBcApiTxResult2 = findViewById(R.id.lyBcTxResult2);
		lyMidBitcoinCat = findViewById(R.id.lyMidBitcoinCat);
		lyBkbcEx = findViewById(R.id.lyBkbcExInfo);
		tvMbtcTwd = (TextView) findViewById(R.id.tvBtcTwdInfo);
		tvAmount = (TextView) findViewById(R.id.tvAmount);
		tvShopBtcAddr = (TextView) findViewById(R.id.tvBitAddr);
		tvUpdateStatus = (TextView) findViewById(R.id.tvUpdateStatus);
		tvBcTxCheckAmount = (TextView) findViewById(R.id.tvBcTxAmount);
		tvBcTxCheckAmount2 = (TextView) findViewById(R.id.tvBcTxAmount2);
		tvBcTxCheckAddr = (TextView) findViewById(R.id.tvBcTxAddr);
		tvHelpBar = (TextView) findViewById(R.id.tvHelpBar);
		tvBcTxCheckHr = (TextView) findViewById(R.id.tvBcTxHr);
		tvBcTxCheckHr2 = (TextView) findViewById(R.id.tvBcTxHr2);
		tvBcTxCheckAddr2 = (TextView) findViewById(R.id.tvBcTxAddr2);
		tvBcTxCheckTime = (TextView) findViewById(R.id.tvBcTxCheckTime);
		tvBcTxCheckWaitDesc = (TextView) findViewById(R.id.tvBcTxCheckWaitDesc);
		tvBcTxCheckTitle = (TextView) findViewById(R.id.tvBcTxTitle);
		tvTime = (TextView) findViewById(R.id.tvTime);

		imgQr1 = (ImageView) findViewById(R.id.imgQr1);
		imgQrBcAddr = (ImageView) findViewById(R.id.imgQrBcAddr);
		imgQrWebSite = (ImageView) findViewById(R.id.imgQrWebSite);

		initPrefsValue(prefs);
		initStateMachine();

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

		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		spool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		soundID = spool.load(this, R.raw.kirby_style_laser, 1);

		prefs.registerOnSharedPreferenceChangeListener(preferenceListener);

		updateQrCodeBlockchainAddr();
		updateQrCodeWebSite();
	}

	private void initPrefsValue(SharedPreferences prefs) {
		tvShopName.setText(prefs.getString(UI.PREF_KEY_SHOP, getResources()
				.getString(R.string.pref_shop_name)));
		tvProductName.setText(prefs.getString(UI.PREF_KEY_PRODUCT,
				getResources().getString(R.string.pref_product_name)));
		int price = Integer.valueOf(prefs.getString(UI.PREF_KEY_PRICE,
				getResources().getString(R.string.pref_price)));
		uiState.setPrice(price);
		uiState.setWebsite(prefs.getString(UI.PREF_KEY_WEBSITE, getResources()
				.getString(R.string.pref_website)));

		String bAddr = prefs.getString(UI.PREF_KEY_BTC_ADDR, getResources()
				.getString(R.string.pref_shop_btc_addr_motor1));
		uiState.setBitcoinAddrShop(bAddr);
		tvShopBtcAddr.setText(bAddr);
		UI.log("[InitPref]" + uiState.toString());
	}

	private SharedPreferences.OnSharedPreferenceChangeListener preferenceListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
		public void onSharedPreferenceChanged(SharedPreferences prefs,
				String key) {
			if (key.equals(UI.PREF_KEY_SHOP)) {
				String v = prefs.getString(key, null);
				tvShopName.setText(v);
			} else if (key.equals(UI.PREF_KEY_PRODUCT)) {
				String v = prefs.getString(key, null);
				tvProductName.setText(v);
			} else if (key.equals(UI.PREF_KEY_BTC_ADDR)) {
				String v = prefs.getString(key, null);
				tvShopBtcAddr.setText(v);
				uiState.setBitcoinAddrShop(v);
			} else if (key.equals(UI.PREF_KEY_WEBSITE)) {
				String v = prefs.getString(key, null);
				uiState.setWebsite(v);
				updateQrCodeWebSite();
			} else if (key.equals(UI.PREF_KEY_PRICE)) {
				int v = Integer.valueOf(prefs.getString(key, null));
				uiState.setPrice(v);
				updatePriceQrCode();
			} else {
				UI.logv("share preferences key=" + key);
			}
		}
	};

	private void updateQrCodeBlockchainAddr() {
		// String content = UI.BC_URL_ADDR_PREFIX + UI.BITCOIN_ADDR_MOTOR1;
		String content = UI.BC_URL_ADDR_PREFIX_zh_cn
				+ uiState.getBitcoinAddrShop();
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

		try {
			int[] pixels = qrcodeEncoder.getPixels(uiState.getWebsite(),
					dimention);
			Bitmap bitmap = Bitmap.createBitmap(width, height,
					Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
			imgQrWebSite.setImageBitmap(bitmap);
		} catch (WriterException e) {
			e.printStackTrace();
		}

	}

	private void updateQrCodeWebSite() {
		int dimention = 500;
		int width = 500;
		int height = 500;
		try {
			String url = uiState.getWebsite().startsWith("http") ? uiState
					.getWebsite() : "http://" + uiState.getWebsite();
			int[] pixels = qrcodeEncoder.getPixels(url, dimention);
			Bitmap bitmap = Bitmap.createBitmap(width, height,
					Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
			imgQrWebSite.setImageBitmap(bitmap);
		} catch (WriterException e) {
			e.printStackTrace();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// MenuInflater inflater = getMenuInflater();
		// inflater.inflate(R.menu.mainmenu, menu);
		menu.add(0, Menu.FIRST, 2, "Settings");
		return super.onCreateOptionsMenu(menu);
	}

	// This method is called once the menu is selected
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case Menu.FIRST:
			// Launch settings activity
			Intent i = new Intent(this, SettingsActivity.class);
			startActivity(i);
			break;
		// more code...
		}
		return true;
	}

	private double getBtcAmountBy24hrmeanAndStd(TwdBit twdbit, int twd) {
		double r = (twd / (twdbit.getMean24hr() - twdbit.getStd24hr()));
		return r;
	}

	private double getBtcAmountByPercentFee(TwdBit twdbit, int twd) {
		double r = (twd / twdbit.getBtctwd())
				* (1 + (UI.FEE_RATE_SRV_PERCENT / 100f));
		return r;
	}

	protected void updatePriceQrCode() {
		tvUpdateStatus.setText("匯率更新中..");
		TwdBit twdbit = uiState.getLastTwdBit();
		checkNotNull(twdbit);
		tvMbtcTwd.setText(UI.DFMT_INT.format(twdbit.getBtctwd()));
		double amount = getBtcAmountByPercentFee(twdbit, uiState.getPrice());
		// update qr code
		String amountFormat = String.format("%.8f", amount);
		tvAmount.setText(amountFormat + " BTC(內含3%手續費)");
		// update status
		this.uiState.setLastUpdateExTime(System.currentTimeMillis());
		inRunRefresh();
		updatePriceQrCode(amount);
	}

	private void updatePriceQrCode(double amount) {
		String content = Bitcoins
				.buildUri(uiState.getBitcoinAddrShop(), amount);
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

	private Runnable runForTest;

	private Runnable runForBcTxCheck;

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
		UI.logv("kcode : " + keyCode);
		switch (keyCode) {
		case KeyEvent.KEYCODE_NUMPAD_0:
			t("0");
			break;
		case KeyEvent.KEYCODE_NUMPAD_1:
			t("1");
			break;
		case KeyEvent.KEYCODE_NUMPAD_2:
			t("2");
			break;
		case KeyEvent.KEYCODE_NUMPAD_3:
			t("3");
			break;
		case KeyEvent.KEYCODE_NUMPAD_4:
			t("4");
			break;
		case KeyEvent.KEYCODE_NUMPAD_5:
			t("5");
			break;
		case KeyEvent.KEYCODE_NUMPAD_6:
			t("6");
			break;
		case KeyEvent.KEYCODE_NUMPAD_7:
			t("7");
			break;
		case KeyEvent.KEYCODE_NUMPAD_8:
			handleKeyPadNum8();
			break;
		case KeyEvent.KEYCODE_NUMPAD_9:
			handleKeyPadNum9();
			break;
		case KeyEvent.KEYCODE_NUMPAD_ENTER:
			handleKeyPadEnter();
			break;
		case KeyEvent.KEYCODE_NUMPAD_MULTIPLY:
			handleKeyPadMultiply();
			break;
		default:
			UI.logv("UnHandle kcode : " + keyCode);
			break;
		}
		return true;
	}

	private void handleKeyPadMultiply() {
		// where state ?
		FsmState cs = uiState.getStateMachine().getCurrentState();
		UI.logv("Key* Current State=" + cs.toString());
		switch (cs) {
		case Home:
			lyHomePriceQr.setVisibility(View.GONE);
			uiState.getStateMachine().fire(FsmEvent.OpenTwdAdjust);
			break;
		default:
			break;
		}

		handleHelpBar();

	}

	private void handleHelpBar() {
		// where state ?
		FsmState cs = uiState.getStateMachine().getCurrentState();
		switch (cs) {
		case Home:
			tvHelpBar.setText("* 輸入價格");
			break;
		case TwdAdjust:
			tvHelpBar.setText("Enter 完成輸入");
			break;
		default:
			break;
		}

	}

	private void handleKeyPadEnter() {
		// startTxCheck();

		FsmState cs = uiState.getStateMachine().getCurrentState();
		switch (cs) {
		case TwdAdjust:
			lyHomePriceQr.setVisibility(View.VISIBLE);
			uiState.getStateMachine().fire(FsmEvent.BackHome);
			break;
		default:
			break;
		}

		handleHelpBar();
	}

	private void handleKeyPadNum8() {
		testFun(206);
	}

	private void testFun(int testId) {
		switch (testId) {
		case 201:
			testTxCheckRunNoDiffToEnd();
			break;
		case 202:
			testBcApiDownload();
			break;
		case 203:
			testTxCheckRunReturnNull();
			break;
		case 204:
			testAutoTurnOnBkbcQrArea();
			break;
		case 205:
			testTxCheckRunOkEnd();
			break;
		case 206:
			testTxCheckRunDownloadError();
			break;
		case 207:
			test207TurnOnPriceInput();
			break;
		case 301:
			testRunStop();
			break;
		case 901:
			test901NetflixGraph();
			break;
		case 902:
			test902HelloSquirrel();
			break;

		default:
			break;
		}
	}

	private void test207TurnOnPriceInput() {
		// stateMachine.fire(FsmEvent.TurnOnEditPrice);
		StateMachineUi sm = uiState.getStateMachine();
		UI.logv("FSM state=" + sm.getCurrentState());
	}

	private void testBcApiDownload() {
		BcApiSingleAddress x = dlBcTx.getSingleAddrResult(uiState
				.getBitcoinAddrShop());
		UI.logv("[TEST] " + x.getN_tx());
	}

	private void runBcTxCheck(final IRemoteBcTxCheckSrv rms) {
		checkState(runForBcTxCheck == null);
		turnOnBcTxCheckArea();
		runForBcTxCheck = new Runnable() {

			int count;

			@Override
			public void run() {
				BcTxCheckResult result = rms.checkLastTxResult(count);
				checkNotNull(result);
				if (count < uiState.getTimeBcTxVerifyMaxCount()) {
					_handler.postDelayed(this, uiState.getTimeBcTxVerifyMs());
					rms.onBcApiTxCheck(result);
				} else {
					// TxCheck fail
					rms.onBcApiTxCheckEnd(result);
				}
				count++;
			}
		};
		_handler.post(runForBcTxCheck);
	}

	private class RemoteBcTxCheckSrv implements IRemoteBcTxCheckSrv {

		private int count;

		@Override
		public BcTxCheckResult checkLastTxResult(int acount) {
			this.count = acount;
			BcTxCheckResult r = null;
			try {
				BcApiSingleAddress addrResult = dlBcTx
						.getSingleAddrResult(uiState.getBitcoinAddrShop());
				if (addrResult != null) {
					BcApiSingleAddrTx lastTx = builderBcTx
							.parseLastTx(addrResult);
					BcApiSingleAddrTx last2Tx = builderBcTx.parseTx(addrResult,
							1);
					r = handleLastTxResult(lastTx, last2Tx, count);
					r.setDownloadError(false);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				UI.loge(ex.getMessage());
				r.setDownloadError(true);
			}

			if (r == null) {
				r = new BcTxCheckResult();
				r.setCheckCount(count);
				r.setFoundDiff(false);
				r.setDownloadError(true);
				UI.loge("[AfterCheck] result is null. create a new one.");
			}
			r.setCheckCount(count);

			UI.logv("[AfterCheck UiState] " + uiState.toString());
			UI.logv("[AfterCheck Result] " + r.toString());

			return r;
		}

		@Override
		public void onBcApiTxCheckEnd(BcTxCheckResult result) {
			tvBcTxCheckTitle.setText("已完成查詢");
			// make countdown stop.
			tvBcTxCheckWaitDesc.setVisibility(View.GONE);
			tvBcTxCheckTime.setText("總確認查詢 " + result.getCheckCount() + " 次");
		}

		@Override
		public void onBcApiTxCheck(BcTxCheckResult result) {
			if (!result.isDownloadError()) {
				if (result.isFoundDiff()) {
					playSound();
				}
				updateBcTxResult(result);
			} else {
				updateBcTxDownloadErrorResult(result);
			}
		}

	}

	private void testTxCheckRunNoDiffToEnd() {
		// fast test
		testUiStateMockSetup();
		runBcTxCheck(new RemoteBcTxCheckSrv() {

			@Override
			public BcTxCheckResult checkLastTxResult(int count) {
				BcTxCheckResult r = testMockUiLastTxFound(false, count);
				UI.logv("[TEST] " + uiState.toString());
				UI.logv("[TEST] " + r.toString());
				return r;
			}
		});
	}

	public void updateBcTxDownloadErrorResult(BcTxCheckResult result) {
		tvBcTxCheckAddr.setText("-");
		tvBcTxCheckAmount.setText("-");
		tvBcTxCheckAddr2.setText("-");
		tvBcTxCheckHr.setText("-");
		tvBcTxCheckHr2.setText("-");
		tvBcTxCheckAmount2.setText("-");
		lyBcApiTxResult2.setVisibility(View.INVISIBLE);
		tvBcTxCheckTitle.setText("交易查詢中");
		tvBcTxCheckTime.setText("已查詢 " + result.getCheckCount() + "/"
				+ uiState.getTimeBcTxVerifyMaxCount() + "次");
	}

	private void testUiStateMockSetup() {
		uiState.setTimeBcTxVerifyMs(20 * 1000);
		uiState.setTimeBcTxVerifyMaxCount(3);
		uiState.setTimeAutoTurnBkbcExQrArea(20 * 1000);
		try {
			BcApiSingleAddress addrResult;
			addrResult = builderBcTx.buildDemo();
			// BcApiSingleAddrTx lastTx = builderBcTx.parseLastTx(addrResult);
			uiState.setBitcoinAddrShop(addrResult.getAddress());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void testTxCheckRunReturnNull() {
		// fast test
		testUiStateMockSetup();
		runBcTxCheck(new RemoteBcTxCheckSrv() {

			@Override
			public BcTxCheckResult checkLastTxResult(int count) {
				BcTxCheckResult r = testMockUiLastTxFound(false, count);
				if (count == 1) {
					r = null;
				}
				return r;
			}
		});
	}

	private void testTxCheckRunOkEnd() {
		// fast test
		testUiStateMockSetup();
		runBcTxCheck(new RemoteBcTxCheckSrv() {

			@Override
			public BcTxCheckResult checkLastTxResult(int count) {
				BcTxCheckResult r = null;
				if (count == 2) {
					r = testMockUiLastTxFound(true, count);
				} else {
					r = testMockUiLastTxFound(false, count);
				}
				UI.logv("[TEST] " + uiState.toString());
				UI.logv("[TEST] " + r.toString());
				return r;
			}
		});
	}

	private void startTxCheck() {
		if (!lyBcApiTxCheck.isShown()) {
			runBcTxCheck(new RemoteBcTxCheckSrv());
		}
	}

	private void testAutoTurnOnBkbcQrArea() {
		uiState.setLastTxCheckTime(System.currentTimeMillis()
				- UI.TimeAutoTurnOnBkbcExQrArea - 1000);
		UI.logv("[TEST] autoTurnOnBkbcQrArea = "
				+ uiState.isAutoTurnOnBkbcExQrArea());
	}

	private BcTxCheckResult testMockUiLastTxFound(boolean isFound,
			int runCheckCount) {
		BcTxCheckResult result = null;
		try {
			BcApiSingleAddress addrResult = builderBcTx.buildDemo();
			BcApiSingleAddrTx lastTx = builderBcTx.parseLastTx(addrResult);
			BcApiSingleAddrTx last2Tx = builderBcTx.parseTx(addrResult, 1);
			result = handleLastTxResult(lastTx, last2Tx, runCheckCount);
			result.setFoundDiff(isFound);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	private void testRunStop() {
		if (runForTest != null) {
			_handler.removeCallbacks(runForTest);
			runForTest = null;
		}
	}

	private void handleKeyPadNum9() {
		testFun(207);
	}

	private String getPrintBtc(BcApiSingleAddrTxItem item) {
		double btc = item.getValue() * 1.0d / Bitcoins.COIN;
		String btcStr = String.format("%.8f BTC", btc);
		return btcStr;
	}

	private String getPrintAddr(BcApiSingleAddrTxItem item) {
		String addr = item.getAddr();
		return addr.substring(0, 6) + "***" + addr.substring(addr.length() - 6);
	}

	private void updateBcTxResult(BcTxCheckResult result) {
		tvBcTxCheckTitle.setText("最新交易");
		if (result.getLastBcApiTx() != null) {
			BcApiSingleAddrTxItem itemIn = result.getItemInput();
			BcApiSingleAddrTxItem itemOut = result.getItemOutput();
			checkNotNull(itemOut);
			tvBcTxCheckAddr.setText(getPrintAddr(itemIn));
			tvBcTxCheckHr.setText(getPrintHR(result.getLastBcApiTx()));
			tvBcTxCheckAmount.setText(getPrintBtc(itemOut));
		} else {
			tvBcTxCheckAddr.setText("-");
			tvBcTxCheckAmount.setText("-");
			tvBcTxCheckHr.setText("-");
		}
		if (result.getLastBcApi2Tx() != null) {
			lyBcApiTxResult2.setVisibility(View.VISIBLE);
			BcApiSingleAddrTxItem item2Out = result.getItem2Output();
			BcApiSingleAddrTxItem item2in = result.getItem2Input();
			tvBcTxCheckAmount2.setText(getPrintBtc(item2Out));
			tvBcTxCheckAddr2.setText(getPrintAddr(item2in));
			tvBcTxCheckHr2.setText(getPrintHR(result.getLastBcApi2Tx()));
		} else {
			lyBcApiTxResult2.setVisibility(View.INVISIBLE);
			tvBcTxCheckAmount2.setText("-");
			tvBcTxCheckAddr2.setText("-");
			tvBcTxCheckHr2.setText("-");
		}

		tvBcTxCheckTime.setText("已查詢 " + result.getCheckCount() + "/"
				+ uiState.getTimeBcTxVerifyMaxCount() + "次");
	}

	private String getPrintHR(BcApiSingleAddrTx tx) {
		String r = "";
		long unixtime = tx.getUnixTime();
		long unixTimeNow = System.currentTimeMillis() / 1000L;
		long diff = unixTimeNow - unixtime;
		if (diff <= UI.ONEHOURSEC) {
			// diff <0 is possible
			r = "1小時內";
		} else {
			long hr = diff / UI.ONEHOURSEC;

			r = hr + "小時前";
		}
		return r;
	}

	private void testTxCheckRunDownloadError() {
		// fast test
		testUiStateMockSetup();
		runBcTxCheck(new RemoteBcTxCheckSrv() {

			@Override
			public BcTxCheckResult checkLastTxResult(int count) {
				BcTxCheckResult r = testMockUiLastTxFound(false, count);
				if (count == 1) {
					r.setDownloadError(true);
				}
				return r;
			}
		});
	}

	private void test902HelloSquirrel() {

		// 3. Build State Transitions
		UntypedStateMachineBuilder builder = StateMachineBuilderFactory
				.create(StateMachineSample.class);
		builder.externalTransition().from(FSMState.A).to(FSMState.B)
				.on(FSMEvent.ToB).callMethod("fromAToB");

		builder.externalTransition().from(FSMState.B).to(FSMState.C)
				.on(FSMEvent.ToC).callMethod("fromBToC");

		builder.onEntry(FSMState.B).callMethod("ontoB");

		// 4. Use State Machine
		UntypedStateMachine fsm = builder.newStateMachine(FSMState.A);
		fsm.fire(FSMEvent.ToB, 10);

		UI.logv("Current state is " + fsm.getCurrentState());
		fsm.fire(FSMEvent.ToC, 4);

		UI.logv("Current state is " + fsm.getCurrentState());

	}

	private void test901NetflixGraph() {
		// NFGraphSpec pageSchema = new NFGraphSpec(
		// new NFNodeSpec("Page",
		// new NFPropertySpec("link", "Page", MULTIPLE | COMPACT)));
		//
		// OrdinalMap<String> pageOrdinals = new OrdinalMap<String>();
		//
		// NFBuildGraph buildGraph = new NFBuildGraph(pageSchema);
		//
		// int homeOrdinal = pageOrdinals.add("HOME");
		// int aboutOrdinal = pageOrdinals.add("ABOUT");
		// int faqOrdinal = pageOrdinals.add("FAQ");
		//
		// buildGraph.addConnection("Page", homeOrdinal, "link",aboutOrdinal);
		// buildGraph.addConnection("Page", homeOrdinal, "link",faqOrdinal);
		//
		// NFCompressedGraph compressedGraph = buildGraph.compress();
		//
		// homeOrdinal = pageOrdinals.get("HOME");
		// OrdinalIterator iter = compressedGraph.getConnectionIterator("Page",
		// homeOrdinal, "link");
		//
		// int currentOrdinal = iter.nextOrdinal();
		//
		// while(currentOrdinal != OrdinalIterator.NO_MORE_ORDINALS) {
		// UI.logd(pageOrdinals.get(currentOrdinal) + " link in The Home page");
		// currentOrdinal = iter.nextOrdinal();
		// }

	}

	private void initStateMachine() {
		StateMachineUi sm = FSMBitcoinPos.createStateMachine();
		uiState.setStateMachine(sm);
		handleHelpBar();
	}
}
