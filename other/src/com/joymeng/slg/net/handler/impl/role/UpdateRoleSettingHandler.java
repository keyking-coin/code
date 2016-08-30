package com.joymeng.slg.net.handler.impl.role;

import java.util.ArrayList;
import java.util.List;

import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.role.RoleSetting;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.CommunicateResp;

public class UpdateRoleSettingHandler extends ServiceHandler {

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		params.put(in.getInt()); // 1-开启 0-关闭
		params.put(in.getInt()); // 1-开启 0-关闭
		params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));// 语言
		int size = in.getInt();
		params.put(size);
		for (int i = 0; i < size; i++) {
			params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT)); // 消息通知
		}
	}

	@Override
	public JoyProtocol handle(UserInfo info, ParametersEntity params) throws Exception {
		CommunicateResp resp = newResp(info);
		Role role = getRole(info);
		if (role == null) {
			resp.fail();
			return resp;
		}
		int soundEffect = params.get(0);
		int music = params.get(1);
		String language = params.get(2);
		List<String> megNotice = new ArrayList<String>();
		int size = params.get(3);
		for (int i = 0; i < size; i++) {
			megNotice.add(String.valueOf(params.get(4 + i)));
		}
		RoleSetting roleSetting = new RoleSetting(soundEffect == 1, music == 1, language, megNotice);
		role.getRoleSetting().updateRoleSetting(roleSetting);
		return resp;
	}
}
