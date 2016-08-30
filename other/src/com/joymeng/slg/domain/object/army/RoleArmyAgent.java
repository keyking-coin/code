package com.joymeng.slg.domain.object.army;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joymeng.Instances;
import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.list.ArmyDetail;
import com.joymeng.log.GameLog;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.dao.DaoData;
import com.joymeng.slg.dao.SqlData;
import com.joymeng.slg.domain.object.army.data.Army;
import com.joymeng.slg.domain.object.build.BuildName;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.mod.AbstractClientModule;
import com.joymeng.slg.net.mod.RespModuleSet;

public class RoleArmyAgent implements Instances {
	private long uid;
	private int cityId;
	private Map<String, Map<Byte, ArmyInfo>> armysMap = new HashMap<String, Map<Byte, ArmyInfo>>();//<ArmyId,<State,num>>

	// private long lastCost;//未下发的部队数量改变后的消耗
	public RoleArmyAgent() {

	}

	public void init(long uid, int cityId) {
		this.uid = uid;
		this.cityId = cityId;
	}

	public void loadFromData(SqlData data) {
		uid = data.getInt(DaoData.RED_ALERT_GENERAL_UID);
		cityId = data.getInt(DaoData.RED_ALERT_GENERAL_CITY_ID);
		String armyId = data.getString(DaoData.RED_ALERT_ARMY_TYPE_ID);
		byte state = (byte) data.getShort(DaoData.RED_ALERT_ARMY_STATE);
		int num = data.getInt(DaoData.RED_ALERT_ARMY_NUM);
		ArmyInfo army = createArmy(armyId, num, state);// new ArmyInfo(armyId,
														// num, state );
		if (army == null) {
			return;
		}
		army.init(uid, cityId);
		//
		Map<Byte, ArmyInfo> armyMap;
		armyMap = armysMap.get(armyId);
		if (armyMap == null) {
			armyMap = new HashMap<Byte, ArmyInfo>();
		}
		armyMap.put(state, army);
		armysMap.put(armyId, armyMap);
	}

	public void serialize(SqlData data) {
		JoyBuffer out = JoyBuffer.allocate(8192);
		out.putInt(armysMap.size());
		for (Map<Byte, ArmyInfo> armyMs : armysMap.values()) {
			out.putInt(armyMs.size());
			for (ArmyInfo army : armyMs.values()) {
				out.putPrefixedString(army.getArmyId(),JoyBuffer.STRING_TYPE_SHORT);
				out.put(army.getState());
				out.putInt(army.getArmyNum());
			}
		}
		data.put(DaoData.RED_ALERT_CITY_ARMYS, out.arrayToPosition());
	}

	public void deserialize(Object data) {
		if (data == null) {
			return;
		}
		JoyBuffer armyDatas = JoyBuffer.wrap((byte[]) data);
		int size = armyDatas.getInt();
		for (int i = 0; i < size; i++) {
			int sizeSt = armyDatas.getInt();
			for (int j = 0; j < sizeSt; j++) {
				String armyId = armyDatas.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
				byte state = armyDatas.get();
				int num = armyDatas.getInt();
				ArmyInfo army = createArmy(armyId, num, state);
				if (army == null) {
					continue;
				}
				army.init(uid, cityId);
				Map<Byte, ArmyInfo> armyMap;
				armyMap = armysMap.get(armyId);
				if (armyMap == null) {
					armyMap = new HashMap<Byte, ArmyInfo>();
				}
				armyMap.put(state, army);
				armysMap.put(armyId, armyMap);
			}
		}
	}
	
	public void sendToClient(RespModuleSet rms,RoleCityAgent city) {
		// 城池部队模块
		AbstractClientModule module = new AbstractClientModule() {
			@Override
			public short getModuleType() {
				return NTC_DTCD_ROLE_ARMY;
			}
		};
		module.add(cityId);// DB城池id,int
		int mapSize = 0;
		for (Map<Byte, ArmyInfo> armyMap : armysMap.values()) {
			mapSize += armyMap.size();
		}
		module.add(mapSize);// 列表数量
		for (Map<Byte, ArmyInfo> armyMap : armysMap.values()) {
			for (ArmyInfo armyInfo : armyMap.values()) {
				module.add(armyInfo.getArmyId());// 兵ID， String
				module.add(armyInfo.getArmyNum());// 数量，int
				module.add(armyInfo.getState());// 部队状态，byte
			}
		}
		module.add(city.getCityArmyConsume());
		rms.addModule(module);
	}
	
