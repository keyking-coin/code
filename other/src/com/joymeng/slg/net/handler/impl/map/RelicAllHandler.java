package com.joymeng.slg.net.handler.impl.map;

import java.util.List;
import java.util.Map;

import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.JsonUtil;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.log.GameLog;
import com.joymeng.log.LogManager;
import com.joymeng.log.NewLogManager;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.event.GameEvent;
import com.joymeng.slg.domain.event.impl.ActvtEvent.ActvtEventType;
import com.joymeng.slg.domain.map.MapUtil;
import com.joymeng.slg.domain.map.fight.BattleField;
import com.joymeng.slg.domain.map.fight.obj.FightTroops;
import com.joymeng.slg.domain.map.fight.obj.enumType.Side;
import com.joymeng.slg.domain.map.fight.result.BattleRecord;
import com.joymeng.slg.domain.map.fight.result.FightReport;
import com.joymeng.slg.domain.map.fight.result.FightResutTemp;
import com.joymeng.slg.domain.map.impl.dynamic.ArmyEntity;
import com.joymeng.slg.domain.map.impl.dynamic.GarrisonTroops;
import com.joymeng.slg.domain.map.impl.dynamic.TroopsData;
import com.joymeng.slg.domain.map.impl.still.copy.Relic;
import com.joymeng.slg.domain.map.impl.still.copy.RoleRelic;
import com.joymeng.slg.domain.map.impl.still.copy.Scene;
import com.joymeng.slg.domain.map.impl.still.copy.data.Ruins;
import com.joymeng.slg.domain.map.impl.still.res.MapEctype;
import com.joymeng.slg.domain.map.physics.PointVector;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.mod.RespModuleSet;
import com.joymeng.slg.net.resp.CommunicateResp;

public class RelicAllHandler extends ServiceHandler {

