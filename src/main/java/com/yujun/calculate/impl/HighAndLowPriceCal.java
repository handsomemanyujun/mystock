package com.yujun.calculate.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.yujun.calculate.HoldingStockCal;
import com.yujun.calculate.TradeOrder;
import com.yujun.domain.OnlinePriceDO;
import com.yujun.domain.PriceDO;
import com.yujun.domain.StockDO;
import com.yujun.util.DevitionUtil;
import com.yujun.util.Money;
import com.yujun.util.TdxResultUtil;
import com.yujun.util.ThreadLocalPool;

/**
 * 上下区间买入卖出算法
 * @author yujun
 *
 */
@Component
public class HighAndLowPriceCal implements TradeOrder{
	Logger log = Logger.getLogger(this.getClass());
	ThreadLocal<StringBuffer> buf = new ThreadLocal<StringBuffer>();
	HoldingStockCal holdingStockCal = new HoldingStockCal();
	public Map<String,StockDO> calculate(StockDO initStockDO, StockDO hoding, OnlinePriceDO online,int range) {
		long[][] priceRegion 	= holdingStockCal.calStockRegion(initStockDO);
		Map<String,StockDO> result = new HashMap<String,StockDO>();
		StockDO[] byAmont = priceRegionByAmont(priceRegion,hoding);
		StockDO[] byPrice = priceRegionByPrice(priceRegion,online);
		
		StockDO low = null;
		StockDO high = null;
		
		low  = byAmont[0];
		high = byAmont[1];
		
		// 如果股数区间的最低价大于 金额区间的最高价
		// 则将买入的最低价以及数量改成金额区间的最高价
		if(low !=null && byPrice!=null&&low.getAvaPrice().getCent() > byPrice[1].getAvaPrice().getCent()) { 
			low  = byPrice[1];
		}
		
		// 如果股数区间的最高价小于于金额区间的最低价
		// 则将卖出的最高价以及数量改成金额区间的最低价  
		if(high !=null && byPrice!=null &&high.getAvaPrice().getCent() < byPrice[0].getAvaPrice().getCent()) {	
			high = byPrice[0];
		}
		
		result.put("low", low);
		result.put("high", high);
		String tempStr;
		if(high!=null) {
			tempStr = "最终价格区间上限是" + high.getAvaPrice() +":" + high.getAmount();
			log.info(tempStr);
			ThreadLocalPool.getStringBuf().append(tempStr);
		} else {
			tempStr = "最终价格区间上限超出计算范围，请手动计算";
			log.info(tempStr);
			ThreadLocalPool.getStringBuf().append(tempStr);
		}
		
		tempStr = "最终价格区间下限是" + low.getAvaPrice()+":" + low.getAmount();
		log.info(tempStr);
		ThreadLocalPool.getStringBuf().append(tempStr);
		
		/*if(online.getNowPrice().greaterThan(hoding.getAvaPrice())){
			log.info("当前价格超过成本线价格， 不作操作");
		}*/
		return result;
	}
	
	public StockDO[] priceRegionByAmont(long[][] priceRegion , StockDO hoding) {
		StockDO low = null;
		StockDO high = null;
		long highest=0;
		long lowest =0;
		long standard = rangeOfPrice(hoding.getZqCode());	
		
		int  index =-1;
		log.info("按照当前持股数是" + hoding.getAmount());
		for(int i = 0 ; i<priceRegion.length  ;i++){
			if(hoding.getAmount() <= priceRegion[i][1]) {
				index = i ;
				log.info("选择到的区间是" + priceRegion[i][0] +":" +priceRegion[i][1] );
				break;
			}
		}
		
		if(index !=-1) {
			highest = priceRegion[index][0] + standard;
			lowest =  priceRegion[index][0] - standard;
			for(int i = 0 ; i<priceRegion.length  ;i++){
				if(highest > priceRegion[0][0]) {
					break;
				}
				if(highest >= priceRegion[i][0]) {
					if(i>1 && priceRegion[index-2][0] >= highest) {
						high = new StockDO(null,priceRegion[index-2][1],new Money(priceRegion[index-2][0]));
					} else if(i>=3 && priceRegion[index-4][0] <= highest) {
						high = new StockDO(null,priceRegion[index-4][1],new Money(priceRegion[index-4][0]));
					} else {
						high = new StockDO(null,priceRegion[i][1],new Money(highest));
					}
					log.info("按持股数计算出的价格区间上限是" + high.getAvaPrice() +":" + high.getAmount());
					break;
				}
			}
			
			if(high==null) {
				log.info("超出计算范围，最高价格是" + highest);
			} 
			
			for (int i = priceRegion.length-1; i > 0; i--) {
				if(lowest <= priceRegion[i][0]) {
					if (priceRegion[index+2][0] <= lowest) {
						low = new StockDO(null,priceRegion[index+2][1] ,new Money(priceRegion[index+2][0]));
					} else if(lowest <priceRegion[index+3][0]){
						low = new StockDO(null,priceRegion[index+4][1] ,new Money(priceRegion[index+4][0]));
					} else {
						low = new StockDO(null,priceRegion[i][1] ,new Money(lowest));
					}
					log.info("按持股数计算出的价格区间下限是" + low.getAvaPrice()+":" + low.getAmount());
					break;
				}
			}
		}
		
		StockDO[] result = new StockDO[2];
		result[0] = low;
		result[1] = high;
		
		return result;
	}
	
