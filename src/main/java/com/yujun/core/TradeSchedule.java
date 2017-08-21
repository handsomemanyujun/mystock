package com.yujun.core;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.yujun.calculate.algorithm.impl.KDJ;
import com.yujun.calculate.algorithm.impl.MA;
import com.yujun.client.StockClient;
import com.yujun.domain.OnlinePriceDO;
import com.yujun.domain.OrderDO;
import com.yujun.domain.Setting;
import com.yujun.domain.StockDO;
import com.yujun.service.Calculata;
import com.yujun.util.DateUtil;
import com.yujun.util.LogUtil;

@Service
public class TradeSchedule {
	@Autowired
	StockClient stockClient;
	@Autowired
	SettingService settingService ;
	@Autowired
	Calculata calculata;
	@Autowired
	AccountService accountService;
	boolean isProduct = true;
	@Scheduled(cron="0 0/1 *  * * ? ")
	public void schedul() {
		try {
			if (isProduct) {
				if(DateUtil.isOpenTradeTime()) {
					if(DateUtil.needCancelOrder()) {	// 收市前最后一分钟，测单
						cancle();
					} else {
						orders();
					}
				}else {
					LogUtil.log("Non trading time");
				}
			} else {
				orders();
			}
		} catch(Exception e) {
			LogUtil.log("error, schedul",e);
		}
	}
		
	public void cancle() throws Exception {
		LogUtil.log("\n\n开始撤单");
		for (Map<String, Setting> account : settingService.getAllUserSetting().values()) {
			for (Setting setting : account.values()) { 
				OrderDO orderDO = stockClient.haveDelegate(setting.getUserId(), true, setting.getCode());
				if(orderDO !=null) {
					stockClient.cancleOrder(setting.getUserId(),orderDO);
				}
			}
		}
	}
	
	public void orders() throws Exception {
		for (Map<String, Setting> account : settingService.getAllUserSetting()
				.values()) {
			for (Setting setting : account.values()) {
				if (accountService.getAccountByUserId(setting.getUserId()) == null) {
					LogUtil.log(setting.getUserId(),"该账户不存在，" + setting.getUserId());
					continue;
				}

				if (setting.getStatus() == 0) {
					continue;
				}

				LogUtil.log(setting.getUserId(),"\n\n开始进入自动下单程序：" + setting.getName());
				
				String zqCode = setting.getCode();
				List<StockDO> holdings = stockClient.queryStockDO(setting.getUserId(), setting.getCode());
				if (holdings == null || holdings.size() == 0) {
					LogUtil.log(setting.getUserId(),"该账户已经不持有这个股票，");
					continue;
				}

				OnlinePriceDO online = stockClient.queryMarket(setting.getUserId(), setting.getCode());
				Map<String, StockDO> lh = calculata.getTradeOrder(setting.getType(), setting.buildStockDO(),
						holdings.get(0), online, new Float(setting.getRate()).intValue());
				
				/*if(online.getNowPrice().greaterThan(holdings.get(0).getAvaPrice())){
					LogUtil.log(setting.getUserId(),"成本价已小于当前价 ，不进操作");
					continue;
				}*/
				
				Date date = DateUtil.addMinute(stockClient.latestOrderTime(setting.getUserId(), setting.getCode()),12);
				if(date!=null && new Date().before(date)){
					LogUtil.log(setting.getUserId(),setting.getName() +",距离上次交易成功时间相差12分钟以内，放弃本轮交易");
					continue;
				}
				
				StockDO target = lh.get("low"); // 订单买入价
				if (target != null) {
					OrderDO orderDO = stockClient.haveDelegate(setting.getUserId(), true, zqCode);
					if (orderDO != null) {
						if (isProduct && !orderDO.getPrice().equals(target.getAvaPrice())) {
							stockClient.cancleOrder(setting.getUserId(), orderDO);
						}
					} else {  
						if(canOrderBuy(setting.getUserId(),setting.getCode())){
							orderDO = new OrderDO();
							orderDO.setAmount(((Math.abs(target.getAmount()
									- holdings.get(0).getAmount())) / 100) * 100);
							orderDO.setBuy(true);
							orderDO.setPrice(target.getAvaPrice());
							orderDO.setZqCode(zqCode);
							if (isProduct) {
								stockClient.crateOrder(setting.getUserId(), orderDO);
							}
						} else {
							LogUtil.log(setting.getUserId(),"当前股票经技术分析后不适合做买入操作");
						}
					}
				}
				target = lh.get("high"); // 订单卖出价
				if (target != null) {
					OrderDO orderDO = stockClient.haveDelegate(setting.getUserId(), false, zqCode);
					if (orderDO != null) {
						if (isProduct&& !orderDO.getPrice().equals(target.getAvaPrice())) {
							stockClient.cancleOrder(setting.getUserId(),
									orderDO);
						}
					} else {
						orderDO = new OrderDO();
						orderDO.setAmount(Math.abs(holdings.get(0)
								.getAmount() - target.getAmount()));
						orderDO.setBuy(false);
						orderDO.setPrice(target.getAvaPrice());
						orderDO.setZqCode(zqCode);
						if (isProduct) {
							stockClient.crateOrder(setting.getUserId(), orderDO);
						}
					}
				}

			}
		}
	}
	
	/**
	 * 通过技术指标判断是否可以买
	 * @return
	 */
	private boolean canOrderBuy(String useId, String code) {
		boolean ma = new MA().getTradeSignal(code, LocalDate.now());
		boolean kdj = new KDJ().getTradeSignal(code, LocalDate.now());
		LogUtil.log(useId, "技术指标MA:" + ma + ",KDJss:" + kdj);
		if (ma && kdj) {
			return true;
		} else {
			return false;
		}
	}
}
