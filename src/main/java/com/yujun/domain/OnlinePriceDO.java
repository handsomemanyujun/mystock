package com.yujun.domain;

import com.yujun.util.Money;

/**
 * 股票实时行情
 * @author yujun
 *
 */
public class OnlinePriceDO {
	/**
	 * 证券代码
	 */
	private String zqCode;
	/**
	 * 证券名称
	 */
	private String zqName;
	
	/**
	 * 昨日收盘价格
	 */
	private Money yPrice;
	
	/**
	 * 今日开盘价格
	 */
	private Money nsPrice;
	
	/**
	 * 当前价格
	 */
	private Money nowPrice;
	
	/**
	 * 
	 * @return 时间
	 */
	private String date;
	
	public String getZqCode() {
		return zqCode;
	}

	public void setZqCode(String zqCode) {
		this.zqCode = zqCode;
	}

	public String getZqName() {
		return zqName;
	}

	public void setZqName(String zqName) {
		this.zqName = zqName;
	}

	public Money getyPrice() {
		return yPrice;
	}

	public void setyPrice(Money yPrice) {
		this.yPrice = yPrice;
	}

	public Money getNsPrice() {
		return nsPrice;
	}

	public void setNsPrice(Money nsPrice) {
		this.nsPrice = nsPrice;
	}

	public Money getNowPrice() {
		return nowPrice;
	}

	public void setNowPrice(Money nowPrice) {
		this.nowPrice = nowPrice;
	}
	
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String toString() {
		return "date"+ date + "zqCode : " + zqCode + ",zqName : " + zqName + ",yPrice : " + yPrice
				+ ",nsPrice : " + nsPrice + ",nowPrice : " + nowPrice;
	}
	
}
