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
package org.blackbananacoin.bitcoin;

import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;

public class TwdBit {

	private double usdtwd = 1.0d;
	private double btcusd = 1.0d;
	private double txfeetwd = 1.0d;
	private double btctwd = 1.0d;

	private Date time;
	private long timems;

	private List<Integer> data24hr = Lists.newArrayList();

	private double mean24hr;

	private double max24hr;

	private double min24hr;

	private double std24hr;

	private double median24hr;

	public double getUsdtwd() {
		return usdtwd;
	}

	public void setUsdtwd(double usdtwd) {
		this.usdtwd = usdtwd;
	}

	public double getBtcusd() {
		return btcusd;
	}

	public void setBtcusd(double btcusd) {
		this.btcusd = btcusd;
	}

	public double getTxfeetwd() {
		return txfeetwd;
	}

	public void setTxfeetwd(double txfeetwd) {
		this.txfeetwd = txfeetwd;
	}

	public double getBtctwd() {
		return btctwd;
	}

	public void setBtctwd(double btctwd) {
		this.btctwd = btctwd;
	}

	public List<Integer> getData24hr() {
		return data24hr;
	}

	public void setData24hr(List<Integer> data24hr) {
		this.data24hr = data24hr;
	}

	public double getMean24hr() {
		return mean24hr;
	}

	public void setMean24hr(double mean24hr) {
		this.mean24hr = mean24hr;
	}

	public double getMax24hr() {
		return max24hr;
	}

	public void setMax24hr(double max24hr) {
		this.max24hr = max24hr;
	}

	public double getMin24hr() {
		return min24hr;
	}

	public void setMin24hr(double min24hr) {
		this.min24hr = min24hr;
	}

	public double getStd24hr() {
		return std24hr;
	}

	public void setStd24hr(double std24hr) {
		this.std24hr = std24hr;
	}

	public double getMedian24hr() {
		return median24hr;
	}

	public void setMedian24hr(double median24hr) {
		this.median24hr = median24hr;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TwdBit [usdtwd=" + usdtwd + ", btcusd=" + btcusd
				+ ", txfeetwd=" + txfeetwd + ", btctwd=" + btctwd + ", time="
				+ time + ", timems=" + timems + ", data24hr=" + data24hr
				+ ", mean24hr=" + mean24hr + ", max24hr=" + max24hr
				+ ", min24hr=" + min24hr + ", std24hr=" + std24hr
				+ ", median24hr=" + median24hr + "]";
	}

}
