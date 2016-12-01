package com.yujun.domain;

import com.yujun.util.Money;

/**
 * 委托
 * @author yujun
 *
 */
public class OrderDO {
	private String zqCode;		//证券代码
	private String zqName;		//证券名称
	private String date;		//委托时间
	private boolean isBuy;		//买卖
	private Money price;		//委托价格
	private	long amount;		//委托数量
	private String orderId;		// 订单id
	private int status;			// 状态
	public static int SUCCESS =1;	// 已成
	public static int CANCLE  =2;	// 已撤
	public static int WAITING =3;	// 已报
	public static int PART_SUCCESS =4;	// 部成
	public static int PART_CANCLE =5;	// 部撤
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
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public boolean isBuy() {
		return isBuy;
	}
	public void setBuy(boolean isBuy) {
		this.isBuy = isBuy;
	}
	public Money getPrice() {
		return price;
	}
	public void setPrice(Money price) {
		this.price = price;
	}
	public long getAmount() {
		return amount;
	}
	public void setAmount(long amount) {
		this.amount = amount;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String toString() {
		return "zqCode : " + zqCode + ",zqName : " + zqName + ",date : " + date
				+ ",isBuy : " + isBuy + ",status : " + status + ",price : " + price + ",amount : "
				+ amount + ",orderId : " + orderId;
	}
}
