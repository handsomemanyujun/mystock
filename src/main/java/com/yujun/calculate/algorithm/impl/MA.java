package com.yujun.calculate.algorithm.impl;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.yujun.calculate.algorithm.Algorithm;
import com.yujun.domain.PriceDO;
import com.yujun.util.DateUtil;
import com.yujun.util.TdxResultUtil;

public class MA implements Algorithm{
	static Logger log = Logger.getLogger(MA.class);
	 private final int day[] = {
		        3, 10
	};
	@Override
	public int getTradeSignal(String code, Date date) {
		List<PriceDO> data = TdxResultUtil.parseDaylineByTxt(code);
		for(int i = 0; i < data.size(); i++) {
			if(!data.get(i).getDate().before(date)) {
				data = data.subList(0, i);
				break;
			}
		}
		return getFlag(data);
	}
	
	private int getFlag(List<PriceDO> data) {
		float[] result = new float[day.length];
        for(int i = 0; i < day.length; i++) {
        	result[i] = getAverage(data.subList(data.size() - day[i], data.size()));
        }
       /* float[] yestdayResult = new float[day.length];        
        for(int i = 0; i < day.length; i++) {
        	yestdayResult[i] = getAverage(data.subList(data.size() - day[i]-1, data.size()-1));
        }*/
        log.info(result[0] + "->" +  result[1] +",");
        if(result[0] >= result[1] 
        		//&& yestdayResult[0] < yestdayResult[1]
        		) {
        	return 1;
        }
        
        if(result[0] < result[1] 
        		//&& yestdayResult[0] > yestdayResult[1]
        		) {
        	return -1;
        }
        
		return 0;
	}
	
	//獲取平均值
    public static float getAverage(List<PriceDO> price) {
        float sum = 0.0f;
        for(int i = 0;i < price.size();i++){
            sum += price.get(i).getClosingPrice().getCent();
            log.info(price.get(i).getDateStr() +"==" +price.get(i).getClosingPrice().getCent());
        }
     
        return sum / price.size();
    }
    
	public static void main(String[] args) {
		MA ma = new MA();
		Date date = new Date();
		for(int i=1; i<100;i++) {
			Date date11 = DateUtil.addDay(date, 0-i);
			log.info(" "+ DateUtil.dateFormat.format(date11) +"," + ma.getTradeSignal("600750",date11));
		}
		
	}

}
