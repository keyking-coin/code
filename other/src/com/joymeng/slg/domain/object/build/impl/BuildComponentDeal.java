package com.joymeng.slg.domain.object.build.impl;

import java.util.List;

import com.joymeng.slg.domain.object.army.ArmyInfo;
import com.joymeng.slg.domain.object.build.BuildComponent;
import com.joymeng.slg.domain.object.build.BuildComponentType;
import com.joymeng.slg.domain.object.build.RoleBuild;
import com.joymeng.slg.domain.object.resource.data.Resourcestype;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;

public class BuildComponentDeal implements BuildComponent {
	private BuildComponentType buildComType;
	
	public BuildComponentDeal(){
		buildComType = BuildComponentType.BUILD_COMPONENT_DEAL;
	}

	/**
	 * 资源援助
	 * @param role
	 * @param targetRole
	 * @param cityId
	 * @param buildId
	 * @param aidResources
	 * @param money
	 * @return
	 */
	public boolean aidResources(Role role,Role targetRole,int cityId,long buildId,List<Resourcestype> aidResources) {
		
		return true;
	}
	
	/**
	 * 士兵援助
	 * @param role
	 * @param targetRole
	 * @param cityId
	 * @param buildId
	 * @param aidArmys
	 * @param money
	 * @return
	 */
	public boolean aidArmys(Role role,Role targetRole,int cityId,long buildId,List<ArmyInfo> aidArmys) {
		
		return true;
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

}
