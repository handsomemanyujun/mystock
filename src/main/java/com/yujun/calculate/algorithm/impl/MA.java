package com.yujun.calculate.algorithm.impl;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import com.yujun.calculate.algorithm.Algorithm;
import com.yujun.domain.PriceDO;
import com.yujun.util.LogUtil;
import com.yujun.util.TdxResultUtil;

public class MA implements Algorithm{
	 private final int day[] = {
		        3, 7
	};
	@Override
	public boolean getTradeSignal(String code, LocalDate date) {
		List<PriceDO> data = TdxResultUtil.parseDaylineByWeb(code);
		data.sort(new Comparator<PriceDO>() {
			public int compare(PriceDO a, PriceDO b) {
				return b.getDate().compareTo(a.getDate());
			}
		});
		for(int i = 0; i < data.size(); i++) {
			if(!data.get(i).getDate().isBefore(date)) {
				data = data.subList(Math.min(i, data.size()),data.size()-1);
				break;
			}
		}
		return getFlag(data)>=0 ? true:false;
	}
	
	private int getFlag(List<PriceDO> data) {
		float[] result = new float[day.length];
        for(int i = 0; i < day.length; i++) {
        	result[i] = getAverage(data.subList(0,day[i]));
        }
       /* float[] yestdayResult = new float[day.length];        
        for(int i = 0; i < day.length; i++) {
        	yestdayResult[i] = getAverage(data.subList(data.size() - day[i]-1, data.size()-1));
        }*/
        LogUtil.log(result[0] + "->" +  result[1] +",");
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
	
    public static float getAverage(List<PriceDO> price) {
        float sum = 0.0f;
        for(int i = 0;i < price.size();i++){
            sum += price.get(i).getClosingPrice().getCent();
         //LogUtil.log(price.get(i).getDateStr() +"==" +price.get(i).getClosingPrice().getCent());
        }
     
        return sum / price.size();
    }
    
	public static void main(String[] args) {
		MA ma  = new MA();
		boolean flag = ma.getTradeSignal("300327", LocalDate.now());
		System.out.println(flag);
	}

}
