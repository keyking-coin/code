package com.joymeng.slg.net.handler.impl.build;

import com.joymeng.log.GameLog;
import com.joymeng.log.NewLogManager;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.object.build.RoleBuild;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.CommunicateResp;

public class BuildCreateHandler extends ServiceHandler {
	
	@Override
	public void _deserialize(JoyBuffer in,ParametersEntity params) {
		params.put(in.getInt());//城市id
		params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));//BaseBuildingSlot的编号
		params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));//建筑固化id
		params.put(in.getInt());//是否金币建造
	}
	
	@Override
	public JoyProtocol handle(UserInfo info,ParametersEntity params) throws Exception {
		CommunicateResp resp = newResp(info);
		Role role = getRole(info);
		if (role == null) {
			NewLogManager.misTakeLog("BuildCreateHandler getRole is null where uid = " + info.getUid());
			resp.fail();
			return resp;
		}
		int cityId   = params.get(0);
		String bbsId = params.get(1);
		String buildingId = params.get(2);
		int money = params.get(3);
		RoleCityAgent agent = role.getCity(cityId);
		byte type = agent.createBuild(bbsId,buildingId,role, money);
		if (type != 0){
			resp.fail();
			resp.add(type);
			return resp;
		}
		RoleBuild build = agent.searchBuildBySoltId(bbsId);
		if(build != null){
			resp.add( build.getId() );
		}else{
			GameLog.error("create build fail.");
		}
		return resp;
	}
}
