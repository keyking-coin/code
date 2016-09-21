package com.joymeng.slg.net.handler.impl.map;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.JsonUtil;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.list.EventName;
import com.joymeng.log.GameLog;
import com.joymeng.log.LogManager;
import com.joymeng.log.NewLogManager;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.actvt.impl.NewServerBuff;
import com.joymeng.slg.domain.actvt.impl.NewServerBuff.BuffTag;
import com.joymeng.slg.domain.event.GameEvent;
import com.joymeng.slg.domain.event.impl.ActvtEvent.ActvtEventType;
import com.joymeng.slg.domain.map.MapUtil;
import com.joymeng.slg.domain.map.fight.BattleField;
import com.joymeng.slg.domain.map.fight.obj.FightTroops;
import com.joymeng.slg.domain.map.fight.obj.enumType.FightBuffType;
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
import com.joymeng.slg.domain.object.effect.ArmyEffVal;
import com.joymeng.slg.domain.object.effect.BuffTypeConst.TargetType;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.task.ConditionType;
import com.joymeng.slg.domain.object.task.TaskEventDelay;
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

	@Override
	public JoyProtocol handle(UserInfo info, ParametersEntity params) throws Exception {
		CommunicateResp resp = newResp(info);
		Role role = getRole(info);
		if (role == null) {
			NewLogManager.misTakeLog("Userinfo : " + info, "uid : " + info.getUid(),
					"className : " + this.getClass().getName(), "params : " + params);
			resp.fail();
			return resp;
		}
		byte opType = params.get(0);
		int type = params.get(1);
		int pos = params.get(2);
		resp.add(opType); // byte类型
		RespModuleSet rms = new RespModuleSet();
		RoleRelic roleRelic = role.getRoleCopys().get(type);// 获取同一类型的副本
		if (roleRelic == null) {
			GameLog.error("getRoleCopys is null type = " + type);
			resp.fail();
			return resp;
		}
		Relic relic = role.searchRoleRelic(type, pos);
		if (relic == null) {
			GameLog.error("searchRoleRelic is null type = " + type + " pos = " + pos);
			resp.fail();
			return resp;
		}
		Ruins ruin = dataManager.serach(Ruins.class, relic.getId());
		if (ruin == null) {
			GameLog.error("read ruins is fail relicId = " + relic.getId());
			resp.fail();
			return resp;
		}
		List<Scene> scList = roleRelic.getScenes();
		int count = scList.size();
		String dung_type = "关卡" + String.valueOf(count);
		String dung_name = ruin.getId() + "_" + ruin.getServerName();
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
				// 添加怪兽的buff
				mobEffectFightTroops(troopses_d, troopses_a, scene.getEffVals());
				record = battle.startFight();
			} catch (Exception e) {
				GameLog.error("Relic fight error", e);
				resp.fail();
				return resp;
			}
			List<FightTroops> fightTroopses = battle.getTroopses(Side.ATTACK);
			Map<Integer, FightResutTemp> preAttackerInfos = MapUtil.computeFightResult(fightTroopses,
					troops.getTroops(), dieProbability);
			// MapUtil.triggerAE_kill_monster(role,defence.getInfo().getLevel(),preAttackerInfos.values());
			fightTroopses = battle.getTroopses(Side.DEFENSE);
			Map<Integer, FightResutTemp> preDefencerInfos = MapUtil.computeFightResult(fightTroopses, defence, 1);
			Side winerSide = battle.GetWinner();
			boolean isWin = false;
			FightReport report = null;
			if (winerSide == null) {// 平局,也算攻击失败
				report = MapUtil.createRelicReport(preAttackerInfos, preDefencerInfos, battle, troops.getTroops(),
						defence, pos, false, record);
				isWin = false;
			} else {
				if (winerSide == Side.DEFENSE) {// 攻击失败
					report = MapUtil.createRelicReport(preAttackerInfos, preDefencerInfos, battle, troops.getTroops(),
							defence, pos, false, record);
					isWin = false;
				} else {
					report = MapUtil.createRelicReport(preAttackerInfos, preDefencerInfos, battle, troops.getTroops(),
							defence, pos, true, record);
					isWin = true;
				}
			}
			if (!isWin) {
				LogManager.pveLog(role, dung_name, dung_type, (byte) 0, EventName.ReplicaRuins.getName(), "0", 0);
			}
			role.handleEvent(GameEvent.TROOPS_SEND);
			// 士兵都死完了,不能出战 ,移除部队
			if (!troops.getTroops().couldFight()) {
				MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_ARMYS_ALL_DIE_OUT);
				// roleRelic = new RoleRelic();// 删除副本信息
				// resp.fail();
				// return resp;
			}
			String battleReport = JsonUtil.ObjectToJsonString(report);
			scene.setBattleReport(battleReport);
			if (isWin) {// 胜利
				scene.setState(1);// 设置挂关卡通过
				// 获取奖励
				ectype.addSceneReward(role, scene, rms);
				Map<Byte, Map<String, Integer>> map = scene.getPackages(); // 每关战利品日志

				StringBuffer sb = new StringBuffer();
				for (Byte bt : map.keySet()) {
					Map<String, Integer> rew = map.get(bt);
					for (String str : rew.keySet()) {
						sb.append(str);
						sb.append(GameLog.SPLIT_CHAR);
						sb.append(rew.get(str));
						sb.append(GameLog.SPLIT_CHAR);
						LogManager.pveLog(role, dung_name, dung_type, (byte) 1, EventName.ReplicaRuins.getName(), str,
								rew.get(str));
					}
				}
				String newStr = sb.toString().substring(0, sb.toString().length() - 1);
				NewLogManager.mapLog(role, "dungeon_reward", newStr);

			} else {// 失败
				scene.setState(0);// 设置挂关卡未通过
			}
			roleRelic.updateScene();
			if (roleRelic.isFinish()) {
				roleRelic.setIsGotReward((byte) 0);// 设置 通关 未领取奖励
				role.handleEvent(GameEvent.ACTIVITY_EVENTS, ActvtEventType.FINISH_INSTANCCE, relic.getType());
				role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.C_CARBON_N, 0);
			}
			role.sendRoleCopysToClient(rms, true);
			TroopsData datas = scene.getMonsterArmys();
			List<ArmyEntity> armys = datas.getArmys();
			StringBuffer sb = new StringBuffer();
			for (int j = 0; j < armys.size(); j++) {
				ArmyEntity entry = armys.get(j);
				sb.append(entry.getKey());
				sb.append(GameLog.SPLIT_CHAR);
				sb.append(entry.getSane());
				sb.append(GameLog.SPLIT_CHAR);
			}
			PointVector point = MapUtil.getPointVector(pos);
			String newStr = sb.toString().substring(0, sb.toString().length() - 1);
			NewLogManager.mapLog(role, "attack_dungeon", ruin.getId(), (int) point.x, (int) point.y, newStr);
			break;
		}
		case ERLIC_RESET: { // 重置
			if (roleRelic.getCurrentFreeResetNum() < 1) {
				MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_BEYOND_FREE_RESET_MAX);
				resp.fail();
				return resp;
			}
			if (roleRelic.isFinish() && roleRelic.getIsGotReward() == 0) {
				roleRelic.getFinishReward(role);
			}
			roleRelic.setCurrentFreeResetNum(roleRelic.getCurrentFreeResetNum() - 1);// 可用次数减1
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
			roleRelic.setIsGotReward((byte) 1);
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

	/**
	 * 战斗对象buff加成
	 * 
	 * @param attributes
	 */
	public void mobEffectFightTroops(List<FightTroops> team, List<FightTroops> emenys, List<ArmyEffVal> effs) {
		TargetType[][] keys = new TargetType[][] {
				{ TargetType.T_A_IMP_SA, TargetType.C_A_RED_ATK, TargetType.C_RED_ALL_DG }, // 火力buff部分
				{ TargetType.T_A_IMP_SD, TargetType.C_A_RED_DEF, }, // 防御buff部分
				{ TargetType.T_A_IMP_AHP, TargetType.C_A_RED_HP }, // 生命buff部分
				{ TargetType.T_A_IMP_IAR, TargetType.C_A_RED_ATR }, // 命中buff部分
				{ TargetType.T_A_IMP_ICR, TargetType.C_A_RED_CRT }, // 暴击buff部分
				{ TargetType.T_A_IMP_IER, TargetType.C_A_RED_EDR }, { TargetType.T_A_IMP_DMG, TargetType.C_A_RED_DMG },
				{ TargetType.C_A_RED_BDMG_ALL, TargetType.C_A_RED_BDMG },
				{ TargetType.T_A_IMP_SS, TargetType.C_A_RED_MB } };
		FightBuffType[] bts = new FightBuffType[] { FightBuffType.ATTACK_EFF, FightBuffType.DEFENDE_EFF,
				FightBuffType.HP_EFF, FightBuffType.HIT_EFF, FightBuffType.CRIT_EFF, FightBuffType.EVADE_EFF,
				FightBuffType.VULNERABILITY_EFF, FightBuffType.MITIGATION_EFF, FightBuffType.SPEED_EFF };
		for (int i = 0; i < keys.length; i++) {
			for (int j = 0; j < keys[i].length; j++) {
				TargetType key = keys[i][j];
				List<FightTroops> lis = null;
				if (key.getSymbol() <= 2) {
					lis = team;
				} else if (key.getSymbol() <= 4) {
					lis = emenys;
				} else if (key.getSymbol() <= 6) {
					lis = new ArrayList<FightTroops>();
					lis.addAll(team);
					lis.addAll(emenys);
				}
				if (lis != null) {
					for (int k = 0; k < lis.size(); k++) {
						FightTroops troops = lis.get(k);
						String armyId = troops.getAttribute().getName();
						float value = getEffVal(effs, key, armyId);
						int symbol = key.getSymbol() % 2 == 0 ? -1 : 1;
						if (value > 0) {
							troops.addBuff(bts[i], value * symbol);
						}
					}
				}
			}
		}
	}

	public float getEffVal(List<ArmyEffVal> effs, TargetType type, String armyId) {
		float value = 0;
		if (effs != null) {
			for (int i = 0; i < effs.size(); i++) {
				ArmyEffVal eff = effs.get(i);
				if (eff.checkTargetInfo(type, armyId)) {
					value += eff.getValue();
				}
			}
		}
		if (type == TargetType.T_A_RED_DR) {
			float newServerBuff = NewServerBuff.iGetBuff(BuffTag.REDUCE_HURT_SOLDIE_RRATE) / 100.0f;
			value -= newServerBuff;
		} else if (type == TargetType.T_A_IMP_SS) {
			float newServerBuff = NewServerBuff.iGetBuff(BuffTag.ADD_TROOP_MOVE_SPEED) / 100.0f;
			value += newServerBuff;
		} else if (type == TargetType.T_A_RED_SPT) {
			float newServerBuff = NewServerBuff.iGetBuff(BuffTag.REDUCE_TRAIN_SOLDIER_TIME) / 100.0f;
			value += newServerBuff;
		}
		return value;
	}
}
