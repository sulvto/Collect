package me.qinchao;

//import org.junit.Test;

import com.alibaba.fastjson.JSONObject;

public class BaiduSearchTest {

//	@Test
	public void testSearch() {
		 final JSONObject search = BaiduSearch.search("s");
		 System.out.println(search.toJSONString());
	}

}
