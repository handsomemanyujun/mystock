package com.yujun.calculate.algorithm.impl;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

import com.yujun.calculate.algorithm.Algorithm;
import com.yujun.domain.CrossStyle;
import com.yujun.domain.PriceDO;
import com.yujun.util.Money;
import com.yujun.util.TdxResultUtil;

public class KDJ implements Algorithm {
	private int N = 7;
	public int K_M1=3;
	public int D_M2=3;
	public LinkedHashMap<LocalDate , KDJEntry> map = new LinkedHashMap<>();
	
	@Override
	public boolean getTradeSignal(String code, LocalDate date) {
		List<PriceDO> data = TdxResultUtil.parseDaylineByWeb(code);
		data.sort(new Comparator<PriceDO>() {
			public int compare(PriceDO a, PriceDO b) {
				return a.getDate().compareTo(b.getDate());
			}
		});
		
		for(int i=1 ;i<=data.size();i++) {
			cal(data.subList(Math.max(0, i-N), i));
		}
		KDJEntry[] li = map.values().toArray(new KDJEntry[1]);
		KDJEntry before = li[li.length-2];
		KDJEntry current = li[li.length-1];
		
		CrossStyle crossStyle = CrossStyle.assessCrossStyle(before.k, before.d, current.k, current.d);
		return crossStyle ==CrossStyle.UpCross ? true:false;
	}
	public void cal(List<PriceDO> datas) {
		KDJEntry entry = new KDJEntry();
		PriceDO targetDO = datas.get(datas.size()-1);
		if(targetDO.getDate().equals(LocalDate.of(2017, 8, 14))) {
			int i =0;
			int	p = i;
		}
		long closePrice = targetDO.getClosingPrice().getCent();
		entry.rsv = convert(((closePrice - lowPrice(datas)) / (maxPrice(datas) - lowPrice(datas))) * 100);
		entry.date = targetDO.getDate();
		map.put(targetDO.getDate(), entry);
		
		entry.k = convert(rsvAveragr(datas));
		entry.d = convert(kAveragr(datas));
		entry.j = convert(3 * entry.k - 2 * entry.d);
		
		//System.out.println(entry);
		
	}
	
	private float lowPrice(List<PriceDO> data) {
		Money lowPrice = null;
		for (PriceDO it : data) {
			if (lowPrice == null)
				lowPrice = it.getLowestPrice();
			if (lowPrice.greaterThan(it.getLowestPrice())) {
				lowPrice = it.getLowestPrice();
			}
		}
		return lowPrice.getCent();
	}
	
	private float maxPrice(List<PriceDO> data) {
		Money maxPrice = null;
		for (PriceDO it : data) {
			if (maxPrice == null)
				maxPrice = it.getHighestPrice();
			if (it.getHighestPrice().greaterThan(maxPrice)) {
				maxPrice = it.getHighestPrice();
			}
		}
		return maxPrice.getCent();
	}
	
	private float rsvAveragr(List<PriceDO> data) {
		KDJEntry today = map.get(data.get(data.size()-1).getDate());
		if(data.size()<2){
			return 100;
		}
		KDJEntry before = map.get(data.get(data.size()-2).getDate());
		if(before==null) {
			return 100;
		} else {
			return (today.rsv+ 2*before.k)/3;
		}
	}
	
	private float kAveragr(List<PriceDO> data) {
		KDJEntry today = map.get(data.get(data.size()-1).getDate());
		if(data.size()<2){
			return 100;
		}
		KDJEntry before = map.get(data.get(data.size()-2).getDate());
		if(before==null) {
			return 100;
		} else {
			return (today.k+ 2*before.d)/3;
		}
	}
	static class KDJEntry{
		private float rsv;
		private float k;
		private float d;
		private float j;
		private LocalDate date;
		
		public String toString() {
			return date +",rsv:" + rsv + ",k:"+k+ ",d:"+ d + ",j:"+ j;
		}
		
	}
	
	public  static void main(String[] s) {
		KDJ kdj  = new KDJ();
		boolean flag = kdj.getTradeSignal("300085", null);
		System.out.println(flag);
	}
	
	 float convert(float value) {
		long l1 = Math.round(value * 100); // 四舍五入
		double ret = l1 / 100.0; // 注意：使用 100.0 而不是 100
		return (float)ret;
	}  
	 
}
