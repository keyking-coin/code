package com.joymeng.http.handler.impl.info.logic;

import com.joymeng.http.request.HttpRequestMessage;
import com.joymeng.log.NewLogManager;

public class HttpClientToServer  extends AbstractHandler{

	@Override
	public String logic(HttpRequestMessage request) {
		//应用启动 游戏启动
		//GET /info/HttpInfo?logic=HttpClientToServer&uuid=1&appId=1001&channelId=0000000&appVersion=1.0.2&osVersion=Windows 7 Service Pack 1 (6.1.7601) 64bit&screenSize=950,595&language=Chinese&identifier=f2abbac2f941a7f2dd5435a35cb1105011993918&deviceModel=Intel(R) Core(TM) i7-4790 CPU @ 3.60GHz (16246 MB)&deviceName=LIKE-PC&systemMemorySize=16246&exit=0 HTTP/1.1
		String uuid = request.getParameter("uuid");
		String appId = request.getParameter("appId");
		String channelId = request.getParameter("channelId");
		String appVersion = request.getParameter("appVersion");
		String osVersion = request.getParameter("osVersion");
		String screenSize = request.getParameter("screenSize");
		String language = request.getParameter("language");
		String identifier = request.getParameter("identifier");
		String deviceModel = request.getParameter("deviceModel");
		String deviceName = request.getParameter("deviceName");
		String systemMemorySize = request.getParameter("systemMemorySize");
		String enter = request.getParameter("enter");
		NewLogManager.gameLog(uuid, appId, channelId, appVersion, osVersion, screenSize, language, identifier,
				deviceModel, deviceName, systemMemorySize, enter);
		return "ok";
	}

}
