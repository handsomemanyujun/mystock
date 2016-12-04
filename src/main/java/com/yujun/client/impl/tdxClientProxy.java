package com.yujun.client.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.yujun.client.StockClient;
import com.yujun.domain.FundPoolDO;
import com.yujun.domain.OnlinePriceDO;
import com.yujun.domain.OrderDO;
import com.yujun.domain.StockDO;

@Component
public class tdxClientProxy implements StockClient{
	private Map<String,TdxClient>  tdxClinets;
	@PostConstruct
	public void init() {
		tdxClinets = new HashMap<String, TdxClient>();
		TdxClient tdxClient = new TdxClient();
		tdxClient.login("62124349","122541","A238440910,0129170967");
		tdxClinets.put("62124349", tdxClient);
		
		TdxClient tdxClient1 = new TdxClient();
		tdxClient1.login("62278700","324877","A317676125,0187394552");
		tdxClinets.put("62278700", tdxClient1);
	}
	@Override
	public FundPoolDO queryFundDO(String userId) {
		return tdxClinets.get(userId).queryFundDO(userId);
	}
	@Override
	public List<StockDO> queryStockDO(String userId, String zqdm) {
		return tdxClinets.get(userId).queryStockDO(userId,zqdm);
	}
	@Override
	public OrderDO haveDelegate(String userId, boolean isBuy, String zqdm) {
		return tdxClinets.get(userId).haveDelegate(userId,isBuy,zqdm);
	}
	@Override
	public List<OrderDO> queryDelegate(String userId) {
		return tdxClinets.get(userId).queryDelegate(userId);
	}
	@Override
	public OnlinePriceDO queryMarket(String userId, String zqdm) {
		return tdxClinets.get(userId).queryMarket(userId,zqdm);
	}
	@Override
	public void cancleOrder(String userId, OrderDO orderDO) {
		tdxClinets.get(userId).queryFundDO(userId);
	}
	@Override
	public void crateOrder(String userId, OrderDO orderDO) {
		tdxClinets.get(userId).queryFundDO(userId);
	}
	
}
