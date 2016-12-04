package com.yujun.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.filter.CharacterEncodingFilter;

@Configuration
@PropertySource("classpath:config.properties")
@EnableScheduling
@ComponentScan("com.yujun")
@EnableAutoConfiguration 
public class Start {
 
    public static void main(String[] args) throws Exception {
        SpringApplication.run(Start.class, args);
    }

}
