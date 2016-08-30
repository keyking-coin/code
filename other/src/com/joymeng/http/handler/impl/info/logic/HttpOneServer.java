package com.joymeng.http.handler.impl.info.logic;

import com.joymeng.http.request.HttpRequestMessage;
import com.joymeng.list.ServerManager;

public class HttpOneServer extends AbstractHandler {
	@Override
	public String logic(HttpRequestMessage request) {
		return ServerManager.getInstance().outPutOnly();
	}
}
