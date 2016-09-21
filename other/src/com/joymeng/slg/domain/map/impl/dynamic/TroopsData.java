package com.joymeng.slg.domain.map.impl.dynamic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.joymeng.Instances;
import com.joymeng.common.util.JsonUtil;
import com.joymeng.log.GameLog;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.domain.event.GameEvent;
import com.joymeng.slg.domain.map.MapUtil;
import com.joymeng.slg.domain.map.data.Monster;
import com.joymeng.slg.domain.map.fight.result.FightResutTemp;
import com.joymeng.slg.domain.map.impl.MapRoleInfo;
import com.joymeng.slg.domain.object.army.ArmyInfo;
import com.joymeng.slg.domain.object.army.ArmyState;
import com.joymeng.slg.domain.object.army.RoleArmyAgent;
import com.joymeng.slg.domain.object.army.data.Army;
import com.joymeng.slg.domain.object.bag.ItemCell;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.effect.BuffTypeConst.TargetType;
import com.joymeng.slg.domain.object.resource.ResourceTypeConst;
import com.joymeng.slg.domain.object.resource.data.Resourcestype;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.role.RoleArmyAttr;
import com.joymeng.slg.domain.object.task.ConditionType;
import com.joymeng.slg.domain.object.task.TaskEventDelay;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.SerializeEntity;
import com.joymeng.slg.net.mod.RespModuleSet;
import com.joymeng.slg.world.GameConfig;

/**
 * 部队信息,依附于行军部队和驻防部队等
 * @author tanyong
 *
 */
public class TroopsData implements SerializeEntity,Instances{
	long id;//编号
	MapRoleInfo info = new MapRoleInfo();
	int comePosition;//出发的坐标;
	boolean leader = true;//是否是军团长
	List<ArmyEntity> armys = new ArrayList<ArmyEntity>();//部队
	Map<Byte,Map<String,Integer>> packages = new HashMap<Byte,Map<String,Integer>>();//物资(0 道具列表;1装备列表;2材料列表;3资源列表)
	public boolean isLite;
	
