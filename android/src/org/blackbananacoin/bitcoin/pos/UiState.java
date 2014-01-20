package org.blackbananacoin.bitcoin.pos;

public class UiState {

	private long lastTxCheckFoundTime;
	private long lastTxCheckTime;
	private long lastUpdateExTime;
	private int timeBcTxVerifyMs = UI.TimeBcTxVerifySec*1000;
	private int timeBcTxVerifyMaxCount = UI.TimeBcTxMaxCount;
	
	private int secondsForTxCheck = UI.TimeForTxCheck;

	public boolean isAutoTurnOnBkbcExQrArea() {
		boolean r = false;
		if (lastTxCheckTime > 0
				&& (System.currentTimeMillis() - lastTxCheckTime > UI.TimeAutoTurnOnBkbcExQrArea)) {
			r = true;
		}
		return r;
	}

	public long getLastTxCheckFoundTime() {
		return lastTxCheckFoundTime;
	}

	public void setLastTxCheckFoundTime(long lastTxCheckFoundTime) {
		this.lastTxCheckFoundTime = lastTxCheckFoundTime;
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

	public int getTimeBcTxVerifyMs() {
		return timeBcTxVerifyMs;
	}

	public void setTimeBcTxVerifyMs(int timeBcTxVerifyMs) {
		this.timeBcTxVerifyMs = timeBcTxVerifyMs;
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

}
