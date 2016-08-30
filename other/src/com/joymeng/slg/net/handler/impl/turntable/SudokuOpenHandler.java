package com.joymeng.slg.net.handler.impl.turntable;

import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.log.GameLog;
import com.joymeng.log.NewLogManager;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.mod.RespModuleSet;
import com.joymeng.slg.net.resp.CommunicateResp;

public class SudokuOpenHandler extends ServiceHandler{

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		params.put(in.getInt()); 	//int  九宫格的pos
	}

	@Override
	public JoyProtocol handle(UserInfo info, ParametersEntity params) throws Exception {
		CommunicateResp resp = newResp(info);
		RespModuleSet rms = new RespModuleSet();
		Role role = getRole(info);
		if (role == null) {
			resp.fail();
			return resp;
		}
		int pos = params.get(0);
		if(pos > 8 || pos < 0){
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_SUDOKU_ERROR_POS);
			resp.fail();
			return resp;
		}
		if (role.getTurntableBody().isOpened(pos)) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_SUDOKU_POS_IS_OPENED);
			resp.fail();
			return resp;
		}
		int num = role.getTurntableBody().getSudokuInfos().size();
		int needGem = num == 0 ? 0 : (int) Math.pow(2.0, (double) (num - 1));
		// TODO 检测 扣除宝石币
		if (role.getGem() < needGem) {
			MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_ROLE_GEM_INSUFFICIENT, needGem);
			resp.fail();
			return resp;
		}
		if (!role.redRoleGem(needGem)) {
			MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_ROLE_GEM_INSUFFICIENT, needGem);
			resp.fail();
			return resp;
		}
		if(!role.getTurntableBody().sudokuOpen(role,pos)){
			resp.fail();
			return resp;
		}
		try {
			NewLogManager.baseEventLog(role, "flop_nine",num);
		} catch (Exception e) {
			GameLog.info("埋点错误");
		}
		role.sendRoleToClient(rms);
		MessageSendUtil.sendModule(rms, role);
		return resp;
	}

}
