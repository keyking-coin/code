package com.joymeng.slg.net.handler.impl;

import com.joymeng.log.GameLog;
import com.joymeng.log.NewLogManager;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.CommunicateResp;

public class ClientToServerHandler  extends ServiceHandler{

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));// 事件ID
		params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));// 参数	
		
	}

	@Override
	public JoyProtocol handle(UserInfo info, ParametersEntity params)
			throws Exception {
		CommunicateResp resp = newResp(info);
		Role role = world.getRole(info.getUid());
		if (role == null) {
			resp.fail();
			return resp;
		}
		
		String   eventID = params.get(0);
		String parameter = params.get(1);
		if(eventID.equals("open_view")){
			NewLogManager.interfaceLog(role,eventID,parameter);
		}else if(eventID.equals("reach_guide_point")||eventID.equals("complete_guide")){
			NewLogManager.guideLog(role, eventID, parameter);
		}else if(eventID.equals("click_question")){
			try {
				NewLogManager.baseEventLog(role,eventID,parameter);
			} catch (Exception e) {
				GameLog.info("埋点错误");
			}
		}else if(eventID.equals("click_maintask_bar")){
			try {
				NewLogManager.baseEventLog(role, eventID,parameter);
			} catch (Exception e) {
				GameLog.info("埋点错误");
			}
		}
		return resp;
	}

}
