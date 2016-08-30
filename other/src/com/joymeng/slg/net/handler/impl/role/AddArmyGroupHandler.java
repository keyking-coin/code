package com.joymeng.slg.net.handler.impl.role;

import java.util.Map;

import com.joymeng.log.GameLog;
import com.joymeng.log.NewLogManager;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.object.army.data.ArmyBriefInfo;
import com.joymeng.slg.domain.object.army.data.ArmyGroup;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.CommunicateResp;

public class AddArmyGroupHandler extends ServiceHandler {

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		params.put(in.getInt()); // groupID
		params.put(in.getInt()); // cityID
		int size = in.getInt();
		params.put(size);
		for (int i = 0; i < size; i++) {
			params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT)); // armyID
			params.put(in.getInt()); // armyNum
			params.put(in.getInt()); // armypos
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
		Map<Integer, ArmyGroup> armyGroups = role.getArmyGroups();
		int armyGroupId = params.get(0);
		int cityId = params.get(1);
		int size = params.get(2);
		for (int i = 0; i < size * 3; i += 3) {
			String armyId = params.get(3 + i);
			int armyNum = params.get(4 + i);
			int armyPos = params.get(5 + i);
			ArmyBriefInfo tempArmy = new ArmyBriefInfo(armyId, armyNum, armyPos);
			ArmyGroup tempGroup = new ArmyGroup(armyGroupId, cityId);
			if (armyGroups.get(armyGroupId) == null) {
				armyGroups.put(armyGroupId, tempGroup);
			}
			ArmyGroup armyGroup = armyGroups.get(armyGroupId);
			armyGroup.addArmyToArmtGroup(tempArmy);
		}
		try {
			NewLogManager.baseEventLog(role, "create_troops");
		} catch (Exception e) {
			GameLog.info("埋点错误");
		}
		return resp;
	}
}
