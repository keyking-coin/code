package com.keyking.admin.data.user;

public class Seller {
	String time;//认证时间
	byte   type;//0个人,1公司
	String key;//身份证号，或者营业执照号
	String pic;//服务器图片名称
	boolean pass;//是否已通过
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
