package com.joymeng.slg.domain.event.impl;

import java.util.HashMap;
import java.util.Map;

import com.joymeng.slg.domain.event.AbstractGameEvent;
import com.joymeng.slg.domain.event.GameEvent;
import com.joymeng.slg.domain.evnt.EvntManager;
import com.joymeng.slg.domain.object.IObject;
import com.joymeng.slg.domain.object.army.data.Army;
import com.joymeng.slg.domain.object.role.Role;

public class ActvtEvent extends AbstractGameEvent {

	public enum ActvtEventType {
		USE_ITEM("useItem"), // 使用道具(道具id、num)
		TRAIN_SOLDIER("trainSoldier"), // 生产士兵(兵种id、num)
		TRAIN_SOLDIER_TYPE("trainSoldierType"), TRAIN_SOLDIERS("trainSoldiers"), // 生产士兵(num)
		KILL_SOLDIER("killSoldier"), // 杀死士兵(兵种id、num)
		KILL_NPC("killNpc"), // 杀死npc(level)
		GATHER_RESOURCE("gatherResource"), // 采集资源(资源id、num)
		ROB_RESOURCE("robResource"), // 掠夺资源(资源id、num)
		TREAT_SOLDIER("treatSoldier"), // 治疗伤兵(兵种id、num)
		TREAT_PERSON("treatPerson"), // 治疗士兵(num)
		TREAT_MACHINE("treatMachine"), // 维修机械(num)
		FINISH_INSTANCCE("finishInstance"), // 通关副本(副本id)
		UPGRADE_BUILD("upgradeBuild"), // 升级建筑(建筑id,level)
		RESEARCH_SCIENCE("researchScience"), // 升级科技(科技id)
		PUTON_EQUIP("putOnEquip"), // 穿装备(num)
		USE_RESOURCE("useResource"), // 消耗资源(资源id、long num)
		SPY("spy"), // 侦查
		ATTACK_PLAYER("attackPlayer"), // 进攻玩家
		BUILD_STRONG_HOLD("buildStrongHold"), // 建造要塞
		ASSEMBLE("assemble"), // 集结
		GARRISON("garrison"), // 驻防
		REINFORCE("reinforce"), // 强化
		RECAST("recast"), // 重铸
		DECOMPOSE("decompose"), // 分解(装备id)
		PRODUCE_MATERIAL("produceMaterial"), // 生产材料(num)
		COMPOSE_MATERIAL("composeMaterial"), // 合成材料(num)
		LUCKY_WHEEL("luckyWheel"), // 幸运转盘
		MUNITION_DEAL("munitionDeal"), // 军火商贸易
		ACCELERATE("accelerate"), UNION_HELP("unionHelp"), UNION_DONATE("unionDonate"), UNION_SHOP_BUY(
				"unionShopBuy"), ARMY_REBELL_OVER("armyRebellOver"),;

		// UNION_OCCUPY_CITY("unionOccupyCity"), // 联盟占领城市
		// CREATE_UNION("createUnion"),
		// DISMISS_UNION("dissmissUnion");

		private String name;

		public String getName() {
			return name;
		}

		private ActvtEventType(String name) {
			this.name = name;
		}
	}

	private static final String CT = "_";
	private static final String CV = "#";

	@Override
	public void _handle(IObject trigger, Object[] params) {
		short code = get(params[0]);
		Role role = get(trigger);
		long roleId = role.getId();
		int deNum = 1;
		EvntManager evntMgr = EvntManager.getInstance();
		switch (code) {
		case GameEvent.ACTIVITY_EVENTS: {
			ActvtEventType type = get(params[1]);
			switch (type) {
			case ASSEMBLE:
			case ATTACK_PLAYER:
			case BUILD_STRONG_HOLD:
			case GARRISON:
			case LUCKY_WHEEL:
			case MUNITION_DEAL:
			case SPY:
			case DECOMPOSE:
			case RECAST:
			case REINFORCE:
			case RESEARCH_SCIENCE:
			case UNION_HELP:
			case UNION_DONATE:
			case UNION_SHOP_BUY: {
				evntMgr.Notify("taskEvent", roleId, type.getName(), deNum);
				break;
			}

			case UPGRADE_BUILD: {
				String buildId = get(params[2]);
				int level = get(params[3]);
				evntMgr.Notify("taskEvent", roleId, type.getName(), deNum, buildId, level);
				break;
			}

			case FINISH_INSTANCCE: {
				int instanceType = get(params[2]);
				evntMgr.Notify("taskEvent", roleId, type.getName(), deNum, instanceType);
				break;
			}

			case KILL_NPC: {
				int level = get(params[2]);
				evntMgr.Notify("taskEvent", roleId, type.getName(), deNum, level);
				break;
			}

			case KILL_SOLDIER: {
				String soldierId = get(params[2]);
				int num = get(params[3]);
				evntMgr.Notify("taskEvent", roleId, type.getName(), num, soldierId);
				break;
			}

			case PRODUCE_MATERIAL:
			case COMPOSE_MATERIAL: {
				int num = get(params[2]);
				evntMgr.Notify("taskEvent", roleId, type.getName(), num);
				break;
			}

			case USE_RESOURCE:
			case GATHER_RESOURCE:
			case ROB_RESOURCE: {
				String resourceId = get(params[2]);
				int num = get(params[3]);
				evntMgr.Notify("taskEvent", roleId, type.getName(), num, resourceId);
				break;
			}

			case TRAIN_SOLDIER: {
				String armyId = get(params[2]);
				Army armyBase = dataManager.serach(Army.class, armyId);
				if (armyBase != null) {
					int num = get(params[3]);
					evntMgr.Notify("taskEvent", roleId, type.getName(), num, armyId);

					evntMgr.Notify("taskEvent", roleId, ActvtEventType.TRAIN_SOLDIER_TYPE.getName(), num, armyBase.getArmyType());

					evntMgr.Notify("taskEvent", roleId, ActvtEventType.TRAIN_SOLDIERS.getName(), num);
				}
				break;
			}

			case TREAT_SOLDIER: {
				String armyId = get(params[2]);
				Army armyBase = dataManager.serach(Army.class, armyId);
				if (armyBase != null) {
					int num = get(params[3]);
					evntMgr.Notify("taskEvent", roleId, type.getName(), num, armyId);

					if (armyBase.getArmyType() == 1) {
//						value = role.getId() + CV + ActvtEventType.TREAT_PERSON.getName() + CV + num;
						evntMgr.Notify("taskEvent", roleId, ActvtEventType.TREAT_PERSON.getName(), num);
					} else {
//						value = role.getId() + CV + ActvtEventType.TREAT_MACHINE.getName() + CV + num;
						evntMgr.Notify("taskEvent", roleId, ActvtEventType.TREAT_MACHINE.getName(), num);
					}
				}
				break;
			}

			case PUTON_EQUIP: {
				int num = get(params[2]);
				evntMgr.Notify("taskEvent", roleId, type.getName(), num);
				break;
			}

			case USE_ITEM: {
				String itemId = get(params[2]);
				long num = get(params[3]);
				evntMgr.Notify("taskEvent", roleId, type.getName(), num, itemId);
				break;
			}

			case ACCELERATE: {
				long time = get(params[2]);
				evntMgr.Notify("accelerate", roleId, time);
				break;
			}

			case ARMY_REBELL_OVER: {
				evntMgr.Notify("rebellAttackOver", role.getId(), params[2], params[3]);
				break;
			}

			default:
				break;
			}

			break;
		}

		default:
			break;
		}
	}

}
