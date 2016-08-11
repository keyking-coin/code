package com.keyking.coin.service.http.handler.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.keyking.coin.service.domain.bourse.BourseInfo;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
import com.keyking.coin.util.JsonUtil;

public class HttpBourseList extends HttpHandler {
	//http://139.196.30.53:32104/HttpDealDel?type=1
	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		String str = request.getParameter("type");//1只在文交所导航,2文交所导航加下拉列表,3文交所导航加下拉列表加热门文交所
		byte type = Byte.parseByte(str);
		List<BourseInfo> infos = null;
		if (type == 1){
			infos = DB.getBourseDao().load();
		}else{
			infos = DB.getBourseDao().load(type);
		}
		if (infos == null){
			infos = new ArrayList<BourseInfo>();
		}
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("list",infos);
		String result = JsonUtil.ObjectToJsonString(data);
		data(request,response,result);
	}
}
