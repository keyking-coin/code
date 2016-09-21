package com.joymeng.http.handler.impl.info.logic;

import java.util.HashMap;
import java.util.Map;

import com.joymeng.http.HtppOprateType;
import com.joymeng.http.request.HttpRequestMessage;
import com.joymeng.list.ServerManager;
import com.joymeng.services.core.JoyServiceApp;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.ServiceApp;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.NeedContinueDoSomthing;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.TransmissionResp;
import com.joymeng.slg.world.GameConfig;

public class HttpGetPropList extends AbstractHandler {
	@Override
	public String logic(HttpRequestMessage request) {
		long userId = GameConfig.SYSTEM_TRANFOEM_ID; 
		final int serverId = ServerManager.getInstance().getOnlineServerId();
//		final int serverId = 12293;
		UserInfo targetInfo = new UserInfo();
		targetInfo.setUid(userId);
		targetInfo.setCid(serverId); // 服务器,http传过来的服务器编号，你要访问哪个服务器
		int protocolId = 0x00000087; // 获取游戏道具列表
		TransmissionResp resp = new TransmissionResp();
		resp.setUserInfo(targetInfo);
		resp.getParams().put(protocolId); // 指令编号
		resp.getParams().put(HtppOprateType.HTPP_OPRATE_REQUEST.ordinal());//
		resp.getParams().put(ServiceApp.instanceId);// 从哪里来的
		resp.getParams().put(serverId);
		ServiceHandler handler = ServiceHandler.REQUEST_HANDLERS.get(protocolId);
		objs.put(userId, new HashMap<String, Object>());
		handler.addNextDo(userId, new NeedContinueDoSomthing() {
			@Override
			public int getId() {
				return serverId;
			}
			
			@Override
			public JoyProtocol succeed(UserInfo info,
					ParametersEntity params) {
				Map<String, Object> map = objs.get(info.getUid());
				map.put("result", "ok");
				map.put("GetPropList", params.get(3));
				int comeFrom = params.get(2);
				remove(info.getUid(), comeFrom);
				return null;
			}

			@Override
			public JoyProtocol fail(UserInfo info, ParametersEntity params) {
				Map<String, Object> map = objs.get(info.getUid());
				map.put("result", "通讯失败");
				int comeFrom = params.get(2);
				remove(info.getUid(), comeFrom);
				return null;
			}
		});
		JoyServiceApp.getInstance().sendMessage(resp);
		insert(userId, serverId);
		interrupt(userId);
		Map<String, Object> map = objs.get(userId);
		objs.remove(userId);
		if (!map.containsKey("result")) {
			return "请求超时";

		} else {
			if (map.get("result").equals("ok")) {
				return  map.get("GetPropList").toString();
			} else {
				return map.get("result").toString();
			}
		}

	}
}
