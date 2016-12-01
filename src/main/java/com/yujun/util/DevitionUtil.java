package com.yujun.util;

import java.util.List;

public class DevitionUtil {
	
	 //獲取平均值
    public static double getAverage(int[] price){
        int sum = 0;
        for(int i = 0;i < price.length;i++){
            sum += price[i];
        }
        return (double)(sum / (price.length));
    }
    
    //删除最高最低值法
    public static double getAverageOther(int[] price){
        int sum = 0;
        int lowest = price[0];
        int highest = price[0];
        
        for(int i = 0;i < price.length;i++){
        	if(lowest > price[i]) lowest = price[i];
        	if(highest < price[i]) highest = price[i];
            sum += price[i];
        }
        return (double)((sum - lowest - highest)/ (price.length-2));
    }
    
    //末日加权平均法
    public static double endWeightedAverage(int[] price){
        int sum = 0;
        for(int i = 1 ;i <price.length;i++){
            sum += price[i];
        }
        sum += 2*price[0];
        return (double)(sum / (price.length+1));
    }
    
    //阶梯加权平均法
    public static double jieTiAverage(int[] price){
        int sum = 0;
        int count = 0;
        for(int i = 0 ;i <price.length;i++){
            sum += price[i] * (price.length-i);
            count+=price.length-i;
        }
        return (sum+0.0d) /count;
    }
   
    //獲取標準差
    public static double getStandardDevition(int[] price){
        double sum = 0;
        for(int i = 0;i < price.length;i++){
            sum += Math.sqrt(((double)price[i] -getAverage(price)) * (price[i] -getAverage(price)));
        }
        return (sum / (price.length - 1));
    }

    public static void main(String[] args) throws InterruptedException {
    	int[] price =  {7,17,22,10,2};
    	System.out.print(jieTiAverage(price));
	}
    
    
}
