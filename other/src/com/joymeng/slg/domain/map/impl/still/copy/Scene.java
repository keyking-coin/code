package com.joymeng.slg.domain.map.impl.still.copy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joymeng.common.util.StringUtils;
import com.joymeng.log.GameLog;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.domain.map.impl.dynamic.TroopsData;
import com.joymeng.slg.domain.map.impl.still.copy.data.Ruinscheckpoin;
import com.joymeng.slg.domain.object.effect.ArmyEffVal;
import com.joymeng.slg.domain.object.effect.ExtendInfo;
import com.joymeng.slg.domain.object.effect.BuffTypeConst.ExtendsType;
import com.joymeng.slg.domain.object.effect.BuffTypeConst.TargetType;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.SerializeEntity;

/**
 * 关卡对象
 * 
 * @author houshanping
 *
 */
public class Scene implements SerializeEntity {
	String sceneId;// 关卡的ID
	int state = 0;// 关卡的状态 0未通过 1已通过
	float dieProbability;// 死兵率
	TroopsData monsterArmys = new TroopsData();// 关卡的守军
	List<ArmyEffVal> effVals = new ArrayList<>();// 守军的buff
	Map<Byte, Map<String, Integer>> packages = new HashMap<Byte, Map<String, Integer>>();// 奖励背包列表
	String battleReport;// 战斗报告

	public Scene() {

	}

	public Scene(Scene scene) {
		this.sceneId = scene.getSceneId();
		this.state = scene.getState();
		this.dieProbability = scene.getDieProbability();
		this.monsterArmys = scene.getMonsterArmys();
		this.packages = scene.getPackages();
		this.battleReport = scene.getBattleReport();
		this.effVals = scene.getEffVals();
	}

	public Scene(String sceneId, int state, float dieProbability, TroopsData monsterArmys,
			Map<Byte, Map<String, Integer>> packages, String battleReport, Ruinscheckpoin checkpoin) {
		this.sceneId = sceneId;
		this.state = state;
		this.dieProbability = dieProbability;
		this.monsterArmys = monsterArmys;
		this.packages = packages;
		this.battleReport = battleReport;
		initArmyEff(checkpoin);
	}

	/**
	 * 初始化副本关卡的怪兽buff
	 * @param checkpoin
	 */
	private void initArmyEff(Ruinscheckpoin checkpoin) {
		List<String> buffParms = checkpoin.getBuffList();
		if (buffParms == null) {
			GameLog.error("ruinscheckpoint  bufflist is null");
			return;
		}
		for (int i = 0; i < buffParms.size(); i++) {
			String parm = buffParms.get(i);
			if (StringUtils.isNull(parm)) {
				GameLog.error("parm is null");
				continue;
			}
			String[] strings = parm.split(":");
			if (strings.length < 3) {
				GameLog.error("params length is fail");
				continue;
			}
			TargetType type = TargetType.search(strings[0]);// 目标效果类型
			ExtendInfo extendInfo = new ExtendInfo(ExtendsType.EXTEND_ALL, 0);
			ArmyEffVal aVal = new ArmyEffVal(type, extendInfo, Float.valueOf(strings[2]), 0);
			effVals.add(aVal);
		}
	}

	public String getSceneId() {
		return sceneId;
	}

	public void setSceneId(String sceneId) {
		this.sceneId = sceneId;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public float getDieProbability() {
		return dieProbability;
	}

	public void setDieProbability(float dieProbability) {
		this.dieProbability = dieProbability;
	}

	public List<ArmyEffVal> getEffVals() {
		return effVals;
	}

	public void setEffVals(List<ArmyEffVal> effVals) {
		this.effVals = effVals;
	}

	public TroopsData getMonsterArmys() {
		return monsterArmys;
	}

	public void setMonsterArmys(TroopsData monsterArmys) {
		this.monsterArmys = monsterArmys;
	}

	public Map<Byte, Map<String, Integer>> getPackages() {
		return packages;
	}

	public void setPackages(Map<Byte, Map<String, Integer>> packages) {
		this.packages = packages;
	}

	public String getBattleReport() {
		return battleReport;
	}

	public void setBattleReport(String battleReport) {
		this.battleReport = battleReport;
	}

	public void sendClient(ParametersEntity param) {
		JoyBuffer out = new JoyBuffer();
		try {
			param.serialize(this, out);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void serialize(JoyBuffer out) {
		out.putPrefixedString(sceneId, JoyBuffer.STRING_TYPE_SHORT); // String
																		// 关卡的ID
		out.putInt(state); // 关卡的状态 int
		out.putFloat(dieProbability);// 死兵率
		monsterArmys.saveSerialize(out);// 守军部队
		out.putInt(packages.size()); // 奖励的大小 int
		for (Byte type : packages.keySet()) {
			out.put(type);// 奖励的类型 byte
			out.putInt(packages.get(type).size());// 奖励的列表 int
			for (String key : packages.get(type).keySet()) {
				out.putPrefixedString(key, JoyBuffer.STRING_TYPE_SHORT);// 奖励的key
																		// String
				out.putInt(packages.get(type).get(key));// 奖励物品的num int
			}
		}
		out.putPrefixedString(battleReport, JoyBuffer.STRING_TYPE_SHORT);// 战报
																			// String
	}

	public void deserialize(JoyBuffer buffer) {
		// TODO Auto-generated method stub
		sceneId = buffer.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
		state = buffer.getInt();
		dieProbability = buffer.getFloat();
		monsterArmys.saveDeserialize(buffer);
		int rewardSize = buffer.getInt();
		for (int i = 0; i < rewardSize; i++) {
			byte rewardType = buffer.get();
			int itemSize = buffer.getInt();
			Map<String, Integer> items = new HashMap<>();
			for (int j = 0; j < itemSize; j++) {
				String itemId = buffer.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
				int itemNum = buffer.getInt();
				items.put(itemId, itemNum);
			}
			packages.put(rewardType, items);
		}
		battleReport = buffer.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
	}

	public void copy(Scene temp) {
		this.battleReport = temp.getBattleReport();
		this.dieProbability = temp.getDieProbability();
		this.monsterArmys = temp.getMonsterArmys();
		this.packages = temp.getPackages();
		this.sceneId = temp.getSceneId();
		this.state = temp.getState();
	}

}
