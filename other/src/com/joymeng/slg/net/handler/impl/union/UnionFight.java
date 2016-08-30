package com.joymeng.slg.net.handler.impl.union;

import java.util.List;

import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.map.union.UnionFightTransformData;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.CommunicateResp;
import com.joymeng.slg.union.UnionBody;

public class UnionFight extends ServiceHandler {

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		
	}

	@Override
	public JoyProtocol handle(UserInfo info, ParametersEntity params)
			throws Exception {
		CommunicateResp resp = newResp(info);
		Role role = getRole(info);
		if (role == null){
			resp.fail();
			return resp;
		}
		UnionBody union = unionManager.search(role.getUnionId());
		if (union == null){
			MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_UNION_NOT_FIND);
			resp.fail();
			return resp;
		}
		List<UnionFightTransformData> datas = union.getFightDatas();
		resp.add(datas);
		return resp;
	}
}
