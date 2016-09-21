package com.joymeng.http.handler.impl.info.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joymeng.common.util.JsonUtil;
import com.joymeng.http.HtppOprateType;
import com.joymeng.http.request.HttpRequestMessage;
import com.joymeng.list.ServerManager;
import com.joymeng.services.core.JoyServiceApp;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.slg.ServiceApp;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.NeedContinueDoSomthing;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.TransmissionResp;
import com.joymeng.slg.world.GameConfig;

public class HttpForbidden extends AbstractHandler{

	@Override
	public String logic(HttpRequestMessage request) {
		int server = Integer.parseInt(request.getParameter("playerServerId"));
		long playerUid = Long.parseLong(request.getParameter("playerUid"));
		byte type  = Byte.parseByte(request.getParameter("type"));
		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("endTime");
		long uid = GameConfig.SYSTEM_TRANFOEM_ID; 
		int protocolId = 0x000000A2; // 玩家封禁协议
		List<Integer> sserverId = new ArrayList<>();
		if (type == (byte) 3 || type == (byte) 4) {  //帐号禁封、设备禁封
			sserverId = ServerManager.getInstance().ServersWorking();
//			sserverId.add(4);
			type = type == (byte) 3 ? (byte) 2 : type;
		} else {
			sserverId.add(server);
		}
		int size = sserverId.size();
		ServiceHandler handler = ServiceHandler.REQUEST_HANDLERS.get(protocolId);
		if (!objs.containsKey(uid)){
			objs.put(uid,new HashMap<String,Object>());
		}
		for (int i = 0 ; i < sserverId.size() ; i++){
			final int serverId = sserverId.get(i) + GameConfig.SERVER_LIST_ID;
			UserInfo targetInfo = new UserInfo();
			targetInfo.setUid(uid);
			targetInfo.setCid(serverId); 
			TransmissionResp resp = new TransmissionResp();
			resp.setUserInfo(targetInfo);
			resp.getParams().put(protocolId); 
			resp.getParams().put(HtppOprateType.HTPP_OPRATE_REQUEST.ordinal());//
			resp.getParams().put(ServiceApp.instanceId);
			resp.getParams().put(serverId);
			resp.getParams().put(playerUid);
			resp.getParams().put(type);
			resp.getParams().put(startTime);
			resp.getParams().put(endTime);
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
					return JsonUtil.ObjectToJsonString(cMap);
				}else{
					cMap.put("status", 0);
					cMap.put("msg", "fail");
					return JsonUtil.ObjectToJsonString(cMap);
				}
				
			} else { 
				return map.get("result").toString();
			}
		}
	}

}
