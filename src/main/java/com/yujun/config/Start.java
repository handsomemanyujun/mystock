package com.yujun.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@PropertySource("classpath:config.properties")
@EnableScheduling
@ComponentScan("com.yujun")
@EnableAutoConfiguration 
public class Start implements EmbeddedServletContainerCustomizer{
 
    public static void main(String[] args) throws Exception {
        SpringApplication.run(Start.class, args);
    }

	@Override
	public void customize(ConfigurableEmbeddedServletContainer arg0) {
		arg0.setPort(80);
	}

}
