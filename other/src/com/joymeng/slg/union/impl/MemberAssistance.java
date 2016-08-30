package com.joymeng.slg.union.impl;

import java.util.ArrayList;
import java.util.List;

import com.joymeng.Instances;
import com.joymeng.common.util.I18nGreeting;
import com.joymeng.log.GameLog;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.domain.object.build.BuildComponentType;
import com.joymeng.slg.domain.object.build.BuildName;
import com.joymeng.slg.domain.object.build.RoleBuild;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.build.impl.BuildComponentResearch;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.role.RoleIcon;
import com.joymeng.slg.domain.object.technology.Technology;
import com.joymeng.slg.domain.timer.TimerLastType;
import com.joymeng.slg.domain.timer.TimerLast;
import com.joymeng.slg.net.SerializeEntity;
import com.joymeng.slg.net.mod.AbstractClientModule;
import com.joymeng.slg.net.mod.RespModuleSet;



/**
 * 联盟成员援助
 * @author tanyong
 *
 */
public class MemberAssistance implements SerializeEntity,Instances{
	long id;
	long uid;
	String name;
	RoleIcon icon = new RoleIcon();
	int cityId;
	long buildId;
	long startTime;//开始
	int effect;//减少的时间
	int maxNum;//最大可以被帮组次数
	String des;
	List<Object> params = new ArrayList<Object>();

	public MemberAssistance() {
	}

