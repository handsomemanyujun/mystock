package com.yujun.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class HttpClient {
	public static BufferedReader getRead(String url) throws Exception {
		CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        RequestConfig requestConfig = RequestConfig.custom()    
                .setConnectTimeout(5000).setConnectionRequestTimeout(1000)    
                .setSocketTimeout(5000).build();    
        httpGet.setConfig(requestConfig);
        httpGet.addHeader("User-Agent", url);
        CloseableHttpResponse httpResponse = httpClient.execute(httpGet);

        System.out.println("GET Response Status:: "
                + httpResponse.getStatusLine().getStatusCode());

        BufferedReader reader = new BufferedReader(new InputStreamReader(
                httpResponse.getEntity().getContent()));

        return reader;
	}
}
