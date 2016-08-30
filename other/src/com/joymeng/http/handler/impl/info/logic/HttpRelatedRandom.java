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

public class HttpRelatedRandom extends AbstractHandler{
	
	int count = 0;
	
	@Override
	public String logic(HttpRequestMessage request) {
//		List<Integer> servers = ServerManager.getInstance().ServersWorking();
		List<Integer> servers = new ArrayList<Integer>();
		servers.add(12305); //暂时写死金立渠道 服务器实例号 (线上服只有金立能进行微信绑定)
		int size = servers.size();
		long playerUid = Long.parseLong(request.getParameter("playerUid"));
		long uid = GameConfig.SYSTEM_TRANFOEM_ID; 
		int protocolId = 0x0000009B; //  微信号关联信息
		ServiceHandler handler = ServiceHandler.REQUEST_HANDLERS.get(protocolId);
		if (!objs.containsKey(uid)){
			objs.put(uid,new HashMap<String,Object>());
		}
		for (int i = 0 ; i < servers.size() ; i++){
			final int serverId  = servers.get(i);
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
			resp.getParams().put(playerUid);
			JoyServiceApp.getInstance().sendMessage(resp);
			insert(uid,serverId);
			handler.addNextDo(uid, new NeedContinueDoSomthing() {
				@Override
				public int getId() {
					return serverId;
				}
				@SuppressWarnings("unchecked")
				@Override
				public JoyProtocol succeed(UserInfo info,ParametersEntity params) {
					synchronized(this){
						Map<String, Object> map = objs.get(info.getUid());
						map.put("result", "ok");
					    String data = params.get(3);
						Map<String,Object> back =JsonUtil.JsonToObject(data, Map.class);
						if(back.size()!=0){
					    	List<Object> datas = get(info.getUid(),"datas");
					    	if(datas == null){
					    		datas = new ArrayList<Object>();
					    		map.put("datas",datas);
					    	}
					    	datas.add(back);
							count++;
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
				if(map.get("datas")!=null && count==size){
					count = 0;
					return JsonUtil.ObjectToJsonString(map.get("datas"));
				}else{
					List<String> list = new ArrayList<String>();
					return JsonUtil.ObjectToJsonString(list);
				}
				
			} else { 
				return map.get("result").toString();
			}
		}
	}

}
