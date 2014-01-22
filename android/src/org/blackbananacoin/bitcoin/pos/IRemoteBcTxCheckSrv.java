package org.blackbananacoin.bitcoin.pos;


public interface IRemoteBcTxCheckSrv {
	
	public BcTxCheckResult checkLastTxResult(int count);
	
	public void onBcApiTxCheckEnd(BcTxCheckResult result);

	public void onBcApiTxCheck(BcTxCheckResult result);
	
}
