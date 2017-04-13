package com.yujun.calculate;

import org.apache.log4j.Logger;

import com.yujun.domain.StockDO;
import com.yujun.util.Money;
import com.yujun.util.ThreadLocalPool;

/**
 * 计算应持有的股票数量
 * @author yujun
 *
 */
public class HoldingStockCal {
	Logger log = Logger.getLogger(this.getClass());
	public long[][] calStockRegion(StockDO initStock) {
		long[][]  priceRegion = new long[70][2];
		
		StringBuffer buffer = new StringBuffer();
		int sum = (int)initStock.getAmount();
		for(int i = 0 ; i < 5 ; i++) {	// 盈利状态
			Money buyPrice 		= initStock.getAvaPrice().multiply(1.01+ 0.01*i);
			long count = Math.round((initStock.getTotalValue().getCent() * 0.1f) / buyPrice.getCent());
			sum-=count;
			priceRegion[4-i][0] = buyPrice.getCent();
			priceRegion[4-i][1] = sum/100*100;
		}
		priceRegion[5][0] = initStock.getAvaPrice().getCent();
		priceRegion[5][1] = initStock.getAmount();
		sum=(int) initStock.getAmount();
		for(int i = 0 ; i < priceRegion.length-6 ; i++) {
			Money buyPrice 		= initStock.getAvaPrice().multiply(0.99 - 0.01*i);
			double rate = (float)(i/5+1)*0.02;
			long count = Math.round((initStock.getTotalValue().getCent() * rate) / buyPrice.getCent());
			sum+=count;
			priceRegion[i+6][0] = buyPrice.getCent();
			priceRegion[i+6][1] = sum/100*100;
		}
		
		for(int i = 0 ; i < priceRegion.length ; i++) {
			buffer.append("[buyPrice:" + priceRegion[i][0]+".count" + priceRegion[i][1] + "],");
			if((i+1)%5==0) {
				buffer.append("\n");
			}
		}
		log.info(initStock.getZqCode() +",的1%波段区间\n" + buffer.toString());
		ThreadLocalPool.getStringBuf().append(initStock.getZqCode() +",的1%波段区间\n" + buffer.toString());
		return priceRegion;
	}
	
	public  static void main(String[] args) {
		HoldingStockCal holdingStockCal = new HoldingStockCal();
		StockDO stockDO= new StockDO();
		stockDO.setAmount(10000);
		stockDO.setAvaPrice(new Money(2339));
		long[][] region = holdingStockCal.calStockRegion(stockDO);
		for(int i=0; i< region.length;i++) {
			System.out.println(i +",[" + region[i][0] + ":" + region[i][1] +"]");
		}
	}
}
