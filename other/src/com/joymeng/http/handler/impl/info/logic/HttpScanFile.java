package com.joymeng.http.handler.impl.info.logic;

import com.joymeng.http.request.HttpRequestMessage;
import com.joymeng.list.ServerManager;

public class HttpScanFile extends AbstractHandler {

	@Override
	public String logic(HttpRequestMessage request) {
		ServerManager.getInstance().scan();
		return "ok";
	}
}
