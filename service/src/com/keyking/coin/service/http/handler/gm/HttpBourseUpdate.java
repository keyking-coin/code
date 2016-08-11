package com.keyking.coin.service.http.handler.gm;

import com.keyking.coin.service.domain.bourse.BourseInfo;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;

public class HttpBourseUpdate extends HttpHandler {

	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		String name  = request.getParameter("name");
		String url   = request.getParameter("url");
		String str1  = request.getParameter("flag1");
		String str2  = request.getParameter("flag2");
		int pos      = Integer.parseInt(request.getParameter("pos"));
		BourseInfo info = new BourseInfo();
		info.setName(name);
		info.setUrl(url);
		info.setPos(pos);
		byte type = 1;
		if (str1.equals("true")){
			type ++;
		} 
		if (str2.equals("true")){
			type ++;
		}
		info.setType(type);
		if (DB.getBourseDao().save(info)){
			response.put("result","ok");
		}else{
			response.put("result","fail");
		}
	}

}
