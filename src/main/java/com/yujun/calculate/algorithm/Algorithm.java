package com.yujun.calculate.algorithm;

import java.time.LocalDate;

public interface Algorithm {
	public boolean getTradeSignal(String code, LocalDate date);
}
