package com.joymeng.list;

import com.joymeng.http.HtppOprateType;
import com.joymeng.services.core.JoyServiceApp;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.slg.ServiceApp;
import com.joymeng.slg.net.resp.TransmissionResp;
import com.joymeng.slg.world.GameConfig;

public class RecordServers {

	public static void sendRecord(long uid, String serverId) {
		
		if (ServiceApp.instanceId == GameConfig.SERVER_LIST_ID){
			return;
		}
		if (ServiceApp.FREEZE){
			return;
		}
		UserInfo targetInfo = new UserInfo();
		targetInfo.setUid(ServiceApp.service_uid);
		targetInfo.setCid(GameConfig.SERVER_LIST_ID);
//		targetInfo.setCid(12290);//测试用本地实例号(0x3003)
		int protocolId = 0x00000092; //玩家新注册通知Radis
		TransmissionResp resp = new TransmissionResp();
		resp.setUserInfo(targetInfo);
		resp.getParams().put(protocolId);//指令编号
		resp.getParams().put(HtppOprateType.HTPP_OPRATE_RESPONSE.ordinal());
		resp.getParams().put(ServiceApp.instanceId);//从哪里来的
		resp.getParams().put(uid);//用户uid
		resp.getParams().put(serverId);//服务器实例号
		JoyServiceApp.getInstance().sendMessage(resp);
	}
}
