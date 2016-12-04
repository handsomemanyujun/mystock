package com.yujun.control;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.yujun.client.StockClient;

@Controller
public class StockControll {
	@Autowired
	StockClient client;
	
	@RequestMapping("/home")  
	public String accountDetail(Map<String, Object> model){  
		model.put("accountId","62124349");  
	    model.put("fund",client.queryFundDO("62124349"));  
	    model.put("stocks",client.queryStockDO("62124349",null));  
	    return "home";  
	} 
	
	@RequestMapping("/stock_detail")  
	public String stockDetail(@RequestParam String zqCode, Map<String, Object> model){  
		model.put("stock",client.queryStockDO("62124349",zqCode).get(0));  
	    return "stockDetail";  
	} 
}
