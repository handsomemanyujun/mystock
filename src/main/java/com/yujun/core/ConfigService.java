package com.yujun.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yujun.domain.Setting;

/**
 * 账户情况
 * @author yujun
 *
 */
@Component
public class ConfigService {
	Logger logger=LoggerFactory.getLogger(ConfigService.class);
	private Map<String, Map<String,Setting>> config;
	Resource re = new ClassPathResource("stock_setting.db");
	
	public Setting getUserConfig(String userId,String code) throws Exception{
		Map<String,Setting> config = getSetting(userId);
		return config !=null ? config.get(code):null;
	}

	public Map<String, Setting> getSetting(String userId) {
		if (config == null) {
			initConfig();
		}
		return config.get(userId);
	}
	
	public void initConfig() {
		try {
			StringBuffer buf = new StringBuffer();
			BufferedReader br;
			br = new BufferedReader(new InputStreamReader(re.getInputStream()));

			String line = "";
			while ((line = br.readLine()) != null) {
				buf.append(line);
			}
			br.close();
			
			
			if(buf.length()>0){
				config = new HashMap<String, Map<String,Setting>>();
				List<Setting> settings = (List<Setting>) JSONArray.parseArray(buf.toString(), Setting.class);
				for(Setting setting : settings) {
					putSetting(setting);
				}
			} 
		} catch (Exception e) {
			logger.error("getSetting error.", e);
		}
	}
	
	private void putSetting(Setting setting) {
		Map<String,Setting> map = config.get(setting.getUserId());
		if(map ==null) {
			map = new HashMap();
		}
		map.put(setting.getCode(), setting);
		config.put(setting.getUserId(), map);
	}
	public void updateNewSetting(Setting setting) {
		if (config == null) {
			initConfig();
		}
		putSetting(setting);
		try {
			List<Setting> list = new ArrayList<Setting>();
			for(Map<String,Setting> userConfig : config.values()) {
				for(Setting it :userConfig.values()){
					list.add(it);
				}
			}
			BufferedWriter br = new BufferedWriter(new FileWriter(re.getFile(),false));
			br.write(JSONObject.toJSONString(list));
			br.flush(); //刷新缓冲区的数据到文件
			br.close();
		} catch (Exception e) {
			logger.error("updateNewSetting error.",e);
		}
	}
	
	public static void main(String[] args) {
		ConfigService service = new ConfigService();
		Setting setting = new Setting();
		setting.setAmount(12);
		setting.setCode("3434");
		setting.setRate(1.2f);
		setting.setValue(23223);
		setting.setUserId("12343");
		service.updateNewSetting(setting);
		
		Setting setting1 = new Setting();
		setting1.setAmount(12);
		setting1.setCode("34343");
		setting1.setRate(1.2f);
		setting1.setValue(23223);
		setting1.setUserId("12343");
		service.updateNewSetting(setting1);
		
		Setting setting2 = new Setting();
		setting2.setAmount(12);
		setting2.setCode("3434");
		setting2.setRate(1.2f);
		setting2.setValue(23223);
		setting2.setUserId("3435");
		service.updateNewSetting(setting2);
	}
	
}
