package com.joymeng.slg.domain.object.build.impl;

import com.joymeng.slg.domain.object.build.BuildComponent;
import com.joymeng.slg.domain.object.build.BuildComponentType;
import com.joymeng.slg.domain.object.build.RoleBuild;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
/**
 * 电力组件
 * @author tanyong
 *
 */
public class BuildComponentElectrical implements BuildComponent {
	private BuildComponentType buildComType;
	
	public BuildComponentElectrical(){
		buildComType = BuildComponentType.BUILD_COMPONENT_ELECTRICAL;
	}
	@Override
	public void tick(Role role,RoleBuild build,long now) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deserialize(String str,RoleBuild build) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String serialize(RoleBuild build) {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		
	}
	@Override
	public void init(long uid, int cityID, long buildId, String buildID) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setBuildParams(RoleBuild build) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean isWorking(Role role, RoleBuild build) {
		// TODO Auto-generated method stub
		return false;
	}

}
