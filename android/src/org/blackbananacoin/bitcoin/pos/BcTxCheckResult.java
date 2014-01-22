package org.blackbananacoin.bitcoin.pos;

import org.blackbananacoin.common.json.BcApiSingleAddrTx;
import org.blackbananacoin.common.json.BcApiSingleAddrTxItem;

public class BcTxCheckResult {
	
	private boolean foundDiff;
	private int checkCount;
	
	private boolean downloadError;
	
	private BcApiSingleAddrTx lastBcApiTx;
	private BcApiSingleAddrTx lastBcApi2Tx;

	private BcApiSingleAddrTxItem itemInput;
	private BcApiSingleAddrTxItem item2Input;
	private BcApiSingleAddrTxItem itemOutput;
	private BcApiSingleAddrTxItem item2Output;
	
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


	public BcApiSingleAddrTx getLastBcApiTx() {
		return lastBcApiTx;
	}

	public void setLastBcApiTx(BcApiSingleAddrTx lastBcApiTx) {
		this.lastBcApiTx = lastBcApiTx;
	}

	public boolean isFoundDiff() {
		return foundDiff;
	}

	public void setFoundDiff(boolean foundDiff) {
		this.foundDiff = foundDiff;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BcTxCheckResult [foundDiff=" + foundDiff + ", checkCount="
				+ checkCount + ", downloadError=" + downloadError
				+ ", lastBcApiTx=" + lastBcApiTx + ", lastBcApi2Tx="
				+ lastBcApi2Tx + ", itemInput=" + itemInput + ", item2Input="
				+ item2Input + ", itemOutput=" + itemOutput + ", item2Output="
				+ item2Output + "]";
	}

	public boolean isDownloadError() {
		return downloadError;
	}

	public void setDownloadError(boolean downloadError) {
		this.downloadError = downloadError;
	}

	public BcApiSingleAddrTx getLastBcApi2Tx() {
		return lastBcApi2Tx;
	}

	public void setLastBcApi2Tx(BcApiSingleAddrTx lastBcApi2Tx) {
		this.lastBcApi2Tx = lastBcApi2Tx;
	}

	public BcApiSingleAddrTxItem getItem2Input() {
		return item2Input;
	}

	public void setItem2Input(BcApiSingleAddrTxItem item2Input) {
		this.item2Input = item2Input;
	}

	public BcApiSingleAddrTxItem getItemOutput() {
		return itemOutput;
	}

	public void setItemOutput(BcApiSingleAddrTxItem itemOutput) {
		this.itemOutput = itemOutput;
	}

	public BcApiSingleAddrTxItem getItem2Output() {
		return item2Output;
	}

	public void setItem2Output(BcApiSingleAddrTxItem item2Output) {
		this.item2Output = item2Output;
	}

	
}
