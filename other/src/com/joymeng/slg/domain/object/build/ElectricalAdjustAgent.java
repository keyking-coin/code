package com.joymeng.slg.domain.object.build;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.joymeng.Const;
import com.joymeng.Instances;
import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.JsonUtil;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.common.util.StringUtils;
import com.joymeng.slg.domain.object.build.data.ElectricalComponent;
import com.joymeng.slg.domain.object.build.impl.BuildComponentProduction;
import com.joymeng.slg.domain.object.effect.Effect;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.mod.AbstractClientModule;
import com.joymeng.slg.net.mod.RespModuleSet;

/**
 * 
 * @package: com.joymeng.slg.domain.object.build
 * @ClassName: ElectricalAdjustAgent
 * @author xufangliang
 * @date 2016年9月20日 上午9:31:49
 * @Description: 电力条件系统
 *
 */
public class ElectricalAdjustAgent implements Instances {

	// key build:comkey
	Map<String, ElectricalComponent> roleBuilds = new HashMap<String, ElectricalComponent>();

	/**
	 * 
	 * @Title: createElectricalComponent
	 * @Description: 生成对应的电力调节对象
	 * 
	 * @return ElectricalComponent
	 * @param bid
	 * @param componentType
	 * @return
	 */
	public ElectricalComponent createElectricalComponent(long bid, BuildComponentType componentType) {
		ElectricalComponent com = new ElectricalComponent();
		com.setBuildId(bid);
		com.setComponentType(componentType);
		com.setPowerRatio(Const.DEFAULT_CONSUMPTION);
		return com;
	}

	/**
	 * 
	 * @Title: search
	 * @Description: 得到对象
	 * 
	 * @return ElectricalComponent
	 * @param bid
	 * @param componentType
	 * @return
	 */
	public ElectricalComponent search(long bid, String componentType) {
		return roleBuilds.get(bid + ":" + componentType);
	}

	/**
	 * 
	 * @Title: searchPoweRatio
	 * @Description: 得到具体数值
	 * 
	 * @return float
	 * @param bid
	 * @param componentType
	 * @return
	 */
	public float searchPoweRatio(long bid, String componentType) {
		ElectricalComponent ecom = search(bid, componentType);
		if (ecom == null)
			return Const.DEFAULT_CONSUMPTION;
		else
			return ecom.getPowerRatio();
	}

	/**
	 * 
	 * @Title: addElectricalComponents
	 * @Description: 添加组件
	 * 
	 * @return void
	 * @param bid
	 * @param componentType
	 */
	public void addElectricalComponents(ElectricalComponent com) {
		roleBuilds.put(com.getBuildId() + ":" + com.getComponentType().getKey(), com);
	}

	/**
	 * 
	 * @Title: motifyElectricalComponents
	 * @Description: 修改
	 * 
	 * @return void
	 * @param com
	 * @param powerRatio
	 */
	public void motifyElectricalComponents(ElectricalComponent com, float powerRatio) {
		com.setPowerRatio(powerRatio);
		addElectricalComponents(com);
	}

