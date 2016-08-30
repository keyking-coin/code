package com.joymeng.slg.net.handler.impl.map;

import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.CommunicateResp;

public class MapSearch extends ServiceHandler {

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		params.put(in.getInt());//int centerX
		params.put(in.getInt());//int centerY
		params.put(in.getInt());//int width
		params.put(in.getInt());//int height
		params.put(in.get());//byte 0进,1退出
	}

	@Override
	public JoyProtocol handle(UserInfo info, ParametersEntity params) throws Exception {
		CommunicateResp resp = newResp(info);
		Role role = getRole(info);
		if (role == null){
			resp.fail();
			return resp;
		}
		int centerX = params.get(0);
		int centerY = params.get(1);
		int width   = params.get(2);
		int height  = params.get(3);
		byte type   = params.get(4);//0 进入大地图，1退出大地图。
		role.setInMap(type == 0);
		resp.add(type);
		//GameLog.info(info.getUid() + " start browse  map");
		if (type == 0){
			mapWorld.searchAround(role,resp,centerX,centerY,width,height);
		}
		//GameLog.info(info.getUid() + " end browse  map");
		return resp;
	}
}
