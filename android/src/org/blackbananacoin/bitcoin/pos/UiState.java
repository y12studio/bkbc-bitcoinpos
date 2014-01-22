package org.blackbananacoin.bitcoin.pos;

import org.blackbananacoin.common.json.TwdBit;

public class UiState {

	private long lastTxCheckTime;
	private long lastUpdateExTime;
	private String bitcoinAddrShop = UI.BITCOIN_ADDR_MOTOR1;
	private BcTxCheckResult lastBcTxCheckResult;
	private long timeBcTxVerifyMs = UI.TimeBcTxVerifySec * 1000;
	private long timeAutoTurnBkbcExQrArea = UI.TimeAutoTurnOnBkbcExQrArea;
	private int timeBcTxVerifyMaxCount = UI.TimeBcTxMaxCount;
	private int price = UI.TWD_DEFAULT_PRICE;
	private TwdBit lastTwdBit = new TwdBit();

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

}
