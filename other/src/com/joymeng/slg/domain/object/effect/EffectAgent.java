package com.joymeng.slg.domain.object.effect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
	ConcurrentHashMap<String, Effect> techBuffsMap = new ConcurrentHashMap<String, Effect>(); // 科技buff
	ConcurrentHashMap<String, Effect> skillBuffsMap = new ConcurrentHashMap<String, Effect>();// 技能buff
	ConcurrentHashMap<String, Effect> itemBuffsMap = new ConcurrentHashMap<String, Effect>();// 道具buff
	ConcurrentHashMap<Long, List<Effect>> equipBuffsMap = new ConcurrentHashMap<Long, List<Effect>>();// 装备buff
	List<Effect> vipBuffMap = new ArrayList<Effect>();// vip buff
	ConcurrentHashMap<String, Effect> unionCityMap = new ConcurrentHashMap<String, Effect>();// 联盟城市

	List<TimerLast> timers = new ArrayList<TimerLast>();// 倒计时
	long uid;

	public EffectAgent() {

	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public Map<String, Effect> getTechBuffsMap() {
		return techBuffsMap;
	}

	public void setTechBuffsMap(ConcurrentHashMap<String, Effect> techBuffsMap) {
		this.techBuffsMap = techBuffsMap;
	}

	public Map<String, Effect> getSkillBuffsMap() {
		return skillBuffsMap;
	}

	public void setSkillBuffsMap(ConcurrentHashMap<String, Effect> skillBuffsMap) {
		this.skillBuffsMap = skillBuffsMap;
	}

	public Map<String, Effect> getItemBuffsMap() {
		return itemBuffsMap;
	}

	public void setItemBuffsMap(ConcurrentHashMap<String, Effect> itemBuffsMap) {
		this.itemBuffsMap = itemBuffsMap;
	}

	public Map<Long, List<Effect>> getEquipBuffsMap() {
		return equipBuffsMap;
	}

	public void setEquipBuffsMap(ConcurrentHashMap<Long, List<Effect>> equipBuffsMap) {
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

	public static Effect createEffect(SourceType sType, TargetType type, ExtendInfo extendInfo, boolean isRate,
			String value, int targetTypeId) {
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
		// 记录effectAgent 添加管理的effect
		// GameLog.info("<BUFF>uid="+getUid()+",Effect|SourceType="+sType.getName()+",TargetType="+type.getName()+",ExtendInfo="+extendInfo.getType().getName());
		return e;
	}

	/**
	 * 
	 * @Title: addTechBuff
	 * @Description: 添加科技类buff
	 * 
	 * @return void
	 * @param role
	 * @param buffId
	 * @param strValue
	 * @param techId
	 */
	public void addTechBuff(Role role, String buffId, String strValue, String techId) {
		Buff buff = dataManager.serach(Buff.class, buffId);
		if (buff == null) {
			GameLog.error("add effect buff params error.");
			return;
		}
		String[] tParams = buff.getBuffTarget().split(":");
		TargetType type = TargetType.search(tParams[1]);
		if (type == null) {
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

		role.handleEvent(GameEvent.EFFECT_UPDATE, e, false);
		techBuffsMap.put(techId, e);
		role.handleEvent(GameEvent.EFFECT_UPDATE, e, false);
	}

	/**
	 * 移除科技类buff
	 * 
	 * @Title: removeTechBuff
	 * @Description:
	 * 
	 * @return void
	 * @param role
	 * @param techId
	 */
	public void removeTechBuff(Role role, String techId) {
		Effect e = techBuffsMap.get(techId);
		if (e == null) {
			return;
		}
		role.handleEvent(GameEvent.EFFECT_UPDATE, e, true);
		techBuffsMap.remove(techId);
	}

	/**
	 * 
	 * @Title: addSkillBuff
	 * @Description: 添加技能类buff
	 * 
	 * @return void
	 * @param role
	 * @param buffId
	 * @param strValue
	 * @param skillId
	 * @param lastTime
	 */
	public void addSkillBuff(Role role, String buffId, String strValue, String skillId, long lastTime) {
		TimerLast timer = new TimerLast(TimeUtils.nowLong() / 1000, lastTime, TimerLastType.TIME_EFFECT);
		timer.registTimeOver(new EffectTimeFinish(this, SourceType.EFF_SKILL, skillId));
		addSkillBuff(role, buffId, strValue, skillId, timer, true);
	}

	/**
	 * 
	 * @Title: addSkillBuff
	 * @Description: 添加技能类buf
	 * 
	 * @return void
	 * @param role
	 * @param buffId
	 * @param strValue
	 * @param skillId
	 * @param timer
	 * @param runMyself
	 */
	public void addSkillBuff(Role role, String buffId, String strValue, String skillId, TimerLast timer,
			boolean runMyself) {
		Buff buff = dataManager.serach(Buff.class, buffId);
		if (buff == null) {
			GameLog.error("add effect buff params error");
			return;
		}
		String[] tParams = buff.getBuffTarget().split(":");
		TargetType type = TargetType.search(tParams[1]);
		if (type == null) {
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
		Effect e = createEffect(SourceType.EFF_SKILL, type, extendInfo, isRate, strValue, buff.getBuffobject());
		e.setSourceId(skillId);
		if (timer != null) {
			e.setTimer(timer);
			e.setRunByAgent(runMyself);
			if (runMyself) {
				timers.add(timer);
			}
		}
		role.handleEvent(GameEvent.EFFECT_UPDATE, e, false);
		skillBuffsMap.put(skillId, e);
		role.handleEvent(GameEvent.EFFECT_UPDATE, e, false);
	}

	/**
	 * 
	 * @Title: removeSkillBuff
	 * @Description: 移除科技类buff
	 * 
	 * @return void
	 * @param role
	 * @param skillId
	 */
	public void removeSkillBuff(Role role, String skillId) {
		Effect e = skillBuffsMap.get(skillId);
		if (e == null) {
			return;
		}
		role.handleEvent(GameEvent.EFFECT_UPDATE, e, true);
		skillBuffsMap.remove(skillId);
	}

	/**
	 * 
	 * @Title: removeSkillBuff
	 * @Description: 移除科技类buff
	 * 
	 * @return void
	 * @param skillId
	 */
	public void removeSkillBuff(String skillId) {
		Role role = world.getOnlineRole(uid);
		if (role == null) {
			return;
		}
		removeSkillBuff(role, skillId);
	}

	/**
	 * 
	 * @Title: addItemBuff
	 * @Description: 添加道具类buff
	 * 
	 * @return void
	 * @param role
	 * @param buffId
	 * @param param
	 * @param itemId
	 * @param targetId
	 * @param lastTime
	 * @param tType
	 */
	public void addItemBuff(Role role, String buffId, String param, String itemId, long targetId, long lastTime,
			TimerLastType tType) {
		Buff buff = dataManager.serach(Buff.class, buffId);
		if (buff == null) {
			GameLog.error("add effect buff params error.");
			return;
		}
		String[] tParams = buff.getBuffTarget().split(":");
		TargetType type = TargetType.search(tParams[1]);
		if (type == null) {
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
			timer.registTimeOver(new EffectTimeFinish(this, SourceType.EFF_ITEM, itemId));
			e.setTimer(timer);
			e.setRunByAgent(true);
			timers.add(timer);
		}
		role.handleEvent(GameEvent.EFFECT_UPDATE, e, false);
		itemBuffsMap.put(itemId, e);
		role.handleEvent(GameEvent.EFFECT_UPDATE, e, false);
	}

	/**
	 * 
	 * @Title: removeItemBuff
	 * @Description: 移除道具buff
	 * 
	 * @return void
	 * @param itemId
	 */
	public void removeItemBuff(String itemId) {
		Role role = world.getRole(uid);
		removeItemBuff(role, itemId);
	}

	/**
	 * 
	 * @Title: removeItemBuff
	 * @Description: 移除道具buff
	 * 
	 * @return void
	 * @param role
	 * @param itemId
	 */
	public void removeItemBuff(Role role, String itemId) {
		Effect e = itemBuffsMap.get(itemId);
		if (e == null) {
			return;
		}
		role.handleEvent(GameEvent.EFFECT_UPDATE, e, true);
		itemBuffsMap.remove(itemId);
	}

	/**
	 * 
	 * @Title: addVipBuffs
	 * @Description: 添加vipbuff
	 * 
	 * @return void
	 * @param role
	 * @param buffId
	 * @param param
	 * @param lastTime
	 */
	public void addVipBuffs(Role role, String buffId, float param, long lastTime) {
		Buff buff = dataManager.serach(Buff.class, buffId);
		if (buff == null) {
			GameLog.error("add effect buff params error.");
			return;
		}
		String[] tParams = buff.getBuffTarget().split(":");
		TargetType type = TargetType.search(tParams[1]);
		if (type == null) {
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
		role.handleEvent(GameEvent.EFFECT_UPDATE, e, false);
		vipBuffMap.add(e);
		role.handleEvent(GameEvent.EFFECT_UPDATE, e, false);
	}

	public void addUnionBuffs(Role role, long cid, String buffId, int num) {
		Buff buff = dataManager.serach(Buff.class, buffId);
		if (buff == null) {
			GameLog.error("add effect buff params error.");
			return;
		}
		String[] tParams = buff.getBuffTarget().split(":");
		TargetType type = TargetType.search(tParams[1]);
		if (type == null) {
			GameLog.error("buff固化表又错了>>>>" + buffId);
			return;
		}
		ExtendInfo extendInfo = null;
		if (tParams.length > 2) {
			extendInfo = new ExtendInfo(ExtendsType.search(tParams[2]), Integer.parseInt(tParams[3]));
		} else {
			extendInfo = new ExtendInfo(ExtendsType.EXTEND_ALL, 0);
		}
		Effect e = createEffect(SourceType.EFF_UCITY, type, extendInfo, false, num + "", buff.getBuffobject());
		e.setSourceId(String.valueOf(cid));
		role.handleEvent(GameEvent.EFFECT_UPDATE, e, false);
		unionCityMap.put(cid + "@@" + buffId, e);
		role.handleEvent(GameEvent.EFFECT_UPDATE, e, false);
	}

	public static void loadUnionBuffs(Map<Long, List<Effect>> unionMaps, long cid, String buffId, int num) {
		Buff buff = dataManager.serach(Buff.class, buffId);
		if (buff == null) {
			GameLog.error("add effect buff params error.");
			return;
		}
		String[] tParams = buff.getBuffTarget().split(":");
		TargetType type = TargetType.search(tParams[1]);
		if (type == null) {
			GameLog.error("buff固化表又错了>>>>" + buffId);
			return;
		}
		ExtendInfo extendInfo = null;
		if (tParams.length > 2) {
			extendInfo = new ExtendInfo(ExtendsType.search(tParams[2]), Integer.parseInt(tParams[3]));
		} else {
			extendInfo = new ExtendInfo(ExtendsType.EXTEND_ALL, 0);
		}
		Effect e = createEffect(SourceType.EFF_UCITY, type, extendInfo, false, num + "", buff.getBuffobject());
		e.setSourceId(String.valueOf(cid));
		List<Effect> ucityBuffList = unionMaps.get(cid);
		if (ucityBuffList == null) {
			ucityBuffList = new ArrayList<Effect>();
		}
		ucityBuffList.add(e);
		unionMaps.put(cid, ucityBuffList);
	}

	/**
	 * 
	 * @Title: removeUnionBuffs
	 * @Description: 道具
	 * 
	 * @return void
	 * @param role
	 * @param equipId
	 */
	public void removeUnionBuffs(Role role, long cid) {
		Iterator<Map.Entry<String, Effect>> unionMaps = unionCityMap.entrySet().iterator();
		while (unionMaps.hasNext()) {
			Map.Entry<String, Effect> entry = unionMaps.next();
			Effect e = entry.getValue();
			if (entry.getKey().startsWith(cid + "@@")) {
				role.handleEvent(GameEvent.EFFECT_UPDATE, e, false);
				unionMaps.remove();
			}

		}
	}

	/**
	 * 
	 * @Title: removeAllUnionBuffs
	 * @Description: 移除联盟城市buff
	 * 
	 * @return void
	 * @param role
	 * @param cid
	 */
	public void removeAllUnionBuffs(Role role) {
		Map<TargetType, Effect> del = new HashMap<TargetType, Effect>();
		for (Effect eff : unionCityMap.values()) {
			del.put(eff.getType(), eff);
		}
		unionCityMap.clear();
		for (Effect e : del.values()) {
			role.handleEvent(GameEvent.EFFECT_UPDATE, e, true);
		}
	}

	/**
	 * 
	 * @Title: updateVipBuffTime
	 * @Description: 移除vipbuff
	 * 
	 * @return void
	 * @param lastTime
	 */
	public void updateVipBuffTime(long lastTime) {
		for (Effect e : vipBuffMap) {
			if (e.getTimer() != null) {
				long time = e.getTimer().getLast();
				e.getTimer().setLast(lastTime + time);
			}
		}
	}

	/**
	 * 
	 * @Title: removeVipBuffs
	 * @Description: 移除vipbuff
	 * 
	 * @return void
	 */
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

	/**
	 * 
	 * @Title: addEquipBuff
	 * @Description: 装备buff
	 * 
	 * @return void
	 * @param role
	 * @param buffId
	 * @param param
	 * @param equipId
	 */
	public void addEquipBuff(Role role, String buffId, String param, long equipId) {
		Buff buff = dataManager.serach(Buff.class, buffId);
		if (buff == null) {
			GameLog.error("add effect buff params error.");
			return;
		}
		String[] tParams = buff.getBuffTarget().split(":");
		TargetType type = TargetType.search(tParams[1]);
		if (type == null) {
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
		role.handleEvent(GameEvent.EFFECT_UPDATE, e, false);
		equipBuffList.add(e);
		equipBuffsMap.put(equipId, equipBuffList);
		role.handleEvent(GameEvent.EFFECT_UPDATE, e, false);
	}

	/**
	 * 
	 * @Title: removeEquipBuff
	 * @Description: 装备buff
	 * 
	 * @return void
	 * @param role
	 * @param equipId
	 */
	public void removeEquipBuff(Role role, long equipId) {
		List<Effect> lst = equipBuffsMap.get(equipId);
		if (lst == null) {
			return;
		}
		for (Effect e : lst) {
			role.handleEvent(GameEvent.EFFECT_UPDATE, e, true);
		}
		equipBuffsMap.remove(equipId);
	}

	/**
	 * 
	 * @Title: handleAllEffectEvent
	 * @Description: 启动更新buff相关操作
	 * 
	 * @return void
	 * @param role
	 */
	public void handleAllEffectEvent(Role role) {
		for (Effect techEff : techBuffsMap.values()) {
			role.handleEvent(GameEvent.EFFECT_UPDATE, techEff, false);
		}
		for (List<Effect> equipList : equipBuffsMap.values()) {
			for (int i = 0; i < equipList.size(); i++) {
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
				if (e.isRunByAgent()) {
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
				if (e.isRunByAgent()) {
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
				if (e.isRunByAgent()) {
					timers.add(timer);
				}
			}
			role.handleEvent(GameEvent.EFFECT_UPDATE, e, false);
			i++;
		}
		for (Effect e : unionCityMap.values()) {
			role.handleEvent(GameEvent.EFFECT_UPDATE, e, false);
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
			try {
				techBuffsMap = JSON.parseObject(obj.toString(), new TypeReference<ConcurrentHashMap<String, Effect>>() {
				});
			} catch (Exception e) {
				GameLog.error(e);
			}
			
		}
		obj = map.get("2");
		if (obj != null) {
			try {
				skillBuffsMap = JSON.parseObject(obj.toString(), new TypeReference<ConcurrentHashMap<String, Effect>>() {
				});
			} catch (Exception e) {
				GameLog.error(e);
			}
			
		}
		obj = map.get("3");
		if (obj != null) {
			try {
				itemBuffsMap = JSON.parseObject(obj.toString(), new TypeReference<ConcurrentHashMap<String, Effect>>() {
				});
			} catch (Exception e) {
				GameLog.error(e);
			}
			
		}
		obj = map.get("4");
		if (obj != null) {
			try {
				equipBuffsMap = JSON.parseObject(obj.toString(),
						new TypeReference<ConcurrentHashMap<Long, List<Effect>>>() {
						});
			} catch (Exception e) {
				GameLog.error(e);
			}
			
		}
		obj = map.get("5");
		if (obj != null) {
			try {
				vipBuffMap = JsonUtil.JsonToObjectList(obj.toString(), Effect.class);
			} catch (Exception e) {
				GameLog.error(e);
			}
		}
		obj = map.get("6");
		if (obj != null) {
			try {
				unionCityMap = JSON.parseObject(obj.toString(), new TypeReference<ConcurrentHashMap<String, Effect>>() {
				});
			} catch (Exception e) {
				GameLog.error(e);
			}
		}
	}

	public String serialize() {
		Map<Integer, Object> datas = new HashMap<Integer, Object>();
		datas.put(1, techBuffsMap);
		datas.put(2, skillBuffsMap);
		datas.put(3, itemBuffsMap);
		datas.put(4, equipBuffsMap);
		datas.put(5, vipBuffMap);
		datas.put(6, unionCityMap);
		return JsonUtil.ObjectToJsonString(datas);
	}

	private void checkSned(Role role, TimerLast timer) {
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

	public void tick(Role role, long now) {
		if (timers.size() > 0) {
			for (int i = 0; i < timers.size();) {
				TimerLast timer = timers.get(i);
				if (timer != null && timer.over(now)) {
					timer.die();
					timers.remove(i);
					if (!role.isOnline()) {
						continue;
					}
					checkSned(role, timer);
				} else {
					i++;
				}
			}
		}
	}

	/**
	 * 
	 * @Title: isProdBuff
	 * @Description: 是否生产类buff
	 * 
	 * @return boolean
	 * @param e
	 * @param type
	 * @return
	 */
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

	/**
	 * 
	 * @Title: getProductionTimerBuff
	 * @Description: 得到资源类buff
	 * 
	 * @return List<Effect>
	 * @param type
	 * @return
	 */
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

	/**
	 * 得到道具类buff
	 * 
	 * @Title: getItemBuffTimer
	 * @Description: 得到道具类buff
	 * 
	 * @return List<TimerLast>
	 * @return
	 */
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

	/**
	 * 
	 * @Title: searchItemTimer
	 * @Description: 得到道具类buff 倒计时 类型
	 * 
	 * @return TimerLast
	 * @param type
	 * @return
	 */
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

	/**
	 * 
	 * @Title: searchProductEffs
	 * @Description:
	 * 
	 * @return List<Effect>
	 * @param type
	 * @return
	 */
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

	/**
	 * 
	 * @Title: searchCollEffs
	 * @Description: 收集类buff
	 * 
	 * @return void
	 * @param type
	 * @param effList
	 */
	public void searchCollEffs(TargetType type, List<EffectListener> effList) {
		long now = TimeUtils.nowLong();
		for (Effect e : skillBuffsMap.values()) {
			TimerLast timer = e.getTimer();
			if (timer != null && e.getType() == type && !timer.over(now)) {
				EffectListener el = new EffectListener(e.getRate(), timer);
				effList.add(el);
			}
		}
		for (Effect e : itemBuffsMap.values()) {
			TimerLast timer = e.getTimer();
			if (timer != null && e.getType() == type && !timer.over(now)) {
				EffectListener el = new EffectListener(e.getRate(), timer);
				effList.add(el);
			}
		}
		for (int i = 0; i < vipBuffMap.size(); i++) {
			Effect e = vipBuffMap.get(i);
			TimerLast timer = e.getTimer();
			if (timer != null && e.getType() == type && !timer.over(now)) {
				EffectListener el = new EffectListener(e.getRate(), timer);
				effList.add(el);
			}
		}
	}

	/**
	 * 
	 * @Title: serchBuffByTargetType
	 * @Description: 得到某块buff数据
	 * 
	 * @return List<Effect>
	 * @param targetType
	 * @return
	 */
	public List<Effect> searchBuffByTargetType(TargetType... targetTypes) {
		List<String> del = new ArrayList<String>();
		List<Effect> list = new ArrayList<Effect>();
		for (TargetType targetType : targetTypes) {
			Iterator<Map.Entry<String, Effect>> skillit = techBuffsMap.entrySet().iterator();
			while (skillit.hasNext()) {
				Map.Entry<String, Effect> entry = skillit.next();
				Effect e = entry.getValue();
				if (e.getType().getValue() == targetType.getValue()) {
					list.add(e);
				}
			}
			//
			for (List<Effect> equipList : equipBuffsMap.values()) {
				for (int i = 0; i < equipList.size(); i++) {
					Effect e = equipList.get(i);
					if (e.getType().getValue() == targetType.getValue()) {
						list.add(e);
					}
				}
			}

			skillit = skillBuffsMap.entrySet().iterator();
			while (skillit.hasNext()) {
				Map.Entry<String, Effect> entry = skillit.next();
				Effect e = entry.getValue();
				TimerLast timer = e.getTimer();
				if (timer != null) {
					if (timer.over()) {
						del.add(entry.getKey());
						continue;
					}
					if (e.getType().getValue() == targetType.getValue()) {
						list.add(e);
					}
				} else {
					if (e.getType().getValue() == targetType.getValue()) {
						list.add(e);
					}
				}
			}
			skillit = itemBuffsMap.entrySet().iterator();
			while (skillit.hasNext()) {
				Map.Entry<String, Effect> entry = skillit.next();
				Effect e = entry.getValue();
				TimerLast timer = e.getTimer();
				if (timer != null) {
					if (timer.over()) {
						del.add(entry.getKey());
						continue;
					}
					if (e.getType().getValue() == targetType.getValue()) {
						list.add(e);
					}
				} else {
					if (e.getType().getValue() == targetType.getValue()) {
						list.add(e);
					}
				}
			}
			for (int i = 0; i < vipBuffMap.size();) {
				Effect e = vipBuffMap.get(i);
				TimerLast timer = e.getTimer();
				if (timer != null) {
					if (timer.over()) {
						vipBuffMap.remove(i);
						continue;
					}
					if (e.getType().getValue() == targetType.getValue()) {
						list.add(e);
					}
				} else {
					if (e.getType().getValue() == targetType.getValue()) {
						list.add(e);
					}
				}
				i++;
			}

			for (Effect e : unionCityMap.values()) {
				if (e.getType().getValue() == targetType.getValue()) {
					list.add(e);
				}
			}
		}
		return list;
	}

	/**
	 * 
	 * @Title: checkHaveBuff
	 * @Description: 是否有buff
	 * 
	 * @return boolean
	 * @param key
	 * @return
	 */
	public boolean checkHaveBuff(String key) {
		if (techBuffsMap.containsKey(key) || skillBuffsMap.containsKey(key) || itemBuffsMap.containsKey(key)
				|| equipBuffsMap.containsKey(key) || unionCityMap.containsKey(key)) {
			return true;
		}
		return false;
	}

	public static void main(String[] args) {
		String a = "111@@bbbb";
		System.out.println(a.startsWith("11@@"));

	}
}
