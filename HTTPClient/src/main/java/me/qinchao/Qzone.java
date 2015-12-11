package me.qinchao;

import java.io.IOException;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngineManager;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class Qzone {

	public static void login() {
		HttpPost post = new HttpPost(
				"http://ptlogin2.qq.com/login?u=592158826&verifycode=!XBO&pt_vcode_v1=0&pt_verifysession_v1=fc9a1a122d764d2e3c7bc80c57ccba6e9c87f167443b6397d56445d7f900486e4937dce3bc709b13eaa0bc8c5a29a83c55b7c04153e3c0e0&p=fD-vPMPQaGzR1yiLaWEBVdirW9c8TJb9Ok9Q5OXON*gpDiCQzdI6WBYI5dkWVtoxabTSOZrb2542e3oXSBNCwTXIZD*YH7GZR201BsBFBuazEPEfWo1An*bKtkbJ3Lm3edqA1IXCiOAinBWXZxe9hYuTyQ8nt-hnfXqRS6txeKMTPyL6XQZAkWbdoB75uMxf7J3ErQJthpnwhjm57QmCJw__&pt_randsalt=0&u1=http%3A%2F%2Fqzs.qq.com%2Fqzone%2Fv5%2Floginsucc.html%3Fpara%3Dizone&ptredirect=0&h=1&t=1&g=1&from_ui=1&ptlang=2052&action=3-3-1438877484045&js_ver=10131&js_type=1&login_sig=&pt_uistyle=32&aid=549000912&daid=5&pt_qzone_sig=1&");
		final CloseableHttpClient build = HttpClients.custom().build();
		String entityString = null;
		try {
			CloseableHttpResponse execute = build.execute(post);
			final HttpEntity entity = execute.getEntity();
			entityString = EntityUtils.toString(entity, "utf-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(entityString);
	}

	public static void qzone(String url) {
		//
		HttpGet get = new HttpGet(url);

		get.addHeader("Referer", "http://qzone.qq.com/");
		get.addHeader("Host", "user.qzone.qq.com");
		get.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		get.addHeader("Accept-Encoding", "gzip, deflate");
		get.addHeader("Accept-Language", "zh-CN,zh;q=0.8");
		get.addHeader("Connection", "keep-alive");
		get.addHeader("DNT", "1");
		get.addHeader("If-Modified-Since", "Thu, 06 Aug 2015 17:04:55 GMT");
		get.addHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.115 Safari/537.36");
		get.addHeader("cookie",
				"RK=IQ8SJ/aMb+; ptcz=867d71b3e72b32e3850e9523b03fdd80bd7f5220da7102f5a0311da4b19fc189; pt2gguin=o0592158826; uin=o0592158826; skey=@eWdyKTiHZ; p_uin=o0592158826; p_skey=OydI7nQDc370AZRfvxzFhnf2ZRosDJB4FoO4hrSaGH4_; pt4_token=FezTZb71lijMydB*k-6PSw__; fnc=2; hasShowWeiyun592158826=1; a2404_pages=2; a2404_times=1; pgv_pvid=8783799650; pgv_info=ssid=s8528856981; qz_screen=1366x768; QZ_FE_WEBP_SUPPORT=1; 592158826_todaycount=0; 592158826_totalcount=143; blabla=dynamic; Loading=Yes; qzspeedup=sdch; cpu_performance_v8=8");
		final CloseableHttpClient build = HttpClients.custom().build();

		String entityString = null;
		try {
			CloseableHttpResponse execute = build.execute(get);
			final HttpEntity entity = execute.getEntity();
			entityString = EntityUtils.toString(entity, "utf-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		// TODO
		if (entityString.matches("^_Callback[\\W|\\w]*")) {
			System.out.println(entityString);
			final String jsonString = entityString.replaceAll("^_Callback\\(", "").replaceAll("[\\W|\\w]\\);$", "");
			System.out.println(JSON.toJSONString(JSON.parseObject(jsonString), true));
		} else {

			// final Document parse = Jsoup.parse(entityString);
			// final Elements li = parse.select("#feed_friend_list > li");
			// li.forEach(item -> {
			// String name = item.select("div.f-user-info a").first().text();
			// System.out.println(item.select("div.f-wrap div.f-info"));
			// System.out.println(name);
			// });
		}
	}

	//

	public static void getLogin() {
		//
		HttpGet get = new HttpGet(
				"http://user.qzone.qq.com/p/r/cgi-bin/right_frame.cgi?uin=592158826&param=3_592158826_0%7C14_592158826%7C8_8_592158826_0_1_0_0_1%7C10%7C11%7C12%7C13_0%7C17%7C20%7C9_0_8_1%7C18&g_tk=182713183");
		get.addHeader("cookie",
				"hasShowWeiyun592158826=1; __Q_w_s__QZN_TodoMsgCnt=1; qzspeedup=sdch; pgv_pvid=227637875; pgv_info=ssid=s2349768370; pt2gguin=o0592158826; uin=o0592158826; skey=@rsUJsRPMI; p_uin=o0592158826; p_skey=WbeXQbargEROOZ0zW92xE27lyXggfLHtQfSnLOWo3iI_; pt4_token=T4JaOJ3pWzLJtAG2hNM6mw__; a2404_pages=19; a2404_times=1; qz_screen=1366x768; 592158826_todaycount=0; 592158826_totalcount=143; QZ_FE_WEBP_SUPPORT=1; cpu_performance_v8=14");
		final CloseableHttpClient build = HttpClients.custom().build();

		String entityString = null;
		try {
			CloseableHttpResponse execute = build.execute(get);
			final HttpEntity entity = execute.getEntity();

			// execute.getProtocolVersion()
			entityString = EntityUtils.toString(entity, "utf-8");
			System.out.println(JSON.toJSONString(execute.getAllHeaders(), true));
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(entityString);
	}

	// new ScriptEngineManager();
	public static void main(String[] args) {
		// login();
		// getLogin();
		// qzone("http://user.qzone.qq.com/592158826");
		// ==========================================
		Map<String, String> map = new HashMap<String, String>();
		// map.put("uin", "592158826");
		map.put("scope", "0");
		map.put("view", "1");
		map.put("daylist", "");
		map.put("uinlist", "");
		map.put("gid", "");
		map.put("flag", "1");
		map.put("filter", "all");
		map.put("applist", "all");
		map.put("refresh", "0");
		map.put("aisortEndTime", "0");
		map.put("aisortOffset", "0");
		map.put("getAisort", "0");
		map.put("aisortBeginTime", "0");
		map.put("pagenum", "2");
		map.put("externparam",
				"basetime%3D1438904667%26pagenum%3D2%26dayvalue%3D0%26recomed%3D1107181534_311_0_de3ffe4115bfc15537ed0400%7C");
		map.put("firstGetGroup", "0");
		map.put("icServerTime", "0");
		map.put("mixnocache", "0");
		map.put("scene", "0");
		map.put("begintime", "1438904667");
		map.put("count", "10");
		map.put("dayspac", "0");
		map.put("sidomain", "cn.qzonestyle.gtimg.cn");
		map.put("useutf8", "1");
		map.put("outputhtmlfeed", "1");
		map.put("rd", "0.42960976739414036");
		map.put("g_tk", "182713183");

		// final String params =
		// Joiner.on("&").withKeyValueSeparator("=").join(map);
		// System.out.println(params);
		qzone("http://user.qzone.qq.com/p/ic2.s8/cgi-bin/feeds/feeds3_html_more?uin=592158826&scope=0&view=1&daylist=&uinlist=&gid=&flag=1&filter=all&applist=all&refresh=0&aisortEndTime=0&aisortOffset=0&getAisort=0&aisortBeginTime=0&pagenum=2&externparam=basetime%3D1438904667%26pagenum%3D2%26dayvalue%3D0%26recomed%3D1107181534_311_0_de3ffe4115bfc15537ed0400%7C&firstGetGroup=0&icServerTime=0&mixnocache=0&scene=0&begintime=1438904667&count=10&dayspac=0&sidomain=cn.qzonestyle.gtimg.cn&useutf8=1&outputhtmlfeed=1&rd=0.42960976739414036&g_tk=1017010202");

		// System.out.println("_Callback({\n ".matches("_Callback(\n|.)*"));
	}
}
