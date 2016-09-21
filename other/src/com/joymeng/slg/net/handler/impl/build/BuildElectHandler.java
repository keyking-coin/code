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

public class BuildElectHandler extends ServiceHandler {
	
	@Override
	public void _deserialize(JoyBuffer in,ParametersEntity params) {
		params.put(in.getInt());//城市id
		params.put(in.getLong());//Buildid
		params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));//组件类型
		params.put(in.getInt());//比例
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
		long bid = params.get(1);
		String componentType = params.get(2);
		int ratio = params.get(3);
		float powerRatio = (float)(ratio*1.0/100.0);
		RoleCityAgent agent = role.getCity(cityId);
		RoleBuild build = agent.searchBuildById(bid);
		if (build == null){
			GameLog.error("searchBuildById build fail.build="+bid+"|cid="+cityId+"|uid="+role.getJoy_id());
			resp.fail();
			return resp;
		}
		int type = agent.geteAgent().motify(role,agent, build, componentType, powerRatio);
		if(type != 0){
			resp.fail();
			resp.add(type);
			return resp;
		}
		return resp;
	}
}
