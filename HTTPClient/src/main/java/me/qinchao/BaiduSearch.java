package me.qinchao;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;

public class BaiduSearch {
	
	public static JSONObject search(String wd) {
		final String params = Joiner.on("&").withKeyValueSeparator("=").join(ImmutableMap.of("wd",wd));
		System.out.println(params);
		final String html = HttpClientUtil.get("https://www.baidu.com/s?"+params);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("html", html);
		
		return	jsonObject;
	}
}
