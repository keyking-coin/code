package com.keyking.coin.service.http.handler.gm;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
import com.keyking.coin.util.JsonUtil;

public class HttpSetAd extends HttpHandler {

	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		try {
			response.setContentType("text/plain");
			response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
			String url     = request.getParameter("ad_url");
			String picName = request.getParameter("ad_pic");
			File file = new File("./ad.data");
			if (!file.exists()){
				file.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(file);
			Map<String, String> map = new HashMap<String, String>();
			map.put("url",url);
			map.put("pic",picName);
			String str = JsonUtil.ObjectToJsonString(map);
			fos.write(str.getBytes("UTF-8"));
			fos.flush();
			fos.close();
			response.put("result","ok");
		}catch (Exception e) {
			e.printStackTrace();
			response.put("result","fail");
		}
	}
}
