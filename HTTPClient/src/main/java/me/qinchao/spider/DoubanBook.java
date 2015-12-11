package me.qinchao.spider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.alibaba.fastjson.JSON;

import me.qinchao.HttpClientUtil;
import me.qinchao.Util;

/**
 * 豆瓣读书 http://book.douban.com/subject/+++/comments/hot?p=1
 * 
 * @author QinChao
 *
 */
public class DoubanBook {
	final static String baseUrl = "http://book.douban.com/subject/";
	final static String basePath = "C:/Users/acer1/Desktop/";

	final static String DOUBAN_BOOK_PATH = "douban_book";
	final static String BOOK_INFO_JSON = "bookinfo.json";

	final static CookieStore cookieStore = new BasicCookieStore();
	final static CloseableHttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();

	static {
		File file = new File(basePath + File.separator + DOUBAN_BOOK_PATH);
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	public static void httpRequestHome(String id) {

		final String html = HttpClientUtil.get(baseUrl);
		final Document document = Jsoup.parse(html);
		final Elements wrapperEle = document.select("div#wrapper");
		final Element spenEle = wrapperEle.select("h1 > span").first();
		final String bookName = spenEle.ownText().trim();
		System.out.println("bookName: " + bookName);
		System.out.println("=================book info=================");
		Elements infoEle = wrapperEle.select("div#info");
		System.out.println(infoEle.html());

		System.out.println("=================comment list=================");
		final Elements commentListWrapperEles = document.select("div#comment-list-wrapper");
		System.out.println(commentListWrapperEles);

	}

	public static List httpRequestComments(String bookId) {
		final List<Map> commentList = new ArrayList<Map>();
		int totalComments = Integer.MAX_VALUE;
		for (int i = 1; i < Integer.MAX_VALUE; i++) {

			HttpGet get = new HttpGet(baseUrl + bookId + "/comments/hot?p=" + i);
			get.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
			get.addHeader("Accept-Encoding", "gzip, deflate");
			get.addHeader("Accept-Language", "zh-CN,zh;q=0.8");
			get.addHeader("Connection", "keep-alive");
			get.addHeader("DNT", "1");
			get.addHeader("If-Modified-Since", "Thu, 06 Aug 2015 17:04:55 GMT");
			get.addHeader("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.115 Safari/537.36");
			String html = null;
			try (CloseableHttpResponse response = httpClient.execute(get);) {
				final HttpEntity entity = response.getEntity();
				html = EntityUtils.toString(entity, "utf-8");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Document document = Jsoup.parse(html);

			Elements wrapperEles = document.select("div#wrapper");

			Elements commentItemEles = wrapperEles.select("li.comment-item");
			if (commentItemEles.isEmpty()) {
				continue;
			}
			final Elements totalCommentsEles = wrapperEles.select("span#total-comments");

			String totalCommentStr = totalCommentsEles.first().ownText();

			if (totalComments == Integer.MAX_VALUE) {
				Pattern pattern = Pattern.compile("\\w+");
				Matcher matcher = pattern.matcher(totalCommentStr);
				if (matcher.find()) {
					totalComments = Integer.parseInt(matcher.group(0));
				}
			}
			commentItemEles.forEach(e -> {
				Map<String, String> map = new HashMap<String, String>();
				map.put("avatar_title", e.select("div.avatar >a ").first().attr("title"));
				map.put("avatar_url", e.select("div.avatar >a ").first().attr("href"));
				map.put("comment-info", e.select("span.comment-info > span").first().attr("title"));
				map.put("comment-date", e.select("span.comment-info > span").last().ownText());
				map.put("vote-count", e.select("span.vote-count").first().ownText());
				map.put("comment-content", e.select("p.comment-content").first().ownText());
				commentList.add(map);
			});
			System.out.println("第 " + i + " 页     " + "已搜集 " + commentList.size());
			if (commentList.size() >= totalComments) {
				break;
			}
		}

		Util.writerFile(basePath + File.separator + DOUBAN_BOOK_PATH + File.separator + "26275861_comments.json", JSON.toJSONString(commentList, true), false);
		return commentList;
	}

	

	private static void getCookies() {
		HttpGet httpGet = new HttpGet("http://book.douban.com");
		try {
			CloseableHttpResponse execute = httpClient.execute(httpGet);
			if (execute.getStatusLine().getStatusCode() == 200) {
				List<Cookie> cookies = cookieStore.getCookies();
				Cookie cookie = cookies.get(0);
				cookieStore.addCookie(cookie);
			} else {
				throw new RuntimeException(execute.getStatusLine().getReasonPhrase());
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static List<Map> searchBook(String bookName) {
		CloseableHttpResponse response = null;
		List<Map> list = new ArrayList<>();
		try {
			for (int i = 0; i < Integer.MAX_VALUE;) {
				HttpGet httpGet = new HttpGet("http://book.douban.com/subject_search?start=" + i + "&search_text="
						+ URLEncoder.encode(bookName));
				response = httpClient.execute(httpGet);
				HttpEntity entity = response.getEntity();
				String html = EntityUtils.toString(entity, "utf-8");
				Document document = Jsoup.parse(html);
				Elements wrapper = document.select("div#wrapper");
				// System.out.println(wrapper);
				Elements subjectItems = wrapper.select("li.subject-item");
				// System.out.println(subjectItem);
				int itemSize = subjectItems.size();
				System.out.println("当前结果数: " + itemSize);
				if (itemSize < 1) {
					break;
				} else {
					i += itemSize;
				}
				subjectItems.forEach(item -> {
					Element infoEle = item.select("div.info").first();
					Element aEle = infoEle.select("h2 > a").first();
					Map map = new HashMap<>();
					map.put("url", aEle.attr("href"));
					String ownText = aEle.text().trim();
					System.out.println("找到——> "+ownText);
					map.put("text", ownText);
					list.add(map);
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return list;
	}
	
	// 10484692
	public static void main(String[] args) {
		getCookies();

		// httpRequestHome();
		// httpRequestComments("26275861");
		List<Map> searchBook = searchBook("java");
		System.out.println(JSON.toJSONString(searchBook, true));
		Util.writerFile(basePath + File.separator + DOUBAN_BOOK_PATH + File.separator +BOOK_INFO_JSON, JSON.toJSONString(searchBook,true), false);
		System.out.println("搜索结果数：" + searchBook.size());
	}

}
