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

import java.util.List;

import org.blackbananacoin.common.json.BcWsAddrSub;
import org.blackbananacoin.common.json.BcWsAddrSubBuilder;
import org.blackbananacoin.common.json.BcWsAddrSubTxItem;

import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpClient.WebSocketConnectCallback;
import com.koushikdutta.async.http.WebSocket;
import com.koushikdutta.async.http.WebSocket.StringCallback;

/**
 * @author
 *
 */
public class AddressBlockChainWatcher {
	
	public static final String TESTONLY_ADDR = "1bonesBjs3DQUbx4wxPQwrbwCkNjWtLB4";
	public static final String WS_URL = "ws://ws.blockchain.info/inv";
	
	public void createWebsocket() {
		UI.logv("[WEBSOCKET CONNECT TO " + WS_URL + "]");
		Future<WebSocket> x = AsyncHttpClient.getDefaultInstance().websocket(WS_URL, null,
				new WebSocketConnectCallback() {
					@Override
					public void onCompleted(Exception ex, WebSocket webSocket) {
						if (ex != null) {
							ex.printStackTrace();
							return;
						}
						String op = buildOp(TESTONLY_ADDR);
						UI.logv("[WEBSOCKET CONNECT] and send " + op);
						webSocket.send(op);
						webSocket.setStringCallback(new StringCallback() {
							@Override
							public void onStringAvailable(String json) {
								UI.logv("[WEBSOCKET READ]" + json);
								BcWsAddrSubBuilder b = new BcWsAddrSubBuilder();
								BcWsAddrSub model = b.toModel(json);
								List<BcWsAddrSubTxItem> outs = model
										.getOutTxs();
								for (BcWsAddrSubTxItem bcWsAddrSubTxItem : outs) {
									UI.logv("[READ TX]" + bcWsAddrSubTxItem);
								}
							}
						});
					}
				});
	}

	private String buildOp(String bitaddr) {
		checkNotNull(bitaddr);
		return "{\"op\":\"addr_sub\", \"addr\":\"" + bitaddr + "\"}";
	}

}
