package com.joymeng.slg.domain.object.build.impl;

import com.joymeng.Const;
import com.joymeng.log.GameLog;
import com.joymeng.slg.domain.object.build.BuildComponent;
import com.joymeng.slg.domain.object.build.BuildComponentType;
import com.joymeng.slg.domain.object.build.RoleBuild;
import com.joymeng.slg.domain.object.build.data.Buildinglevel;
import com.joymeng.slg.domain.object.resource.data.Resourcestype;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;

public class BuildComponentStorage implements BuildComponent {
	private static final Object RESOURCE_TYPE_FOOD = null;
	private static final String RESOURCE_TYPE_METAL = null;
	private static final Object RESOURCE_TYPE_OIL = null;
	private static final Object RESOURCE_TYPE_ALLOY = null;
	private BuildComponentType buildComType;
	long uid;
	int cityID;
	long buildId;
	public BuildComponentStorage(){
		buildComType = BuildComponentType.BUILD_COMPONENT_STORAGE;
	}
	
	@Override
	public void tick(Role role,RoleBuild build,long now) {
		
	}

	@Override
	public void deserialize(String str,RoleBuild build) {
		
	}

	@Override
	public String serialize(RoleBuild build) {
		return null;
	}

	@Override
	public void sendToClient(ParametersEntity params) {
		params.put(buildComType.getKey());
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
		this.uid=uid;
		this.cityID=cityID;
		this.buildId=buildId;	
	}
	@Override
	public void setBuildParams(RoleBuild build) {
		// TODO Auto-generated method stub		
	}
	/**
	 * 获取资源保护的基础
	 * @param paramList
	 */
	public long getBaseResourceByType(RoleBuild build,Resourcestype type) {
		long baseResource = 0;
		if(type.equals(RESOURCE_TYPE_FOOD)){
			Buildinglevel buildinglevel = build.getBuildingLevel();
			baseResource = Long.parseLong(buildinglevel.getParamList().get(0));
		}else if(type.equals(RESOURCE_TYPE_METAL)){
			Buildinglevel buildinglevel = build.getBuildingLevel();
			baseResource = Long.parseLong(buildinglevel.getParamList().get(1));
		}else if (type.equals(RESOURCE_TYPE_OIL)) {
			Buildinglevel buildinglevel = build.getBuildingLevel();
			baseResource = Long.parseLong(buildinglevel.getParamList().get(2));
		}else if (type.equals(RESOURCE_TYPE_ALLOY)) {
			Buildinglevel buildinglevel = build.getBuildingLevel();
			baseResource = Long.parseLong(buildinglevel.getParamList().get(3));
		}
		return baseResource;
	}
	
	/**
	 * 获取Buff增加的资源保护
	 * @param paramList	
	 */
	public long getBuffResourceByType() {
		
		
		return 0;
	}

	@Override
	public boolean isWorking(Role role, RoleBuild build) {
		// TODO Auto-generated method stub
		return false;
	}
}
