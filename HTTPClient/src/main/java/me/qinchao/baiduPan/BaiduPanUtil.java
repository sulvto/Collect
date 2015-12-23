package me.qinchao.baiduPan;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import junit.framework.Assert;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.Asserts;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;
import java.util.*;
import java.util.logging.Logger;

public class BaiduPanUtil {

    public final static Header User_Agent = new BasicHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.36");

    private final static CookieStore cookieStore = new BasicCookieStore();

    private final static CloseableHttpClient httpClient = HttpClients.custom()
            .setDefaultCookieStore(cookieStore)
//			.setProxy(new HttpHost("118.114.77.47",8080))
            .build();
    private final static String tempFilePath = BaiduPanUtil.class.getClass()
            .getResource("/").getPath()
            + File.separator + "temp";

    public static void main(String[] args) {

        readCookiesToStore();
//        login("", "");
        getPanHomeHtmlSource();
        JSONObject loginInfo = parsePanHome(BaiduPanUtil.getPanHomeHtmlSource());

//        writeCookiesToFile();
        JSONObject fileList = BaiduPanUtil.apiList("/源代码", (String) loginInfo.get("bdstoken"));

        System.out.println(JSON.toJSONString(fileList, true));

    }

    private static void writeCookiesToFile() {
        if (cookieStore.getCookies() != null && cookieStore.getCookies().size() > 0) {

        } else {
            System.out.println("Cookies is Empty");
            return;
        }
        File tempPathFile = new File(tempFilePath);
        if (!tempPathFile.exists()) {
            tempPathFile.mkdirs();
        }
        File tempFile = new File(tempFilePath + File.separator + "cookies.txt");
        try {
            tempFile.createNewFile();
            Files.write(JSON.toJSONString(cookieStore, true), tempFile,
                    Charsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void readCookiesToStore() {

        File tempFile = new File(tempFilePath + File.separator + "cookies.txt");
        if (tempFile.exists()) {
            try {
                String cookieString = Files.toString(tempFile, Charsets.UTF_8);
                cookieStore.clear();

                JSONObject oldCookieStore = JSON.parseObject(cookieString);
//				if (oldCookieStore != null&&oldCookieStore.getCookies()!=null&&oldCookieStore.getCookies().size()>0){
                if (oldCookieStore.containsKey("cookies")) {
                    JSONArray jsonArray = (JSONArray) oldCookieStore.get("cookies");
//						System.out.println("history cookies size"+oldCookieStore.getCookies().size());
//						oldCookieStore.getCookies().forEach(cookieStore::addCookie);
                    jsonArray.forEach(v -> {
                        JSONObject jsonObject = (JSONObject) v;
                        BasicClientCookie basicClientCookie = new BasicClientCookie((String) jsonObject.get("name"), (String) jsonObject.get("value"));
                        if (jsonObject.containsKey("domain"))
                            basicClientCookie.setDomain((String) jsonObject.get("domain"));
                        if (jsonObject.containsKey("path"))
                            basicClientCookie.setPath((String) jsonObject.get("path"));
                        if (jsonObject.containsKey("version"))
                            basicClientCookie.setVersion((Integer) jsonObject.get("version"));
                        if (jsonObject.containsKey("expiryDate"))
                            basicClientCookie.setExpiryDate(new Date((Long) jsonObject.get("expiryDate")));
                        if (jsonObject.containsKey("creationDate"))
                            basicClientCookie.setCreationDate(new Date((Long) jsonObject.get("creationDate")));
                        if (jsonObject.containsKey("secure"))
                            basicClientCookie.setSecure((Boolean) jsonObject.get("secure"));

                        cookieStore.addCookie(basicClientCookie);
                    });
                } else {
                    System.out.println("history cookies is Empty");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String rsaEncrypt(String content, String key) throws FileNotFoundException, ScriptException {

        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        ScriptEngine engine = scriptEngineManager.getEngineByName("nashorn");
        try {
            engine.eval(new FileReader(
                    "/home/qinchao/development/workspace/java/JavaTest/HTTPClient/js/login_0f57343e.js"));
            Invocable invocable = (Invocable) engine;
            String invokeFunction = (String) invocable.invokeFunction(
                    "myEncrypt", content, key);

            System.out.println("rsaEncrypt ==> " + invokeFunction);
            return invokeFunction.trim();
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

        try {
            httpClient.execute(httpPost);
            List<Cookie> cookies = cookieStore.getCookies();
            Optional<Cookie> findFirst = cookies.stream()
                    .filter(v -> {
                        return "BAIDUID".equals(v.getName());
                    }).findFirst();
            return findFirst.isPresent() ? findFirst.get().getValue() : "";
        } catch (IOException e) {
        }
        return "";
    }

    /**
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

        try {
            HttpResponse response = httpClient.execute(httpPost);
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
     * @param baiduid {@see getBAIDUID()}
     * @param gid     {@see guideRandom()}
     */
    public static String getToken(String baiduid, String gid) {
        Assert.assertFalse(Strings.isNullOrEmpty(baiduid));
        Assert.assertFalse(Strings.isNullOrEmpty(gid));
        String url = "https://passport.baidu.com/v2/api/?getapi&tpl=mn&apiver=v3&tt=1450621860252&class=login&gid="
                + gid + "&logintype=dialogLogin&callback=bd__cbs__p676ul";
        HttpGet httpPost = new HttpGet(url);
        httpPost.addHeader("Cookie", "BAIDUID=" + baiduid + ";");

        String token = "";
        try {
            HttpResponse response = httpClient.execute(httpPost);
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

    public static void login(String username, String password) {

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

        try {
            password = rsaEncrypt(password, (String) rsaKey.get("pubkey"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull("password encrypt failed ", password);

        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("apiver", "v3"));
        nvps.add(new BasicNameValuePair("callback", "parent.bd__pcbs__rytbz"));
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
        nvps.add(new BasicNameValuePair("ppui_logintime", "6266"));
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
        httpPost.addHeader("Host", "passport.baidu.com");
        httpPost.addHeader("Referer", "https://www.baidu.com/");
//		httpPost.addHeader("Cookie", "BAIDUID=" + baiduid+";");
        httpPost.addHeader(User_Agent);
        String entityString = null;
        try {
            CloseableHttpResponse execute = httpClient.execute(httpPost);
            final HttpEntity entity = execute.getEntity();
            entityString = EntityUtils.toString(entity, "UTF-8");

            if (entityString.indexOf("err_no=0") != -1) {
                System.out.println("login success ");
            } else {
                System.out.println(entityString);
            }
            // err_no=257 验证码
            // err_no=4 密码错误

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
     * @param token
     * @param gid
     * @return Map <String,String>
     */
    public static Map panList(String token, String gid) {
        Assert.assertFalse(Strings.isNullOrEmpty(token));
        Assert.assertFalse(Strings.isNullOrEmpty(gid));
        String url = "//https://pan.baidu.com/api/list?order=name&desc=0&showempty=0&web=1&page=1&num=100&dir=%2F&t=0.7630364120973585&bdstoken=56b52250de0b27b349d2481d154dac3f&channel=chunlei&clienttype=0&web=1&app_id=250528";
        HttpGet httpPost = new HttpGet(url);

        try {
            HttpResponse response = httpClient.execute(httpPost);
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
     * 必须存在登录baidu.com后的cookie
     *
     * @return Charset UTF_8 Html Source
     */
    public static String getPanHomeHtmlSource() {
        String url = "http://pan.baidu.com/disk/home";
        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader(User_Agent);
        Header cookie = cookie2Header();
        Assert.assertNotNull("cookie is null", cookie);
        httpGet.addHeader(cookie);

        try {
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity, Charsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * HttpGet <a href='http://pan.baidu.com/api/list'>http://pan.baidu.com/api/list </a><br/>
     * 获取指定目录下的文件信息
     * 必须存在登录<a href=‘baidu.com’>baidu.com</a>后的cookie
     *
     * @return JSONObject
     */
    public static JSONObject apiList(String dir, String bdstoken) {
        //TODO page num ...
        String url = "http://pan.baidu.com/api/list?order=name&desc=1&showempty=0&web=1&page=1&num=100&dir=" + dir + "&t=0.19144635694101453&bdstoken=" + bdstoken + "&channel=chunlei&clienttype=0&web=1";
        HttpGet httpGet = new HttpGet(url);

        httpGet.addHeader("Host", "pan.baidu.com");
        httpGet.addHeader("Connection", "keep-alive");
        httpGet.addHeader("Cache-Control", "max-age=0");
        httpGet.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");
        httpGet.addHeader("X-Requested-With", "XMLHttpRequest");
        httpGet.addHeader("Referer", "http://pan.baidu.com/disk/home");
        httpGet.addHeader("Accept-Language", "zh-CN,zh;q=0.8");
        httpGet.addHeader(User_Agent);
        Header cookie = cookie2Header();
        Assert.assertNotNull("cookie is null", cookie);
        httpGet.addHeader(cookie);

        try {
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String entityString = EntityUtils.toString(entity, Charsets.UTF_8);
//            JSONString
            System.out.println(entityString);
            return JSON.parseObject(entityString);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * HttpGet <a href='http://pan.baidu.com/api/list'>http://pan.baidu.com/api/list </a><br/>
     * 获取指定目录下的文件信息
     * 必须存在登录<a href=‘baidu.com’>baidu.com</a>后的cookie
     *
     * @return JSONObject
     */
    public static JSONObject apiPrecreate(String dir, String bdstoken) {
        //TODO page num ...
        String url = "http://pan.baidu.com/api/precreate?bdstoken=" + bdstoken + "&channel=chunlei&clienttype=0&web=1&app_id=250528";
        HttpPost httpPost = new HttpPost(url);

        httpPost.addHeader("Origin", "http://pan.baidu.com");
        httpPost.addHeader("Host", "pan.baidu.com");
        httpPost.addHeader("Connection", "keep-alive");
        httpPost.addHeader("Accept", "*/*");
        httpPost.addHeader("X-Requested-With", "XMLHttpRequest");
        httpPost.addHeader("Referer", "http://pan.baidu.com/disk/home");
        httpPost.addHeader("Accept-Language", "zh-CN,zh;q=0.8");
        httpPost.addHeader(User_Agent);
        Header cookie = cookie2Header();
        Assert.assertNotNull("cookie is null", cookie);
        httpPost.addHeader(cookie);


        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("apiver", "v3"));
        nvps.add(new BasicNameValuePair("path", "/cookies.txt"));
        nvps.add(new BasicNameValuePair("autoinit", "1"));
        //block_list在 http://s1.pan.bdstatic.com/box-static/disk-system-cdn/pkg/plugin-upload_b52213f.js 文件中  search==> o='[
        nvps.add(new BasicNameValuePair("block_list", "[\"5910a591dd8fc18c32a8f3df4fdc1761\"]"));

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String entityString = EntityUtils.toString(entity, Charsets.UTF_8);
//            JSONString
            System.out.println(entityString);
            return JSON.parseObject(entityString);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * get cookie value by name if isPresent
     *
     * @param name
     * @return
     */
    private String getCookie(String name) {
        if (Strings.isNullOrEmpty(name)) {
            throw new IllegalArgumentException();
        }
        Optional<Cookie> first = cookieStore.getCookies().stream().filter(cookie -> name.equals(cookie.getName())).findFirst();
        if (first.isPresent()) {
            return first.get().getValue();
        }
        return null;
    }

    /**
     * @return new BasicHeader("Cookie", "..."): <br/> or null if cookieStore is empty;
     */
    private static Header cookie2Header() {
        List<Cookie> cookieList = cookieStore.getCookies();
        if (cookieList == null || cookieList.size() < 1) {
            return null;
        }

        //Header value
        Optional<String> stringOptional = cookieList
                .stream()
                .map(cookie -> cookie.getName() + "=" + cookie.getValue() + "; ")
                .reduce(String::concat);
        return stringOptional.isPresent() ? new BasicHeader("Cookie", stringOptional.get()) : null;
    }

    /**
     * @return JSONObject
     */
    public static JSONObject parsePanHome(String htmlSource) {
        String jsonString = htmlSource.replaceFirst("[\\w|\\W]+yunData.setData\\(\\{", "{").replaceFirst("}\\);[\\w|\\W]+", "}").trim();
        String plugin_upload_url = htmlSource.replaceFirst("[\\w|\\W]+\"disk-system:p5\":\\{\"url\":\"", "").replaceFirst("\\.js\"}[\\w|\\W]+", ".js").trim();
        System.out.println(plugin_upload_url);

        System.out.println(jsonString);

        JSONObject jsonObject = JSON.parseObject(jsonString);
        if (plugin_upload_url.length() > 0)
            jsonObject.put("pluginUploadUrl", plugin_upload_url);

        return jsonObject;

    }


}
