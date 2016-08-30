package com.joymeng.slg.net.handler.impl.map;

import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.log.GameLog;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.map.MapObject;
import com.joymeng.slg.domain.map.MapUtil;
import com.joymeng.slg.domain.map.impl.dynamic.GarrisonTroops;
import com.joymeng.slg.domain.map.impl.still.res.MapEctype;
import com.joymeng.slg.domain.map.impl.still.role.MapCity;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.timer.TimerLastType;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.mod.RespModuleSet;
import com.joymeng.slg.net.resp.CommunicateResp;

public class RetreatTroops extends ServiceHandler {

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		params.put(in.getInt());//int 坐标格子位置
		params.put(in.getLong());//long 驻防部队编号
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
		int position = params.get(0);
		long id      = params.get(1);
		GarrisonTroops occuper = MapUtil.searchGarrisons(position,id);
		if (occuper == null){
			resp.fail();
			MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_MAP_RETREAT_NOT_FIND);
			return resp;
		}
		if (occuper.getTroops().getInfo().getUid() != info.getUid()){
			resp.fail();
			GameLog.error("have no permission");
			return resp;
		}
		if (occuper.getTimer().getType() == TimerLastType.TIME_MAP_MASS){
			MapCity mc  = mapWorld.searchObject(position);
			if (mc.getInfo().getUid() == info.getUid()){//集结发起者取消集结
				mc.massCancle(role);
				return resp;
			}
		}
		occuper.die();
		MapObject mapObject = mapWorld.searchObject(position);
		if (mapObject instanceof MapEctype) { //从副本撤退
			if (!role.removeRoleRelic((MapEctype) mapObject)) {
				GameLog.error("removeRoleRelic is fail");
			}
			RespModuleSet rms = new RespModuleSet();
			if (role.isOnline()) {
				role.sendRoleCopysToClient(rms, true);
			}
		}
		return resp;
	}

}
