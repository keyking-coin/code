package com.joymeng.slg.net.handler.impl;

import com.joymeng.log.NewLogManager;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.CommunicateResp;

public class UseItemhandler extends ServiceHandler{
	@Override
	public void _deserialize(JoyBuffer in,ParametersEntity params) {
		params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));//物品iD，String
		params.put(in.getLong());//物品数量，long
		params.put(in.get());//道具是否用金币代替
		params.put(in.getLong());//道具作用目标Id
	}
	
	@Override
	public JoyProtocol handle(UserInfo info,ParametersEntity params) throws Exception{
		CommunicateResp resp = newResp(info);
		String itemId = params.get(0);
		long num = params.get(1);
		byte type = params.get(2);
		long targetId = params.get(3); 
		Role role = world.getRole(info.getUid());
		if (role == null){
			NewLogManager.misTakeLog("UseItemhandler getRole is null where uid = " + info.getUid());
			resp.fail();
			return resp;
		}
		resp.add(itemId);
		if(!role.getBagAgent().useItem(role, itemId, num, type, targetId)){
			resp.fail();
			return resp;
		}
		return resp;
	}
}
