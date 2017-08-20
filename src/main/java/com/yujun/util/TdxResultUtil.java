package com.yujun.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yujun.domain.PriceDO;

public class TdxResultUtil {
	static Map<String,List<PriceDO>> datePricemap = new HashMap<String,List<PriceDO>>();
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
			StringBuffer sb = new StringBuffer();
			DateFormat format =  new java.text.SimpleDateFormat("yyyyMMdd");
			while ((fis.read(buf)) != -1) {
				PriceDO offlinePriceDO= new PriceDO();
				offlinePriceDO.setDate(LocalDate.parse(TdxResultUtil.byteArrayToInt(buf,0,4)+""));
				offlinePriceDO.setOpenPrice(new Money((long)byteArrayToInt(buf,4,8)));
				offlinePriceDO.setHighestPrice(new Money((long)byteArrayToInt(buf,8,12)));
				offlinePriceDO.setLowestPrice(new Money((long)byteArrayToInt(buf,12,16)));
				offlinePriceDO.setClosingPrice(new Money((long)byteArrayToInt(buf,16,20)));
				list.add(offlinePriceDO);
				buf = new byte[32];// 重新生成，避免和上次读取的数据重复
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return list;
	}
	
	public static List<PriceDO> parseDaylineByWeb(String zqcode) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String now = LocalDate.now().format(formatter);
		String before = LocalDate.now().minusDays(365).format(formatter);
		
		String requestUrl = "http://stock.liangyee.com/bus-api/stock/freeStockMarketData/getDailyKBar"
				+ "?userKey=690F0B3176CB4A14915063A7891E5CB5&startDate="+before + "&endDate="+now;
		
		List<PriceDO> list = new ArrayList<PriceDO>();
		String key = now + "_" + zqcode;
		if(datePricemap.containsKey(key)){
			return datePricemap.get(key);
		} 
		try {
			if (TdxResultUtil.isSHCode(zqcode)) {
				requestUrl += "&type=0&symbol="+zqcode;
			} else {
				requestUrl += "&type=1&symbol="+zqcode;
			}
			BufferedReader reader =  HttpClient.getRead(requestUrl);
			DateFormat format =  new java.text.SimpleDateFormat("yyyy-MM-dd");
			StringBuffer buf = new StringBuffer();
			String line ="";
			while (!StringUtils.isEmpty(line=reader.readLine())) {	
				buf.append(line);
			}
			JSONObject json = new JSONObject().parseObject(buf.toString());
			int i = 0;
			JSONArray jsarr = json.getJSONArray("result");
			
			for(Object obj : jsarr) {
				String[] item =((String)obj).split(",");
				PriceDO offlinePriceDO= new PriceDO();
				offlinePriceDO.setDate(LocalDate.parse(item[0]));
				offlinePriceDO.setOpenPrice(new Money(item[1]));
				offlinePriceDO.setHighestPrice(new Money(item[3]));
				offlinePriceDO.setLowestPrice(new Money(item[4]));
				offlinePriceDO.setClosingPrice(new Money(item[2]));
				list.add(offlinePriceDO);
			}
		/*	while (!StringUtils.isEmpty(line=reader.readLine())) {	
				
				String[] item = line.split(",");
				if(new Money(item[5]).getCent()==0) continue;
				PriceDO offlinePriceDO= new PriceDO();
				offlinePriceDO.setDate(format.parse(item[0]));
				offlinePriceDO.setOpenPrice(new Money(item[1]));
				offlinePriceDO.setHighestPrice(new Money(item[2]));
				offlinePriceDO.setLowestPrice(new Money(item[3]));
				offlinePriceDO.setClosingPrice(new Money(item[4]));
				list.add(offlinePriceDO);
			}*/
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		datePricemap.put(key, list);
		return list;
	}
	
	public static void main(String[] args) {
		List<PriceDO>  list = parseDaylineByWeb("601012");
		for(PriceDO price: list) {
			System.out.println(price);
		}
	}
}