	public TroopsData() {
		//TODO Auto-generated constructor stub
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public MapRoleInfo getInfo() {
		return info;
	}

	public void setInfo(MapRoleInfo info) {
		this.info = info;
	}

	public int getComePosition() {
		return comePosition;
	}

	public void setComePosition(int comePosition) {
		this.comePosition = comePosition;
	}

	public List<ArmyEntity> getArmys() {
		return armys;
	}

	public Map<Byte, Map<String, Integer>> getPackages() {
		return packages;
	}

	public void setPackages(Map<Byte, Map<String, Integer>> packages) {
		this.packages = packages;
	}

	public boolean isLeader() {
		return leader;
	}

	public void setLeader(boolean leader) {
		this.leader = leader;
	}

	public boolean checkUnion(long unionId){
		return info.getUnionId() == unionId && unionId != 0;
	}
	
	public boolean checkUnion(TroopsData other){
		if (other == null){
			return false;
		}
		return checkUnion(other.info.getUnionId());
	}
	
	public float computeCollectSpeed(Role role, String key,float value){
		//平均采集速度 = ((士兵1采集力+……士兵N采集力)*(1+科技提升的采集速度比例))/士兵类型总数量
		StringBuffer buffer = new StringBuffer("--------computeCollectSpeed--uid="+role.getJoy_id()+"|key="+key+"------\n");
		float collectSpeed = 0;
		int armyTypeNum = 0;
		for (int i = 0 ; i < armys.size() ; i++){
			ArmyEntity armyEntity = armys.get(i);
			String armyId = armyEntity.getKey();
			Army army = dataManager.serach(Army.class,armyId);
			float rate = role.getResourceCollectEffect(info.getCityId(),armyId, key);
			rate -= value;
			rate = Math.max(0,rate);
			collectSpeed += army.getHarvestSpeed() + army.getHarvestSpeed() * rate;
			buffer.append("armys="+armyEntity.getKey()+"|rate="+rate+"|army.getHarvestSpeed()="+army.getHarvestSpeed()+"|army.getHarvestSpeed() * rate="+(army.getHarvestSpeed() * rate)+"\n");
			armyTypeNum ++;
		}
		float result = collectSpeed / armyTypeNum;
		buffer.append("collectSpeed="+collectSpeed+"|armyTypeNum="+armyTypeNum+"\n");
		GameLog.info(buffer.append("-----------result="+result).toString());
		return result;
	}
	
	public float computeWeight(Role role){
		StringBuffer buffer = new StringBuffer("--------computeWeight---------uid="+role.getJoy_id()+"\n");
		float result = 0;
		for (int i = 0 ; i < armys.size() ; i++){//先算士兵的负重
			ArmyEntity armyEntity = armys.get(i);
			Army army = dataManager.serach(Army.class,armyEntity.getKey());
			float num = army.getWeight() * armyEntity.getSane();
			float rate = RoleArmyAttr.getEffVal(role,TargetType.T_A_IMP_SW,armyEntity.getKey());
			num *= (1 + rate);
			result += num;
			buffer.append("armys="+armyEntity.getKey()+"|weight="+num+"|rate="+rate+"|result="+result+"\n");
		}
		Map<String,Integer> reses = packages.get(ExpeditePackageType.PACKAGE_TYPE_RESOURCE.getType());
		if (reses != null){//计算携带的资源包裹已占用负重
			for (String key : reses.keySet()){
				int num = reses.get(key).intValue();
				Resourcestype resType = dataManager.serach(Resourcestype.class,key);
				result -= num * resType.getWeight();
				buffer.append("reses="+key+"|num="+num+"|weight="+(num * resType.getWeight())+"|result="+result+"\n");
			}
		}
		GameLog.info(buffer.append("----------result="+result+"------------").toString());
		return result;
	}
	
	public void copy(TroopsData data) {
		info.copy(data.info);
		leader       = data.leader;
		comePosition = data.comePosition;
		for(ArmyEntity ae : data.armys){
			ArmyEntity temp = new ArmyEntity();
			temp.copy(ae);
			armys.add(temp);
		}
		packages.putAll(data.packages);
	}
	
	public void sendArmysClient(ParametersEntity param) {
		param.put(armys.size());//int 士兵列表长度
		for (int i = 0 ; i < armys.size() ; i++){
			ArmyEntity entity = armys.get(i);
			param.put(entity.getKey());//string 驻防士兵固化编号
			param.put(entity.getSane());//int 驻防士兵数量
			param.put(entity.getPos());//string 士兵位置
		}
	}
	
	@Override
	public void serialize(JoyBuffer out) {
		out.put((byte)(leader ? 1 : 0));//是不是军团长
		info.serialize(out);
		out.putInt(armys.size());//int 士兵列表长度
		for (int i = 0 ; i < armys.size() ; i++){
			ArmyEntity entity = armys.get(i);
			out.putPrefixedString(entity.getKey(),JoyBuffer.STRING_TYPE_SHORT);//string 驻防士兵固化编号
			out.putLong(entity.getSane());//long 驻防士兵数量
			out.putPrefixedString(entity.getPos(),JoyBuffer.STRING_TYPE_SHORT);//string 士兵位置
		}
	}

	public float computMoveSpeed(Role role) {
		List<String> result = new ArrayList<String>();
		for (int i = 0 ; i < armys.size() ; i++){//计算健全的士兵
			ArmyEntity entity = armys.get(i);
			if (entity.getSane() > 0){
				result.add(entity.getKey());
			}
		}
		if (result.size() == 0){//如果没有健全的士兵，计算伤兵
			for (int i = 0 ; i < armys.size() ; i++){
				ArmyEntity entity = armys.get(i);
				if (entity.getInjurie() > 0){
					result.add(entity.getKey());
				}
			}
		}
		if (result.size() == 0){//如果连伤兵都没有，计算死兵
			for (int i = 0 ; i < armys.size() ; i++){
				ArmyEntity entity = armys.get(i);
				if (entity.getDied()> 0){
					result.add(entity.getKey());
				}
			}
		}
		float speed = 0;
//		if (role != null){
			speed = MapUtil.computeMoveSpeed(result, role, info.getCityId()) * GameConfig.EXPEDITE_SPEED_EFFECT;//王健规定的速度因子
//		}
		return speed;
	}
	
	public void armyBack(RespModuleSet rms,Role role) {
		RoleCityAgent city = role.getCity(info.getCityId());
		RoleArmyAgent armyAgent = city.getCityArmys();
		List<ArmyInfo> sanes = new ArrayList<ArmyInfo>();
		List<ArmyInfo> injuries = new ArrayList<ArmyInfo>();
		List<ArmyInfo> dieds = new ArrayList<ArmyInfo>();
		for (int i = 0 ; i < armys.size() ; i++){
			ArmyEntity entity = armys.get(i);
			String key = entity.getKey();
			int num = entity.getSane();
			if (num > 0){
				ArmyInfo ai = armyAgent.createArmy(key,num,ArmyState.ARMY_OUT_BATTLE.getValue());
				sanes.add(ai);
			}
			num = entity.getInjurie();
			if (num > 0){
				ArmyInfo ai = armyAgent.createArmy(key,num,ArmyState.ARMY_OUT_BATTLE.getValue());
				injuries.add(ai);
			}	
			num = entity.getDied();
			if (num > 0){
				ArmyInfo ai = armyAgent.createArmy(key,num,ArmyState.ARMY_OUT_BATTLE.getValue());
				dieds.add(ai);
			}
		}
		armyAgent.updateArmysState(ArmyState.ARMY_IN_NORMAL.getValue(),sanes);
		armyAgent.updateArmysState(ArmyState.ARMY_IN_HOSPITAL.getValue(),injuries);
		armyAgent.updateArmysState(ArmyState.ARMY_DIED.getValue(),dieds);
		armyAgent.sendToClient(rms,city);//下发城里士兵状态
		//部队叛军、资源田逻辑处理(pve)
		
		//任务事件
//		List<Object> reses = new ArrayList<Object>();
//		Map<String, Integer> values = packages.get((byte)ExpeditePackageType.PACKAGE_TYPE_RESOURCE.ordinal());
//		if(values != null){
//			for(Map.Entry<String, Integer> mapSet : values.entrySet()){
//				ResourceTypeConst resType = ResourceTypeConst.search(mapSet.getKey());
//				if(resType != null){
//					reses.add(resType.getKey());
//					reses.add(mapSet.getValue());
//				}
//			}
//		}
		role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.C_FIGHT_BACK);
	}
	
