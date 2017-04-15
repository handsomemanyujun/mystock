package com.yujun.domain;

import com.yujun.util.Money;

public class Setting {
	private String userId;	//账户号码
	private String code;
	private float rate;
	private int amount;
	private float price;
	private int type=1;
	private String name;
	private int status =0; //1： 执行中，0：未执行
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public float getRate() {
		return rate;
	}

	public void setRate(float rate) {
		this.rate = rate;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	public StockDO buildStockDO(){
		StockDO stockDO = new StockDO();
		stockDO.setZqCode(this.code);
		stockDO.setAmount(this.amount);
		stockDO.setAvaPrice(new Money(this.price));
		return stockDO;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
