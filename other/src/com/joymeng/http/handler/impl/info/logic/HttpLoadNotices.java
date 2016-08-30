package com.joymeng.http.handler.impl.info.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joymeng.common.util.JsonUtil;
import com.joymeng.http.HtppOprateType;
import com.joymeng.http.request.HttpRequestMessage;
import com.joymeng.services.core.JoyServiceApp;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.slg.ServiceApp;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.NeedContinueDoSomthing;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.TransmissionResp;
import com.joymeng.slg.world.GameConfig;

public class HttpLoadNotices  extends AbstractHandler{
	@Override
	public String logic(HttpRequestMessage request) {
		String server = request.getParameter("playerServerId"); // 多个服务器
		String[] sserverId = server.split(",");
		int size = sserverId.length;
		String loaderUrl = request.getParameter("loaderUrl");
		long uid = GameConfig.SYSTEM_TRANFOEM_ID; 
		int protocolId = 0x00000091; //  服务器远程下载公告xml
		ServiceHandler handler = ServiceHandler.REQUEST_HANDLERS.get(protocolId);
		if (!objs.containsKey(uid)){
			objs.put(uid,new HashMap<String,Object>());
		}
		for (int i = 0 ; i < sserverId.length ; i++){
			String servId = sserverId[i];
			final int serverId = Integer.parseInt(servId);
			UserInfo targetInfo = new UserInfo();
			targetInfo.setUid(uid);
			targetInfo.setCid(serverId); 
			TransmissionResp resp = new TransmissionResp();
			resp.setUserInfo(targetInfo);
			resp.getParams().put(protocolId); 
			resp.getParams().put(HtppOprateType.HTPP_OPRATE_REQUEST.ordinal());//
			resp.getParams().put(ServiceApp.instanceId);
			resp.getParams().put(uid);
			resp.getParams().put(serverId);
			resp.getParams().put(loaderUrl);
			JoyServiceApp.getInstance().sendMessage(resp);
			insert(uid,serverId);
			handler.addNextDo(uid, new NeedContinueDoSomthing() {
				@Override
				public int getId() {
					return serverId;
				}
				@Override
				public JoyProtocol succeed(UserInfo info,ParametersEntity params) {
					synchronized(this){
						Map<String, Object> map = objs.get(info.getUid());
						map.put("result", "ok");
					    String data = params.get(3);
						List<Object> datas = get(info.getUid(), "datas");
						if (datas == null) {
							datas = new ArrayList<Object>();
							map.put("datas", datas);
						}
						datas.add(data);
					    int comeFrom = params.get(2);
						remove(info.getUid(),comeFrom);
						return null;
					}
				}

				@Override
				public JoyProtocol fail(UserInfo info, ParametersEntity params) {
					synchronized(this){
						Map<String, Object> map = objs.get(info.getUid());
						map.put("result", "通讯失败");
						int comeFrom = params.get(2);
						remove(info.getUid(),comeFrom);
						return null;
					}
				}
			});
		}
		interrupt(uid);
		Map<String, Object> map = objs.get(uid);
		objs.remove(uid);
		if (!map.containsKey("result")) {
			return "请求超时";
		} else {
			if (map.get("result").equals("ok")) {
				Map<String, Object> cMap = new HashMap<String, Object>();
				List<Object> list = JsonUtil.JsonToObjectList(map.get("datas").toString(), Object.class);
				if(map.get("datas")!=null && list.size()==size){
					cMap.put("status", 1);
					cMap.put("msg", "success");
					cMap.put("data", "服务器下载完成");
					return JsonUtil.ObjectToJsonString(cMap);
				}else{
					cMap.put("status", 0);
					cMap.put("msg", "fail");
					cMap.put("data", "服务器下载失败");
					return JsonUtil.ObjectToJsonString(cMap);
				}
				
			} else { 
				return map.get("result").toString();
			}
		}
	}
}
