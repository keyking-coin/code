package com.keyking.admin.data.user;

public class RechargeOrder {
	String time;//��ֵʱ��
	float value;//��ֵ����
	byte type;//��ֵ;�� 0 ������1֧������2΢�š�
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public float getValue() {
		return value;
	}
	public void setValue(float value) {
		this.value = value;
	}
	public byte getType() {
		return type;
	}
	public void setType(byte type) {
		this.type = type;
	}
}
