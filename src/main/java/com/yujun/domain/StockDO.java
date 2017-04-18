package com.yujun.domain;

import com.yujun.util.Money;

public class StockDO {
	private String zqCode;	//证券代码
	private String zqName;	//证券名称
	private long amount; // 股票数量
	private Money avaPrice; // 成本价格
	private String userId;
	
	private long salesAmount; // 可卖数量
	private Money nowPrice;	//当前价
	private Money nowValue; //最新市值
	private Money floatValue; //浮动盈亏
	private float floatRate;	//浮动比例
	
	public StockDO() {
		
	}
	public StockDO(String zqCode,long amount,Money avaPrice) {
		this.zqCode 	= zqCode;
		this.amount 	= amount;
		this.avaPrice 	= avaPrice;
	}
	public long getAmount() {
		return amount;
	}
	public void setAmount(long amount) {
		this.amount = amount;
	}
	public Money getTotalValue() {
		return avaPrice.multiply(amount);
	}
	public Money getAvaPrice() {
		return avaPrice;
	}
	public void setAvaPrice(Money avaPrice) {
		this.avaPrice = avaPrice;
	}
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
	
	public long getSalesAmount() {
		return salesAmount;
	}
	public void setSalesAmount(long salesAmount) {
		this.salesAmount = salesAmount;
	}
	public Money getNowPrice() {
		return nowPrice;
	}
	public Money getNowValue() {
		return nowValue;
	}
	public void setNowValue(Money nowValue) {
		this.nowValue = nowValue;
	}
	public Money getFloatValue() {
		return floatValue;
	}
	public void setFloatValue(Money floatValue) {
		this.floatValue = floatValue;
	}
	public void setNowPrice(Money nowPrice) {
		this.nowPrice = nowPrice;
	}
	public float getFloatRate() {
		return floatRate;
	}
	public void setFloatRate(float floatRate) {
		this.floatRate = floatRate;
	}
	public void setFloatRate(long floatRate) {
		this.floatRate = floatRate;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String toString() {
		return "zqCode : " + zqCode + ",zqName : " + zqName + ",amount : " + amount
				+ ",avaPrice : " + avaPrice;
	}
}
