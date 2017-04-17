package com.yujun.util;


public class MathUtil {
	/* **
	 * 得到将要买入的股票数量
	 */
	public static int getCount(Money price, Money total) {
		//四舍五入，以100做一个单位输出
		return  Math.round((total.getCent()/(float)(price.getCent() * 100)))*100;
	}
}
