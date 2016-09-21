package com.joymeng.slg.domain.object.build.impl;

import java.util.HashMap;
import java.util.Map;

import com.joymeng.Const;

import com.joymeng.common.util.JsonUtil;
import com.joymeng.common.util.StringUtils;

import com.joymeng.slg.domain.object.army.ArmyInfo;

import com.joymeng.slg.domain.object.build.BuildComponent;
import com.joymeng.slg.domain.object.build.BuildComponentType;
import com.joymeng.slg.domain.object.build.RoleBuild;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.world.GameConfig;

public class BuildComponentDeal implements BuildComponent {

	// 交易所新增CD清除功能，每次清除CD所需的金币数量：10/20/40/60/80/100，6次之后每次消耗100金币；每日0点更新已使用的清除次数；
	// 交易所将税率取消，资源转换时不在扣除玩家资源；
	// 交易所资源转换CD时间，调整为固定时间12小时；

	private BuildComponentType buildComType;
	long uid;
	int cityId;
	long buildId;
	int count;// 清除交易CD次数

	public BuildComponentDeal() {
		buildComType = BuildComponentType.BUILD_COMPONENT_DEAL;
	}

	public int getCount() {
		return count;
	}

	/**
	 * 根据清除CD次数，返回money
	 */

	public int getMoney(int count) {
		int money = count * 20;
		money = Math.max(money, Const.MIN_DEAL_ACCELERATE);
		return Math.min(Const.MAX_DEAL_ACCELERATE, money);
	}

	public void addCount() {
		count++;
	}

	public void initCount() {
		count = 0;
	}

	@Override
	public void tick(Role role, RoleBuild build, long now) {

	}

	@Override
	public void deserialize(String str, RoleBuild build) {
		if (StringUtils.isNull(str)) {
			return;
		}
		Map<String, String> map = JsonUtil.JsonToObjectMap(str, String.class, String.class);
		count = Byte.parseByte(map.get("count"));
	}

	@Override
	public String serialize(RoleBuild build) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("count", String.valueOf(count));
		String result = JsonUtil.ObjectToJsonString(map);
		return result;
	}

	@Override
	public void sendToClient(ParametersEntity params) {
		params.put(buildComType.getKey());
		int money = getMoney(count);
		params.put(money);
		params.put(GameConfig.BUILD_TRAND_CD);
	}

	@Override
	public BuildComponentType getBuildComponentType() {
		return buildComType;
	}

	@Override
	public void finish() {

	}

	@Override
	public void init(long uid, int cityID, long buildId, String buildID) {
		this.uid = uid;
		this.cityId = cityID;
		this.buildId = buildId;
		initCount();
	}

	@Override
	public void setBuildParams(RoleBuild build) {

	}


	@Override
	public boolean isWorking(Role role, RoleBuild build) {
		// TODO Auto-generated method stub
		return false;
	}

}
