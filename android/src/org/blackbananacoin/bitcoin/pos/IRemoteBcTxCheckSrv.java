package org.blackbananacoin.bitcoin.pos;


public interface IRemoteBcTxCheckSrv {
	
	public BcTxCheckResult checkLastTxResult();
	
	public void onBcApiTxCheckEndNotFound(BcTxCheckResult result);

	public void onBcApiTxCheckFound(BcTxCheckResult result);

	public void onBcApiTxCheckMidNotFound(BcTxCheckResult result);
	
}
