package com.joymeng.slg.domain.object.technology;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joymeng.Instances;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.dao.DaoData;
import com.joymeng.slg.dao.SqlData;
import com.joymeng.slg.domain.data.SearchFilter;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.technology.data.Tech;
import com.joymeng.slg.domain.object.technology.data.Techupgrade;
import com.joymeng.slg.net.mod.AbstractClientModule;
import com.joymeng.slg.net.mod.RespModuleSet;

public class RoleTechAgent implements Instances{
	private long uid;
	private int cityId;
	private Map<String, Technology> techMap = new HashMap<String, Technology>();
	
	public RoleTechAgent(){
		
	}
	public void init( long uid, int cityId){
		this.uid = uid;
		this.cityId = cityId;
	}
	
	/*
	 * 判断前置条件是否满足
	 */
	
	public boolean JudgTechCondition (List<String> tech){
		if(tech.contains("ture")){
			return true;
		}
		for(String techId:tech){
			Technology tc= getTech(techId);
			if(tc==null){
				return false;
			}
			Tech t1 =dataManager.serach(Tech.class,techId);  
			if(tc.getLevel()==t1.getMaxPoints()){
				return true;
			}
		}
		
		return false;
	}
	
	
	/*
	 * 获取科技
	 */
	public Technology getTech(String techId){
		
		if( techMap.get(techId) == null){
			return null;
		}
		return techMap.get(techId);
		
	}
	
	
	
	/**
	 * 获取科技等级
	 * @param techId
	 * @return
	 */
	public int getTechLevel(String techId){
		if( techMap.get(techId) == null){
			return 0;
		}
		return techMap.get(techId).getLevel();
	}
	
	public Map<String, Technology> getTechMap() {
		return techMap;
	}

	public void setTechMap(Map<String, Technology> techMap) {
		this.techMap = techMap;
	}

	public Technology getTechnology(String techId){
		if(techMap == null){
			return null;
		}
		return techMap.get(techId);
	}
	
	/***
	 * 科技升级
	 * @param role
	 * @param techId
	 */
	public void techLevelup(Role role,final String techId) {
		Technology tech = techMap.get(techId);
		if (tech != null) {
			tech.setLevel(tech.getLevel() + 1);
			techMap.put(techId, tech);
		} else {
			tech = new Technology(uid, cityId, techId, 1);
			techMap.put(techId, tech);
		}
		final int level = tech.getLevel();
		Techupgrade techUpgrade = dataManager.serach(Techupgrade.class, new SearchFilter<Techupgrade>() {
			@Override
			public boolean filter(Techupgrade data) {
				if (data.getTechID().equals(techId) && data.getLevel() == level) {
					return true;
				}
				return false;
			}
		});
		if (techUpgrade == null) {
			return;
		}
		List<String> buffLst = techUpgrade.getBuffList();
		if (buffLst != null) {
			if (tech.getLevel() > 1) {
				role.getEffectAgent().removeTechBuff(role,techId);
			}
			role.getEffectAgent().addTechBuff(role,buffLst.get(0), buffLst.get(1), techId);
		}
	}
	
	public void addTechBuff(Role role,final Technology tech){
		final int level = tech.getLevel();
		Techupgrade techUpgrade = dataManager.serach(Techupgrade.class, new SearchFilter<Techupgrade>(){
			@Override
			public boolean filter(Techupgrade data){
				if(data.getTechID().equals(tech.getTechId()) && data.getLevel() == level){
					return true;
				}
				return false;
			}
		});
		if(techUpgrade == null){
			return;
		}
		List<String> buffLst = techUpgrade.getBuffList();
		if(tech.getLevel() > 1){
			role.getEffectAgent().removeTechBuff(role,tech.getTechId());
		}
		role.getEffectAgent().addTechBuff(role,buffLst.get(0), buffLst.get(1), tech.getTechId());
	}
	
	/**
	 * 发送科技树到客户端
	 * @param rms
	 */
	public void sendToClient(RespModuleSet rms) {
		// 城市的模块
		AbstractClientModule module = new AbstractClientModule() {
			@Override
			public short getModuleType() {
				return NTC_DTCD_ROLE_TECH;
			}
		};

		module.add(techMap.size());// 列表大小， int
		for (Technology tech : techMap.values()) {
			module.add(tech.getTechId()); // 科技ID, String
			module.add(tech.getLevel());// 科技等级, int
		}
		rms.addModule(module);
	}
	
	/**
	 * 获取科技战斗力
	 * @param data
	 */
	public int getTechBattleEffec(){
		int value = 0;
		return value;
	}
	
	public void loadFromData(SqlData data) {
		String techId = data.getString(DaoData.RED_ALERT_TECH_TYPE_ID);
		uid = data.getLong(DaoData.RED_ALERT_GENERAL_UID);
		cityId = data.getInt(DaoData.RED_ALERT_GENERAL_CITY_ID);
		int level = data.getInt(DaoData.RED_ALERT_TECH_LEVEL);
		Technology tech = new Technology(uid, cityId, techId, level);
		techMap.put(techId, tech);
	}
	
	public void serialize(SqlData data){
		JoyBuffer out = JoyBuffer.allocate(4096);
		out.putInt(techMap.size());
		for(Technology tech : techMap.values()){
			out.putPrefixedString(tech.getTechId(),JoyBuffer.STRING_TYPE_SHORT);
			out.putInt(tech.getLevel());
		}
		data.put(DaoData.RED_ALERT_CITY_TECHDATAS, out.arrayToPosition());
	}
	
	public void deserialize(Object data){
		if(data == null){
			return;
		}
		JoyBuffer buffer = JoyBuffer.wrap((byte[])data);
		int size = buffer.getInt();
		for(int i=0; i < size; i++){
			String techId = buffer.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
			int level = buffer.getInt();
			Technology tech = new Technology(uid, cityId, techId, level);
			techMap.put(techId, tech);
		}
	}
	
	/**
	 * 
	 */
	public int getMaxLevelTechs(){
		int count = 0;
		for(Technology tech : techMap.values()){
			Tech data = dataManager.serach(Tech.class, tech.getTechId());
			if(data.getMaxPoints() == tech.getLevel()){
				count ++;
			}
		}
		return count;
	}
	
}
