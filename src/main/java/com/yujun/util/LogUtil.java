package com.yujun.util;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

public class LogUtil {
	static Logger log = Logger.getLogger(LogUtil.class);
	
	public static void log(String logInfo,Exception e) {
		log.info(logInfo,e);
	}
	
	public static void log(String logInfo) {
		log.info(logInfo);
	}
	public static void log(String userId, String logInfo) {
		log.info(getSpFlag(userId)+logInfo);
	}
	
	public static String trimSpString(String userId,String logInfo) {
		return logInfo.replace(getSpFlag(userId), "");
	}
	
	public static String getSpFlag(String userId) {
		return "spflag:userId:"+ userId;
	}
}
