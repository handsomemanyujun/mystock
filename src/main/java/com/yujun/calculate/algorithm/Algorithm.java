package com.yujun.calculate.algorithm;

import java.util.Date;

public interface Algorithm {
	/**
	 * 得到买入卖出信号 1：买入 2：卖出  0：无操作
	 */
	public int getTradeSignal(String code, Date date);
}