	public MemberAssistance(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public RoleIcon getIcon() {
		return icon;
	}

	public void setIcon(RoleIcon icon) {
		this.icon = icon;
	}

	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public long getBuildId() {
		return buildId;
	}

	public void setBuildId(long buildId) {
		this.buildId = buildId;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public int getEffect() {
		return effect;
	}

	public void setEffect(int effect) {
		this.effect = effect;
	}

	public int getMaxNum() {
		return maxNum;
	}

	public void setMaxNum(int maxNum) {
		this.maxNum = maxNum;
	}

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	public List<Object> getParams() {
		return params;
	}

	public void setParams(List<Object> params) {
		this.params = params;
	}

	public boolean init(Role role, TimerLast timer, RoleBuild build, RoleCityAgent city) {
		uid = role.getId();
		cityId = city.getId();
		buildId = build.getId();
		name = role.getName();
		icon.copy(role.getIcon());
		startTime = timer.getStart();
		effect = role.getBuildAssistanceEffect(city.getId());
		maxNum = role.getBuildAssistanceNum(city.getId());
		if (timer.getType() == TimerLastType.TIME_CREATE) {
			des = I18nGreeting.DES_ASSISTANCE_BUILD_CREATE;
			params.add(build.getName());
		} else if (timer.getType() == TimerLastType.TIME_LEVEL_UP) {
			des = I18nGreeting.DES_ASSISTANCE_BUILD_LEVEL_UP;
			params.add(build.getLevel() + 1);
			params.add(build.getName());
		} else if (timer.getType()== TimerLastType.TIME_CURE) {
			String buildkey = build.getName();
			if (buildkey.equals(BuildName.HOSPITAL.getKey())) {
				des = I18nGreeting.DES_ASSISTANCE_BUILD_CURE;
			} else {
				des = I18nGreeting.DES_ASSISTANCE_BUILD_REPAIR;
			}
		} else if (timer.getType() == TimerLastType.TIME_RESEARCH) {
			des = I18nGreeting.DES_ASSISTANCE_BUILD_RESEARCH;
			BuildComponentResearch researchComponent = build.getComponent(BuildComponentType.BUILD_COMPONENT_RESEARCH);
			Technology tech = researchComponent.getResearchingTechnology(city);
			params.add(tech.getTechName());
			params.add(tech.getLevel() + 1);
		}else{
			GameLog.error("error timer type = " + timer.getType().toString());
			return false;
		}
		return true;
	}
	
	@Override
	public void serialize(JoyBuffer out) {
		out.putLong(id);//long 帮助编号
		out.putLong(uid);//long 玩家编号
		out.putPrefixedString(name,JoyBuffer.STRING_TYPE_SHORT);//string 玩家名称
		icon.serialize(out);
		out.putInt(cityId);	//int ★
		out.putLong(buildId);//long★
		out.putLong(startTime);//long★
		out.putPrefixedString(des,JoyBuffer.STRING_TYPE_SHORT);//string 显示描述
		out.putInt(params.size());	//int > 0 上面des的参数列表 ;=0没有参数	
		for (int i = 0 ; i < params.size() ; i++){
			Object param = params.get(i);
			out.putPrefixedString(param.toString(),JoyBuffer.STRING_TYPE_SHORT);//string 参数的字符串
		}
		Role role = world.getRole(uid);
		int helperSize = 0;
		if (role != null && role.getCity(0) != null && role.getCity(0).getUnionHelpers().get(buildId) != null) {
			helperSize = role.getCity(0).getUnionHelpers().get(buildId).size();
		}
		List<UnionHelper> heList = role.getCity(0).getUnionHelpers().get(buildId);
		out.putInt(helperSize);// 当前已被帮助的成员列表
		out.putInt(helperSize);// 当前已被帮助的成员列表
		if (helperSize > 0) {
			for (int i = 0; i < heList.size(); i++) {
				// out.putLong(helpers.get(i).getUid());
				out.putPrefixedString(heList.get(i).getRoleName(), JoyBuffer.STRING_TYPE_SHORT);
			}
		}
		out.putInt(maxNum);//int 最大可被帮助次数
	}
	
	public RespModuleSet sendToClient(byte type){
		RespModuleSet rms = new RespModuleSet();
		sendToClient(rms,type);
		return rms;
	}

	public void sendToClient(RespModuleSet rms,byte type){
		AbstractClientModule module = new AbstractClientModule(){
			@Override
			public short getModuleType() {
				return NTC_DTCD_UNION_ASSISTANCE;
			}
		};
		module.add(type);//操作类型 0 添加，1删除，2修改
		module.add(this);
		rms.addModule(module);
	}

	/**
	 * 能帮助
	 * @param role
	 * @return
	 */
	public boolean couldHelp(Role role) {
		Role currentRole = world.getRole(uid);
		if (currentRole == null) {
			GameLog.error("get Role is fail!");
			return false;
		}
		if (currentRole.getCity(0) == null) {
			GameLog.error("getCity(0) is fail!");
			return false;
		}
		List<UnionHelper> helpers = currentRole.getCity(0).getUnionHelpers().get(buildId);
		if (helpers == null) {
			helpers = new ArrayList<>();
		}
		if (helpers.size() < maxNum && !helpersContainRole(helpers, role)) {
			return true;
		}
		return false;
	}

	/**
	 * 已包含
	 * @param helpers
	 * @param role
	 * @return
	 */
	public boolean helpersContainRole(List<UnionHelper> helpers, Role role) {
		for (int index = 0; index < helpers.size(); index++) {
			UnionHelper helper = helpers.get(index);
			if (helper == null) {
				continue;
			}
			if (helper.getUid() == role.getId()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isMyassistance(RoleBuild build , TimerLast timer) {
		return build.getId() == buildId && build.getCityId() == cityId && startTime == timer.getStart();
	}
	
	public synchronized boolean addhelp(Role role) {
		if (!isHelper(role)) {
			UnionHelper helper = new UnionHelper(role.getId(), role.getName());
			Role target = world.getRole(uid);
			RoleBuild build = target.getCity(cityId).searchBuildById(buildId);
			List<TimerLast> timers = build.getTimers();
			for (int i = 0; i < timers.size(); i++) {
				TimerLast timer = timers.get(i);
				if (timer.getStart() == startTime) {
					build.updateUnionHelperList(target, timer, effect);
					timer.setStart(timer.getStart() - effect);
					// 加入城市帮助的增加
					target.addBuildHelper(0, buildId, helper);
					build.sendToClient(); // 下发更新的建筑时间更新
					return true;
				}
			}
			return false;
		}
		return false;
	}

	/**
	 * 帮助过了
	 * 
	 * @param role
	 * @return
	 */
	public boolean isHelper(Role role) {
		List<UnionHelper> helpers = role.getCity(0).getUnionHelpers().get(buildId);
		if (helpers == null) {
			helpers = new ArrayList<>();
		}
		if (helpers.size() < 1) {
			return false;
		}
		for (int i = 0; i < helpers.size(); i++) {
			UnionHelper helper = helpers.get(i);
			if (helper != null && helper.getUid() == role.getId()) {
				return true;
			}
		}
		return false;
	}
}
