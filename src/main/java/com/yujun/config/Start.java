package com.yujun.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Configuration
@PropertySource("classpath:config.properties")
@EnableScheduling
@ComponentScan("com.yujun")
@EnableAutoConfiguration 
@Controller
public class Start {
	@RequestMapping("/")
	 @ResponseBody
    String home() {
        return "Hello World!";
    }
	
	@RequestMapping("/hello")  
	public String hello(Map<String, Object> model){  
	    List<String> l= new ArrayList(); 
	    l.add("哈喽，hadoop");  
	    l.add("哈喽，hbase");  
	    l.add("哈喽，hive");  
	    l.add("哈喽，pig");  
	    l.add("哈喽，zookeeper");  
	    l.add("哈喽，三劫散仙");  
	    //将数据存放map里面，可以直接在velocity页面，使用key访问  
	    model.put("data",l);  
	  
	    return "hello";  
	}  
	
    public static void main(String[] args) throws Exception {
        SpringApplication.run(Start.class, args);
    }

}
