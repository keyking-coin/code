package com.joymeng.slg.net.handler.impl.map;

import java.util.List;

import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.map.impl.still.union.MapUnionCity;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.CommunicateResp;
import com.joymeng.slg.union.UnionBody;
import com.joymeng.slg.union.data.UnionPostType;

public class UnionCityOprate extends ServiceHandler {

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		params.put(in.getInt());//int 城市坐标
		params.put(in.get());//byte 0占领，1放弃,2取消放弃
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
		byte oprate = params.get(1);
		UnionBody union = unionManager.search(role.getUnionId());
		boolean permission = union == null ? false : (oprate == 0 ? union.checkPermission(info.getUid(),UnionPostType.UNION_POST_OCCUPY_CITY) : union.checkPermission(info.getUid(),UnionPostType.UNION_POST_DROP_CITY));
		if (!permission){
			MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_UNION_NO_OPERAT_PERMISSION);
			resp.fail();
			return resp;
		}
		int cityPos = params.get(0);
		MapUnionCity city = mapWorld.searchObject(cityPos);
		if (city.getUnionId() != union.getId()){
			MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_UNION_OCCUPY_CITY_NO_YOUR);
			resp.fail();
			return resp;
		}
		List<MapUnionCity> citys = mapWorld.searchUnionCity(role.getUnionId());
		for (int i = 0 ; i < citys.size() ; i++){
			MapUnionCity muc = citys.get(i);
			if (muc.equals(city) || muc.getState() < 2){
				continue;
			}
			muc.oprate(3);
		}
		city.oprate(oprate);
		return resp;
	}

}
