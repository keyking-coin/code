package com.joymeng.slg.net.handler.impl.map;

import com.joymeng.log.GameLog;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.map.impl.still.role.MapFortress;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.CommunicateResp;

public class MapFortressComplete extends ServiceHandler {

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		params.put(in.getInt());//int 要塞的格子坐标
		params.put(in.get());//byte 加速类型:0金币;1道具
		params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));//string 如果是道具就是使用的道具'编号,数量';
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
		int startPos = params.get(0);
		byte type    = params.get(1);
		String datas = params.get(2);
		MapFortress fortress = mapWorld.searchObject(startPos);
		if (fortress == null){
			GameLog.error("select position is not a fortress");
			resp.fail();
			return resp;
		}
		if (!fortress.completeCreate(role,type,datas)){
			GameLog.error("fortress completed building failure");
			resp.fail();
			return resp;
		}
		return resp;
	}

}
