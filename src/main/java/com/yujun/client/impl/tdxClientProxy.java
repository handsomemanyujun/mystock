package com.yujun.client.impl;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yujun.client.StockClient;
import com.yujun.core.AccountService;
import com.yujun.domain.Account;
import com.yujun.domain.FundPoolDO;
import com.yujun.domain.OnlinePriceDO;
import com.yujun.domain.OrderDO;
import com.yujun.domain.StockDO;
import com.yujun.util.TdxResultUtil;

@Component
public class tdxClientProxy implements StockClient{
	private boolean isMock = false;
	public boolean login(String userId,String passWord,String gddm) {
		return true;
	}
	private Map<String,StockClient>  tdxClinets;
	@Autowired
	private AccountService accountService;
	@PostConstruct
	public void init() {
		tdxClinets = new HashMap<String, StockClient>();
		for(Account account : accountService.getAllAccount()) {
			StockClient tdxClient = isMock ?  new MockClient() : new TdxClient() ;
			tdxClient.login(account.getId(), account.getPassword(), account.getGddm());
			tdxClinets.put(account.getId(), tdxClient);
		}
	}
	@Override
	public FundPoolDO queryFundDO(String userId) {
		return tdxClinets.get(userId)!=null ? tdxClinets.get(userId).queryFundDO(userId): null;
	}
	@Override
	public List<StockDO> queryStockDO(String userId, String zqdm) {
		return tdxClinets.get(userId)!=null ? tdxClinets.get(userId).queryStockDO(userId,zqdm):null;
	}
	@Override
	public OrderDO haveDelegate(String userId, boolean isBuy, String zqdm) {
		return tdxClinets.get(userId)!=null ? tdxClinets.get(userId).haveDelegate(userId,isBuy,zqdm):null;
	}
	@Override
	public List<OrderDO> queryDelegate(String userId,String zqdm) {
		return tdxClinets.get(userId)!=null ? tdxClinets.get(userId).queryDelegate(userId,zqdm):null;
	}
	@Override
	public OnlinePriceDO queryMarket(String userId, String zqdm) {
		return TdxResultUtil.queryOnlinePrice(zqdm);
		//return tdxClinets.get(userId)!=null ? tdxClinets.get(userId).queryMarket(userId,zqdm):null;
	}
	@Override
	public void cancleOrder(String userId, OrderDO orderDO) {
		if(tdxClinets.get(userId)!=null){
			tdxClinets.get(userId).cancleOrder(userId,orderDO);
		}
	}
	@Override
	public void crateOrder(String userId, OrderDO orderDO) {
		if(tdxClinets.get(userId)!=null){
			tdxClinets.get(userId).crateOrder(userId,orderDO);
		}
	}
	@Override
	public Date latestOrderTime(String userId, String zqdm)
			throws ParseException {
		if(tdxClinets.get(userId)!=null){
			return tdxClinets.get(userId).latestOrderTime(userId,zqdm);
		}
		return null;
	}
	
}
