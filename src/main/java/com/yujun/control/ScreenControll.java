package com.yujun.control;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.WebApplicationContext;

import com.yujun.calculate.OrderCalculate;
import com.yujun.client.StockClient;
import com.yujun.core.AccountService;
import com.yujun.core.SettingService;
import com.yujun.domain.Account;
import com.yujun.domain.OnlinePriceDO;
import com.yujun.domain.Setting;
import com.yujun.domain.StockDO;
import com.yujun.service.Calculata;
import com.yujun.util.LogUtil;

@Controller
public class ScreenControll {
	Logger logger=LoggerFactory.getLogger(ScreenControll.class);
	@Autowired
	WebApplicationContext webApplicationContext;
	@Autowired
	StockClient client;
	@Autowired 
	SettingService stockService;
	@Autowired
	OrderCalculate highAndLowPriceCal;
	@Autowired
	SettingService settingService;
	@Autowired
	AccountService accountService;
	@Autowired
	Calculata calculata;
	private static String ACCOUNT ="accountId";
	@RequestMapping("/")  
	public String root(Map<String, Object> mode,HttpSession session){ 
		if(session.getAttribute(ACCOUNT)==null){ 
			return "redirect:login";
		}else{
			return "redirect:home";  
		}
	} 
	
	/**
	 * 账户首页
	 * @param model
	 * @param session
	 * @return
	 */
	@RequestMapping("/home")  
	public String accountDetail(Map<String, Object> model,HttpSession session){ 
		if(session.getAttribute(ACCOUNT)==null) {
			return "login";
		}
		
		String accountId = ((String)session.getAttribute(ACCOUNT));
		model.put("accountId",accountId);  
	    model.put("fund",client.queryFundDO(accountId));  
	    model.put("stocks",client.queryStockDO(accountId,null));  
	    return "home";  
	} 
	
	/**
	 * 股票详情
	 * @param zqCode
	 * @param model
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/detail")  
	public String stockDetail(@RequestParam String zqCode, Map<String, Object> model,HttpSession session) throws Exception{  
		if(session.getAttribute(ACCOUNT)==null) {
			return "login";
		}
		
		String accountId = ((String)session.getAttribute(ACCOUNT));
		
		StockDO stockDO  = client.queryStockDO(accountId,zqCode).get(0);
		model.put("stock",stockDO);  
		model.put("orders",client.queryDelegate(accountId,zqCode)); 
		Setting set = settingService.getUserConfig(accountId,zqCode);
		if(set!=null) {
			model.put("status",	set.getStatus()==0 ? "关闭":"开启");
		}
		
		List<StockDO> holdings= client.queryStockDO(accountId, zqCode);
		OnlinePriceDO online 	= client.queryMarket(set.getUserId(), set.getCode());
		calculata.getTradeOrder(set.getType(),set.buildStockDO(),holdings.get(0), online,new Float(set.getRate()).intValue());
	
	    return "detail";  
	}
	
	@RequestMapping(value="/console")
	public String console(Map<String, Object> model,@RequestParam(value="size", defaultValue="20000") int size,HttpSession session) {
		try {
			String buf = getLogInfo(size,null);
			model.put("price",	buf.toString());  
		} catch (Exception e) {
			logger.error("fetch info.log error.", e);
		}	
		return "log";
	}
	
	@RequestMapping(value="/log")
	public String log(Map<String, Object> model,@RequestParam(value="size", defaultValue="20000") int size,HttpSession session) {
		try {
			if(session.getAttribute(ACCOUNT)==null) {
				return "login";
			}
			String accountId = ((String)session.getAttribute(ACCOUNT));
			
			String buf = getLogInfo(size,accountId);
			model.put("price",	buf.toString());  
		} catch (Exception e) {
			logger.error("fetch info.log error.", e);
		}	
		return "log";  
	}
	/**
	 * 被监控股票配置列表
	 * @param action
	 * @param code
	 * @param model
	 * @param session
	 * @return
	 */
	@RequestMapping(value="/stockConfig",method=RequestMethod.GET)
	public String stockConfig(String action,String code,Map<String, Object> model,HttpSession session) {
		if(session.getAttribute(ACCOUNT)==null) {
			return "login";
		}
		
		
		String accountId = ((String)session.getAttribute(ACCOUNT));
		if("1".equals(action)) {
			settingService.deleteSetting(accountId,code);
		} else if("2".equals(action)) {
			Map map = settingService.getSetting(accountId);
			model.put("update", map.get(code));
		}
		model.put("settings", settingService.getSetting(accountId));
		return "stockConfig";
	}
	
