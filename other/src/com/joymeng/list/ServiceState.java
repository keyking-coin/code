package com.joymeng.list;


public class ServiceState {
	int serviceId;//服务器实例号
	byte state;//服务器当前状态
	int onLineNum;//服务器在线人数
	
	public ServiceState(){
		
	}
	
	public ServiceState(int serviceId){
		this.serviceId = serviceId;
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
	
	public int getOnLineNum() {
		return onLineNum;
	}
	
	public void setOnLineNum(int onLineNum) {
		this.onLineNum = onLineNum;
	}
}
