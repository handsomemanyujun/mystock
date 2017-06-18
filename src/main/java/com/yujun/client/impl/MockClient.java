package com.yujun.client.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import com.yujun.client.StockClient;
import com.yujun.domain.FundPoolDO;
import com.yujun.domain.OnlinePriceDO;
import com.yujun.domain.OrderDO;
import com.yujun.domain.StockDO;
import com.yujun.util.Money;

//@Component
public class MockClient implements StockClient{

	@Override
	public FundPoolDO queryFundDO(String userId) {
		FundPoolDO fundPoolDO= new FundPoolDO();
		fundPoolDO.setAvailableFunds(new Money(123));
		fundPoolDO.setStockValue(new Money(123));
		fundPoolDO.setTotalValue(new Money(123));
		fundPoolDO.setFeatchFunds(new Money(123));
		fundPoolDO.setFreezeFunds(new Money(0));
		return fundPoolDO;
	}

	@Override
	public List<StockDO> queryStockDO(String userId, String zqdm) {
		List<StockDO> list = new ArrayList<StockDO>();
		StockDO stockDO = new StockDO();
		stockDO.setAmount(12000);
		stockDO.setAvaPrice(new Money(1236));
		stockDO.setZqCode("300032");
		stockDO.setZqName("深圳通宝");
		stockDO.setSalesAmount(12330l);
		stockDO.setNowPrice(new Money(1230));
		stockDO.setNowValue(new Money(14323));
		stockDO.setFloatValue(new Money(32421));
		stockDO.setFloatRate(1.2f);
		list.add(stockDO);
		list.add(stockDO);
		return list;
	}

	@Override
	public OrderDO haveDelegate(String userId, boolean isBuy, String zqdm) {
		OrderDO orderDO = new OrderDO();
		orderDO.setAmount(12);
		orderDO.setBuy(true);
		orderDO.setDate("20161203");
		orderDO.setOrderId("123");
		orderDO.setPrice(new Money(123));
		orderDO.setStatus(1);
		orderDO.setZqCode("343");
		orderDO.setZqName("深圳通宝");
		return orderDO;
	}

	@Override
	public List<OrderDO> queryDelegate(String userId, String zqdm) {
		List<OrderDO> list = new ArrayList<OrderDO>();
		OrderDO orderDO = new OrderDO();
		orderDO.setAmount(12);
		orderDO.setBuy(true);
		orderDO.setDate("20161203");
		orderDO.setOrderId("123");
		orderDO.setPrice(new Money(123));
		orderDO.setStatusDesc("已成交");
		orderDO.setStatus(1);
		orderDO.setZqCode("300031");
		orderDO.setZqName("深圳通宝");
		list.add(orderDO);
		list.add(orderDO);
		return list;
	}

	@Override
	public OnlinePriceDO queryMarket(String userId, String zqdm) {
		OnlinePriceDO priceDO = new OnlinePriceDO();
		priceDO.setDate("20161203");
		priceDO.setNowPrice(new Money(123));
		priceDO.setZqCode("300031");
		priceDO.setZqName("深圳通宝");
		return priceDO;
	}

	@Override
	public void cancleOrder(String userId, OrderDO orderDO) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void crateOrder(String userId, OrderDO orderDO) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean login(String userId, String passWord, String gddm) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Date latestOrderTime(String userId, String zqdm)
			throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}
}
