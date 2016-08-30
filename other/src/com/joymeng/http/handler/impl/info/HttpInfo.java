package com.joymeng.http.handler.impl.info;

import com.joymeng.http.handler.HttpHandler;
import com.joymeng.http.handler.impl.info.logic.LogicHandler;
import com.joymeng.http.request.HttpRequestMessage;
import com.joymeng.http.response.HttpResponseMessage;

public class HttpInfo extends HttpHandler {
	//http://127.0.0.1:12121/info/HttpInfo?logic=GetPlayerInfo&uid=1647384&serverId=12290&userName=664433
	@SuppressWarnings("unchecked")
	@Override
	public boolean handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		String apiName = request.getParameter("logic");
		String className = getClass().getPackage().getName() + ".logic." + apiName;
		Class<? extends LogicHandler> clazz = null;
		className = className.replaceAll("/",".");
		try {
			clazz = (Class<? extends LogicHandler>)Class.forName(className);
			LogicHandler logic = clazz.newInstance();
			String result  = logic.logic(request);
			response.appendBody(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
