package com.yujun.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yujun.domain.Setting;

/**
 * 账户情况
 * @author yujun
 *
 */
@Service
public class SettingService {
	Logger logger=LoggerFactory.getLogger(SettingService.class);
	private Map<String, Map<String,Setting>> settings = new HashMap<String, Map<String,Setting>>();
	File db = new File("C:/Users/Administrator/Desktop/newstock/mystock/stock_setting.db");
	public Map<String, Map<String,Setting>> getAllUserSetting() throws Exception{
		if(settings == null) {
			initConfig();	
		}
		return settings;
	}
	
	public Setting getUserConfig(String userId,String code) throws Exception{
		Map<String,Setting> setting = getSetting(userId);
		return setting !=null ? setting.get(code):null;
	}

	public Map<String, Setting> getSetting(String userId) {
		if (settings == null) {
			initConfig();
		}
		return settings.get(userId);
	}
	
	@PostConstruct
	public void initConfig() {
		try {
			StringBuffer buf = new StringBuffer();
			BufferedReader br;
			br = new BufferedReader(new InputStreamReader(new FileInputStream(db)));

			String line = "";
			while ((line = br.readLine()) != null) {
				buf.append(line);
			}
			br.close();
			
			
			if(buf.length()>0){
				Map<String, Map<String,Setting>>  newsettings = new HashMap<String, Map<String,Setting>>();
				List<Setting> settings = (List<Setting>) JSONArray.parseArray(buf.toString(), Setting.class);
				for(Setting setting : settings) {
					Map<String,Setting> map = newsettings.get(setting.getUserId());
					if(map ==null) {
						map = new HashMap();
					}
					map.put(setting.getCode(), setting);
					newsettings.put(setting.getUserId(), map);
				}
				this.settings = newsettings;
			} 
		} catch (Exception e) {
			logger.error("getSetting error.", e);
		}
	}
	
	private void putSetting(Setting setting) {
		Map<String,Setting> map = settings.get(setting.getUserId());
		if(map ==null) {
			map = new HashMap();
		}
		map.put(setting.getCode(), setting);
		settings.put(setting.getUserId(), map);
	}
	public void updateNewSetting(Setting target) {
		if (settings == null) {
			initConfig();
		}
		putSetting(target);
		try {
			List<Setting> list = new ArrayList<Setting>();
			for(Map<String,Setting> userSetting : settings.values()) {
				for(Setting it :userSetting.values()){
					list.add(it);
				}
			}
			BufferedWriter br = new BufferedWriter(new FileWriter(db,false));
			br.write(JSONObject.toJSONString(list));
			br.flush(); //刷新缓冲区的数据到文件
			br.close();
		} catch (Exception e) {
			logger.error("updateNewSetting error.",e);
		}
	}
	
	public void deleteSetting(String userId,String code) {
		Map map = settings.get(userId);
		map.remove(code);
	}
	public static void main(String[] args) {
		SettingService service = new SettingService();
		Setting setting = new Setting();
		setting.setAmount(12);
		setting.setCode("3434");
		setting.setRate(1.2f);
		setting.setPrice(23223);
		setting.setUserId("12343");
		service.updateNewSetting(setting);
		
		Setting setting1 = new Setting();
		setting1.setAmount(12);
		setting1.setCode("34343");
		setting1.setRate(1.2f);
		setting1.setPrice(23223);
		setting1.setUserId("12343");
		service.updateNewSetting(setting1);
		
		Setting setting2 = new Setting();
		setting2.setAmount(12);
		setting2.setCode("3434");
		setting2.setRate(1.2f);
		setting2.setPrice(23223);
		setting2.setUserId("3435");
		service.updateNewSetting(setting2);
	}
	
}
