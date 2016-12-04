package com.yujun.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateUtil {
	public static Calendar cal = Calendar.getInstance();//ʹ��Ĭ��ʱ�������Ի������һ�������� 
	public static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");  
	public static boolean isOpenTradeTime() {
		Calendar open = Calendar.getInstance();
		open.set(Calendar.HOUR_OF_DAY, 9);
		open.set(Calendar.MINUTE,15);
		
		Calendar close = Calendar.getInstance();
		close.set(Calendar.HOUR_OF_DAY, 15);
		close.set(Calendar.MINUTE,0);
		
		if (open.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
				|| open.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
			return false;
		}
		
		Date now = new Date();
		for (Date noTradeDay : noTradeDays) {
			try {
				if (daysBetween(now,noTradeDay) ==0) {
					return false;
				}
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
		}
		return now.after(open.getTime()) && now.before(close.getTime());
		
	}
	
	/**
	 * �Ƿ�������ǰ���һ����
	 * @return
	 */
	public static boolean needCancelOrder() {
		Date now = new Date();
		
		Calendar close = Calendar.getInstance();
		close.set(Calendar.HOUR_OF_DAY, 14);
		close.set(Calendar.MINUTE,58);
		
		return now.after(close.getTime());
	}
	public static void main(String[] args) throws InterruptedException {
		
	}
	
	
	private static  List<Date> noTradeDays = new ArrayList<Date>();
	static {
		try {
			noTradeDays.add(dateFormat.parse("2016/05/02"));
			noTradeDays.add(dateFormat.parse("2016/06/09"));
			noTradeDays.add(dateFormat.parse("2016/06/10"));
			noTradeDays.add(dateFormat.parse("2016/09/15"));
			noTradeDays.add(dateFormat.parse("2016/09/16"));
			noTradeDays.add(dateFormat.parse("2016/09/18"));
			noTradeDays.add(dateFormat.parse("2016/10/03"));
			noTradeDays.add(dateFormat.parse("2016/10/04"));
			noTradeDays.add(dateFormat.parse("2016/10/05"));
			noTradeDays.add(dateFormat.parse("2016/10/06"));
			noTradeDays.add(dateFormat.parse("2016/10/07"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	 public static int daysBetween(Date smdate,Date bdate) throws ParseException    
	    {    
	        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");  
	        smdate=sdf.parse(sdf.format(smdate));  
	        bdate=sdf.parse(sdf.format(bdate));  
	        Calendar cal = Calendar.getInstance();    
	        cal.setTime(smdate);    
	        long time1 = cal.getTimeInMillis();                 
	        cal.setTime(bdate);    
	        long time2 = cal.getTimeInMillis();         
	        long between_days=(time2-time1)/(1000*3600*24);  
	            
	       return Integer.parseInt(String.valueOf(between_days));           
	    }    
	 
	 public static Date addDay(Date date , int day) {
		 cal.setTime(date); 
		 cal.add(Calendar.DATE, day);//ȡ��ǰ���ڵ�ǰһ��.  
		 return cal.getTime();
	 }
}
