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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import android.util.Log;

public class UI {

	private static final String ACTIVITY_TAG = "BKBC_BitCoinPOS";
	public static final String BC_URL_ADDR_PREFIX = "https://blockchain.info/address/";
	public static final String BC_URL_ADDR_PREFIX_zh_cn = "https://blockchain.info/zh-cn/address/";
	public static final float FEE_RATE_SRV_PERCENT = 3.0f;
	public static final String PREF_KEY_PRICE = "Price";
	public static final String PREF_KEY_WEBSITE = "WebSite";
	public static final String PREF_KEY_PRODUCT = "ProductName";
	public static final String PREF_KEY_SHOP = "ShopName";
	public static final String PREF_KEY_BTC_ADDR = "BitconAddr";

	public static final long TimeDownloadInterval = 5 * 60 * 1000;
	public static final long TimeRefreshInterval = 1000;
	public static final long TimeAutoTurnOnBkbcExQrArea = 5 * 60 * 1000;
	public static final int TimeBcTxMaxCount = 3;
	public static final int TimeBcTxVerifySec = 60;
	public static final long TimeBcTxVerifyMs = TimeBcTxVerifySec * 1000;

	public static final NumberFormat DFMT_INT = new DecimalFormat("#,###");
	public static final NumberFormat DFMT_2D = new DecimalFormat("#,###.##");

	public static final SimpleDateFormat TFMT = new SimpleDateFormat(
			"yyyy/MM/dd HH:mm:ss");
	public static final int TimeForTxCheck = 600;// 600 secs = 10mins

	public static final long ONEHOURSEC = 60 * 60;

	public static void logd(String log) {
		Log.d(ACTIVITY_TAG, log);
	}

	public static void logv(String log) {
		Log.v(ACTIVITY_TAG, log);
	}

	public static void loge(String log) {
		Log.e(ACTIVITY_TAG, log);
	}

	public static void log(String log) {
		Log.i(ACTIVITY_TAG, log);
	}

	
}
