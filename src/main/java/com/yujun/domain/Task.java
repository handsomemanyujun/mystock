package com.yujun.domain;

import com.yujun.calculate.TradeOrder;
import com.yujun.core.TdxClient;

public class Task {
	StockDO stockDO;
	TradeOrder tradeOrder;
	TdxClient tdxClient;
	public Task(StockDO stockDO, TradeOrder tradeOrder,TdxClient tdxClient) {
		this.stockDO = stockDO;
		this.tradeOrder =tradeOrder;
		this.tdxClient = tdxClient;
	}
	
	public TdxClient getTdxClient() {
		return tdxClient;
	}
	public void setTdxClient(TdxClient tdxClient) {
		this.tdxClient = tdxClient;
	}

	public StockDO getStockDO() {
		return stockDO;
	}
	public void setStockDO(StockDO stockDO) {
		this.stockDO = stockDO;
	}
	public TradeOrder getTradeOrder() {
		return tradeOrder;
	}
	public void setTradeOrder(TradeOrder tradeOrder) {
		this.tradeOrder = tradeOrder;
	}
}
