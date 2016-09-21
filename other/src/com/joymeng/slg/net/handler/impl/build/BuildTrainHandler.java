package com.joymeng.slg.net.handler.impl.build;

import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.log.GameLog;
import com.joymeng.log.NewLogManager;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.object.army.data.Army;
import com.joymeng.slg.domain.object.build.BuildComponentType;
import com.joymeng.slg.domain.object.build.BuildName;
import com.joymeng.slg.domain.object.build.RoleBuild;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.build.impl.BuildComponentArmyTrain;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.CommunicateResp;

public class BuildTrainHandler extends ServiceHandler {

	@Override
	public void _deserialize(JoyBuffer in,ParametersEntity params) {
		params.put(in.getInt());//城市id
		params.put(in.getLong());//建筑数据库主键
		params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));//兵种id
		params.put(in.getInt());//训练数量
		params.put(in.getInt());//是否金币秒
	}

	@Override
	public JoyProtocol handle(UserInfo info,ParametersEntity params) throws Exception {
		CommunicateResp resp = newResp(info);
		Role role = getRole(info);
		if (role == null){
			NewLogManager.misTakeLog("BuildTrainHandler getRole is null where uid = " + info.getUid());
			resp.fail();
			return resp;
		}
		int cityId = params.get(0);//城市ID
		long id = params.get(1);//建筑id
		String armyId = params.get(2);//兵种id
		int num = params.get(3);//训练数量
		int money=params.get(4);//金币秒
		RoleCityAgent agent = role.getCity(cityId);
		RoleBuild build = agent.searchBuildById(id);
		if (build == null){
			NewLogManager.misTakeLog("BuildTrainHandler getRoleBuild is null where uid = " + info.getUid());
			resp.fail();
			MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_BUILD_NOT_FIND,id);
			return resp;
		}
		if (build.getBuildId().equals(BuildName.MILITARY_FACT.getKey())){
			//军工厂生成陷阱
			Army army = dataManager.serach(Army.class, armyId);
			if (army == null){
				GameLog.error("固化表错误 army is null ,error armyId = " + armyId);
				resp.fail();
				return resp;
			}
			if (num * army.getSpace() + agent.getFenceCurTrip() > agent.getFenceMaxTrip()){
				MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_TRAIN_HOOK_NOT_HAVE_SPACE);
				resp.fail();
				return resp;
			}
		}
		BuildComponentArmyTrain comArmyTrain = build.getComponent(BuildComponentType.BUILD_COMPONENT_ARMYTRAIN);
		if (comArmyTrain == null || !comArmyTrain.trainArmy(role, cityId, id, armyId, num, money)){
			resp.fail();
		}
		resp.add(id);
		return resp;
	}
}
