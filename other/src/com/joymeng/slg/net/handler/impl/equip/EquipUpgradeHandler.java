package com.joymeng.slg.net.handler.impl.equip;

import java.util.ArrayList;
import java.util.List;

import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.object.build.BuildComponentType;
import com.joymeng.slg.domain.object.build.RoleBuild;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.build.impl.BuildComponentForging;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.CommunicateResp;

public class EquipUpgradeHandler extends ServiceHandler {

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		params.put(in.getInt()); // 城市id
		params.put(in.getLong()); // 建筑id
		params.put(in.getLong()); // 装备数据库Id
		int size = in.getInt();// material size int
		params.put(size);
		for (int i = 0; i < size; i++) {
			params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT)); // materialID:num eg:"material12|1"
		}
	}

	@Override
	public JoyProtocol handle(UserInfo info, ParametersEntity params)
			throws Exception {
		CommunicateResp resp = newResp(info);
		Role role = getRole(info);
		if (role == null) {
			resp.fail();
			return resp;
		}
		int cityId = params.get(0);
		long buildId = params.get(1);
		long equipId = params.get(2);
		int size = params.get(3);
		List<String> materials = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			materials.add(String.valueOf(params.get(4 + i)));
		}
		RoleCityAgent agent = role.getCity(cityId);
		if (agent == null) {
			resp.fail();
			return resp;
		}
		RoleBuild build = agent.searchBuildById(buildId);
		if (build == null) {
			resp.fail();
			return resp;
		}
		BuildComponentForging comEquipForge = build.getComponent(BuildComponentType.BUILD_COMPONENT_FORGING);
		if (comEquipForge != null && !comEquipForge.upgradeEquipment(role, cityId, buildId, equipId, materials)) {
			resp.fail();
			return resp;
		}
		return resp;
	}

}
