package com.joymeng.http.handler.impl.info.logic;

import com.joymeng.http.request.HttpRequestMessage;
import com.joymeng.list.ServerManager;

public class HttpServerRecord extends AbstractHandler{

	@Override
	public String logic(HttpRequestMessage request) {
		long uid = Long.valueOf(request.getParameter("uid"));
		return ServerManager.getInstance().serverRecord(uid);
	}

}
