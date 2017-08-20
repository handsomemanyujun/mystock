package com.yujun.domain;

public enum CrossStyle {
	UpCross,	//金叉
	DownCross;	//死叉
	
	static public CrossStyle assessCrossStyle(
            float beforeValueK,
            float beforeValueD,
            float currentValueK,
            float currentValueD) {
		
		if(beforeValueK>=beforeValueD){
			if(currentValueK>=currentValueD) {
				return UpCross;
			} else {
				return DownCross;
			}
		} else {
			if(currentValueK>=currentValueD) {
				return UpCross;
			} else {
				return DownCross;
			}
		}
	}

}
