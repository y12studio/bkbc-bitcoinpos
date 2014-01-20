package org.blackbananacoin.bitcoin.pos;

import org.blackbananacoin.common.json.BcApiSingleAddrTxItem;

public class BcTxCheckResult {
	
	private boolean found;
	private int checkCount;

	private BcApiSingleAddrTxItem itemInput;
	private BcApiSingleAddrTxItem itemOut;
	
	public boolean isFound() {
		return found;
	}

	public void setFound(boolean found) {
		this.found = found;
	}

	public BcApiSingleAddrTxItem getItemInput() {
		return itemInput;
	}

	public void setItemInput(BcApiSingleAddrTxItem itemInput) {
		this.itemInput = itemInput;
	}

	public int getCheckCount() {
		return checkCount;
	}

	public void setCheckCount(int checkCount) {
		this.checkCount = checkCount;
	}

	public BcApiSingleAddrTxItem getItemOut() {
		return itemOut;
	}

	public void setItemOut(BcApiSingleAddrTxItem itemOut) {
		this.itemOut = itemOut;
	}

}
