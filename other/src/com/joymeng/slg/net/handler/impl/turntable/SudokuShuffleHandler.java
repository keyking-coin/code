package com.joymeng.slg.net.handler.impl.turntable;

import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.mod.RespModuleSet;
import com.joymeng.slg.net.resp.CommunicateResp;

public class SudokuShuffleHandler extends ServiceHandler {

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {

	}

	@Override
	public JoyProtocol handle(UserInfo info, ParametersEntity params) throws Exception {
		CommunicateResp resp = newResp(info);
		Role role = getRole(info);
		if (role == null) {
			resp.fail();
			return resp;
		}
		RespModuleSet rms = new RespModuleSet();
		role.getTurntableBody().setTurntableNum(role.getTurntableBody().getTurntableNum() + 1);
		role.getTurntableBody().setTurntableState(2);
		role.getTurntableBody().updateTurntableId(role);
		role.getTurntableBody().sendTurntableToClient(rms);
		MessageSendUtil.sendModule(rms, role);
		return resp;
	}

}
