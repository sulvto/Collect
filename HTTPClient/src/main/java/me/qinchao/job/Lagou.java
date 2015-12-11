package me.qinchao.job;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.MongoDatabase;

import me.qinchao.Util;

/**
 * http://www.lagou.com
 * 
 * @author QinChao
 *
 */
public class Lagou {
	final static CookieStore cookieStore = new BasicCookieStore();
	final static CloseableHttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
	final static String BASEPATH = "C:/Users/acer1/Desktop/job/lagou";
	final static String JOBSPATH = BASEPATH+File.separator+"jobs";
	final static String COMPANYPATH = BASEPATH+File.separator+"company";
	
	static {
		if (!new File(JOBSPATH).exists()) {
			new File(JOBSPATH).mkdirs();
		}
		if (!new File(COMPANYPATH).exists()) {
			new File(COMPANYPATH).mkdirs();
		}
	}

	private static void gongsi() {
		int sn = -1;
		int countA = -1;
		int countB = 0;
		boolean start = false;
		List<Map> list = new ArrayList<>();
		while (true) {
			if (sn > 10000000) {
				break;
			}
			sn++;
			HttpGet httpGet = new HttpGet("http://www.lagou.com/gongsi/" + sn + ".html");
			
			CloseableHttpResponse execute;
			try {
				execute = httpClient.execute(httpGet);
				HttpEntity entity = execute.getEntity();
				String html = EntityUtils.toString(entity);
				Document document = Jsoup.parse(html);
				Elements containerEles = document.select("div#content-container");
				if (containerEles.isEmpty()) {
					if (countA > 1000) {
						break;
					} else {
						if (start)
							countA++;
					}

					if (document.select("div#container div.position_del").isEmpty()) {
						Util.writerFile(COMPANYPATH+File.separator+"error.txt", sn + "---empty"+"\r\n", true);
					} else {
						Util.writerFile(COMPANYPATH+File.separator+"null.txt", sn + "---null"+"\r\n", true);
					}
					System.out.println(sn + " A-"+countA+" B-"+countB+" --> null");

					continue;
				} else {
					countB++;
					countA = 0;
					if (!start)
						start = true;
				}
				Map<String, String> map = new HashMap<>();
				Element first = containerEles.select("h1.ellipsis a").first();
				map.put("url", first.attr("href"));
				final String name = first.text();
				map.put("name", name);
				map.put("companyName", first.attr("title"));
				map.put("link", httpGet.getURI().toString());
				System.out.println(sn + " A-"+countA+" B-"+countB+" --> " + name);
				Util.writerFile(COMPANYPATH+File.separator+"company.txt", JSON.toJSONString(map)+"\r\n", true);
//				list.add(map);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				Util.writerFile(COMPANYPATH+File.separator+"exception.txt", sn + "---" + e.getMessage()+"\r\n", true);
			} catch (IOException e) {
				e.printStackTrace();
				Util.writerFile(COMPANYPATH+File.separator+"exception.txt", sn + "---" + e.getMessage()+"\r\n", true);
			}
			
		}
		
		
	}
	private static void jobs() {
		int sn = -1;
		int countA = -1;
		int countB = 0;
		boolean start = false;
		List<Map> list = new ArrayList<>();
		while (true) {
			if (sn > 10000000) {
				break;
			}
			sn++;
			HttpGet httpGet = new HttpGet("http://www.lagou.com/jobs/" + sn + ".html");
			
			try {
				CloseableHttpResponse execute = httpClient.execute(httpGet);
				HttpEntity entity = execute.getEntity();
				String html = EntityUtils.toString(entity);
				
				Document document = Jsoup.parse(html);
				Elements clearfixEles = document.select("div#container > div.clearfix");
				if (clearfixEles.isEmpty()) {
					if (countA > 3000) {
						break;
					} else {
						if (start)
							countA++;
					}

					if (document.select("div#container div.position_del").isEmpty()) {
						Util.writerFile(JOBSPATH+File.separator+"error.txt", sn + "---empty"+"\r\n", true);
					} else {
						Util.writerFile(JOBSPATH+File.separator+"null.txt", sn + "---null"+"\r\n", true);
					}
					System.out.println(sn + " A-"+countA+" B-"+countB+" --> null");

					continue;
				} else {
					countB++;
					countA = 0;
					if (!start)
						start = true;
				}
				Map<String, String> map = new HashMap<>();
				Element h1Ele = clearfixEles.select("dl.job_detail h1").first();
				map.put("job", h1Ele.attr("title").trim());
				Element first2 = clearfixEles.select("div.content_r > dl.job_company img.b2").first();
				map.put("companyName", first2.attr("alt").trim());
				map.put("jobid", document.select("#jobid").attr("value").trim());
				map.put("companyid", document.select("#companyid").attr("value").trim());
				map.put("link", httpGet.getURI().toString());
				System.out.println(sn + " A-"+countA+" B-"+countB+" --> " + first2.attr("alt"));
				Util.writerFile(JOBSPATH+File.separator+"job.txt", JSON.toJSONString(map)+"\r\n", true);
//				list.add(map);

				execute.close();
			} catch (ClientProtocolException e) {
				Util.writerFile(JOBSPATH+File.separator+"exception.txt", sn + "---" + (e.getCause()!=null?e.getCause().getMessage():e.getMessage())+"\r\n", true);
			} catch (IOException e) {
				Util.writerFile(JOBSPATH+File.separator+"exception.txt", sn + "---" + (e.getCause()!=null?e.getCause().getMessage():e.getMessage())+"\r\n", true);
			}
		}
		
		
	}
	
	
	//TODO
	public static void spiderByUrl(String url){
		HttpGet httpGet = new HttpGet(url);
		CloseableHttpResponse execute;
		try {
			execute = httpClient.execute(httpGet);
			HttpEntity entity = execute.getEntity();
			String html = EntityUtils.toString(entity);
			Document document = Jsoup.parse(html);
			final Elements titleEle = document.select("#container > div.clearfix > div.content_l.content_l_jobdetail > dl.job_detail > dt > h1");
			if(titleEle.isEmpty()){
				//TODO logger
				return;
			}
			final String text = titleEle.first().text();
			
			Elements jobBtEle = document.select("#container > div.clearfix > div.content_l.content_l_jobdetail > dl.job_detail > dd.job_bt");
			final String jobBtHtml = jobBtEle.first().html();
			Element jobRequestEle = document.select("#container > div.clearfix > div.content_l.content_l_jobdetail > dl.job_detail > dd.job_request").first();
			String jobRequest = jobRequestEle.html();
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

	public static void main(String[] args) {
//		gongsi();
		jobs();
	}

}
