package com.joymeng.slg.net.handler.impl;

import com.joymeng.common.util.expression.ProtoExpression;
import com.joymeng.log.GameLog;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.role.data.Guide;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.CommunicateResp;

public class GuideCheckHandler extends ServiceHandler{

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));	//引导的ID
	}

	@Override
	public JoyProtocol handle(UserInfo info, ParametersEntity params) throws Exception {
		CommunicateResp resp = newResp(info);
		Role role = getRole(info);
		if (role == null) {
			resp.fail();
			return resp;
		}
		long uid = role.getId();
		String guideId = params.get(0);
		Guide guide = dataManager.serach(Guide.class, guideId);
		if (guide == null) {
			resp.fail();
			return resp;
		}
		String guideExpression = guide.getGuideTrigger();
		if (guideExpression == null) {
			GameLog.error("Error ---- read guide base is null");
			resp.fail();
			return resp;
		}
		guideExpression = guideExpression.replace("'uid'", "'" + String.valueOf(uid) + "'");
		guideExpression = guideExpression.replace("'guideId'", "'" + guideId + "'");
		boolean expressionResult = false;
		Object object = ProtoExpression.ExecuteExpression(guideExpression);
		expressionResult = (object instanceof Boolean) ? ((Boolean)object).booleanValue() : false;
		if (!expressionResult) {
			resp.fail();
		}
		return resp;
	}

}
