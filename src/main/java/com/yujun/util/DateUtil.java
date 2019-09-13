package com.yujun.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateUtil {
	public static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");  
	public static boolean isOpenTradeTime() throws Exception{
		//return true;
		Calendar open = Calendar.getInstance();
		open.set(Calendar.HOUR_OF_DAY, 9);
		open.set(Calendar.MINUTE,31);
		
		Calendar close = Calendar.getInstance();
		close.set(Calendar.HOUR_OF_DAY, 15);
		close.set(Calendar.MINUTE,0);
		
		if (open.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
				|| open.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
			return false;
		}
		
		Date now = new Date();
		for (Date noTradeDay : noTradeDays) {
			if (daysBetween(now,noTradeDay) ==0) {
				return false;
			}
		}
		return now.after(open.getTime()) && now.before(close.getTime());
		
	}
	
	/**
	 * 是否是收盘前最后一分钟
	 * @return
	 */
	public static boolean needCancelOrder() {
		Date now = new Date();
		
		Calendar close = Calendar.getInstance();
		close.set(Calendar.HOUR_OF_DAY, 14);
		close.set(Calendar.MINUTE,55);
		
		return now.after(close.getTime());
	}
	
	
	
	private static  List<Date> noTradeDays = new ArrayList<Date>();
	static {
		try {
			noTradeDays.add(dateFormat.parse("2019/09/13"));
			noTradeDays.add(dateFormat.parse("2017/02/04"));
			noTradeDays.add(dateFormat.parse("2017/02/05"));
			noTradeDays.add(dateFormat.parse("2017/02/06"));
			noTradeDays.add(dateFormat.parse("2017/02/07"));
			noTradeDays.add(dateFormat.parse("2017/02/08"));
			noTradeDays.add(dateFormat.parse("2017/04/05"));
			noTradeDays.add(dateFormat.parse("2017/05/29"));
			noTradeDays.add(dateFormat.parse("2017/05/30"));
			noTradeDays.add(dateFormat.parse("2017/05/01"));
			noTradeDays.add(dateFormat.parse("2017/06/07"));
			noTradeDays.add(dateFormat.parse("2017/09/13"));
			noTradeDays.add(dateFormat.parse("2019/10/01"));
			noTradeDays.add(dateFormat.parse("2019/10/02"));
			noTradeDays.add(dateFormat.parse("2019/10/03"));
			noTradeDays.add(dateFormat.parse("2019/10/04"));
			noTradeDays.add(dateFormat.parse("2019/10/05"));
			noTradeDays.add(dateFormat.parse("2019/10/06"));
			noTradeDays.add(dateFormat.parse("2019/10/07"));
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
		 Calendar cal = Calendar.getInstance();//使用默认时区和语言环境获得一个日历。 
		 cal.setTime(date); 
		 cal.add(Calendar.DATE, day);//取当前日期的前一天.  
		 return cal.getTime();
	 }
	 
	 public static Date addMinute(Date date, int minute) throws ParseException {
		 	if(date ==null) return null;
			Calendar cal = Calendar.getInstance();// 使用默认时区和语言环境获得一个日历。
			cal.setTime(date);
			cal.add(Calendar.MINUTE, minute);// 取当前日期的前一天.
			return cal.getTime();
		}
	 
	public static Date getNowDateByTime(String time) throws ParseException {
		String date = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return sdf.parse(date + " " + time);
	}
	
	public static void main(String[] args) throws InterruptedException, ParseException {
		Date date = addMinute(new Date(),10);
		System.out.println(date);
	}
}