	public  void addSomethingToPackage(ExpeditePackageType type ,String key,int num){
		MapUtil.addSomethingToPackage(type,key,num,packages);
	}

	public void addResourceToCity(Role role, List<ItemCell> changes, List<Object> objs) {
		role.addPackage(packages, changes, objs);
		packages.clear();// 清空包裹
	}

	public void clear() {
		info.clear();
		comePosition = 0;
		armys.clear();
		packages.clear();//清空包裹
	}

	public boolean couldFight() {
		for (int i = 0 ; i < armys.size() ; i++){
			ArmyEntity entity = armys.get(i);
			if (entity.getSane() > 0){
				return true;
			}
		}
		return false;
	}
	
	public static TroopsData create(Monster monster,int position){
		TroopsData troops = new TroopsData();
		troops.getInfo().setName(monster.getId());
		troops.getInfo().setLevel(monster.getLevel());
		troops.getInfo().setPosition(position);
		List<String> troopsData = monster.getTroops();
		for (int i = 0 ; i < troopsData.size() ; i++){
			String troopsStr = troopsData.get(i);
    		String[] ss = troopsStr.split(":");
    		ArmyEntity army = new ArmyEntity();
    		String key = ss[2];
    		army.setKey(key);
    		int number = Integer.parseInt(ss[3]);
    		army.setSane(number);
    		army.setPos(ss[0] + "," + ss[1]);
    		troops.armys.add(army);
    	}
		return troops;
	}

	public ArmyEntity search(int fightId) {
		for (int i = 0 ; i < armys.size() ; i++){
			ArmyEntity army = armys.get(i);
			if (army.getId() == fightId){
				return army;
			}
		}
		return null;
	}
	
	public void resetArmys(){
		for (int i = 0 ; i < armys.size() ; i++){
			ArmyEntity army = armys.get(i);
			army.reset();
		}
	}
	
	public int getAliveNum(){
		int num = 0;
		for (int i = 0 ; i < armys.size() ; i++){
			ArmyEntity army = armys.get(i);
			if (army.getSane() > 0){
				num += army.getSane();
			}
		}
		return  num;
	}
	
	public int getDieNum(){
		int num = 0;
		for (int i = 0 ; i < armys.size() ; i++){
			ArmyEntity army = armys.get(i);
			num += army.getDied();
		}
		return  num;
	}
	
	public int getInjurieNum(){
		int num = 0;
		for (int i = 0 ; i < armys.size() ; i++){
			ArmyEntity army = armys.get(i);
			num += army.getInjurie();
		}
		return  num;
	}
	
	/**
	 * 
	* @Title: reportEarnings 
	* @Description:  计算收益
	* 
	* @return void
	* @param earnings
	* @param city
	 */
	public void reportEarnings(List<String> earnings,RoleCityAgent city) {
		for (Byte type : packages.keySet()){
			Map<String,Integer> values = packages.get(type);
			ExpeditePackageType etype = ExpeditePackageType.search(type.byteValue());
			for (String key : values.keySet()){
				long have = values.get(key).longValue();
				long total = have;
				if (city != null && etype == ExpeditePackageType.PACKAGE_TYPE_RESOURCE){
					total = city.getResource(ResourceTypeConst.search(key));
				}
				earnings.add(etype.toString() + "|" + key + "|" + have + "|" + total);
			}
		}
	}
	
	
	public void saveSerialize(JoyBuffer out) {
		out.putPrefixedString(JsonUtil.ObjectToJsonString(info), JoyBuffer.STRING_TYPE_SHORT);
		out.putInt(comePosition);//出发的坐标;
		out.put((byte)(leader ? 1 : 0));//是不是军团长
		out.putPrefixedString(JsonUtil.ObjectToJsonString(armys), JoyBuffer.STRING_TYPE_SHORT);
	}
	
	public void saveDeserialize(JoyBuffer buffer) {
		String infoData = buffer.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
		info = JsonUtil.JsonToObject(infoData, MapRoleInfo.class);
		comePosition = buffer.getInt();
		leader = buffer.get() == (byte) 1;
		String armysData = buffer.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
		armys = JSONArray.parseArray(armysData,ArmyEntity.class);
	}

}
