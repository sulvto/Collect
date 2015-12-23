package me.qinchao.baiduPan;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import junit.framework.Assert;
import junit.framework.TestCase;

import java.util.Map;

/**
 * Created by qinchao on 15-12-23.
 */
public class BaiduPanUtilTest extends TestCase {

    public void testMain() throws Exception {

    }

    public void testRsaEncrypt() throws Exception {

    }

    public void testGuideRandom() throws Exception {

    }

    public void testGetBAIDUID() throws Exception {

    }

    public void testGetPublicKey() throws Exception {

    }

    public void testGetToken() throws Exception {

    }

    public void testLogin() throws Exception {

    }

    public void testPanList() throws Exception {

    }

    public void testPanHome() throws Exception {

    }

    public void testPanList1() throws Exception {
        Map map = BaiduPanUtil.apiList("%2F","8a9d073c0f618221d5650cf0e30cc4c8");
    }

    public void testParsePanHome() throws Exception {
        String html = "\n" +
                "<!DOCTYPE html>\n" +
                "<head>\n" +
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n" +
                "<title>百度云 网盘-全部文件</title>\n" +
                "<script>\n" +
                "// pc和mobile端会稍有不同，必须严格按照该文档来部署\n" +
                "void function(g,f,j,c,h,d,b){g.alogObjectName=h,g[h]=g[h]||function(){(g[h].q=g[h].q||[]).push(arguments)},g[h].l=g[h].l||+new Date,d=f.createElement(j),d.async=!0,d.src=c,b=f.getElementsByTagName(j)[0],b.parentNode.insertBefore(d,b)}(window,document,\"script\",\"http://img.baidu.com/hunter/alog/alog.min.js\",\"alog\");void function(){function c(){return;}window.PDC={mark:function(a,b){alog(\"speed.set\",a,b||+new Date);alog.fire&&alog.fire(\"mark\")},init:function(a){alog(\"speed.set\",\"options\",a)},view_start:c,tti:c,page_ready:c}}();void function(n){var o=!1;n.onerror=function(n,e,t,c){var i=!0;return!e&&/^script error/i.test(n)&&(o?i=!1:o=!0),i&&alog(\"exception.send\",\"exception\",{msg:n,js:e,ln:t,col:c}),!1},alog(\"exception.on\",\"catch\",function(n){alog(\"exception.send\",\"exception\",{msg:n.msg,js:n.path,ln:n.ln,method:n.method,flag:\"catch\"})})}(window);\n" +
                "</script><link rel=\"shortcut icon\" href=\"/box-static/disk-system/images/favicon.ico\"/>\n" +
                "<script type=\"text/javascript\" src=\"/box-static/base/js/lib/mod.js?t=1450851795927\"></script>\n" +
                "<script type=\"text/javascript\" src=\"http://s1.pan.bdstatic.com/box-static/disk-system-cdn/pkg/environment_36dd474.js\"></script>\n" +
                "\n" +
                "\n" +
                "<script type=\"text/javascript\">\n" +
                "                (function(){\n" +
                "        var yunData = require('disk-system:widget/data/yunData.js');\n" +
                "        yunData.setData({\"loginstate\":1,\"username\":\"\\u5c71\\u9a6c\\u9a91\\u58eb\",\"third\":0,\"flag\":1,\"file_list\":null,\"uk\":4013344022,\"task_key\":\"503675dcd628bc33a6a021f12ae94b374cde2f64\",\"task_time\":1450881062,\"sampling\":{\"expvar\":[\"poms_pub\",\"webV3_pub\"]},\"bdstoken\":\"8a9d073c0f618221d5650cf0e30cc4c8\",\"is_vip\":0,\"bt_paths\":null,\"applystatus\":1,\"sign1\":\"20caae8f2fac282a946df454ae52976111d18f9a\",\"sign2\":\"function s(j,r){var a=[];var p=[];var o=\\\"\\\";var v=j.length;for(var q=0;q<256;q++){a[q]=j.substr((q%v),1).charCodeAt(0);p[q]=q}for(var u=q=0;q<256;q++){u=(u+p[q]+a[q])%256;var t=p[q];p[q]=p[u];p[u]=t}for(var i=u=q=0;q<r.length;q++){i=(i+1)%256;u=(u+p[i])%256;var t=p[i];p[i]=p[u];p[u]=t;k=p[((p[i]+p[u])%256)];o+=String.fromCharCode(r.charCodeAt(q)^k)}return o};\",\"sign3\":\"d76e889b6aafd3087ac3bd56f4d4053a\",\"timestamp\":1450881062,\"timeline_status\":1,\"face_status\":0,\"srv_ts\":1450881062,\"need_tips\":0,\"is_year_vip\":0,\"show_vip_ad\":0,\"vip_end_time\":null,\"is_evip\":0,\"urlparam\":[],\"XDUSS\":\"pansec_DCb740ccc5511e5e8fedcff06b081203-%2BWrSNH9s%2BJifUSspfZnEg%2BV7mNS7kUPHwgD48hX5T6f8KZKs9abD3phIUPOIEj8CxHv8U2EgpLBw51wjQNVlj8NM%2F%2F%2FeiIMLT0BzDvWBDoW3iIVzLFN3dYU89Un9u%2FJlTiLQC1XKfmmkDFvPEeENPXBPzzOCClhfCNVL71PX4NGmE2AkrIdnYiQDzTut9oxEkDvvtNquHy7QI3X79VNtJY6RF2p87F3l93LQfLDaWDVPddTYXjZYExIu15dX9Gy0GXjF%2F3YdZy4drQHqqHf9YA%3D%3D\"});\n" +
                "        yunData.setData('SERVERTIME', 1450881062825);\n" +
                "        })();\n" +
                "            </script>\n" +
                "\n" +
                "<meta content=\"b31ebb7c3759312418b3645de4991aef\" name=\"baidu-tc-verification\"/>\n" +
                "<meta content=\"max-age=30\" http-equiv=\"Cache-Control\"/>\n" +
                "<meta content=\"网盘,百度网盘,百度云,网络U盘,网络硬盘,免费网盘,网盘下载,网盘资源,同步,云存储,外链分享,离线下载\" name=\"keywords\"/>\n" +
                "<meta content=\"百度网盘为您提供文件的网络备份、同步和分享服务。空间大、速度快、安全稳固，支持教育网加速，支持手机端。现在注册即有机会享受15G的免费存储空间\" name=\"description\"/>\n" +
                "\n" +
                "<style type=\"text/css\">\n" +
                "\n" +
                "        html {\n" +
                "            *overflow-y: hidden!important;\n" +
                "        }\n" +
                "        body {\n" +
                "            overflow: hidden;\n" +
                "        }\n" +
                "        body, button, input, select, textarea {\n" +
                "            font: 12px/1.5 tahoma, arial, \"宋体\"!important;\n" +
                "            color: #666;\n" +
                "        }\n" +
                "        /* layout begin */\n" +
                "        #layoutApp {\n" +
                "            overflow: hidden;\n" +
                "            position: absolute;\n" +
                "            top: 0;\n" +
                "            right: 0;\n" +
                "            bottom: 0;\n" +
                "            left: 0;\n" +
                "\t\t\tmin-width: 1103px;\n" +
                "\t\t\t_width: 100%;\n" +
                "\t\t\t_height: 630px;\n" +
                "        }\n" +
                "        #layoutAside {\n" +
                "            width: 210px;\n" +
                "            height: 100%;\n" +
                "        }\n" +
                "\t\t/* layout end */\n" +
                "        /* icon begin */\n" +
                "        .icon {\n" +
                "            display: inline-block;\n" +
                "            width: 20px;\n" +
                "            height: 20px;\n" +
                "            background: url(\"/box-static/disk-system/images/icon_743d29d.png\") no-repeat 0 0;\n" +
                "        }\n" +
                "        .icon-aside {\n" +
                "            width: 23px;\n" +
                "            height: 22px;\n" +
                "            margin: 6px 10px 0 2px;\n" +
                "        }\n" +
                "        /* icon end */\n" +
                "    </style>\n" +

                "    <body></body></html>\n";
        JSONObject jsonObject = BaiduPanUtil.parsePanHome(html);
        String bdstoken = (String)jsonObject.get("bdstoken");
        String username = (String)jsonObject.get("username");
        Long uk = (Long)jsonObject.get("uk");
        Assert.assertNotNull(bdstoken);
        Assert.assertFalse(Strings.isNullOrEmpty(bdstoken));
        System.out.println(bdstoken);
        System.out.println(username);
        System.out.println(uk);
    }
}