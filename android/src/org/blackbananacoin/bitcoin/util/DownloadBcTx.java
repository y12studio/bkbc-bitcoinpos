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
package org.blackbananacoin.bitcoin.util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.blackbananacoin.common.json.BcApiSingleAddress;
import org.blackbananacoin.common.json.BcApiSingleAddressBuilder;
import org.blackbananacoin.common.json.TwdBit;
import org.blackbananacoin.common.json.TwdJsonBuilder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpResponse;

public class DownloadBcTx {

	private BcApiSingleAddressBuilder builder = new BcApiSingleAddressBuilder();

	public BcApiSingleAddress getSingleAddrResult() {
		String url = builder.getUrl(UI.BITCOIN_ADDR_MOTOR1);
		UI.logv("Start download tx json from url" + url);
		Future<String> futureGet = AsyncHttpClient.getDefaultInstance().getString(url,
				new AsyncHttpClient.StringCallback() {
					public void onCompleted(Exception e,
							AsyncHttpResponse response, String result) {
						if (e != null) {
							e.printStackTrace();
							return;
						}
						UI.logv("json=" + result);
					}
				});
		BcApiSingleAddress target = null;
		try {
			String json = futureGet.get(10, TimeUnit.SECONDS);
			target = builder.toModel(json);
			// UI.logv("parse=" + target.toString());
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			e1.printStackTrace();
		} catch (TimeoutException e1) {
			e1.printStackTrace();
		}
		return target;
	}

}
