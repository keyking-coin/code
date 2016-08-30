package com.joymeng.slg.domain.object.effect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.joymeng.Instances;
import com.joymeng.common.util.JsonUtil;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.common.util.StringUtils;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.log.GameLog;
import com.joymeng.slg.domain.event.GameEvent;
import com.joymeng.slg.domain.map.impl.still.res.EffectListener;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.effect.BuffTypeConst.ExtendsType;
import com.joymeng.slg.domain.object.effect.BuffTypeConst.SourceType;
import com.joymeng.slg.domain.object.effect.BuffTypeConst.TargetType;
import com.joymeng.slg.domain.object.effect.data.Buff;
import com.joymeng.slg.domain.object.resource.ResourceTypeConst;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.timer.TimerLast;
import com.joymeng.slg.domain.timer.TimerLastType;
import com.joymeng.slg.net.mod.RespModuleSet;

public class EffectAgent implements Instances {
	Map<String, Effect> techBuffsMap = new HashMap<String, Effect>(); //科技buff
	Map<String, Effect> skillBuffsMap = new HashMap<String, Effect>();//技能buff
	Map<String, Effect> itemBuffsMap = new HashMap<String, Effect>();//道具buff
	Map<Long, List<Effect>> equipBuffsMap = new HashMap<Long, List<Effect>>();//装备buff
	List<Effect> vipBuffMap = new ArrayList<Effect>();//vip buff
	List<TimerLast> timers = new ArrayList<TimerLast>();//倒计时
	long uid;
	
