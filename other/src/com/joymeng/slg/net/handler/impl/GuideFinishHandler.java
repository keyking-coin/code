package com.joymeng.slg.net.handler.impl;

import com.joymeng.common.util.StringUtils;
import com.joymeng.log.GameLog;
import com.joymeng.log.LogManager;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.CommunicateResp;

public class GuideFinishHandler extends ServiceHandler {

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));	//引导的ID			
	}

	@Override
	public JoyProtocol handle(UserInfo info, ParametersEntity params) throws Exception {
		CommunicateResp resp = newResp(info);
		Role role = getRole(info);
		if (role == null){
			resp.fail();
			return resp;
		}
		String guideId = params.get(0);
		if (StringUtils.isNull(guideId)) {
			GameLog.error("guideId is null");
			resp.fail();
			return resp;
		}
		if (!role.getGuideIdList().contains(guideId)) {
			role.getGuideIdList().add(guideId);	
			LogManager.guideLog(role,guideId);//记录完成的指引
			GameLog.info(String.valueOf(role.getId()) + "完成了指引:" + guideId);
		} else {
			GameLog.info(String.valueOf(role.getId()) + "已经包含了指引:" + guideId);
		}
		return resp;
	}

}
