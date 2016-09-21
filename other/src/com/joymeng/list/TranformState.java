package com.joymeng.list;

public class TranformState {
	int  priority;//展示优先级  数字越大 优先级越高
	String name;//服务器名称展示结果
	int serviceId;//服务器实例号
	byte state;//服务器当前状态
	byte record;//玩家是否有角色
	String tips;//服务器待开放：预计开放时间2016.8.15 15:30

	public TranformState(int priority, String name, int serviceId, byte state, byte record, String tips) {
		this.priority = priority;
		this.name = name;
		this.serviceId = serviceId;
		this.state = state;
		this.record = record;
		this.tips = tips;
	}
	
	public int getPriority() {
		return priority;
	}
	
	public void setPriority(int priority) {
		this.priority = priority;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getServiceId() {
		return serviceId;
	}
	
	public void setServiceId(int serviceId) {
		this.serviceId = serviceId;
	}
	
	public byte getState() {
		return state;
	}
	
	public void setState(byte state) {
		this.state = state;
	}

	public byte getRecord() {
		return record;
	}

	public void setRecord(byte record) {
		this.record = record;
	}

	public String getTips() {
		return tips;
	}

	public void setTips(String tips) {
		this.tips = tips;
	}
	
}