	public EffectAgent() {

	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public Map<String, Effect> getTechBuffsMap() {
		return techBuffsMap;
	}

	public void setTechBuffsMap(Map<String, Effect> techBuffsMap) {
		this.techBuffsMap = techBuffsMap;
	}

	public Map<String, Effect> getSkillBuffsMap() {
		return skillBuffsMap;
	}

	public void setSkillBuffsMap(Map<String, Effect> skillBuffsMap) {
		this.skillBuffsMap = skillBuffsMap;
	}

	public Map<String, Effect> getItemBuffsMap() {
		return itemBuffsMap;
	}

	public void setItemBuffsMap(Map<String, Effect> itemBuffsMap) {
		this.itemBuffsMap = itemBuffsMap;
	}

	public Map<Long, List<Effect>> getEquipBuffsMap() {
		return equipBuffsMap;
	}

	public void setEquipBuffsMap(Map<Long, List<Effect>> equipBuffsMap) {
		this.equipBuffsMap = equipBuffsMap;
	}

	public List<Effect> getVipBuffMap() {
		return vipBuffMap;
	}

	public void setVipBuffMap(List<Effect> vipBuffMap) {
		this.vipBuffMap = vipBuffMap;
	}

	public List<TimerLast> getTimers() {
		return timers;
	}

	public void setTimers(List<TimerLast> timers) {
		this.timers = timers;
	}

	public long getUid() {
		return uid;
	}

	public Effect createEffect(SourceType sType, TargetType type, ExtendInfo extendInfo, boolean isRate, String value,
			int targetTypeId) {
		Effect e = new Effect();
		e.setType(type);
		e.setTargetTypeId(targetTypeId);
		e.setsType(sType);
		e.setExtendInfo(extendInfo);
		e.setPercent(isRate);
		if (isRate) {
			e.setRate(Float.parseFloat(value));
		} else {
			e.setNum((int) Float.parseFloat(value));
		}
		return e;
	}

	public void addTechBuff(Role role,String buffId, String strValue, String techId) {
		Buff buff = dataManager.serach(Buff.class, buffId);
		if (buff == null) {
			GameLog.error("add effect buff params error.");
			return;
		}
		String[] tParams = buff.getBuffTarget().split(":");
		TargetType type = TargetType.search(tParams[1]);
		if (type == null){
			GameLog.error("buff固化表又错了>>>>" + buffId);
			return;
		}
		ExtendInfo extendInfo = null;
		if (tParams.length > 2) {
			extendInfo = new ExtendInfo(ExtendsType.search(tParams[2]), Integer.parseInt(tParams[3]));
		} else {
			extendInfo = new ExtendInfo(ExtendsType.EXTEND_ALL, 0);
		}
		boolean isRate = buff.getBuffdatatype() == 0 ? true : false;
		Effect e = createEffect(SourceType.EFF_TECH, type, extendInfo, isRate, strValue, buff.getBuffobject());
		e.setSourceId(techId);
		techBuffsMap.put(techId, e);
		role.handleEvent(GameEvent.EFFECT_UPDATE, e, false);
	}

	public void removeTechBuff(Role role,String techId) {
		Effect e = techBuffsMap.get(techId);
		if (e == null) {
			return;
		}
		role.handleEvent(GameEvent.EFFECT_UPDATE,e,true);
		techBuffsMap.remove(techId);
	}

	public void addSkillBuff(Role role,String buffId, String strValue, String skillId, long lastTime) {
		TimerLast timer = new TimerLast(TimeUtils.nowLong()/1000, lastTime,
		TimerLastType.TIME_EFFECT);
		timer.registTimeOver(new EffectTimeFinish(this, SourceType.EFF_SKILL,skillId));
		addSkillBuff(role,buffId,strValue,skillId,timer,true);
	}
	
	public void addSkillBuff(Role role,String buffId, String strValue, String skillId, TimerLast timer,boolean runMyself) {
		Buff buff = dataManager.serach(Buff.class, buffId);
		if (buff == null) {
			GameLog.error("add effect buff params error");
			return;
		}
		String[] tParams = buff.getBuffTarget().split(":");
		TargetType type = TargetType.search(tParams[1]);
		if (type == null){
			GameLog.error("buff固化表又错了>>>>" + buffId);
			return;
		}
		ExtendInfo extendInfo = null;
		if (tParams.length > 2) {
			extendInfo = new ExtendInfo(ExtendsType.search(tParams[2]), Integer.parseInt(tParams[3]));
		} else {
			extendInfo = new ExtendInfo(ExtendsType.EXTEND_ALL, 0);
		}
		boolean isRate = buff.getBuffdatatype() == 0 ? true : false;
		Effect e = createEffect(SourceType.EFF_SKILL,type,extendInfo,isRate,strValue,buff.getBuffobject());
		e.setSourceId(skillId);
		if (timer != null){
			e.setTimer(timer);
			e.setRunByAgent(runMyself);
			if (runMyself){
				timers.add(timer);
			}
		}
		skillBuffsMap.put(skillId, e);
		role.handleEvent(GameEvent.EFFECT_UPDATE, e, false);
	}
	
	public void removeSkillBuff(Role role, String skillId) {
		Effect e = skillBuffsMap.get(skillId);
		if (e == null) {
			return;
		}
		role.handleEvent(GameEvent.EFFECT_UPDATE, e, true);
		skillBuffsMap.remove(skillId);
	}

	public void removeSkillBuff(String skillId) {
		Role role = world.getOnlineRole(uid);
		if (role == null) {
			return;
		}
		removeSkillBuff(role, skillId);
	}

	public void addItemBuff(Role role,String buffId, String param, String itemId, long targetId, long lastTime,
			TimerLastType tType) {
		Buff buff = dataManager.serach(Buff.class, buffId);
		if (buff == null) {
			GameLog.error("add effect buff params error.");
			return;
		}
		String[] tParams = buff.getBuffTarget().split(":");
		TargetType type = TargetType.search(tParams[1]);
		if (type == null){
			GameLog.error("buff固化表又错了>>>>" + buffId);
			return;
		}
		ExtendInfo extendInfo = null;
		if (tParams.length > 2) {
			extendInfo = new ExtendInfo(ExtendsType.search(tParams[2]), Integer.parseInt(tParams[3]));
		} else {
			extendInfo = new ExtendInfo(ExtendsType.EXTEND_ALL, 0);
		}
		boolean isRate = buff.getBuffdatatype() == 0 ? true : false;
		Effect e = createEffect(SourceType.EFF_ITEM, type, extendInfo, isRate, param, buff.getBuffobject());
		e.setSourceId(itemId);
		if (targetId != 0) {
			e.setTargetId(String.valueOf(targetId));
		}
		if (lastTime > 0) {
			TimerLast timer = new TimerLast(TimeUtils.nowLong() / 1000, lastTime, tType);
			timer.registTimeOver(new EffectTimeFinish(this,SourceType.EFF_ITEM, itemId));
			e.setTimer(timer);
			e.setRunByAgent(true);
			timers.add(timer);
		}
		itemBuffsMap.put(itemId, e);
		role.handleEvent(GameEvent.EFFECT_UPDATE, e, false);
	}
	
	public void removeItemBuff(String itemId) {
		Role role = world.getRole(uid);
		removeItemBuff(role,itemId);
	}
	
	public void removeItemBuff(Role role , String itemId) {
		Effect e = itemBuffsMap.get(itemId);
		if (e == null) {
			return;
		}
		role.handleEvent(GameEvent.EFFECT_UPDATE, e, true);
		itemBuffsMap.remove(itemId);
	}

	public void addVipBuffs(Role role ,String buffId, float param, long lastTime) {
		Buff buff = dataManager.serach(Buff.class, buffId);
		if (buff == null) {
			GameLog.error("add effect buff params error.");
			return;
		}
		String[] tParams = buff.getBuffTarget().split(":");
		TargetType type = TargetType.search(tParams[1]);
		if (type == null){
			GameLog.error("buff固化表又错了>>>>" + buffId);
			return;
		}
		ExtendInfo extendInfo = null;
		if (tParams.length > 2) {
			extendInfo = new ExtendInfo(ExtendsType.search(tParams[2]), Integer.parseInt(tParams[3]));
		} else {
			extendInfo = new ExtendInfo(ExtendsType.EXTEND_ALL, 0);
		}
		boolean isRate = buff.getBuffdatatype() == 0 ? true : false;
		Effect e = createEffect(SourceType.EFF_VIP, type, extendInfo, isRate, param + "", buff.getBuffobject());
		e.setSourceId("VIP");
		if (lastTime > 0) {
			TimerLast timer = new TimerLast(TimeUtils.nowLong() / 1000, lastTime, TimerLastType.TIME_EFFECT);
			timer.registTimeOver(new EffectTimeFinish(this, SourceType.EFF_VIP, String.valueOf(uid)));
			e.setTimer(timer);
		}
		vipBuffMap.add(e);
		role.handleEvent(GameEvent.EFFECT_UPDATE, e, false);
	}

	public void updateVipBuffTime(long lastTime) {
		for (Effect e : vipBuffMap) {
			if (e.getTimer() != null) {
				long time = e.getTimer().getLast();
				e.getTimer().setLast(lastTime + time);
			}
		}
	}

	public void removeVipBuffs() {
		Role role = world.getOnlineRole(uid);
		if (role == null) {
			return;
		}
		for (int i = 0; i < vipBuffMap.size(); i++) {
			Effect e = vipBuffMap.get(i);
			if (e != null) {
				role.handleEvent(GameEvent.EFFECT_UPDATE, e, true);
			}
		}
		vipBuffMap.clear();
	}

	public void addEquipBuff(Role role ,String buffId, String param, long equipId) {
		Buff buff = dataManager.serach(Buff.class, buffId);
		if (buff == null) {
			GameLog.error("add effect buff params error.");
			return;
		}
		String[] tParams = buff.getBuffTarget().split(":");
		TargetType type = TargetType.search(tParams[1]);
		if (type == null){
			GameLog.error("buff固化表又错了>>>>" + buffId);
			return;
		}
		ExtendInfo extendInfo = null;
		if (tParams.length > 2) {
			extendInfo = new ExtendInfo(ExtendsType.search(tParams[2]), Integer.parseInt(tParams[3]));
		} else {
			extendInfo = new ExtendInfo(ExtendsType.EXTEND_ALL, 0);
		}
		boolean isRate = buff.getBuffdatatype() == 0 ? true : false;
		Effect e = createEffect(SourceType.EFF_EQUIP, type, extendInfo, isRate, param, buff.getBuffobject());
		e.setSourceId(String.valueOf(equipId));
		List<Effect> equipBuffList = equipBuffsMap.get(equipId);
		if (equipBuffList == null) {
			equipBuffList = new ArrayList<Effect>();
		}
		equipBuffList.add(e);
		equipBuffsMap.put(equipId, equipBuffList);
		role.handleEvent(GameEvent.EFFECT_UPDATE, e, false);
	}

	public void removeEquipBuff(Role role ,long equipId) {
		List<Effect> lst = equipBuffsMap.get(equipId);
		if (lst == null) {
			return;
		}
		for (Effect e : lst) {
			role.handleEvent(GameEvent.EFFECT_UPDATE, e, true);
		}
		equipBuffsMap.remove(equipId);
	}

	public void handleAllEffectEvent(Role role) {
		for (Effect techEff : techBuffsMap.values()) {
			role.handleEvent(GameEvent.EFFECT_UPDATE, techEff, false);
		}
		for (List<Effect> equipList : equipBuffsMap.values()) {
			for (int i = 0 ; i < equipList.size() ; i++){
				Effect e = equipList.get(i);
				role.handleEvent(GameEvent.EFFECT_UPDATE, e, false);
			}
		}
		Iterator<Map.Entry<String, Effect>> skillit = skillBuffsMap.entrySet().iterator();
		while (skillit.hasNext()) {
			Map.Entry<String, Effect> entry = skillit.next();
			Effect e = entry.getValue();
			TimerLast timer = e.getTimer();
			if (timer != null) {
				if (timer.over()) {
					skillit.remove();
					continue;
				}
				timer.registTimeOver(new EffectTimeFinish(this, SourceType.EFF_SKILL, String.valueOf(uid)));
				if (e.isRunByAgent()){
					timers.add(timer);
				}
			}
			role.handleEvent(GameEvent.EFFECT_UPDATE, e, false);
		}

		Iterator<Map.Entry<String, Effect>> itemit = itemBuffsMap.entrySet().iterator();
		while (itemit.hasNext()) {
			Map.Entry<String, Effect> entry = itemit.next();
			Effect e = entry.getValue();
			TimerLast timer = e.getTimer();
			if (timer != null) {
				if (timer.over()) {
					itemit.remove();
					continue;
				}
				timer.registTimeOver(new EffectTimeFinish(this, SourceType.EFF_ITEM, String.valueOf(uid)));
				if (e.isRunByAgent()){
					timers.add(timer);
				}
			}
			role.handleEvent(GameEvent.EFFECT_UPDATE, e, false);
		}
		for (int i = 0; i < vipBuffMap.size();) {
			Effect e = vipBuffMap.get(i);
			TimerLast timer = e.getTimer();
			if (timer != null) {
				if (timer.over()) {
					vipBuffMap.remove(i);
					continue;
				}
				timer.registTimeOver(new EffectTimeFinish(this, SourceType.EFF_VIP, String.valueOf(uid)));
				if (e.isRunByAgent()){
					timers.add(timer);
				}
			}
			role.handleEvent(GameEvent.EFFECT_UPDATE, e, false);
			i++;
		}
	}

	@SuppressWarnings("unchecked")
	public void deserialize(String str) {
		if (StringUtils.isNull(str)) {
			return;
		}
		Map<Integer, Object> map = (Map<Integer, Object>) JSON.parse(str);
		Object obj = map.get("1");
		if (obj != null) {
			techBuffsMap = JSON.parseObject(obj.toString(), new TypeReference<Map<String, Effect>>() {});
		}
		obj = map.get("2");
		if (obj != null) {
			skillBuffsMap = JSON.parseObject(obj.toString(), new TypeReference<Map<String, Effect>>() {});
		}
		obj = map.get("3");
		if (obj != null) {
			itemBuffsMap = JSON.parseObject(obj.toString(), new TypeReference<Map<String, Effect>>() {});
		}
		obj = map.get("4");
		if (obj != null) {
			equipBuffsMap = JSON.parseObject(obj.toString(), new TypeReference<Map<Long, List<Effect>>>() {});
		}
		obj = map.get("5");
		if (obj != null) {
			vipBuffMap = JsonUtil.JsonToObjectList(obj.toString(), Effect.class);
		}
	}

	public String serialize() {
		Map<Integer, Object> datas = new HashMap<Integer, Object>();
		datas.put(1, techBuffsMap);
		datas.put(2, skillBuffsMap);
		datas.put(3, itemBuffsMap);
		datas.put(4, equipBuffsMap);
		datas.put(5, vipBuffMap);
		return JsonUtil.ObjectToJsonString(datas);
	}

	private void checkSned(Role role , TimerLast timer){
		TimerLastType type = timer.getType();
		if (type == TimerLastType.TIME_ITEM_TROOPS_LIMIT || type == TimerLastType.TIME_ITEM_IMP_DEF
			|| type == TimerLastType.TIME_ITEM_IMP_ATK || type == TimerLastType.TIME_ITEM_RED_FOOD
			|| type == TimerLastType.TIME_ITEM_IMP_COLL) {
			for (int j = 0; j < role.getCityAgents().size(); j++) {
				RoleCityAgent agent = role.getCityAgents().get(j);
				RespModuleSet rms = new RespModuleSet();
				agent.sendCityStateToClient(role, rms);
				MessageSendUtil.sendModule(rms, role.getUserInfo());
			}
		}
	}
	
	public void tick(Role role,long now) {
		if (timers.size() > 0) {
			for (int i = 0 ; i < timers.size() ; ) {
				TimerLast timer = timers.get(i) ;
				if (timer != null && timer.over(now)) {
					timer.die();
					timers.remove(i);
					if (!role.isOnline()) {
						continue;
					}
					checkSned(role,timer);
				} else {
					i++;
				}
			}
		}
	}

	public boolean isProdBuff(Effect e, ResourceTypeConst type) {
		switch (type) {
		case RESOURCE_TYPE_FOOD:
			if (e.getType() == TargetType.T_B_IMP_FP || e.getType() == TargetType.B_ADD_FOOD_PROD) {
				return true;
			}
			break;
		case RESOURCE_TYPE_METAL:
			if (e.getType() == TargetType.T_B_IMP_MP || e.getType() == TargetType.B_ADD_METAL_PROD) {
				return true;
			}
			break;
		case RESOURCE_TYPE_OIL:
			if (e.getType() == TargetType.T_B_IMP_OP || e.getType() == TargetType.B_ADD_OIL_PROD) {
				return true;
			}
			break;
		case RESOURCE_TYPE_ALLOY:
			if (e.getType() == TargetType.T_B_IMP_AP || e.getType() == TargetType.B_ADD_ALLOY_PROD) {
				return true;
			}
			break;
		default:
			break;
		}
		return false;
	}

	public List<Effect> getProductionTimerBuff(ResourceTypeConst type) {
		List<Effect> proLst = new ArrayList<Effect>();
		long now = TimeUtils.nowLong() / 1000;
		for (Effect e : techBuffsMap.values()) {
			if (e.getTimer() != null && (e.getTimer().getStart() + e.getTimer().getLast()) <= now) {
				if (isProdBuff(e, type)) {
					proLst.add(e);
				}
			}
		}
		for (Effect e : skillBuffsMap.values()) {
			if (e.getTimer() != null && (e.getTimer().getStart() + e.getTimer().getLast()) <= now) {
				if (isProdBuff(e, type)) {
					proLst.add(e);
				}
			}
		}
		for (Effect e : itemBuffsMap.values()) {
			if (e.getTimer() != null && (e.getTimer().getStart() + e.getTimer().getLast()) <= now) {
				if (isProdBuff(e, type)) {
					proLst.add(e);
				}
			}
		}
		for (Effect e : vipBuffMap) {
			if (e.getTimer() != null && (e.getTimer().getStart() + e.getTimer().getLast()) <= now) {
				if (isProdBuff(e, type)) {
					proLst.add(e);
				}
			}
		}
		return proLst;
	}

	public List<TimerLast> getItemBuffTimer() {
		List<TimerLast> itemTimers = new ArrayList<TimerLast>();
		for (Effect e : itemBuffsMap.values()) {
			if (e.getTimer() == null) {
				continue;
			}
			if (e.getTimer().getType() == TimerLastType.TIME_ITEM_TROOPS_LIMIT
					|| e.getTimer().getType() == TimerLastType.TIME_ITEM_IMP_DEF
					|| e.getTimer().getType() == TimerLastType.TIME_ITEM_IMP_ATK
					|| e.getTimer().getType() == TimerLastType.TIME_ITEM_RED_FOOD
					|| e.getTimer().getType() == TimerLastType.TIME_ITEM_IMP_COLL) {
				itemTimers.add(e.getTimer());
			}
		}
		return itemTimers;
	}

	public TimerLast searchItemTimer(TimerLastType type) {
		for (Effect e : itemBuffsMap.values()) {
			if (e.getTimer() != null) {
				if (e.getTimer().getType().equals(type)) {
					return e.getTimer();
				}
			}
		}
		return null;
	}

	public List<Effect> searchProductEffs(ResourceTypeConst type) {
		List<Effect> proLst = new ArrayList<Effect>();
		for (Effect e : techBuffsMap.values()) {
			if (isProdBuff(e, type)) {
				proLst.add(e);
			}
		}
		for (Effect e : skillBuffsMap.values()) {
			if (isProdBuff(e, type)) {
				proLst.add(e);
			}
		}
		for (Effect e : itemBuffsMap.values()) {
			if (isProdBuff(e, type)) {
				proLst.add(e);
			}
		}
		for (int i = 0; i < vipBuffMap.size(); i++) {
			Effect e = vipBuffMap.get(i);
			if (isProdBuff(e, type)) {
				proLst.add(e);
			}
		}
		for (Long lg : equipBuffsMap.keySet()) {
			List<Effect> effect = equipBuffsMap.get(lg);
			for (int i = 0; i < effect.size(); i++) {
				Effect e = effect.get(i);
				if (isProdBuff(e, type)) {
					proLst.add(e);
				}
			}
		}	
		return proLst;
	}

	public void searchCollEffs(TargetType type, List<EffectListener> effList) {
		long now = TimeUtils.nowLong();
		for (Effect e : skillBuffsMap.values()) {
			TimerLast timer = e.getTimer();
			if (timer != null && e.getType() == type && !timer.over(now)) {
				EffectListener el = new EffectListener(e.getRate(),timer);
				effList.add(el);
			}
		}
		for (Effect e : itemBuffsMap.values()) {
			TimerLast timer = e.getTimer();
			if (timer != null && e.getType() == type && !timer.over(now)) {
				EffectListener el = new EffectListener(e.getRate(),timer);
				effList.add(el);
			}
		}
		for (int i = 0; i < vipBuffMap.size(); i++) {
			Effect e = vipBuffMap.get(i);
			TimerLast timer = e.getTimer();
			if (timer != null && e.getType() == type && !timer.over(now)) {
				EffectListener el = new EffectListener(e.getRate(),timer);
				effList.add(el);
			}
		}
	}
	
	public boolean checkHaveBuff(String key){
		if (techBuffsMap.containsKey(key) || skillBuffsMap.containsKey(key) || itemBuffsMap.containsKey(key) || equipBuffsMap.containsKey(key)){
			return true;
		}
		return false;
	}
}
