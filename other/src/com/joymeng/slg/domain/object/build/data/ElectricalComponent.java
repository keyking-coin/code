package com.joymeng.slg.domain.object.build.data;

import com.joymeng.slg.domain.object.build.BuildComponentType;

public class ElectricalComponent {
	long buildId;
	//组件
	BuildComponentType componentType;
	//比例
	float powerRatio;
	
	public long getBuildId() {
		return buildId;
	}
	public void setBuildId(long buildId) {
		this.buildId = buildId;
	}
	public BuildComponentType getComponentType() {
		return componentType;
	}
	public void setComponentType(BuildComponentType componentType) {
		this.componentType = componentType;
	}
	public float getPowerRatio() {
		return powerRatio;
	}
	public void setPowerRatio(float powerRatio) {
		this.powerRatio = powerRatio;
	}
	

}