	/**
	 * 打包所有的部队信息
	 * 
	 * @param rms
	 */
	public void sendToClient(RespModuleSet rms,Role role) {
		if (role == null){
			role = world.getRole(uid);
		}
		RoleCityAgent agent = role.getCity(cityId);
		sendToClient(rms,agent);
	}

	/**
	 * 打包指定状态的部队信息
	 * 
	 * @param rms
	 * @param state
	 */
	public void sendToClient(RespModuleSet rms, byte state) {
		// 城池部队模块
		AbstractClientModule module = new AbstractClientModule() {
			@Override
			public short getModuleType() {
				return NTC_DTCD_ROLE_ARMY;
			}
		};
		module.add(cityId);// DB城池id,int
		int mapSize = 0;
		for (Map<Byte, ArmyInfo> armyMap : armysMap.values()) {
			if (armyMap.get(state) != null)
				mapSize++;
		}
		module.add(mapSize);// 列表数量
		for (Map<Byte, ArmyInfo> armyMap : armysMap.values()) {
			ArmyInfo armyinfo = armyMap.get(state);
			if (armyinfo != null) {
				module.add(armyinfo.getArmyId());// 兵ID， String
				module.add(armyinfo.getArmyNum());// 数量，int
				module.add(armyinfo.getState());// 部队状态，byte
			}
		}
		Role role = world.getOnlineRole(uid);
		if (role != null) {
			RoleCityAgent agent = role.getCity(cityId);
			module.add(agent.getCityArmyConsume());
		}
		rms.addModule(module);
	}

	/**
	 * 判断兵种数量是否正确
	 * 
	 * @param armyLst
	 * @return
	 */
	public boolean checkArmysOut(List<ArmyInfo> armyLst) {
		if (armysMap.size() == 0) {
			return false;
		}
		for (ArmyInfo need : armyLst) {
			Map<Byte, ArmyInfo> armyMap = armysMap.get(need.getArmyId());
			if (armyMap == null || armyMap.get(ArmyState.ARMY_IN_NORMAL.getValue()) == null) {// 没有空闲部队
				return false;
			}
			ArmyInfo army = armyMap.get(ArmyState.ARMY_IN_NORMAL.getValue());
			if (army.getArmyNum() < need.getArmyNum()) {// 选择出征部队数量不足
				return false;
			}
		}
		return true;
	}

	/**
	 * 把伤兵加入到医院
	 * 
	 * @param num
	 * @param maxNum
	 * @param armyLst
	 */
	private void putArmyInHospital(int num, int maxNum, List<ArmyInfo> armyLst) {
		Collections.sort(armyLst);
		for (int i = 0; i < armyLst.size(); i++) {
			ArmyInfo army = armyLst.get(i);
			if (maxNum > 0) {// 医院还有位置
				if (army.getArmyNum() <= maxNum) {
					maxNum -= army.getArmyNum();
					addArmys(army.getArmyId(), army.getArmyNum(), ArmyState.ARMY_IN_HOSPITAL.getValue());
				} else if (army.getArmyNum() > maxNum) {
					addArmys(army.getArmyId(), maxNum, ArmyState.ARMY_IN_HOSPITAL.getValue());
					int dieNum = army.getArmyNum() - maxNum;
					maxNum = 0;
					addArmys(army.getArmyId(), dieNum, ArmyState.ARMY_DIED.getValue());
				}
			} else {// 医院位置不够的直接死亡
				addArmys(army.getArmyId(), army.getArmyNum(), ArmyState.ARMY_DIED.getValue());
			}
		}
	}

