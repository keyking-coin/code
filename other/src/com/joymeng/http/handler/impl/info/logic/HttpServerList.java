package com.joymeng.http.handler.impl.info.logic;

import com.joymeng.common.util.StringUtils;
import com.joymeng.http.request.HttpRequestMessage;
import com.joymeng.list.ServerManager;

public class HttpServerList extends AbstractHandler{
	//http://redlist.joymeng.com:12121/info/HttpInfo?logic=HttpServerList&channelId=0000000&language=zh
	@Override
	public String logic(HttpRequestMessage request) {
		String uStr = request.getParameter("uid");
		long uid = -1;
		if (!StringUtils.isNull(uStr)){
			uid = Long.valueOf(uStr);
		}
		String channelId = request.getParameter("channelId");
		String language = request.getParameter("language");
		language = "zh";
		return ServerManager.getInstance().serverToChan(channelId,language,uid);
	}
}
