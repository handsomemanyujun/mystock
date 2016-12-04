package com.yujun.domain;

import com.yujun.util.Money;

/**
 * 资金池
 * @author yujun
 *
 */
public class FundPoolDO {
	/**
	 * 总资产
	 */
	private Money totalValue;
	
	/**
	 * 股票市值
	 */
	private Money stockValue;
	
	/**
	 * 冻结资金
	 */
	private Money freezeFunds;
	
	/**
	 * 可用资金
	 */
	private Money availableFunds;
	
	/**
	 * 可取资金
	 */
	private Money featchFunds;

	public Money getTotalValue() {
		return totalValue;
	}

	public void setTotalValue(Money totalValue) {
		this.totalValue = totalValue;
	}

	public Money getStockValue() {
		return stockValue;
	}

	public void setStockValue(Money stockValue) {
		this.stockValue = stockValue;
	}

	public Money getAvailableFunds() {
		return availableFunds;
	}

	public void setAvailableFunds(Money availableFunds) {
		this.availableFunds = availableFunds;
	}

	public Money getFeatchFunds() {
		return featchFunds;
	}

	public void setFeatchFunds(Money featchFunds) {
		this.featchFunds = featchFunds;
	}

	public Money getFreezeFunds() {
		return freezeFunds;
	}

	public void setFreezeFunds(Money freezeFunds) {
		this.freezeFunds = freezeFunds;
	}

	public String toString() {
		return "totalValue : " + totalValue + ",stockValue : " + stockValue + ",availableFunds : " + availableFunds+",featchFunds : " + featchFunds +",freezeFunds : " + freezeFunds +"\n";
	}
	
}
