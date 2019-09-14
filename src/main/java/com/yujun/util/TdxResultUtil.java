package com.yujun.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.yujun.domain.OnlinePriceDO;
import com.yujun.domain.PriceDO;

public class TdxResultUtil {
	static Cache<String, List<PriceDO>>  priceCache = CacheBuilder.newBuilder().expireAfterWrite(8, TimeUnit.HOURS).build();
    
	static Cache<String, OnlinePriceDO>  onlinecache = CacheBuilder.newBuilder().expireAfterWrite(50, TimeUnit.SECONDS).build();
     
	public static String[][] parseStr(String result){
		if(result !=null) {
			String[] lines	 = result.split("\n");
			String[] values	 = lines[0].split("\t");
			
			String[][] res = new String[lines.length][values.length];
			for(int i =0;i<lines.length ;i++) {
				values	 = lines[i].split("\t");
				for(int j =0;j<values.length ;j++) {
					res[i][j] = values[j];
				}
			}
			return res;
		}
		return null;
	}
	
	public static boolean isSHCode(String ZqCode) {
		if(ZqCode.startsWith("6")) {
			return true;
		} else {
			return false;
		}
	}
	
	public static int byteArrayToInt(byte[] b,int start,int end){ 
		   int iOutcome = 0;
		    byte bLoop;

		    for (int i = start; i < end; i++) {
		        bLoop = b[i];
		        iOutcome += (bLoop & 0xFF) << (8 * i);
		    }
		    return iOutcome;
	}
	
	public static List<List<String>>  parseCommonByTxt(String zqcode) {
		List<List<String>> list = new ArrayList();
		try {
			String path = "D:/new_gjzq_v6/T0002/export/SZ#";
			if (TdxResultUtil.isSHCode(zqcode)) {
				path = "D:/new_gjzq_v6/T0002/export/SH#";
			}
			path += zqcode + ".txt";
			BufferedReader  reader = new BufferedReader(new FileReader(new File(path)));
            String tempString = null;
            int line = 1;
			while ((tempString = reader.readLine()) != null) {
				String[] strs = tempString.split("\t");
				if (strs.length > 0) {
					List items = new ArrayList();
					for (String item : strs) {
						items.add(item);
					}
					list.add(items);
				}
			}
            reader.close();
            
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return list;
	}
	public static List<PriceDO> parseDaylineByTxt(String zqcode) {
		List<PriceDO> list = new ArrayList<PriceDO>();
		try {
			String path = "D:/new_gjzq_v6/T0002/export/SZ#";
			if (TdxResultUtil.isSHCode(zqcode)) {
				path = "D:/new_gjzq_v6/T0002/export/SH#";
			}
			path += zqcode + ".txt";
			BufferedReader  reader = new BufferedReader(new FileReader(new File(path)));
            String tempString = null;
            int line = 1;
            DateFormat format =  new java.text.SimpleDateFormat("yyyy/MM/dd");
            while ((tempString = reader.readLine()) != null) {
            	String[] str = tempString.split("\t");
            	if(str.length >2) {
	            	PriceDO offlinePriceDO= new PriceDO();
					offlinePriceDO.setDate(LocalDate.parse(str[0]));
					offlinePriceDO.setOpenPrice(new Money(str[1]));
					offlinePriceDO.setHighestPrice(new Money(str[2]));
					offlinePriceDO.setLowestPrice(new Money(str[3]));
					offlinePriceDO.setClosingPrice(new Money(str[4]));
					list.add(offlinePriceDO);
            	}
            }
            reader.close();
            
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return list;
	}
	
	public static OnlinePriceDO queryOnlinePrice(String zqdm) {
		try {
			return onlinecache.get(zqdm, ()->{
				String requestUrl = String.format(
						"http://hq.sinajs.cn/list=%s%s", zqdm.startsWith("6")? "sh":"sz",zqdm);
				try {			
					BufferedReader reader =  HttpClient.getRead(requestUrl);
					String line =reader.readLine();
					String[] parts = line.split(",");
					OnlinePriceDO priceDO= new OnlinePriceDO();
					priceDO.setNowPrice(new Money(parts[3]));
								priceDO.setNsPrice(new Money(parts[1]));
					priceDO.setyPrice(new Money(parts[2]));
					return priceDO;
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			});
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}
		
	}
	public static List<PriceDO> parseDayline(String zqcode) {
		FileInputStream fis = null;
		List<PriceDO> list = new ArrayList<PriceDO>();
		try {
			String path = "D:/new_gjzq_v6/vipdoc/sz/lday/sz";
			if (TdxResultUtil.isSHCode(zqcode)) {
				path = "D:/new_gjzq_v6/vipdoc/sh/lday/sh";
			}
			path += zqcode + ".day";

			
			File file = new File(path);
			fis = new FileInputStream(file);
			byte[] buf = new byte[32];
			while ((fis.read(buf)) != -1) {
				PriceDO priceDO= new PriceDO();
				priceDO.setDate(LocalDate.parse(TdxResultUtil.byteArrayToInt(buf,0,4)+""));
				priceDO.setOpenPrice(new Money((long)byteArrayToInt(buf,4,8)));
				priceDO.setHighestPrice(new Money((long)byteArrayToInt(buf,8,12)));
				priceDO.setLowestPrice(new Money((long)byteArrayToInt(buf,12,16)));
				priceDO.setClosingPrice(new Money((long)byteArrayToInt(buf,16,20)));
				list.add(priceDO);
				buf = new byte[32];// 重新生成，避免和上次读取的数据重复
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return list;
	}
	
	public static List<PriceDO> parseDaylineByWeb(String zqcode) {
		try {
			return priceCache.get(zqcode, ()->{
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
				String now = LocalDate.now().format(formatter);
				String before = LocalDate.now().minusDays(365).format(formatter);
				String requestUrl = String.format(
						"http://q.stock.sohu.com/hisHq?code=cn_"+zqcode+"&start=%s&end=%s&stat=1&order=D&period=d", before, now);
				try {
					List<PriceDO> list = new ArrayList<PriceDO>();
					BufferedReader reader =  HttpClient.getRead(requestUrl);
					StringBuffer buf = new StringBuffer();
					String line ="";
					while (!StringUtils.isEmpty(line=reader.readLine())) {	
						buf.append(line);
					}
					JSONArray jsarr = JSONArray.parseArray(buf.toString());
					for(Object obj : jsarr.getJSONObject(0).getJSONArray("hq")) {
						JSONArray item =((JSONArray)obj);
						PriceDO priceDO= new PriceDO();
						priceDO.setDate(LocalDate.parse(item.get(0).toString()));
						priceDO.setOpenPrice(new Money(item.get(1).toString()));
						priceDO.setHighestPrice(new Money(item.get(6).toString()));
						priceDO.setLowestPrice(new Money(item.get(5).toString()));
						priceDO.setClosingPrice(new Money(item.get(2).toString()));
						list.add(priceDO);
					}
					return list;
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			
			});
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	public static void main(String[] args) {
		OnlinePriceDO  price = queryOnlinePrice("000413");
		System.out.println(price);
		queryOnlinePrice("000413");
	}
}
