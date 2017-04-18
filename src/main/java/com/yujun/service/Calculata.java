package com.yujun.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.yujun.calculate.OrderCalculate;
import com.yujun.calculate.impl.HighAndLowPriceCal;
import com.yujun.calculate.impl.TenPercentAndOther;
import com.yujun.domain.OnlinePriceDO;
import com.yujun.domain.StockDO;

@Service
public class Calculata {
	private static Map<Integer, OrderCalculate> cal;
	
	@PostConstruct
	public void init() {
		cal = new HashMap();
		cal.put(2,  new HighAndLowPriceCal() );
		cal.put(1,  new TenPercentAndOther() );
	}
	public Map<String,StockDO> getTradeOrder(int type ,StockDO initStock, StockDO hoding, OnlinePriceDO online,int range) {
		return cal.get(type).calculate(initStock, hoding, online, range);
	}
	
	
}
