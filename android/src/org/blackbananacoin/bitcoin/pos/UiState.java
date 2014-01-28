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

import org.blackbananacoin.bitcoinpos.lib.FSMBitcoinPos.StateMachineUi;
import org.blackbananacoin.common.json.TwdBit;

public class UiState {

	private long lastTxCheckTime;
	private long lastUpdateExTime;
	private String bitcoinAddrShop;
	private String website;
	private BcTxCheckResult lastBcTxCheckResult;
	private long timeBcTxVerifyMs = UI.TimeBcTxVerifySec * 1000;
	private long timeAutoTurnBkbcExQrArea = UI.TimeAutoTurnOnBkbcExQrArea;
	private int timeBcTxVerifyMaxCount = UI.TimeBcTxMaxCount;
	private int price;
	private TwdBit lastTwdBit = new TwdBit();
	
	private StateMachineUi stateMachine;

	private int secondsForTxCheck = UI.TimeForTxCheck;

	public boolean isAutoTurnOnBkbcExQrArea() {
		boolean r = false;
		long diff = System.currentTimeMillis() - lastTxCheckTime;
		if (lastTxCheckTime > 0 && diff > getTimeAutoTurnBkbcExQrArea()) {
			r = true;
		}
		return r;
	}

	public long getLastTxCheckTime() {
		return lastTxCheckTime;
	}

	public void setLastTxCheckTime(long lastTxCheckTime) {
		this.lastTxCheckTime = lastTxCheckTime;
	}

	public long getLastUpdateExTime() {
		return lastUpdateExTime;
	}

	public void setLastUpdateExTime(long lastUpdateExTime) {
		this.lastUpdateExTime = lastUpdateExTime;
	}

	public int getTimeBcTxVerifyMaxCount() {
		return timeBcTxVerifyMaxCount;
	}

	public void setTimeBcTxVerifyMaxCount(int timeBcTxVerifyMaxCount) {
		this.timeBcTxVerifyMaxCount = timeBcTxVerifyMaxCount;
	}

	public int getSecondsForTxCheck() {
		return secondsForTxCheck;
	}

	public void setSecondsForTxCheck(int secondsForTxCheck) {
		this.secondsForTxCheck = secondsForTxCheck;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public TwdBit getLastTwdBit() {
		return lastTwdBit;
	}

	public void setLastTwdBit(TwdBit lastTwdBit) {
		this.lastTwdBit = lastTwdBit;
	}

	public BcTxCheckResult getLastBcTxCheckResult() {
		return lastBcTxCheckResult;
	}

	public void setLastBcTxCheckResult(BcTxCheckResult lastBcTxCheckResult) {
		this.lastBcTxCheckResult = lastBcTxCheckResult;
	}

	public String getBitcoinAddrShop() {
		return bitcoinAddrShop;
	}

	public void setBitcoinAddrShop(String bitcoinAddrShop) {
		this.bitcoinAddrShop = bitcoinAddrShop;
	}

	public long getTimeAutoTurnBkbcExQrArea() {
		return timeAutoTurnBkbcExQrArea;
	}

	public void setTimeAutoTurnBkbcExQrArea(long timeAutoTurnBkbcExQrArea) {
		this.timeAutoTurnBkbcExQrArea = timeAutoTurnBkbcExQrArea;
	}

	public long getTimeBcTxVerifyMs() {
		return timeBcTxVerifyMs;
	}

	public void setTimeBcTxVerifyMs(long timeBcTxVerifyMs) {
		this.timeBcTxVerifyMs = timeBcTxVerifyMs;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "UiState [lastTxCheckTime=" + lastTxCheckTime
				+ ", lastUpdateExTime=" + lastUpdateExTime
				+ ", bitcoinAddrShop=" + bitcoinAddrShop
				+ ", lastBcTxCheckResult=" + lastBcTxCheckResult
				+ ", timeBcTxVerifyMs=" + timeBcTxVerifyMs
				+ ", timeAutoTurnBkbcExQrArea=" + timeAutoTurnBkbcExQrArea
				+ ", timeBcTxVerifyMaxCount=" + timeBcTxVerifyMaxCount
				+ ", price=" + price + ", lastTwdBit=" + lastTwdBit
				+ ", secondsForTxCheck=" + secondsForTxCheck + "]";
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public StateMachineUi getStateMachine() {
		return stateMachine;
	}

	public void setStateMachine(StateMachineUi stateMachine) {
		this.stateMachine = stateMachine;
	}

}