	/**
	 * 伤兵进医院
	 * 
	 * @param armyLst
	 * @return
	 */
	private boolean backInjuredArmy(List<ArmyInfo> armyLst) {
		Role role = world.getObject(Role.class, uid);
		if (role == null) {
			GameLog.error("add army in hospital error where role not exsit uid=" + uid);
			return false;
		}
		RoleCityAgent agent = role.getCity(cityId);
		int maxHospitalNum = agent.getRepairerHospital(BuildName.HOSPITAL.getKey());
		int maxRepairNum = agent.getRepairerHospital(BuildName.REPAIRER.getKey());
		List<ArmyInfo> armyHLst = new ArrayList<ArmyInfo>();
		List<ArmyInfo> armyRLst = new ArrayList<ArmyInfo>();
		int countHNum = 0;
		int countRNum = 0;
		for (ArmyInfo army : armyLst) {
			if (army.getArmyNum() == 0) {
				continue;
			}
			Army armyBase = dataManager.serach(Army.class, army.getArmyId());
			if (armyBase.getArmyType() == 1) {
				countHNum += army.getArmyNum();
				armyHLst.add(army);
			} else {
				countRNum += army.getArmyNum();
				armyRLst.add(army);
			}
			removeArmys(army.getArmyId(), army.getArmyNum(), army.getState());
		}
		// 医院或维修厂的最大值需要减去已经存在的伤兵
		for (Map.Entry<String, Map<Byte, ArmyInfo>> entry : armysMap.entrySet()) {
			ArmyInfo armyInfo = entry.getValue().get(ArmyState.ARMY_IN_HOSPITAL.getValue());
			if (armyInfo != null) {
				Army armyBase = dataManager.serach(Army.class, armyInfo.getArmyId());
				if (armyBase.getArmyType() == 1) {
					maxHospitalNum -= armyInfo.getArmyNum();
				} else {
					maxRepairNum -= armyInfo.getArmyNum();
				}
			}
		}
		if (armyHLst.size() > 0) {
			putArmyInHospital(countHNum, maxHospitalNum, armyHLst);
		}
		if (armyRLst.size() > 0) {
			putArmyInHospital(countRNum, maxRepairNum, armyRLst);
		}
		return true;
	}

	/**
	 * 更新部队状态
	 * 
	 * @param state
	 *            要更新的目标状态
	 * @param armyLst
	 *            要更新的部队列表
	 * @return
	 */
	public boolean updateArmysState(byte state, List<ArmyInfo> armyLst) {
		if (armysMap.size() == 0) {
			return false;
		}
		// 伤兵进医院，先检查医院存储上限,优先扣除低级兵
		if (state == ArmyState.ARMY_IN_HOSPITAL.getValue()) {
			if (!backInjuredArmy(armyLst)) {
				return false;
			}
			return true;
		}
		for (ArmyInfo armyInfo : armyLst) {
			// 删除
			Map<Byte, ArmyInfo> armyMap = armysMap.get(armyInfo.getArmyId());
			if (armyMap == null || armyMap.get(armyInfo.getState()) == null) { // 没有空闲部队
				return false;
			}
			ArmyInfo army = armyMap.get(armyInfo.getState());
			int num = army.getArmyNum() - armyInfo.getArmyNum();
			if (num < 0) {
				return false;
			} else if (num == 0) {
				armyMap.remove(army.getState());
				army.setState(ArmyState.ARMY_REMOVE.getValue());
			} else {
				army.setArmyNum(num);
				armyMap.put(army.getState(), army);
			}
			if (state == ArmyState.ARMY_DIED.getValue()) {
				continue;
			}
			// 添加
			ArmyInfo add = armyMap.get(state);
			if (add == null) {
				add = createArmy(armyInfo.getArmyId(), armyInfo.getArmyNum(), state);
				add.init(uid, cityId);
				armyMap.put(state, add);
			} else {
				add.setArmyNum(add.getArmyNum() + armyInfo.getArmyNum());
				armyMap.put(state, add);
			}
		}

		return true;
	}

	/**
	 * 创建部队
	 * 
	 * @param armyId
	 * @param num
	 * @param state
	 * @return
	 */
	public ArmyInfo createArmy(String armyId, int num, byte state) {
		ArmyInfo armyInfo = new ArmyInfo(armyId, num, state);
		armyInfo.init(uid, cityId);
		Army armyBase = dataManager.serach(Army.class, armyId);
		if (armyBase == null) {
			GameLog.error("cannot create army,no army base information where armyId=" + armyId);
			return null;
		}
		armyInfo.setArmyBase(armyBase);
		return armyInfo;
	}

