package com.yujun.calculate.algorithm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MACD {
	   /** 
	 * Calculate EMA, 
	 *  
	 * @param list 
	 *            :Price list to calculate，the first at head, the last at tail. 
	 * @return 
	 */  
	public static final Double getEXPMA(final List<Double> list, final int number) {  
	    // 开始计算EMA值，  
	    Double k = 2.0 / (number + 1.0);// 计算出序数  
	    Double ema = list.get(0);// 第一天ema等于当天收盘价  
	    for (int i = 1; i < list.size(); i++) {  
	        // 第二天以后，当天收盘 收盘价乘以系数再加上昨天EMA乘以系数-1  
	        ema = list.get(i) * k + ema * (1 - k);  
	    }  
	    return ema;  
	}  
	  
	/** 
	 * calculate MACD values 
	 *  
	 * @param list 
	 *            :Price list to calculate，the first at head, the last at tail. 
	 * @param shortPeriod 
	 *            :the short period value. 
	 * @param longPeriod 
	 *            :the long period value. 
	 * @param midPeriod 
	 *            :the mid period value. 
	 * @return 
	 */  
	public static final HashMap<String, Double> getMACD(List<Double> list, final int shortPeriod, final int longPeriod, int midPeriod) {  
	    HashMap<String, Double> macdData = new HashMap<String, Double>();  
	    List<Double> diffList = new ArrayList<Double>();  
	    Double shortEMA = 0.0;  
	    Double longEMA = 0.0;  
	    Double dif = 0.0;  
	    Double dea = 0.0;  
	  
	    for (int i = list.size() - 1; i >= 0; i--) {  
	        List<Double> sublist = list.subList(0, list.size() - i);  
	        shortEMA = getEXPMA(sublist, shortPeriod);  
	        longEMA = getEXPMA(sublist, longPeriod);  
	        dif = shortEMA - longEMA;  
	        diffList.add(dif);  
	    }  
	    dea = getEXPMA(diffList, midPeriod);  
	    macdData.put("DIF", dif);  
	    macdData.put("DEA", dea);  
	    macdData.put("MACD", (dif - dea) * 2);  
	    return macdData;  
	}  
}
