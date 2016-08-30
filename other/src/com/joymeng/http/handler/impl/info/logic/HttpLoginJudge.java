package com.joymeng.http.handler.impl.info.logic;

import java.util.HashMap;
import java.util.Map;

import com.joymeng.common.util.JsonUtil;
import com.joymeng.http.request.HttpRequestMessage;
import com.joymeng.list.LoginJudge;

public class HttpLoginJudge extends AbstractHandler {

	@Override
	public String logic(HttpRequestMessage request) {
		
		String memory = request.getParameter("memory");
		String version = request.getParameter("version");
		String country = request.getParameter("country");
		String language = request.getParameter("language");
		version="Android7.0";
		country="Chinese";
		language="zh";
		boolean judge = LoginJudge.getInstance().allowLogin(memory, version,country, language);
		Map<String,Object> map = new HashMap<String,Object>();
		
		if(judge){
			map.put("status", 1);
			map.put("msg", "满足游戏启动条件");
		}else{
			map.put("status", 0);
			map.put("msg", "内存不足,不能完美运行游戏");
		}
		return JsonUtil.ObjectToJsonString(map);
	}

}
