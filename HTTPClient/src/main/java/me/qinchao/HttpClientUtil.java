package me.qinchao;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class HttpClientUtil {

	public static String get(String url) {

		return get(url, "UTF-8");
	}

	public static String get(String url, String charset) {

		HttpGet get = new HttpGet(url);
		get.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		get.addHeader("Accept-Encoding", "gzip, deflate");
		get.addHeader("Accept-Language", "zh-CN,zh;q=0.8");
		get.addHeader("Connection", "keep-alive");
		get.addHeader("DNT", "1");
		get.addHeader("If-Modified-Since", "Thu, 06 Aug 2015 17:04:55 GMT");	
		get.addHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.115 Safari/537.36");
		final CloseableHttpClient build = HttpClients.custom().build();
		String entityString = null;
		try {
			CloseableHttpResponse execute = build.execute(get);
			final HttpEntity entity = execute.getEntity();
			entityString = EntityUtils.toString(entity, charset);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return entityString;

	}

}
