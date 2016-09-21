package com.joymeng.slg.domain.object.build.impl;

import com.joymeng.Const;
import com.joymeng.slg.domain.object.build.BuildComponent;
import com.joymeng.slg.domain.object.build.BuildComponentType;
import com.joymeng.slg.domain.object.build.RoleBuild;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;

public class BuildComponentHelp implements BuildComponent {
	private BuildComponentType buildComType;
	
	public BuildComponentHelp(){
		buildComType = BuildComponentType.BUILD_COMPONENT_HELP;
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
