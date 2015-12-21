package me.qinchao.yunpan360;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class Yunpan360Util {

	private static final CookieStore cookieStore = new BasicCookieStore();

	private static final CloseableHttpClient httpClient = HttpClients.custom()
			.setDefaultCookieStore(cookieStore)
			//TODO
			.setProxy(new HttpHost("27.115.75.114",8080))
			.setMaxConnPerRoute(30000)
			.setMaxConnTotal(30000)
			.build();

	public static void main(String[] args) {
		login360("userName","password");
		login360Yunpan();
		yunpanList();
	}

	private static String getToken(String userName) {
		HttpGet get = new HttpGet(
				"http://login.360.cn/?func=jQuery1111012313855840305121_1450711016685&src=pcw_cloud&from=pcw_cloud&charset=UTF-8&requestScema=https&o=sso&m=getToken&userName="
						+ userName);
		try {
			CloseableHttpResponse response = httpClient.execute(get);
			HttpEntity entity = response.getEntity();
			String string = EntityUtils.toString(entity)
					.replaceFirst(".*\\(", "").replaceFirst("\\)$", "");
			System.out.println(string);
			JSONObject parseObject = JSON.parseObject(string);
			if (parseObject.containsKey("token")) {
				return (String) parseObject.get("token");
			} else {
				System.err.println("error ==>  " + string);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * 登录后获取cookie  返回存在name为Q和T的cookie表明登录成功
	 * @param userName
	 * @param password md5值
	 */
	public static void login360(String userName ,String password) {
		String token = getToken(userName);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("account", userName));
		nvps.add(new BasicNameValuePair("callback", "QiUserJsonp711016835"));
		nvps.add(new BasicNameValuePair("captFlag", "1"));
		nvps.add(new BasicNameValuePair("captcha", ""));
		nvps.add(new BasicNameValuePair("captchaApp", "i360"));
		nvps.add(new BasicNameValuePair("charset", "UTF-8"));
		nvps.add(new BasicNameValuePair("from", "pcw_cloud"));
		nvps.add(new BasicNameValuePair("func", "QiUserJsonp711016835"));
		nvps.add(new BasicNameValuePair("isKeepAlive", ""));
		nvps.add(new BasicNameValuePair("lm", "0"));
		nvps.add(new BasicNameValuePair("m", "login"));
		nvps.add(new BasicNameValuePair("o", "sso"));
		nvps.add(new BasicNameValuePair("password",password));
		nvps.add(new BasicNameValuePair("proxy",
				"http://c31.yunpan.360.cn/psp_jump.html"));
		nvps.add(new BasicNameValuePair("requestScema", "https"));
		nvps.add(new BasicNameValuePair("rtype", "data"));
		nvps.add(new BasicNameValuePair("src", "pcw_cloud"));
		nvps.add(new BasicNameValuePair("token", token));
		nvps.add(new BasicNameValuePair("type", "normal"));
		nvps.add(new BasicNameValuePair("userName", userName));
		nvps.add(new BasicNameValuePair("validatelm", "0"));
		HttpPost httpPost = new HttpPost("http://login.360.cn/");
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		httpPost.addHeader("Host", "login.360.cn");
		httpPost.addHeader("User-Agent",
				"Mozilla/5.0 (X11; Linux x86_64; rv:38.0) Gecko/20100101 Firefox/38.0");
		httpPost.addHeader("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		httpPost.addHeader("Accept-Language",
				"zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
		httpPost.addHeader("Accept-Encoding", "gzip, deflate");
		httpPost.addHeader("Referer",
				"http://c31.yunpan.360.cn/?u=http://c31.yunpan.360.cn/my/index/");
		httpPost.addHeader("Cookie",
				"__guid=5445921.23584532994747388.1450711016812.1055");
		httpPost.addHeader("Connection", "keep-alive");
		httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
		String entityString = null;
		try {
			CloseableHttpResponse execute = httpClient.execute(httpPost);
			final HttpEntity entity = execute.getEntity();
			entityString = EntityUtils.toString(entity, "UTF-8");

			if (entityString.indexOf("errno=0") != -1) {
				System.out.print("success");
			} else {
				System.out.println(entityString);
			}

			System.out.println("cookie size  :: "
					+ cookieStore.getCookies().size());
			cookieStore.getCookies().forEach(
					cookie -> {
						System.out.println(cookie.getName() + " -- "
								+ cookie.getValue());
					});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * {@see login360()}
	 * 必须先登录http://login.360.cn/并成功获取到需要的cookie（name为Q和T的cookie)
	 * 
	 * 返回存在name为token的cookie表明登录成功
	 */
	public static void login360Yunpan() {
		HttpGet get = new HttpGet(
				"http://c31.yunpan.360.cn/user/login?u=http%3A%2F%2Fc31.yunpan.360.cn%2Fmy%2Findex%2F&st=1450713574&sid=&keepalive=0");
		try {
			CloseableHttpResponse response = httpClient.execute(get);
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out
				.println("cookie size  :: " + cookieStore.getCookies().size());
		cookieStore.getCookies().forEach(cookie -> {
			System.out.println(cookie.getName() + " -- " + cookie.getValue());
		});
	}

	public static void yunpanList() {
		HttpPost post = new HttpPost("http://c31.yunpan.360.cn/file/list");
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("ajax", "1"));
		nvps.add(new BasicNameValuePair("field", "file_name"));
		nvps.add(new BasicNameValuePair("order", "asc"));
		nvps.add(new BasicNameValuePair("page", "0"));
		nvps.add(new BasicNameValuePair("page_size", "300"));
		nvps.add(new BasicNameValuePair("path", "/"));
		nvps.add(new BasicNameValuePair("t", "0.11674355103561884"));
		nvps.add(new BasicNameValuePair("type", "2"));
		
		
		post.addHeader("Host","c31.yunpan.360.cn");
		post.addHeader("User-Agent","Mozilla/5.0 (X11; Linux x86_64; rv:38.0) Gecko/20100101 Firefox/38.0");
		post.addHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		post.addHeader("Accept-Language","zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
		post.addHeader("Accept-Encoding","gzip, deflate");
		post.addHeader("Cache-Control","no-cache");
		post.addHeader("Content-Type","application/x-www-form-urlencoded UTF-8; charset=UTF-8");
		post.addHeader("Pragma","no-cache");
		post.addHeader("X-Requested-With","XMLHttpRequest");
		post.addHeader("Referer","http://c31.yunpan.360.cn/my/index/");
		post.addHeader("Connection","keep-alive");


		try {
			post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			CloseableHttpResponse response = httpClient.execute(post);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			System.out.println("statusCode :: "+statusCode);
			HttpEntity httpEntity = response.getEntity();
			
			String entityString = EntityUtils.toString(httpEntity,"utf-8");
			System.out.println("--------------");
			System.out.println(entityString);
			JSONObject parseObject = JSON.parseObject(entityString);
			System.out.println(parseObject.toJSONString());
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out
				.println("cookie size  :: " + cookieStore.getCookies().size());
		cookieStore.getCookies().forEach(cookie -> {
			System.out.println(cookie.getName() + " -- " + cookie.getValue());
		});
	}

}
