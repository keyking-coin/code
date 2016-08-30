package com.joymeng.http.handler.impl.info.logic;

import com.joymeng.http.request.HttpRequestMessage;
import com.joymeng.list.LoginJudge;

public class HttpScanJudge extends AbstractHandler {
	@Override
	public String logic(HttpRequestMessage request) {
		LoginJudge.getInstance().load();
		return "Success";
	}
}
