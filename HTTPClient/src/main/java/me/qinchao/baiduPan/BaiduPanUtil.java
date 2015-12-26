package me.qinchao.baiduPan;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import junit.framework.Assert;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.FormBodyPartBuilder;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.LoggerFactory;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class BaiduPanUtil {
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(BaiduPanUtil.class);
    public final static Header User_Agent = new BasicHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.36");

    private final static CookieStore cookieStore = new BasicCookieStore();

    private final static CloseableHttpClient httpClient = HttpClients.custom()
            .setDefaultCookieStore(cookieStore)
//			.setProxy(new HttpHost("118.114.77.47",8080))
            .build();
    private final static String tempFilePath = BaiduPanUtil.class.getClass()
            .getResource("/").getPath()
            + File.separator + "temp";

    public static void main(String[] args) throws IOException {

//        readCookiesToStore();
        login("", "");

//        uploadFile("/home/xxx/下载/node-v4.2.3/android-configure", "/test/android-configure.txt");

        String dlink = getDlink("831325154488496");
        System.out.println(dlink);
        writeCookiesToFile();
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

    public static String getBAIDUID() throws IOException {

        String url = "http://pan.baidu.com/#list/path=%2F";
        HttpGet httpPost = new HttpGet(url);
        clientExecute(httpPost);
        httpClient.execute(httpPost);
        List<Cookie> cookies = cookieStore.getCookies();
        Optional<Cookie> findFirst = cookies.stream()
                .filter(v -> {
                    return "BAIDUID".equals(v.getName());
                }).findFirst();
        return findFirst.isPresent() ? findFirst.get().getValue() : "";
    }

    /**
     * @param token
     * @param gid
     * @return Map <String,String>
     */
    public static Map getPublicKey(String token, String gid) throws IOException {
        Assert.assertFalse(Strings.isNullOrEmpty(token));
        Assert.assertFalse(Strings.isNullOrEmpty(gid));
        String url = "https://passport.baidu.com/v2/getpublickey?token="
                + token + "&tpl=mn&apiver=v3&tt=" + new Date().getTime()
                + "&gid=" + gid + "&callback=bd__cbs__k9cuw0";
        HttpGet httpPost = new HttpGet(url);

        String entityString = clientExecute(httpPost);
        // to json and replace(',")
        String jsonObject = entityString.replaceFirst(".*\\(", "")
                .replaceFirst("\\)$", "").replaceAll("\":'", "\":\"")
                .replaceAll("',\"", "\",\"").replaceAll("'}", "\"}")
                .replaceAll("']", "\"]");
        System.out.println("getPublicKey ==>  " + jsonObject);
        JSONObject parseObject = JSON.parseObject(jsonObject);
        return parseObject.containsKey("key") ? parseObject : null;
    }

    /**
     * get baidu toke
     *
     * @param baiduid {@see getBAIDUID()}
     * @param gid     {@see guideRandom()}
     */
    public static String getToken(String baiduid, String gid) throws IOException {
        Assert.assertFalse(Strings.isNullOrEmpty(baiduid));
        Assert.assertFalse(Strings.isNullOrEmpty(gid));
        String url = "https://passport.baidu.com/v2/api/?getapi&tpl=mn&apiver=v3&tt=1450621860252&class=login&gid="
                + gid + "&logintype=dialogLogin&callback=bd__cbs__p676ul";
        HttpGet httpPost = new HttpGet(url);
        httpPost.addHeader("Cookie", "BAIDUID=" + baiduid + ";");

        String token = "";

        String entityString = clientExecute(httpPost);
        // bd__cbs__p676ul({"errInfo":{ "no": "0" }, "data": {
        // "rememberedUserName" : "", "codeString" : "", "token" :
        // "the fisrt two args should be string type:0,1!", "cookie" : "0",
        // "usernametype":"", "spLogin" : "rate", "disable":"",
        // "loginrecord":{ 'email':[ ], 'phone':[ ] } }})

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
        return token;
    }

    public static void login(String username, String password) throws IOException {

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
        httpPost.setEntity(new UrlEncodedFormEntity(nvps, Charsets.UTF_8));

        httpPost.addHeader("Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        httpPost.addHeader("Accept-Encoding", "gzip, deflate");
        httpPost.addHeader("Accept-Language", "zh-CN,zh;q=0.8");
        httpPost.addHeader("Connection", "keep-alive");
        httpPost.addHeader("Host", "passport.baidu.com");
        httpPost.addHeader("Referer", "https://www.baidu.com/");
//		httpPost.addHeader("Cookie", "BAIDUID=" + baiduid+";");
        httpPost.addHeader(User_Agent);

        String entityString = clientExecute(httpPost);

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
    }

    /**
     * @param token
     * @param gid
     * @return Map <String,String>
     */
    public static Map panList(String token, String gid) throws IOException {
        Assert.assertFalse(Strings.isNullOrEmpty(token));
        Assert.assertFalse(Strings.isNullOrEmpty(gid));
        String url = "//https://pan.baidu.com/api/list?order=name&desc=0&showempty=0&web=1&page=1&num=100&dir=%2F&t=0.7630364120973585&bdstoken=56b52250de0b27b349d2481d154dac3f&channel=chunlei&clienttype=0&web=1&app_id=250528";
        HttpGet httpPost = new HttpGet(url);
        String entityString = clientExecute(httpPost);
        // to json and replace(',")
        String jsonObject = entityString.replaceFirst(".*\\(", "")
                .replaceFirst("\\)$", "").replaceAll("\":'", "\":\"")
                .replaceAll("',\"", "\",\"").replaceAll("'}", "\"}")
                .replaceAll("']", "\"]");
        System.out.println("getPublicKey ==>  " + jsonObject);
        JSONObject parseObject = JSON.parseObject(jsonObject);
        return parseObject.containsKey("key") ? parseObject : null;
    }


    /**
     * 必须存在登录baidu.com后的cookie
     *
     * @return Charset UTF_8 Html Source
     */
    public static String getPanHomeHtmlSource() throws IOException {
        String url = "http://pan.baidu.com/disk/home";
        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader(User_Agent);
        Header cookie = cookie2Header();
        Assert.assertNotNull("cookie is null", cookie);
        httpGet.addHeader(cookie);

        return clientExecute(httpGet);

    }

    /**
     * HttpGet <a href='http://pan.baidu.com/api/list'>http://pan.baidu.com/api/list </a><br/>
     * 获取指定目录下的文件信息
     * 必须存在登录<a href=‘baidu.com’>baidu.com</a>后的cookie
     *
     * @return JSONObject
     */
    public static JSONObject apiList(String dir, String bdstoken) throws IOException {
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

        String entityString = clientExecute(httpGet);
        return JSON.parseObject(entityString);
    }

    /**
     * HttpGet <a href='http://pan.baidu.com/api/precreate'>http://pan.baidu.com/api/precreate</a><br/>
     * <p>
     * 上传文件前<br/>
     * <p>
     * 必须存在登录<a href=‘baidu.com’>baidu.com</a>后的cookie
     *
     * @param path     "/cookies.txt"
     * @param bdstoken
     * @return JSONObject {"path":"\/cookies.txt","uploadid":"N1-MTAzLjI0LjE4NS4yNTI6MTQ1MDkzNjE1Mjo4MjkzMTkzNDA5OTUwNDk0ODUx","return_type":1,"block_list":[],"errno":0,"request_id":8293193409950494851}
     */
    public static JSONObject apiPrecreate(String path, String bdstoken, String block) throws IOException {
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
        nvps.add(new BasicNameValuePair("path", path));
        nvps.add(new BasicNameValuePair("autoinit", "1"));
        //block_list在 http://s1.pan.bdstatic.com/box-static/disk-system-cdn/pkg/plugin-upload_b52213f.js 文件中  search==> o='[
        //TODO [5910a591dd8fc18c32a8f3df4fdc1761, a5fc157d78e6ad1c7e114b056c92821e]
        nvps.add(new BasicNameValuePair("block_list", "[\"" + block + "\"]"));

        httpPost.setEntity(new UrlEncodedFormEntity(nvps, Charsets.UTF_8));
        String entityString = clientExecute(httpPost);

        return JSON.parseObject(entityString);
    }

    /**
     * HttpPost <a href='http://c2.pcs.baidu.com/rest/2.0/pcs/superfile2'>http://c2.pcs.baidu.com/rest/2.0/pcs/superfile2</a><br/>
     * <p>
     * 上传文件<br/>
     *
     * @param filePath 要上传的文件路径
     * @param path     百度网盘存放路径 "/cookies.txt"
     * @param bduss
     * @param uploadid {@linkplain BaiduPanUtil#apiPrecreate(String, String, String)}
     * @return JSONObject {"md5":"f6c93536500fa6b4f62a70a8475df509","request_id":8303065634367135628}
     */
    public static JSONObject upload(String filePath, String path, String bduss, String uploadid) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IllegalArgumentException("file ==> " + filePath + "   文件不存在");
        }


        return upload(file, path, bduss, uploadid);
    }

    public static JSONObject upload(File file, String path, String bduss, String uploadid) throws IOException {
        System.out.println(path);
//        try {
//            path = URLEncoder.encode(path, Charsets.UTF_8.toString());
//            System.out.println(path);
//        } catch (UnsupportedEncodingException e) {
//            throw new IllegalArgumentException(e);
//        }
        String url = "http://c2.pcs.baidu.com/rest/2.0/pcs/superfile2?method=upload&app_id=250528&BDUSS=" + bduss + "&path=" + path + "&uploadid=" + uploadid + "&partseq=0";
        HttpPost httpPost = new HttpPost(url);

        httpPost.addHeader("Origin", "http://pan.baidu.com");
        httpPost.addHeader("Host", "pan.baidu.com");
        httpPost.addHeader("Connection", "keep-alive");
        httpPost.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        httpPost.addHeader("Referer", "http://pan.baidu.com/disk/home");
        httpPost.addHeader("Accept-Encoding", "gzip, deflate");
        httpPost.addHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
//        httpPost.addHeader("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundaryCvWNV6lTkBkYw2rQ");
        httpPost.addHeader(User_Agent);

        FormBodyPart bodyPart = FormBodyPartBuilder.create()
                .setName("file")
                .addField("Content-Disposition", "form-data; name=\"file\"; filename=\"blob\"")
                .addField("Content-Type", "application/octet-stream")
                .setBody(new FileBody(file))
                .build();
        HttpEntity httpEntity = MultipartEntityBuilder
                .create()
                .addPart(bodyPart)
                .build();
        httpPost.setEntity(httpEntity);

        String entityString = clientExecute(httpPost);
        return JSON.parseObject(entityString);
    }

    /**
     * HttpPost <a href='http://pan.baidu.com/api/create'>http://pan.baidu.com/api/create</a><br/>
     * <p>
     * 创建文件<br/>
     *
     * @param path     百度网盘存放路径 "/cookies.txt"
     * @param bdstoken
     * @param uploadid {@linkplain BaiduPanUtil#apiPrecreate(String, String, String)}
     * @param md5      update file md5
     * @param size     create file size
     * @return JSONObject {"path":"/test/cookies(2).txt","errno":0,"size":2837,"server_filename":"cookies(2).txt","name":"/test/cookies(2).txt","ctime":1451058390,"category":4,"mtime":1451058390,"fs_id":831325154488496,"isdir":0,"md5":"612bc2a142e9d5a050dec5b6df915ab4"}
     */
    public static JSONObject createFile(String path, String bdstoken, String uploadid, String md5, Long size) throws IOException {

        String url = "http://pan.baidu.com/api/create?isdir=0&rtype=1&bdstoken=" + bdstoken + "&channel=chunlei&clienttype=0&web=1&app_id=250528";
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
        nvps.add(new BasicNameValuePair("uploadid", uploadid));
        nvps.add(new BasicNameValuePair("path", path));
        nvps.add(new BasicNameValuePair("size", size + ""));
        // block md5...
        nvps.add(new BasicNameValuePair("block_list", "[\"" + md5 + "\"]"));

        httpPost.setEntity(new UrlEncodedFormEntity(nvps, Charsets.UTF_8));
        String entityString = clientExecute(httpPost);
        ;
        return JSON.parseObject(entityString);
    }


    public static JSONObject uploadFile(String from, String to) throws IOException {
        JSONObject loginInfo = parsePanHome(BaiduPanUtil.getPanHomeHtmlSource());
        List<String> blockList = parseBlockList((String) loginInfo.get("pluginUploadUrl"));
        String bdstoken = (String) loginInfo.get("bdstoken");
        JSONObject precreate = apiPrecreate(to, bdstoken, blockList.get(0));
        System.out.println(precreate.toJSONString());
        String xduss = (String) loginInfo.get("XDUSS"); //or bduss
        String uploadid = (String) precreate.get("uploadid");
        File file = new File(from);

        if (!file.exists()) {
            throw new IllegalArgumentException("file ==> " + from + "   文件不存在");
        }
        JSONObject upload = upload(file, to, xduss, uploadid);
        System.out.println(upload.toJSONString());
        String md5 = (String) upload.get("md5");
        JSONObject jsonObject = createFile(to, bdstoken, uploadid, md5, file.length());
        System.out.println(jsonObject.toJSONString());
        return jsonObject;
    }

    /**
     * get download link by <a href="http://pan.baidu.com/api/download?sign=XLhIytsgrSDLWRvXd0P1PC9olo3QPLTssBqMz7RoApsjao0ngUyQuQ%3D%3D&timestamp=1451061491&fidlist=%5B831325154488496%5D&type=dlink&bdstoken=8a9d073c0f618221d5650cf0e30cc4c8&channel=chunlei&clienttype=0&web=1&app_id=250528">http://pan.baidu.com/api/download<a/>
     *
     * @return
     * @throws IOException
     */
    public static String getDlink(String fs_id) throws IOException {
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        ScriptEngine engine = scriptEngineManager.getEngineByName("nashorn");
        JSONObject loginInfo = parsePanHome(getPanHomeHtmlSource());
        String bdstoken = (String) loginInfo.get("bdstoken");
        String sign1 = (String) loginInfo.get("sign1");
        String sign2 = (String) loginInfo.get("sign2");
        String sign3 = (String) loginInfo.get("sign3");
        Integer timestamp = (Integer) loginInfo.get("timestamp");

        String sign = null;
        // sign2 is s(s,r)
        // eval for Charsets and base64Encode
        // sign2.replaceAll("/\"","'")+
        // sign2 = function s(j,r){var a=[];var p=[];var o="";var v=j.length;for(var q=0;q<256;q++){a[q]=j.substr((q%v),1).charCodeAt(0);p[q]=q}for(var u=q=0;q<256;q++){u=(u+p[q]+a[q])%256;var t=p[q];p[q]=p[u];p[u]=t}for(var i=u=q=0;q<r.length;q++){i=(i+1)%256;u=(u+p[i])%256;var t=p[i];p[i]=p[u];p[u]=t;k=p[((p[i]+p[u])%256)];o+=String.fromCharCode(r.charCodeAt(q)^k)}return o};
        try {
            engine.eval(sign2.replaceAll("\"","'").replaceAll("/\"","'")+
                    " function my_sign(sign1,sign3){" +
                    "  return base64Encode(s(sign3,sign1));" +
                    "}" +
                    "function base64Encode(t) {\n" +
                    "            var r, e, i, n, a, o, s = \"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/\";\n" +
                    "            for (i = t.length,\n" +
                    "            e = 0,\n" +
                    "            r = \"\"; i > e; ) {\n" +
                    "                if (n = 255 & t.charCodeAt(e++),\n" +
                    "                e == i) {\n" +
                    "                    r += s.charAt(n >> 2),\n" +
                    "                    r += s.charAt((3 & n) << 4),\n" +
                    "                    r += \"==\";\n" +
                    "                    break\n" +
                    "                }\n" +
                    "                if (a = t.charCodeAt(e++),\n" +
                    "                e == i) {\n" +
                    "                    r += s.charAt(n >> 2),\n" +
                    "                    r += s.charAt((3 & n) << 4 | (240 & a) >> 4),\n" +
                    "                    r += s.charAt((15 & a) << 2),\n" +
                    "                    r += \"=\";\n" +
                    "                    break\n" +
                    "                }\n" +
                    "                o = t.charCodeAt(e++),\n" +
                    "                r += s.charAt(n >> 2),\n" +
                    "                r += s.charAt((3 & n) << 4 | (240 & a) >> 4),\n" +
                    "                r += s.charAt((15 & a) << 2 | (192 & o) >> 6),\n" +
                    "                r += s.charAt(63 & o)\n" +
                    "            }\n" +
                    "            return r\n" +
                    "        }");
            Invocable invocable = (Invocable) engine;
            sign = (String) invocable.invokeFunction(
                    "my_sign", sign1, sign3);

        } catch (ScriptException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        if (sign == null) {
            return null;
        }
        System.out.println(sign);
        // %5B==>[    %5D==>]
        HttpGet httpGet = new HttpGet("http://pan.baidu.com/api/download?sign=" + sign + "&timestamp="+timestamp+"&fidlist=%5B"+fs_id+"%5D&type=dlink&bdstoken="+bdstoken+"&channel=chunlei&clienttype=0&web=1&app_id=250528");

        httpGet.setHeader("Accept", "application/json, text/javascript, */*; q=0.01");
        httpGet.setHeader("Accept-Encoding", "gzip, deflate, sdch");
        httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
        httpGet.setHeader("Connection", "keep-alive");
        httpGet.setHeader("Host", "pan.baidu.com");
        httpGet.setHeader("Referer", "http://pan.baidu.com/disk/home");
        httpGet.setHeader("Cache-Control","max-age=0");
        httpGet.setHeader("Upgrade-Insecure-Requests", "1");
        httpGet.setHeader(User_Agent);
        Header cookieHeader = cookie2Header();
        httpGet.setHeader(cookieHeader);
        String entity = clientExecute(httpGet);
        JSONObject result = JSON.parseObject(entity);
        //没有 dlink
        if (!result.containsKey("dlink")) {
            System.err.println(result);
            return null;
        }
        JSONArray dlinkArray = result.getJSONArray("dlink");

        Optional<JSONObject> objectOptional = dlinkArray.parallelStream()
                .map(o -> JSON.parseObject(JSON.toJSONString(o)))
                .filter(jsonObject -> fs_id.equals(jsonObject.get("fs_id")))
                .findFirst();
        if (objectOptional.isPresent()) {
            return objectOptional.get().getString("dlink");
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
     * pring cookie
     */
    private static void showCookies() {
        List<Cookie> cookieList = cookieStore.getCookies();
        if (cookieList == null || cookieList.size() < 1) {
            return ;

        }
        cookieList
                .stream().forEach(cookie -> {
            System.out.println(cookie.getName()+"       "+cookie.getValue());
        });
    }

    /**
     * @return JSONObject
     */
    public static JSONObject parsePanHome(String htmlSource) {
        String jsonString = htmlSource.replaceFirst("[\\w|\\W]+yunData.setData\\(\\{", "{").replaceFirst("}\\);[\\w|\\W]+", "}").trim();
        String plugin_upload_url = htmlSource.replaceFirst("[\\w|\\W]+\"disk-system:p5\":\\{\"url\":\"", "").replaceFirst("\\.js\"}[\\w|\\W]+", ".js").trim();
        System.out.println("plugin_upload_url :: " + plugin_upload_url);

        JSONObject jsonObject = JSON.parseObject(jsonString);

        if (plugin_upload_url.length() > 0)
            jsonObject.put("pluginUploadUrl", plugin_upload_url);

        return jsonObject;

    }


    /**
     * old http://s1.pan.bdstatic.com/box-static/disk-system-cdn/pkg/plugin-upload_b52213f.js
     *
     * @param url
     */
    public static List<String> parseBlockList(String url) throws IOException {
        HttpGet httpGet = new HttpGet(url);
        String entity = clientExecute(httpGet);
        String array = entity.replaceFirst("^[\\W|\\w]+o='\\[", "[").replaceFirst("]';[\\W|\\w]+", "]");
        return JSON.parseArray(array, String.class);
    }


    /**
     * @return entity string
     */
    private static String clientExecute(final HttpUriRequest request) throws IOException {
        HttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();
        return EntityUtils.toString(entity, Charsets.UTF_8);
    }
}
