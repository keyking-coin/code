package com.joymeng.slg.net.handler.impl.build;

import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;

public class BuildDealHandler extends ServiceHandler {

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		// TODO Auto-generated method stub
		params.put(in.getInt()); // 援助的类型 : 1--资源援助 2--士兵援助
		params.put(in.getLong()); // 对方的Uid
		params.put(in.getInt()); // 城市Id
		params.put(in.getLong()); // 建筑Id
		// 援助的资源列表 或 援助的士兵列表
		int size = in.getInt(); // 列表的大小
		params.put(size);
		for (int i = 0; i < size; i++) {
			params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT)); // 资源Id// 或者// 士兵Id																																				
			params.put(in.getInt()); // 资源数量或者士兵数量
		}
	}
	@Override
	public JoyProtocol handle(UserInfo info, ParametersEntity params)	throws Exception {
		// TODO Auto-generated method stub
//		CommunicateResp resp=newResp(info);
//		Role	role=getRole(info);
//		if (role==null) {
//			resp.fail();
//			return resp;
//		}
		return null;
	}

}
