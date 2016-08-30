package com.joymeng.http.handler.impl.info.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joymeng.common.util.JsonUtil;
import com.joymeng.http.HtppOprateType;
import com.joymeng.http.request.HttpRequestMessage;
import com.joymeng.services.core.JoyServiceApp;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.ServiceApp;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.NeedContinueDoSomthing;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.TransmissionResp;
import com.joymeng.slg.world.GameConfig;

public class HttpQueryBykey extends AbstractHandler {
	@Override
	public String logic(HttpRequestMessage request) {
		String server = request.getParameter("playerServerId"); // 多个服务器
		String[] sserverId = server.split(",");
		String parameter = request.getParameter("playerParameter");
		long uid = GameConfig.SYSTEM_TRANFOEM_ID; 
		int protocolId = 0x00000058; // 多服务器玩家信息
		ServiceHandler handler = ServiceHandler.REQUEST_HANDLERS.get(protocolId);
		if (!objs.containsKey(uid)){
			objs.put(uid,new HashMap<String,Object>());
		}
		for (int i = 0 ; i < sserverId.length ; i++){
			String servId = sserverId[i];
			final int serverId = Integer.parseInt(servId) + GameConfig.SERVER_LIST_ID;
			UserInfo targetInfo = new UserInfo();
			targetInfo.setUid(uid);
			targetInfo.setCid(serverId); // 服务器,http传过来的服务器编号，你要访问哪个服务器
			TransmissionResp resp = new TransmissionResp();
			resp.setUserInfo(targetInfo);
			resp.getParams().put(protocolId); // 指令编号
			resp.getParams().put(HtppOprateType.HTPP_OPRATE_REQUEST.ordinal());//
			resp.getParams().put(ServiceApp.instanceId);// 从哪里来的
			resp.getParams().put(uid);
			resp.getParams().put(serverId);
			resp.getParams().put(parameter);
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
					    List<Object> list =JsonUtil.JsonToObjectList(data,Object.class);
					    if(list!=null&&list.size()!=0){
					    	List<Object> datas = get(info.getUid(),"datas");
					    	if(datas == null){
					    		datas = new ArrayList<Object>();
					    		map.put("datas",datas);
					    	}
					    	for(int i=0; i<list.size();i++){
					    		datas.add(list.get(i));
					    	}
					    }
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
				Map<String, Object> dMap = new HashMap<String, Object>();
				if(map.get("datas")!=null){
					dMap.put("PlayerOfServers", map.get("datas"));
					cMap.put("status", 1);
					cMap.put("msg", "success");
					cMap.put("data", dMap);
					return JsonUtil.ObjectToJsonString(cMap);
				}else{
					cMap.put("status", 0);
					cMap.put("msg", "fail");
					cMap.put("data", "未查到任何信息");
					return JsonUtil.ObjectToJsonString(cMap);
				}
				
			} else { 
				return map.get("result").toString();
			}
		}
	}
}
