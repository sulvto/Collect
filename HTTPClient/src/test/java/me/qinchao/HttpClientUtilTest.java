package me.qinchao;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import com.alibaba.fastjson.JSONObject;

public class HttpClientUtilTest {

	@Test
	public void testHttpClientUtil() {
		final String baseUrl = "http://www.iteye.com/news";
		final String html = HttpClientUtil.get(baseUrl);
		//
		final Document document = Jsoup.parse(html);
		final Elements indexMain = document.select("div#index_main div.content");
		List<JSONObject> list = new ArrayList<JSONObject>();

		indexMain.parallelStream().forEach(e->{

			final Element aEl = e.select("a[href~=/news/[0-9]+").first();

			final String title = aEl.attr("title");
			final String href = aEl.attr("href");

			JSONObject jsonObject = new JSONObject();
			list.add(jsonObject);
			jsonObject.put("title", title);
			jsonObject.put("href", baseUrl+href);
		});
		
		System.out.println(list);
	}

	
	@Test
	public void testQzone() {
		final String html = HttpClientUtil.get("http://user.qzone.qq.com/592158826");
		System.out.println(html);
	}
	
}
