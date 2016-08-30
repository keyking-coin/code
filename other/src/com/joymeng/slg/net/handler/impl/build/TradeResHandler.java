package com.joymeng.slg.net.handler.impl.build;

import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.CommunicateResp;

public class TradeResHandler extends ServiceHandler{

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		params.put(in.getInt());//cityId int
//		int size = in.getInt();//mem size
//		params.put(size);
//		for(int i=0; i < size; i++){
		params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));
		params.put(in.getLong());
		params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));
		params.put(in.getLong());
//		}
	}

	@Override
	public JoyProtocol handle(UserInfo info, ParametersEntity params) throws Exception {
		CommunicateResp resp = newResp(info);
		Role role = getRole(info);
		if (role == null){
			resp.fail();
			return resp;
		}
		int cityId = params.get(0);
		String type1 = params.get(1);
		long num1 = params.get(2);
		String type2 = params.get(3);
		long num2 = params.get(4);
//		int size = params.get(1);
//		Object[] obs = new Object[size*2];
//		for(int i=0; i < size*2; i++){
//			obs[i] = params.get(i+2);
//			i++;
//			obs[i] = params.get(i+2);
//		}
		RoleCityAgent agent = role.getCity(cityId);
		if(!agent.roleTradeCityResource(role, type1, num1, type2, num2)){
			resp.fail();
		}
		return resp;
	}

}
