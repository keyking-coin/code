package com.joymeng.http.handler.impl.info.logic;

import com.joymeng.http.request.HttpRequestMessage;
import com.joymeng.list.Announcement;

public class HttpScanNotice extends AbstractHandler {

	@Override
	public String logic(HttpRequestMessage request) {
		Announcement.getInstance().load();
		return "Success";
	}
	
}