	static final byte RELIC_CHALLENGE = 0;// 挑战
	static final byte ERLIC_RESET = 1;// 重置
	static final byte GET_FINISH_REWARD = 2;// 领取通关奖励

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		byte type = in.get();
		params.put(type);
		switch (type) {
		case RELIC_CHALLENGE:
		case ERLIC_RESET:
		case GET_FINISH_REWARD: {
			params.put(in.getInt()); // 副本的类型
			params.put(in.getInt()); // 副本的坐标
		}
			break;
		default:
			break;
		}
	}

	@SuppressWarnings("unused")
	@Override
	public JoyProtocol handle(UserInfo info, ParametersEntity params) throws Exception {
		CommunicateResp resp = newResp(info);
		Role role = getRole(info);
		if (role == null) {
			resp.fail();
			return resp;
		}
		byte opType = params.get(0);
		int type = params.get(1);
		int pos = params.get(2);
		resp.add(opType); //byte类型
		RespModuleSet rms = new RespModuleSet();
		RoleRelic roleRelic = role.getRoleCopys().get(type);// 获取同一类型的副本
		Relic relic = role.searchRoleRelic(type, pos);
		Ruins ruin = dataManager.serach(Ruins.class, relic.getId());
		List<Scene> scList = roleRelic.getScenes();
		int count = scList.size();
		String dung_type = "关卡"+String.valueOf(count);
		String dung_name = ruin.getId()+"_"+ruin.getServerName();

		if (ruin == null) {
			GameLog.error("read ruins is fail");
			resp.fail();
			return resp;
		}
		switch (opType) {
		case RELIC_CHALLENGE: {
			MapEctype ectype = mapWorld.searchObject(pos);
			if (ectype == null) {
				GameLog.error("mapWorld.searchObject(" + pos + ")is null error");
				resp.fail();
				return resp;
			}
			GarrisonTroops troops = ectype.searchTroops(relic.getTroopId());
			if (troops == null) {
				GameLog.error("ectype.searchTroops(" + relic.getTroopId() + ") is null error!");
				resp.fail();
				return resp;
			}
			if (!troops.getTroops().couldFight()) {// 士兵都死完了,不能出战
				MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_ARMYS_ALL_DIE_OUT);
				resp.fail();
				return resp;
			}
			if (roleRelic.isFinish()) {
				MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_ALL_SCENES_HAVE_FINISH);
				resp.fail();
				return resp;
			}
			if (roleRelic.searchLastScene() == null) {
				GameLog.error("searchChallengeScene is null");
				resp.fail();
				return resp;
			}
			Scene scene = roleRelic.searchLastScene();
			TroopsData defence = new TroopsData();
			defence.copy(scene.getMonsterArmys());// 关卡怪兽
			defence.getInfo().setPosition(pos);
			float dieProbability = scene.getDieProbability();// 死兵率
			BattleField battle = new BattleField();
			battle.add(troops.getTroops(), Side.ATTACK);
			battle.add(defence, Side.DEFENSE);
			BattleRecord record = null;
			try {
				List<FightTroops> troopses_a = battle.getTroopses(Side.ATTACK);
				List<FightTroops> troopses_d = battle.getTroopses(Side.DEFENSE);
				role.effectFightTroops(troopses_a, troopses_d, 0);
				record = battle.startFight();
			} catch (Exception e) {
				GameLog.error("Relic fight error",e);
				resp.fail();
				return resp;
			}
			List<FightTroops> fightTroopses = battle.getTroopses(Side.ATTACK);
			Map<Integer, FightResutTemp> preAttackerInfos = MapUtil.computeFightResult(fightTroopses, troops.getTroops(),dieProbability);
			//MapUtil.triggerAE_kill_monster(role,defence.getInfo().getLevel(),preAttackerInfos.values());
			fightTroopses = battle.getTroopses(Side.DEFENSE);
			Map<Integer, FightResutTemp> preDefencerInfos = MapUtil.computeFightResult(fightTroopses, defence, 1);
			Side winerSide = battle.GetWinner();
			boolean isWin = false;
			FightReport report = null;
			if (winerSide == null) {// 平局,也算攻击失败
				report = MapUtil.createRelicReport(preAttackerInfos, preDefencerInfos, battle, troops.getTroops(),defence, pos, false, record);
				isWin = false;
			} else {
				if (winerSide == Side.DEFENSE) {// 攻击失败
					report = MapUtil.createRelicReport(preAttackerInfos, preDefencerInfos, battle, troops.getTroops(),defence, pos, false, record);
					isWin = false;
				} else {
					report = MapUtil.createRelicReport(preAttackerInfos, preDefencerInfos, battle, troops.getTroops(),defence, pos, true, record);
					isWin = true;
				}
			}
			if(!isWin){
				LogManager.pveLog(role, dung_name, dung_type, (byte)0, "0", 0);
			}
			role.handleEvent(GameEvent.TROOPS_SEND);
			// 士兵都死完了,不能出战 ,移除部队
			if (!troops.getTroops().couldFight()) {
				MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_ARMYS_ALL_DIE_OUT);
//				roleRelic = new RoleRelic();// 删除副本信息
//				resp.fail();
//				return resp;
			}
			String battleReport = JsonUtil.ObjectToJsonString(report);
			scene.setBattleReport(battleReport);
			if (isWin) {// 胜利
				scene.setState(1);// 设置挂关卡通过
				// 获取奖励
				ectype.addSceneReward(role, scene, rms);
				Map<Byte, Map<String, Integer>> map = scene.getPackages(); //每关战利品日志
		
				StringBuffer sb = new StringBuffer();
				for (Byte bt : map.keySet()) {
					Map<String, Integer> rew = map.get(bt);
					for (String str : rew.keySet()) {
						sb.append(str);
						sb.append(GameLog.SPLIT_CHAR);
						sb.append(rew.get(str));
						sb.append(GameLog.SPLIT_CHAR);
						LogManager.pveLog(role, dung_name, dung_type, (byte) 1,str, rew.get(str));
					}
				}
				String newStr = sb.toString().substring(0, sb.toString().length() - 1);
				NewLogManager.mapLog(role, "dungeon_reward",newStr);
				
			} else {// 失败
				scene.setState(0);// 设置挂关卡未通过
			}
			roleRelic.updateScene();
			if (roleRelic.isFinish()) {
				roleRelic.setIsGotReward((byte)0);//设置 通关 未领取奖励
				role.handleEvent(GameEvent.ACTIVITY_EVENTS,ActvtEventType.FINISH_INSTANCCE,relic.getType());
			}
			role.sendRoleCopysToClient(rms, true);
			TroopsData datas= scene.getMonsterArmys();
			List<ArmyEntity> armys  =datas.getArmys();
			StringBuffer sb = new StringBuffer();
			for (int j = 0; j < armys.size(); j++) {
				ArmyEntity entry = armys.get(j);
				sb.append(entry.getKey());
				sb.append(GameLog.SPLIT_CHAR);
				sb.append(entry.getSane());
				sb.append(GameLog.SPLIT_CHAR);
			}
			PointVector point = MapUtil.getPointVector(pos);
			String newStr = sb.toString().substring(0,sb.toString().length() - 1);
			NewLogManager.mapLog(role, "attack_dungeon",ruin.getId(),(int)point.x,(int)point.y,newStr);
			break;
		}
		case ERLIC_RESET: { // 重置
			if (roleRelic.getCurrentFreeResetNum() < 1) {
				MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_BEYOND_FREE_RESET_MAX);
				resp.fail();
				return resp;
			}
			if(roleRelic.isFinish() && roleRelic.getIsGotReward() == 0){
				roleRelic.getFinishReward(role);
			}
			roleRelic.setCurrentFreeResetNum(roleRelic.getCurrentFreeResetNum() - 1);//可用次数减1
			roleRelic.resetScenes();
			role.sendRoleCopysToClient(rms, true);
			break;
		}
		case GET_FINISH_REWARD: {
			if (roleRelic.getIsGotReward() == 2) {
				MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_RELIC_NO_FINISH);
				resp.fail();
				return resp;
			}
			if (roleRelic.getIsGotReward() == 1) {
				MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_RELIC_REWARD_HAS_GOT);
				resp.fail();
				return resp;
			}
			roleRelic.getFinishReward(role);
			roleRelic.setIsGotReward((byte)1);
			role.sendRoleCopysToClient(rms, false);
			MessageSendUtil.sendModule(rms, role.getUserInfo());
			break;
		}
		default:
			GameLog.error("客户端传的类型错了!!!");
			resp.fail();
			break;
		}
		return resp;
	}
	
	
}
