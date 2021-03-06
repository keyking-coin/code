package com.joymeng.http.handler.impl.info.logic;

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

public class HttpQueryUnionDl extends AbstractHandler{
	@Override
	public String logic(HttpRequestMessage request) {
		long userId = GameConfig.SYSTEM_TRANFOEM_ID;
		int server = Integer.parseInt(request.getParameter("playerServerId"));
		long unionId = Long.valueOf(request.getParameter("playerUnionId"));
		final int serverId = server + GameConfig.SERVER_LIST_ID;
		List<Integer> serList = newList();
		if (serList.contains(serverId)) {
			UserInfo targetInfo = new UserInfo();
			targetInfo.setUid(userId);
			targetInfo.setCid(serverId); 
			int protocolId = 0x00000097; // 指定联盟详细信息
			TransmissionResp resp = new TransmissionResp();
			resp.setUserInfo(targetInfo);
			resp.getParams().put(protocolId); // 指令编号
			resp.getParams().put(HtppOprateType.HTPP_OPRATE_REQUEST.ordinal());//
			resp.getParams().put(ServiceApp.instanceId);// 从哪里来的
			resp.getParams().put(serverId);
			resp.getParams().put(unionId);
			ServiceHandler handler = ServiceHandler.REQUEST_HANDLERS
					.get(protocolId);
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
					map.put("uinons", params.get(3));
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
					return map.get("uinons").toString();
				} else {
					return map.get("result").toString();
				}
			}
		}

		Map<String, Object> bMap = new HashMap<String, Object>();
		bMap.put("status", 0);
		bMap.put("msg", "请求失败，serverId不存在");
		return JsonUtil.ObjectToJsonString(bMap);
	}

}