	/*
	 * 添加已存在兵种数量
	 */
	public void addOneArmy(String armyId, int num, byte state) { //
		synchronized (armysMap) {
			ArmyInfo armyInfo;
			Map<Byte, ArmyInfo> armyMap;
			armyMap = armysMap.get(armyId);
			armyInfo = armyMap.get(state);
			if (armyInfo != null) {
				armyInfo.setArmyNum(armyInfo.getArmyNum() + num);
			} else {
				armyInfo = createArmy(armyId, num, state);
			}
			armyMap.put(state, armyInfo);
			armysMap.put(armyId, armyMap);
		}
	}

	/**
	 * 添加部队
	 * 
	 * @param cityId
	 * @param armyId
	 * @param num
	 */
	private void addArmys(String armyId, int num, byte state) {
		synchronized (armysMap) {
			ArmyInfo armyInfo;
			Map<Byte, ArmyInfo> armyMap;
			if (armysMap.size() == 0 || armysMap.get(armyId) == null) {
				armyInfo = createArmy(armyId, num, state);
				armyMap = new HashMap<Byte, ArmyInfo>();
			} else {
				armyMap = armysMap.get(armyId);
				armyInfo = armyMap.get(state);
				if (armyInfo != null) {
					armyInfo.setArmyNum(armyInfo.getArmyNum() + num);
				} else {
					armyInfo = createArmy(armyId, num, state);
				}
			}
			armyMap.put(state, armyInfo);
			armysMap.put(armyId, armyMap);
		}
	}

	/**
	 * 移除部队
	 * 
	 * @param armyId
	 * @param num
	 * @return
	 */
	private boolean removeArmys(String armyId, int num, byte state) {
		if (armysMap.size() == 0 || armysMap.get(armyId) == null) {
			return false;
		}
		Map<Byte, ArmyInfo> armyMap = armysMap.get(armyId);
		ArmyInfo army = armyMap.get(state);
		if (army == null) {
			return false;
		}
		if (army.getArmyNum() - num <= 0) {
			armyMap.remove(state);
		} else {
			army.setArmyNum(army.getArmyNum() - num);
		}
		return true;
	}

	/**
	 * 添加某一类的士兵，伤兵，出征士兵，驻扎士兵等
	 * 
	 * @param state
	 * @param armys
	 */
	public boolean addClassArmys(List<ArmyInfo> armys) {
		if (armys.size() == 0) {
			return false;
		}
		for (ArmyInfo army : armys) {
			addArmys(army.getArmyId(), army.getArmyNum(), army.getState());
		}
		return true;
	}

	public void addOneClassArmy(ArmyInfo army) {
		addArmys(army.getArmyId(), army.getArmyNum(), army.getState());
	}

	public boolean removeClassArmys(List<ArmyInfo> armys) {
		for (ArmyInfo army : armys) {
			removeArmys(army.getArmyId(), army.getArmyNum(), army.getState());
		}
		return true;
	}

	/**
	 * 获取部队列表
	 * 
	 * @param state
	 * @param cityArmys
	 * @return
	 */
	public boolean getCityArmys(byte state, List<ArmyInfo> cityArmys) {
		if (armysMap.size() == 0) {
			return false;
		}
		for (Map<Byte, ArmyInfo> armyMap : armysMap.values()) {
			for (ArmyInfo army : armyMap.values()) {
				if (army.state == state) {
					cityArmys.add(army);
				}
			}
		}
		if (cityArmys.size() == 0) {
			return false;
		}
		return true;
	}

	/**
	 * 获取部队列表 受伤、死的和待删除的除外
	 * 
	 * @param state
	 * @param cityArmys
	 * @return
	 */
	public List<ArmyInfo> getCityArmy() {
		List<ArmyInfo> cityArmys = new ArrayList<ArmyInfo>();
		if (armysMap.size() == 0) {
			return cityArmys;
		}
		for (Map<Byte, ArmyInfo> armyMap : armysMap.values()) {
			for (ArmyInfo army : armyMap.values()) {
				if (army.state != 2 && army.state != 5 && army.state != 99) {
					cityArmys.add(army);
				}
			}
		}
		if (cityArmys.size() == 0) {
			return null;
		}
		return cityArmys;
	}

