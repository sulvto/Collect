package me.qinchao.baiduPan;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import me.qinchao.RSAUtils;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.Asserts;
import org.apache.http.util.EntityUtils;
import org.jsoup.helper.StringUtil;
import org.junit.Assert;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;

public class BaiduPanUtil {

	public static void main(String[] args) {
		// String gid = guideRandom();
		//
		//
		// String baiduid = getBAIDUID();
		// String token = getToken(baiduid,gid );
		// Map publicKey = getPublicKey(token,gid );
		//
		// System.out.println(publicKey.get("key"));
		login("username","password");
		

		// getBAIDUID();
		
	}

	

	public static String rsaEncrypt(String content,String key) {

		ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
		ScriptEngine engine = scriptEngineManager.getEngineByName("nashorn");
		try {
			engine.eval(new FileReader(
					"/home/qinchao/development/workspace/java/JavaTest/HTTPClient/js/login_0f57343e.js"));
			Invocable invocable = (Invocable) engine;
			String invokeFunction = (String) invocable.invokeFunction(
					"myEncrypt", content,key);

			System.out.println("rsaEncrypt ==> " + invokeFunction);
			return invokeFunction.trim();
		} catch (FileNotFoundException | ScriptException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}

		return null;

	}

	public static String guideRandom() {
		String js = "        \"xxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx\".replace(/[xy]/g, function(c) { "
				+ "           	var r = Math.random() * 16 | 0 "
				+ "             	, v = c == \"x\" ? r : (r & 3 | 8); "
				+ "          	 return v.toString(16) "
				+ "       		} "
				+ "       ).toUpperCase(); ";
		ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
		ScriptEngine engine = scriptEngineManager.getEngineByName("nashorn");
		try {
			Object eval = engine.eval(js);
			return (String) eval;
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	public static String getBAIDUID() {

		String url = "http://pan.baidu.com/#list/path=%2F";
		HttpGet httpPost = new HttpGet(url);
		CookieStore cookieStore = new BasicCookieStore();
		final HttpClient client = HttpClients.custom()
				.setDefaultCookieStore(cookieStore).build();

		try {
			client.execute(httpPost);
			List<Cookie> cookies = cookieStore.getCookies();
			Optional<Cookie> findFirst = cookies.stream()
					.filter(v -> "BAIDUID".equals(v.getName())).findFirst();
			return findFirst.isPresent() ? findFirst.get().getValue() : "";
		} catch (IOException e) {
		}
		return "";
	}

	/**
	 * 
	 * @param token
	 * @param gid
	 * @return Map <String,String>
	 */
	public static Map getPublicKey(String token, String gid) {
		Assert.assertFalse(Strings.isNullOrEmpty(token));
		Assert.assertFalse(Strings.isNullOrEmpty(gid));
		String url = "https://passport.baidu.com/v2/getpublickey?token="
				+ token + "&tpl=mn&apiver=v3&tt=" + new Date().getTime()
				+ "&gid=" + gid + "&callback=bd__cbs__k9cuw0";
		HttpGet httpPost = new HttpGet(url);
		final HttpClient client = HttpClients.custom().build();

		try {
			HttpResponse response = client.execute(httpPost);
			HttpEntity entity = response.getEntity();
			String entityString = EntityUtils.toString(entity);
			// to json and replace(',")
			String jsonObject = entityString.replaceFirst(".*\\(", "")
					.replaceFirst("\\)$", "").replaceAll("\":'", "\":\"")
					.replaceAll("',\"", "\",\"").replaceAll("'}", "\"}")
					.replaceAll("']", "\"]");
			System.out.println("getPublicKey ==>  " + jsonObject);
			JSONObject parseObject = JSON.parseObject(jsonObject);
			return parseObject.containsKey("key") ? parseObject : null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * get baidu toke
	 * 
	 * @param baiduid
	 *            {@see getBAIDUID()}
	 * @param gid
	 *            {@see guideRandom()}
	 */
	public static String getToken(String baiduid, String gid) {
		Assert.assertFalse(Strings.isNullOrEmpty(baiduid));
		Assert.assertFalse(Strings.isNullOrEmpty(gid));

		String url = "https://passport.baidu.com/v2/api/?getapi&tpl=mn&apiver=v3&tt=1450621860252&class=login&gid="
				+ gid + "&logintype=dialogLogin&callback=bd__cbs__p676ul";
		HttpGet httpPost = new HttpGet(url);
		httpPost.addHeader("Cookie", "BAIDUID=" + baiduid + ";");
		final HttpClient client = HttpClients.custom().build();
		String token = "";
		try {
			HttpResponse response = client.execute(httpPost);
			HttpEntity entity = response.getEntity();
			// bd__cbs__p676ul({"errInfo":{ "no": "0" }, "data": {
			// "rememberedUserName" : "", "codeString" : "", "token" :
			// "the fisrt two args should be string type:0,1!", "cookie" : "0",
			// "usernametype":"", "spLogin" : "rate", "disable":"",
			// "loginrecord":{ 'email':[ ], 'phone':[ ] } }})
			String entityString = EntityUtils.toString(entity);

			String jsonString = entityString.replaceFirst(".*\\(\\{", "{")
					.replaceFirst("\\)$", "");
			JSONObject jsonObject = JSON.parseObject(jsonString);
			if (jsonObject.containsKey("data")) {
				JSONObject dataObject = (JSONObject) jsonObject.get("data");
				if (dataObject.containsKey("token")) {
					token = (String) dataObject.get("token");
					System.out.println("getToken ==>  " + token);
				} else {
					System.err.println("no token");
				}
			} else {
				System.err.println("no data");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return token;
	}


	public static void login(String username ,String password ) {

		String gid = guideRandom();
		long nowTime = new Date().getTime();
		String baiduid = getBAIDUID();
		System.out.println("baiduid ==> " + baiduid);
		Assert.assertFalse("baiduid is isNullOrEmpty",
				Strings.isNullOrEmpty(baiduid));
		String token = getToken(baiduid, gid);

		Map rsaKey = getPublicKey(token, gid);
		
		
		Assert.assertNotNull("publicKey is null", rsaKey);
		String rsakey = (String) rsaKey.get("key");
				
		password = rsaEncrypt(password,(String)rsaKey.get("pubkey"));
		Assert.assertNotNull("password encrypt failed ", password);


		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("apiver", "v3"));
		nvps.add(new BasicNameValuePair("callback", "parent.bd__pcbs__kj51oj"));
		nvps.add(new BasicNameValuePair("charset", "UTF-8"));
		nvps.add(new BasicNameValuePair("codestring", ""));
		nvps.add(new BasicNameValuePair("countrycode", ""));
		nvps.add(new BasicNameValuePair("crypttype", "12"));
		nvps.add(new BasicNameValuePair("detect", "1"));
		nvps.add(new BasicNameValuePair("gid", gid));
		nvps.add(new BasicNameValuePair("idc", ""));
		nvps.add(new BasicNameValuePair("isPhone", "false"));
		nvps.add(new BasicNameValuePair("logLoginType", "pc_loginDialog"));
		nvps.add(new BasicNameValuePair("loginmerge", "true"));
		nvps.add(new BasicNameValuePair("logintype", "dialogLogin"));
		nvps.add(new BasicNameValuePair("mem_pass", "on"));

		nvps.add(new BasicNameValuePair("password", password));
		nvps.add(new BasicNameValuePair("ppui_logintime", "8169"));
		nvps.add(new BasicNameValuePair("quick_user", "0"));
		nvps.add(new BasicNameValuePair("rsakey", rsakey));
		nvps.add(new BasicNameValuePair("safeflg", "0"));
		nvps.add(new BasicNameValuePair("splogin", "rate"));
		nvps.add(new BasicNameValuePair("staticpage",
				"https://www.baidu.com/cache/user/html/v3Jump.html"));
		nvps.add(new BasicNameValuePair("subpro", ""));
		nvps.add(new BasicNameValuePair("token", token));
		nvps.add(new BasicNameValuePair("tpl", "mn"));
		nvps.add(new BasicNameValuePair("tt", "" + nowTime));
		nvps.add(new BasicNameValuePair("username", username));
		nvps.add(new BasicNameValuePair("u", "https://www.baidu.com/"));
		nvps.add(new BasicNameValuePair("verifycode", ""));

		HttpPost httpPost = new HttpPost(
				"https://passport.baidu.com/v2/api/?login");
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		httpPost.addHeader("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		httpPost.addHeader("Accept-Encoding", "gzip, deflate");
		httpPost.addHeader("Accept-Language", "zh-CN,zh;q=0.8");
		httpPost.addHeader("Connection", "keep-alive");
		httpPost.addHeader("DNT", "1");
		httpPost.addHeader("If-Modified-Since", "Thu, 06 Aug 2015 17:04:55 GMT");
		httpPost.addHeader(				"Cookie",				"BAIDUID="+ baiduid);
		httpPost.addHeader(
				"User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.115 Safari/537.36");

		CookieStore cookieStore = new BasicCookieStore();
		final CloseableHttpClient build = HttpClients.custom()
				.setDefaultCookieStore(cookieStore)
				.build();
		String entityString = null;
		try {
			CloseableHttpResponse execute = build.execute(httpPost);
			final HttpEntity entity = execute.getEntity();
			entityString = EntityUtils.toString(entity, "UTF-8");
			

			if(entityString.indexOf("err_no=0")!=-1){
				System.out.print("success");	
			}else{
				System.out.println(entityString);
			}
			//err_no=257  验证码
			//err_no=4    密码错误
			
			
			System.out.println("cookie size  :: "+cookieStore.getCookies().size());
			cookieStore.getCookies().forEach(
					cookie -> {
						System.out.println(cookie.getName() + " -- "+ cookie.getValue());
					});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
