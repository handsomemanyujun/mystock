package com.yujun.util;


public class ThreadLocalPool {
	private static ThreadLocal<StringBuffer> buf = new ThreadLocal<StringBuffer>();
	
	public static StringBuffer getStringBuf(){
		if(buf.get()==null) {
			buf.set(new StringBuffer());
		}
		return buf.get();
	}
}
