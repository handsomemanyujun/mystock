package com.yujun.control;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.yujun.client.StockClient;
import com.yujun.core.ConfigService;

@Controller
public class StockControll {
	@Autowired
	StockClient client;
	@Autowired 
	ConfigService stockService;
	
	@RequestMapping("/home")  
	public String accountDetail(Map<String, Object> model){  
		model.put("accountId","62124349");  
	    model.put("fund",client.queryFundDO("62124349"));  
	    model.put("stocks",client.queryStockDO("62124349",null));  
	    return "home";  
	} 
	
	@RequestMapping("/detail")  
	public String stockDetail(@RequestParam String zqCode, Map<String, Object> model){  
		model.put("stock",client.queryStockDO("62124349",zqCode).get(0));  
	    return "detail";  
	}
	
	@RequestMapping(value="/setting",method=RequestMethod.GET)
	public String setting(@RequestParam String userId,Map<String, Object> model) {
		model.put("stock", stockService.getSetting("62124349"));
		return "detail";
	}
	
	@RequestMapping(value="/setting",method= RequestMethod.POST)
	public String settingSubmit(@RequestParam String zqCode,
			@RequestParam int rate, @RequestParam int amount,
			@RequestParam int value, Map<String, Object> model) {
		model.put("stock", client.queryStockDO("62124349", zqCode).get(0));
		return "detail";
	}
}
