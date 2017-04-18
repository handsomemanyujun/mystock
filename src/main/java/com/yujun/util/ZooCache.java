package com.yujun.util;

import java.util.HashMap;
import java.util.Map;

public class ZooCache {
	
	private static Map zoo = new HashMap();

	public static Object getZooValue(String key) {
		return zoo.get(key);
	}

	public static Object setZooValue(String key,Object value) {
		 return zoo.put(key, value);
	}
	
	
}