	public StockDO[] priceRegionByPrice(long[][] priceRegion ,OnlinePriceDO online) {
		if(online.getNowPrice().getCent() <=0) {	
			//还没开盘
			return null;
		}
		StockDO low = null;
		StockDO high = null;
		for(int i = priceRegion.length-2 ; i>=0  ;i--){
			if(online.getNowPrice().getCent() <= priceRegion[i][0] ) {
				high 	= new StockDO(null,priceRegion[i][1],new Money(priceRegion[i][0]));
				low 	= new StockDO(null,priceRegion[i+1][1],new Money(priceRegion[i+1][0]));
				break;
			};
		}
		
		if(low!=null && high!=null) {
			log.info("按照当前股票价格" + online.getNowPrice());
			log.info("按价格计算的价格区间上限是" + high.getAvaPrice() +":" + high.getAmount());
			log.info("按价格计算的价格区间下限是" + low.getAvaPrice()+":" + low.getAmount());
			
			StockDO[] result = new StockDO[2];
			result[0] = low;
			result[1] = high;
			return result;
		} 
		return null;
	}
	
	public long rangeOfPrice(String zqCode) {
		try {
			List<PriceDO> list = null;
			/*if(daylineMap.containsKey(zqCode)) {
			    list = daylineMap.get(zqCode);
			} else {*/
				list = TdxResultUtil.parseDaylineByWeb(zqCode);
				//daylineMap.put(zqCode, list);
			//}
			int[] price = new int[7];
			String priceStr ="";
			for (int i = 1; i <= price.length; i++) {
				PriceDO beYestday = list.get(list.size() - 1 - i);
				PriceDO yestday = list.get(list.size() - i);
				price[i-1] = (int)(Math.max(Math.abs(yestday.getHighestPrice().getCent()-beYestday.getClosingPrice().getCent()),Math.abs(yestday.getLowestPrice().getCent()-beYestday.getClosingPrice().getCent())));
				priceStr +="[" +yestday.getDateStr() +"," +price[i-1]+"]";
			}
			
			/*for (int i = 0; i < list.size(); i++) {
				if(date.equals(list.get(i+1).getDate())) {
					for (int j = 0; j < price.length; j++) {
						OfflinePriceDO beYestday = list.get(i-j-1);
						OfflinePriceDO yestday = list.get(i-j );
						price[j] = (int)(Math.max(Math.abs(yestday.getHighestPrice().getCent()-beYestday.getClosingPrice().getCent()),Math.abs(yestday.getLowestPrice().getCent()-beYestday.getClosingPrice().getCent())));
						priceStr +=price[j]+",";
					}
					break;
				}
			}*/
			
			double average = DevitionUtil.getAverageOther(price);
			double standard = DevitionUtil.getStandardDevition(price);
			log.info("最近的几天的价格波动范围是" + priceStr);
			log.info("波动加权算数平均值是" + average);
			log.info("股票波动范围标准差是" + standard);
			
			for (int i = 0; i < price.length; i++) {
				PriceDO of1 = list.get(list.size() - 1 - i);
				PriceDO of2 = list.get(list.size() - 2 - i);
				price[i] = (int)(of1.getClosingPrice().getCent()-of2.getClosingPrice().getCent());
				priceStr +=price[i]+",";
			}
			double priceStandard = DevitionUtil.getStandardDevition(price);
			log.info("股票价格标准差是" + priceStandard);
			
			return (long)(average);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		 ThreadFactory  factory = Executors.defaultThreadFactory(); 
		 factory.newThread(new TT()).start();
		 TimeUnit.SECONDS.sleep(1);
		 factory.newThread(new TT()).start();
		 TimeUnit.SECONDS.sleep(1);
		 factory.newThread(new TT()).start();
	}
	
	 public static class TT implements Runnable {
		public void run() {
			System.out.println(this.hashCode() +":"+ lock.tryLock());
			System.out.println(this.hashCode() +":"+ lock.getHoldCount());
			System.out.println(this.hashCode() +":"+ lock.tryLock());
			System.out.println(this.hashCode() +":"+ lock.getHoldCount());
		}
	}
	 
	 static ReentrantLock lock = new ReentrantLock();
}
