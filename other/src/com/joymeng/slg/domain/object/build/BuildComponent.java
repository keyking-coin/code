package com.joymeng.slg.domain.object.build;

import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.timer.TimerOver;
import com.joymeng.slg.net.ParametersEntity;

/**
 * 功能组件
 * @author tanyong
 *
 */
public interface BuildComponent extends TimerOver{
	
	public BuildComponentType getBuildComponentType();
	
	public void init(long uid, int cityId, long buildId, String buildID);
	
	public void setBuildParams(RoleBuild build);
	
	public void tick(Role role,RoleBuild build,long now);
	
	public void deserialize(String str,RoleBuild build);
	
	public String serialize(RoleBuild build);
	
	public void sendToClient(ParametersEntity params);
	
	public boolean isWorking(Role role,RoleBuild build);
	
	
}
