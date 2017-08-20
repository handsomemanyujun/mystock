package com.yujun.calculate.algorithm.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.yujun.calculate.algorithm.Algorithm;

public class MACD implements Algorithm {
	private static int shortPeriod=12;
	private static int longPeriod=26;
	private static int midPeriod=9;
	
	
	   /** 
	 * Calculate EMA, 
	 *  
	 * @param list 
	 *            :Price list to calculate��the first at head, the last at tail. 
	 * @return 
	 */  
	public static final Double getEXPMA(final List<Double> list, final int number) {  
	    Double k = 2.0 / (number + 1.0);
	    Double ema = list.get(0);
	    for (int i = 1; i < list.size(); i++) {  
	        ema = list.get(i) * k + ema * (1 - k);  
	    }  
	    return ema;  
	}  
	  
	/** 
	 * calculate MACD values 
	 *  
	 * @param list 
	 *            :Price list to calculate��the first at head, the last at tail. 
	 * @param shortPeriod 
	 *            :the short period value. 
	 * @param longPeriod 
	 *            :the long period value. 
	 * @param midPeriod 
	 *            :the mid period value. 
	 * @return 
	 */  
	public static final HashMap<String, Double> getMACD(List<Double> list) {  
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
	
	public static void main(String[] args) {
		MACD macd  = new MACD();
		boolean flag = macd.getTradeSignal("300085", LocalDate.now());
		System.out.println(flag);
	}

	@Override
	public boolean getTradeSignal(String code, LocalDate date) {
		// TODO Auto-generated method stub
		return false;
	}

}
