package com.yujun.calculate.impl;

/**
 * 盈利状态下也能进行波段操作
 */
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.yujun.calculate.TradeOrder;
import com.yujun.domain.OnlinePriceDO;
import com.yujun.domain.PriceDO;
import com.yujun.domain.StockDO;
import com.yujun.util.DevitionUtil;
import com.yujun.util.MathUtil;
import com.yujun.util.Money;
import com.yujun.util.TdxResultUtil;
import com.yujun.util.ThreadLocalPool;
@Component
public class TenPercentAndOther implements TradeOrder {
	Logger log = Logger.getLogger(this.getClass());
	@Override
	public Map<String,StockDO> calculate(StockDO initStockDO, StockDO hoding, OnlinePriceDO online,int miniRange) {
		long[][] priceRegion 	= calStockRegion(initStockDO);
		Map<String,StockDO> result = new HashMap<String,StockDO>();
		StockDO[] byAmont = priceRegionByAmont(priceRegion,hoding,miniRange);
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
		String tempStr=null;
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
	
	public StockDO[] priceRegionByAmont(long[][] priceRegion , StockDO hoding,int miniRange) {
		StockDO low = null;
		StockDO high = null;
		
		
		int  index =-1;
		log.info("按照当前持股数是" + hoding.getAmount());
		for(int i = 0 ; i<priceRegion.length  ;i++){
			if(hoding.getAmount() <= priceRegion[i][1]) {
				index = i ;
				log.info("选择到的区间是" + priceRegion[i][0] +":" +priceRegion[i][1] );
				break;
			}
		}
		
	
		long standard = rangeOfPrice(hoding.getZqCode());	
		if(index >= miniRange-1) {
			long highest = Math.max(priceRegion[index][0] + standard,priceRegion[index-miniRange][0]);
			for(int i = miniRange ; i<priceRegion.length  ;i++){
				if(highest > priceRegion[0][0]) {
					break;
				}
				if(highest >=priceRegion[i-miniRange][0]) {
					high = new StockDO(null,priceRegion[i-miniRange][1],new Money(highest));
					break;
				} 
			}
			if(high==null) {
				log.info("超出计算范围，最高价格是" + highest);
			} else {
				log.info("按持股数计算出的价格区间上限是" + high.getAvaPrice() +":" + high.getAmount());
			}
			
			 
			long lowest = Math.min(priceRegion[index][0] - standard,priceRegion[index+miniRange][0]);
			for (int i = priceRegion.length-miniRange-1; i > 0; i--) {
				if(lowest < priceRegion[priceRegion.length-1][0]) {
					break;
				}
				if(lowest <=  priceRegion[i][0]) {
					low = new StockDO(null,priceRegion[i][1] ,new Money(lowest));
					break;
				}
			}
			if(low==null) {
				log.info("超出计算范围，最低价格是" + highest);
			} else {
				log.info("按持股数计算出的价格区间下限是" + low.getAvaPrice()+":" + low.getAmount());
			}
		}
		
		StockDO[] result = new StockDO[2];
		result[0] = low;
		result[1] = high;
		
		return result;
	}
	
	public StockDO[] priceRegionByPrice(long[][] priceRegion ,OnlinePriceDO online ) {
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
	
	public long[][] calStockRegion(StockDO initStock) {
		long[][]  priceRegion = new long[70][2];
		Money startPrcie = initStock.getAvaPrice().multiply(1.124);
		Money startMoney = initStock.getTotalValue().multiply(0.8);
		
		priceRegion[0][0] = startPrcie.getCent();
		priceRegion[0][1] = MathUtil.getCount(startPrcie, startMoney);
		
		for(int i = 0 ; i < 10; i++) {
			Money buyPrice  = startPrcie.multiply(0.99 - 0.01*i);
			double rate = (float)(i/5+1)*0.02;
			long num = MathUtil.getCount(buyPrice, startMoney.multiply(rate));
			priceRegion[1+i][0] = buyPrice.getCent();
			priceRegion[1+i][1] = (priceRegion[i][1] +num)/100*100;;
			
		}
		
		
		startPrcie = initStock.getAvaPrice();
		startMoney = initStock.getTotalValue();
		
		priceRegion[11][0] = startPrcie.getCent();
		priceRegion[11][1] = priceRegion[10][1] + MathUtil.getCount(startPrcie, startMoney.multiply(0.04));
		for(int i = 6 ; i < 60 ; i++) {
			Money buyPrice  = startPrcie.multiply(0.99 - 0.01*(i-6));
			double rate = (float)(i/5+1)*0.02;
			long num = MathUtil.getCount(buyPrice, startMoney.multiply(rate));
			priceRegion[6+i][0] = buyPrice.getCent();
			priceRegion[6+i][1] = (priceRegion[5+i][1] +num)/100*100;
			
		}
		
		StringBuffer buffer = new StringBuffer();
		for(int i=0; i<priceRegion.length;i++) {
			buffer.append(i +".[buyPrice:" + priceRegion[i][0]+".count" + priceRegion[i][1] + "],");
			if(i%5==0) {
				buffer.append("\n");
			}
		}
		log.info(initStock.getZqCode() +",的1%波段区间是" + buffer.toString());
		ThreadLocalPool.getStringBuf().append(initStock.getZqCode() +",的1%波段区间\n" + buffer.toString());
		return priceRegion;
	}

	
	public  static void main(String[] args) {
		TenPercentAndOther tenPercentAndOther = new TenPercentAndOther();
		StockDO stockDO= new StockDO();
		stockDO.setAmount(10000);
		stockDO.setAvaPrice(new Money(2339));
		long[][] region = tenPercentAndOther.calStockRegion(stockDO);
		for(int i=0; i< region.length;i++) {
			System.out.println(i +",[" + region[i][0] + ":" + region[i][1] +"]");
		}
	}
}
