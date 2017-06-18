package com.yujun.client;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import com.yujun.domain.FundPoolDO;
import com.yujun.domain.OnlinePriceDO;
import com.yujun.domain.OrderDO;
import com.yujun.domain.StockDO;

public interface StockClient {
	public boolean login(String userId,String passWord,String gddm);
	/**
	 * 查询目前总资金情况
	 * @return
	 * @throws Exception 
	 */
	public FundPoolDO queryFundDO(String userId);
	
	
	/**
	 * 查询目前持有股票情况
	 * @return
	 */
	public List<StockDO> queryStockDO(String userId,String zqdm);
	/**
	 * 是否有委托单
	 * @param isBuy
	 * @param zqdm
	 * @return
	 */
	public OrderDO haveDelegate(String userId,boolean isBuy, String zqdm);
	
	
	/**
	 * 查询委托情况
	 * @return
	 */
	public List<OrderDO> queryDelegate(String userId,String zqdm);
	
	/**
	 * 查询目标股票价格
	 * @param zqdm
	 * @return
	 */
	public OnlinePriceDO queryMarket(String userId,String zqdm) ;
	/**
	 * 撤单订单
	 * @param zqdm
	 */
	public void cancleOrder(String userId,OrderDO orderDO);
	
	
	/**
	 * 下单
	 * @param zqdm
	 * @param money
	 * @param amount
	 * @param isBuy
	 */
	public void crateOrder(String userId, OrderDO orderDO) ;
	
	/**
	 * 查询最新成交的订单时间
	 * @param isBuy
	 * @param zqdm
	 * @return
	 * @throws ParseException 
	 */
	public Date latestOrderTime(String userId, String zqdm) throws ParseException;
	
}
