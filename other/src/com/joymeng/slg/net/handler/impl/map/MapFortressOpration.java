package com.joymeng.slg.net.handler.impl.map;

import java.util.List;

import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.list.EventName;
import com.joymeng.log.GameLog;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.map.data.Worldbuilding;
import com.joymeng.slg.domain.map.data.Worldbuildinglevel;
import com.joymeng.slg.domain.map.impl.still.role.MapFortress;
import com.joymeng.slg.domain.object.build.BuildName;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.mod.RespModuleSet;
import com.joymeng.slg.net.resp.CommunicateResp;

public class MapFortressOpration extends ServiceHandler {

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		params.put(in.get());//byte 0升级,1拆除,2取消升级,3取消拆除
		params.put(in.getInt());//int 要塞的格子坐标
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
		byte opration = params.get(0);
		int pos = params.get(1);
		MapFortress fortress = mapWorld.searchObject(pos);
		if (fortress == null){
			GameLog.error("select position is not a fortress");
			resp.fail();
			return resp;
		}
		if (opration == 0){
			//升级
			String buildKey = BuildName.MAP_FORTRESS.getKey();
			Worldbuilding wb = dataManager.serach(Worldbuilding.class,buildKey);
			int targetLevel = fortress.getLevel() + 1;
			Worldbuildinglevel wbl = dataManager.serach(Worldbuildinglevel.class,buildKey + targetLevel);
			RoleCityAgent city = role.getCity(fortress.getInfo().getCityId());
			//等级限制
			if (fortress.getLevel() >= wb.getMaxLevel()){
				MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_MAP_FORTRESS_MAX_LEVEL);
				resp.fail();
			}
			//建筑条件
			if (!city.checkBuildConditions(role,wbl.getNeedBuildingIDList())){
				resp.fail();
				return resp;
			}
			//资源条件
			if (!city.checkResConditions(role,wbl.getBuildCostList())){
				resp.fail();
				return resp;
			}
			if (!fortress.levelUp(wbl,role)){
				resp.fail();
			}else{
				RespModuleSet rms = new RespModuleSet();
				List<Object> costs = city.redCostResource(wbl.getBuildCostList(),0,EventName.FortressLevelUp.getName());
				role.sendResourceToClient(rms,city.getId(),costs.toArray());
			}
		}else if (opration == 1){
			//拆除
			if (!fortress.drop(role)){
				resp.fail();
			}
		}else if (opration >= 2){
			//取消升级,拆除
			if (!fortress.cancle(info)){
				resp.fail();
			}
		}
		return resp;
	}
}
