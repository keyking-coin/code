package com.keyking.admin.data.user;

public class Seller {
	String time;//��֤ʱ��
	byte   type;//0����,1��˾
	String key;//���֤�ţ�����Ӫҵִ�պ�
	String pic;//������ͼƬ����
	boolean pass;//�Ƿ���ͨ��
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public byte getType() {
		return type;
	}
	public void setType(byte type) {
		this.type = type;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getPic() {
		return pic;
	}
	public void setPic(String pic) {
		this.pic = pic;
	}
	public boolean isPass() {
		return pass;
	}
	public void setPass(boolean pass) {
		this.pass = pass;
	}
}