	/**
	 * 监控股票submit
	 * @param setting
	 * @param session
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value="/stockConfig",method= RequestMethod.POST)
	public String settingSubmit(Setting setting,HttpSession session) throws Exception {
		if(session.getAttribute(ACCOUNT)==null) {
			return "login";
		}
		String accountId = ((String)session.getAttribute(ACCOUNT));
		
		List<StockDO> list = client.queryStockDO(accountId,setting.getCode());
		if(list.size()==1) {
			setting.setName(list.get(0).getZqName());
			setting.setUserId(accountId);
			settingService.updateNewSetting(setting);
		}
		return "redirect:stockConfig";
	}
	
	/**
	 * 登录
	 */
	@RequestMapping(value="/login",method= RequestMethod.GET)
	public void login() {
	}
	
	/**
	 * 登录
	 * @param password
	 * @param session
	 * @return
	 */
	@RequestMapping(value="/login",method= RequestMethod.POST)
	public String login(@RequestParam String password, HttpSession session) {
		Account account = accountService.getAccountByPassWord(password);
		if(account !=null) {
			session.setAttribute(ACCOUNT, account.getId());
		}
		
		return "redirect:home";
	}
	
	/**
	 * 登录
	 * @param password
	 * @param session
	 * @return
	 */
	@RequestMapping(value="/loginout",method= RequestMethod.GET)
	public String login(HttpSession session) {
		session.setAttribute(ACCOUNT, null);
		return "redirect:login";
	}
	
	@RequestMapping(value="/start",method= RequestMethod.POST)
	public String start(@RequestParam String zqCode,HttpSession session) throws Exception {
		if(session.getAttribute(ACCOUNT)==null) {
			return "login";
		}
		
		String accountId = ((String)session.getAttribute(ACCOUNT));
		
		Setting setting = settingService.getUserConfig(accountId, zqCode);
		if(setting !=null) {
			setting.setStatus(1);
			settingService.updateNewSetting(setting);
		}
		return "redirect:home";
	}
	
	@RequestMapping(value="/stop",method= RequestMethod.POST)
	public String stop(@RequestParam String zqCode,HttpSession session,HttpServletRequest req) throws Exception {
		if(session.getAttribute(ACCOUNT)==null) {
			return "login";
		}
		req.getParameterMap();
		String accountId = ((String)session.getAttribute(ACCOUNT));
		
		Setting setting = settingService.getUserConfig(accountId, zqCode);
		if(setting !=null) {
			setting.setStatus(0);
			settingService.updateNewSetting(setting);
		}
		return "redirect:home";
	}
	
	private String getLogInfo(int size, String accountId)
			throws Exception {
		Resource re = webApplicationContext.getResource("info.log");
		String result = "";
		RandomAccessFile randAccFile = new RandomAccessFile(re.getFile().getPath(), "r");
		long length = randAccFile.length(); // 获得文件的长度,以便定位末尾
		long pos = length - 1; // 定位文件尾
		while (pos > 0) { // 判断文件是否到达头
			--pos;
			randAccFile.seek(pos); // 定位文件指针所指的位置
			if (randAccFile.readByte() == '\n') { // 如果是换行符,就可以读取该行了
				String line = new String(randAccFile.readLine().getBytes("ISO-8859-1"), "utf-8");
				if (StringUtils.isEmpty(accountId)) {
					result = line + "</br>" + result;
				} else {
					if (line.contains(LogUtil.getSpFlag(accountId))) {
						result = LogUtil.trimSpString(accountId, line)  + "</br>" + result;
					}
				}
			}

			if (result.length() > size) {
				break;
			}
		}

		randAccFile.close();
		return result;
	}
}