	public static boolean isPowerType(BuildComponent component) {
		if (component.getBuildComponentType() == BuildComponentType.BUILD_COMPONENT_ARMYTRAIN
				|| component.getBuildComponentType() == BuildComponentType.BUILD_COMPONENT_ARMYTRAIN
				|| component.getBuildComponentType() == BuildComponentType.BUILD_COMPONENT_ARMYTRAIN
				|| component.getBuildComponentType() == BuildComponentType.BUILD_COMPONENT_ARMYTRAIN) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @Title: checkPowerType
	 * @Description: 检测类型
	 * 
	 * @return BuildComponent
	 * @param roleBuild
	 * @param buildType
	 * @return
	 */
	private BuildComponent checkPowerType(RoleBuild roleBuild, String buildType) {
		if (roleBuild != null) {
			if (buildType.equals(BuildComponentType.BUILD_COMPONENT_ARMYTRAIN.getKey())
					|| buildType.equals(BuildComponentType.BUILD_COMPONENT_RESEARCH.getKey())
					|| buildType.equals(BuildComponentType.BUILD_COMPONENT_PRODUCTION.getKey())
					|| buildType.equals(BuildComponentType.BUILD_COMPONENT_STORAGE.getKey())) {
				BuildComponentType type = BuildComponentType.search(buildType);
				if (type != null) {
					if (roleBuild.getComponent(type) == null) {
						return null;
					} else {
						return roleBuild.getComponent(type);
					}
				}
			}
		}
		return null;
	}

	public boolean checkPower(RoleCityAgent agent, RoleBuild build, String componentType, float powerRatio) {
		int have = 0, use = 0, need = 0;
		have = agent.getAllPower();
		use = agent.getUserPower(new RoleBuild[] { build });
		need = build.getCostPowerModify(agent, componentType, powerRatio);
		return have >= use + need;
	}
	
	public void oldLoadData(RoleCityAgent agent){
		if(roleBuilds.size()<=0){
			List<RoleBuild> builds = agent.getBuilds();
			for(RoleBuild build : builds){
				build.initPowerRatio(agent);
			}
		}
	}

	/**
	 * 
	 * @Title: isCanMotify
	 * @Description: 是否可以修改
	 * 
	 * @return int
	 * @param role
	 * @param agent
	 * @param roleBuild
	 * @param componentType
	 * @param powerRatio
	 * @return
	 */
	public int motify(Role role, RoleCityAgent agent, RoleBuild roleBuild, String componentType, float powerRatio) {
		// 校验等级
		if (roleBuild.getLevel() < Const.ELECTR_LEVLE) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ROLE_BUILD_LEVEL_NOEXIST);
			return ErrorCodeConst.ERR_NORMAL.getKey();
		}
		// 校验类型
		BuildComponent com = checkPowerType(roleBuild, componentType);
		// 资源类型不同
		if (com == null) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ROLE_BUILD_TYPE_ERROR);
			return ErrorCodeConst.ERR_NORMAL.getKey();
		}
		// 是否最大
		ElectricalComponent coms = search(roleBuild.getId(), componentType);
		if (coms == null) {
			coms = createElectricalComponent(roleBuild.getId(), BuildComponentType.search(componentType));
			addElectricalComponents(coms);
		}
		if (coms.getPowerRatio() == Const.MAX_RATIO) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ROLE_BUILD_POWER_MAX);
			return ErrorCodeConst.ERR_NORMAL.getKey();
		}
		// 校验电力 是否足够
		if (!checkPower(agent, roleBuild, componentType, powerRatio)) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_BUILD_NO_POWER);
			return ErrorCodeConst.ERR_NORMAL.getKey();
		}
		// 校验使用状态
		if (com.isWorking(role, roleBuild)) {// 正在工作
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ROLE_BUILD_WORKING);
			return ErrorCodeConst.ERR_NORMAL.getKey();
		}
		if (componentType.equals(BuildComponentType.BUILD_COMPONENT_PRODUCTION.getKey())) {// 先收取
			BuildComponentProduction production = (BuildComponentProduction) com;
			production.updateResBuffRate();
		}
		coms.setPowerRatio(powerRatio);
		addElectricalComponents(coms);
		// 下发客户端
		this.sendToClient(role, agent);

		return ErrorCodeConst.SUC_RETURN.getKey();
	}

	public void sendToClient(Role role, RoleCityAgent agent) {
		List<Down> list = new ArrayList<Down>();
		for (ElectricalComponent elec : roleBuilds.values()) {
			RoleBuild rb = agent.searchBuildById(elec.getBuildId());
			if (rb != null) {
				Down down = new Down(elec.getBuildId(), rb.getBuildId(), rb.getLevel(),
						elec.getComponentType().getKey(), elec.getPowerRatio());
				list.add(down);
			}
		}
		if (role != null) {
			RespModuleSet rms = new RespModuleSet();
			AbstractClientModule module = new AbstractClientModule() {
				@Override
				public short getModuleType() {
					return NTC_DTCD_ECECT;
				}
			};
			module.add(list.size());
			for (Down down : list) {
				// long
				module.add(down.bid);
				// buildkey
				module.add(down.getBuildKey());
				// level
				module.add(down.getLevel());
				// comkey
				module.add(down.getComKey());
				// powerRatio
				module.add(down.getPowerRatio());
			}

			MessageSendUtil.sendModule(rms, role.getUserInfo());
		}
	}

	public void sendToClient(RespModuleSet rms, RoleCityAgent agent) {
		List<Down> list = new ArrayList<Down>();
		for (ElectricalComponent elec : roleBuilds.values()) {
			RoleBuild rb = agent.searchBuildById(elec.getBuildId());
			if (rb != null) {
				Down down = new Down(elec.getBuildId(), rb.getBuildId(), rb.getLevel(),
						elec.getComponentType().getKey(), elec.getPowerRatio());
				list.add(down);
			}
		}
		// RespModuleSet rms = new RespModuleSet();
		AbstractClientModule module = new AbstractClientModule() {
			@Override
			public short getModuleType() {
				return NTC_DTCD_ECECT;
			}
		};
		module.add(list.size());
		for (Down down : list) {
			// long
			module.add(down.bid);
			// buildkey
			module.add(down.getBuildKey());
			// level
			module.add(down.getLevel());
			// comkey
			module.add(down.getComKey());
			// powerRatio
			module.add((int)(down.getPowerRatio()*100));
		}
		rms.addModule(module);
		// MessageSendUtil.sendModule(rms, role.getUserInfo());
	}

	public class Down {
		long bid;
		String buildKey;
		int level;
		String comKey;
		float powerRatio;

		public Down(long bid, String buildKey, int level, String comKey, float powerRatio) {
			this.bid = bid;
			this.buildKey = buildKey;
			this.level = level;
			this.comKey = comKey;
			this.powerRatio = powerRatio;
		}

		public long getBid() {
			return bid;
		}

		public void setBid(long bid) {
			this.bid = bid;
		}

		public String getBuildKey() {
			return buildKey;
		}

		public void setBuildKey(String buildKey) {
			this.buildKey = buildKey;
		}

		public int getLevel() {
			return level;
		}

		public void setLevel(int level) {
			this.level = level;
		}

		public String getComKey() {
			return comKey;
		}

		public void setComKey(String comKey) {
			this.comKey = comKey;
		}

		public float getPowerRatio() {
			return powerRatio;
		}

		public void setPowerRatio(float powerRatio) {
			this.powerRatio = powerRatio;
		}

	}

	public String serialize() {
		String result = JsonUtil.ObjectToJsonString(roleBuilds);
		return result;
	}

	public void deserialize(String str) {
		if (StringUtils.isNull(str)) {
			return;
		}
		roleBuilds = JSON.parseObject(str, new TypeReference<Map<String, ElectricalComponent>>() {});
//		roleBuilds = JsonUtil.JsonToObjectMap(str, String.class, ElectricalComponent.class);
	}

}
