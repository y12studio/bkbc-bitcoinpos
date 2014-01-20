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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.blackbananacoin.common.json.TwdBit;
import org.blackbananacoin.common.json.TwdJsonBuilder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpResponse;

public class DownloadBkbcEx {

	public static final String EXURL = "http://blackbananacoin.org/json/twdbtc.json";
	
	private TwdJsonBuilder builder = new TwdJsonBuilder();

	public TwdBit getExchange() {
		UI.logv("Start download json from url" + EXURL);
		Future<String> xx = AsyncHttpClient.getDefaultInstance().getString(
				EXURL, new AsyncHttpClient.StringCallback() {
					// Callback is invoked with any exceptions/errors, and the
					// result, if available.
					public void onCompleted(Exception e,
							AsyncHttpResponse response, String result) {
						if (e != null) {
							e.printStackTrace();
							return;
						}
						UI.logv("json=" + result);
					}

				});
		
		TwdBit tb = null;
		try {
			String json = xx.get(5, TimeUnit.SECONDS);
			tb = builder.toTwdBit(json);
			UI.logv("parse=" + tb.toString());
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			e1.printStackTrace();
		} catch (TimeoutException e1) {
			e1.printStackTrace();
		}
		return tb;
	}

}