	/**
	 * 返回所有的军队
	 * 
	 * @return
	 */
	public List<ArmyInfo> getAllCityArmy() {
		List<ArmyInfo> cityArmys = new ArrayList<ArmyInfo>();
		if (armysMap.size() == 0) {
			return null;
		}
		for (Map<Byte, ArmyInfo> armyMap : armysMap.values()) {
			for (ArmyInfo army : armyMap.values()) {
				cityArmys.add(army);
			}
		}
		if (cityArmys.size() == 0) {
			return null;
		}
		return cityArmys;
	}

	/**
	 * 返回玩家火力List
	 * 
	 * @return
	 */
	public List<ArmyDetail> getArmyDetails() {
		List<ArmyDetail> list = new ArrayList<ArmyDetail>();
		if (armysMap.size() == 0) {
			return null;
		}
		for (Map<Byte, ArmyInfo> armyMap : armysMap.values()) {
			int allNum = 0;
			int injureNum = 0;
			String name = "";
			for (ArmyInfo army : armyMap.values()) {
				name = army.armyId;
				allNum += army.getArmyNum();
				if (army.getState() == 2) {
					injureNum += army.getArmyNum();
				}
			}
			ArmyDetail army = new ArmyDetail();
			army.setArmyName(name);
			army.setAllNum(allNum);
			army.setInjureNum(injureNum);
			list.add(army);
		}
		return list;
	}

	/**
	 * 检查用户城市有没有部队
	 * @return
	 */
	public boolean checkCityArmys() {
		boolean isHave = false;
		if (armysMap.size() == 0) {
			return isHave;
		}
		for (Map<Byte, ArmyInfo> armyMap : armysMap.values()) {
			for (ArmyInfo army : armyMap.values()) {
				if (army.state == ArmyState.ARMY_IN_HOSPITAL.getValue()) {
					continue;
				}
				isHave = true;
			}
		}
		return isHave;
	}

	/**
	 * 获取某种士兵的数量
	 * 
	 * @param state
	 * @param armyId
	 * @return
	 */
	public int getCityArmysNum(byte state, String armyId) {
		if (armysMap.size() == 0) {
			return 0;
		}
		Map<Byte, ArmyInfo> armyMap = armysMap.get(armyId);
		for (ArmyInfo army : armyMap.values()) {
			if (army.state == state) {
				return army.getArmyNum();
			}
		}
		return 0;
	}

	/**
	 * 解散士兵 返回80%的资源
	 * 
	 * @param role
	 * @param cityId
	 * @param buildId
	 * @param armyId
	 * @param num
	 * @param armyType 
	 * @return
	 */
	public boolean disMissArmy(Role role, String armyId, int num, byte armyState) {
		if (!removeArmys(armyId, num, armyState)) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ROLE_ARMY_INSUFFICIENT);
			return false;
		}
		role.getRoleStatisticInfo().updataRoleArmyFight(role);
		RespModuleSet rms = new RespModuleSet();
		sendToClient(rms,role);
		MessageSendUtil.sendModule(rms, role);
		return true;
	}

	/**
	 * 获取部队战斗力
	 */
	public int getArmysBattleEffec() {
		int value = 0;
		for (Map<Byte, ArmyInfo> armyMap : armysMap.values()) {
			for (ArmyInfo armyInfo : armyMap.values()) {
				if (armyInfo.getState() == 2) {// 伤兵不计算战斗力
					continue;
				}
				Army armyBase = dataManager.serach(Army.class, armyInfo.getArmyId());
				if(armyBase == null){
					continue;
				}
				value += armyBase.getFightingForce();
			}
		}
		return value;
	}

	/**
	 * 获取某种类型的士兵的数量
	 * 
	 * @param state
	 * @param armyId
	 * @return
	 */
	public int getCityArmysNumByArmyType(byte armyType) {
		int num = 0;
		if (armysMap.size() == 0) {
			return num;
		}
		for (Map<Byte, ArmyInfo> armyMap : armysMap.values()) {
			for (ArmyInfo army : armyMap.values()) {
				if (army.state != 2 && army.armyBase.getArmyType() == armyType) {
					num += army.getArmyNum();
				}
			}
		}
		return num;
	}

}
