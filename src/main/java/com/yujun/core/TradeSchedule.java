package com.yujun.core;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.yujun.client.StockClient;
import com.yujun.domain.OnlinePriceDO;
import com.yujun.domain.OrderDO;
import com.yujun.domain.Setting;
import com.yujun.domain.StockDO;
import com.yujun.service.Calculata;

@Service
public class TradeSchedule {
	Logger log = Logger.getLogger(this.getClass());
	@Autowired
	StockClient stockClient;
	@Autowired
	SettingService settingService ;
	@Autowired
	Calculata calculata;
	@Autowired
	AccountService accountService;
	boolean isTest = false;
	@Scheduled(cron="0 0/1 *  * * ? ")
	public void schedul() {
		try {
			for(Map<String,Setting> account : settingService.getAllUserSetting().values()){
				for(Setting  setting : account.values())  {
					if(accountService.getAccountByUserId(setting.getUserId())==null ) {
						log.info("该账户不存在，"+ setting.getUserId());
						continue;
					}
					
					if(setting.getStatus() ==0) {
						continue;
					}
					
					log.info("\n\n开始进入自动下单程序："+ setting.getCode());
					
					
					String zqCode 			= setting.getCode();
					List<StockDO>	holdings= stockClient.queryStockDO(setting.getUserId(), setting.getCode());
					if(holdings==null || holdings.size()==0) {
						log.info("该账户已经不持有这个股票，");
						continue;
					}
					
					OnlinePriceDO online 	= stockClient.queryMarket(setting.getUserId(), setting.getCode());
					Map<String,StockDO> lh 	= calculata.getTradeOrder(setting.getType(),setting.buildStockDO(),holdings.get(0), online,new Float(setting.getRate()).intValue());
					
					StockDO target = lh.get("low");	// 订单买入价
					if(target !=null) {
						OrderDO orderDO = stockClient.haveDelegate(setting.getUserId(),true,zqCode);
						if(orderDO !=null) {
							if(!isTest && !orderDO.getPrice().equals(target.getAvaPrice())) {
								stockClient.cancleOrder(setting.getUserId(),orderDO);
							} 
						} else {
							orderDO = new OrderDO();
							orderDO.setAmount(((Math.abs(target.getAmount()-holdings.get(0).getAmount()))/100)*100);
							orderDO.setBuy(true);
							orderDO.setPrice(target.getAvaPrice());
							orderDO.setZqCode(zqCode);
							if(!isTest) {
								stockClient.crateOrder(setting.getUserId(),orderDO);
							}
						}
					}
					target = lh.get("high");	// 订单卖出价
					if(target !=null) {
						OrderDO orderDO = stockClient.haveDelegate(setting.getUserId(),false,zqCode);
						if(orderDO !=null) {
							if(!isTest && !orderDO.getPrice().equals(target.getAvaPrice())) {
								stockClient.cancleOrder(setting.getUserId(),orderDO);
							} 
						} else {
							orderDO = new OrderDO();
							orderDO.setAmount(((Math.abs(holdings.get(0).getAmount()-target.getAmount()))/100)*100);
							orderDO.setBuy(false);
							orderDO.setPrice(target.getAvaPrice());
							orderDO.setZqCode(zqCode);
							if(!isTest) {
								stockClient.crateOrder(setting.getUserId(),orderDO);
							}
						}
					}
					
				}
			}
		} catch (Exception e) {
			log.error("schedul error",e);
		}
	}
}
