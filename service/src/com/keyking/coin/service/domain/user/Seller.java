package com.keyking.coin.service.domain.user;

import com.keyking.coin.service.domain.data.EntitySaver;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.util.JsonUtil;

public class Seller extends EntitySaver{
	String time;
	byte   type;//0个人,1公司
	String key;
	String pic;
	float deposit;
	boolean pass;

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

	public float getDeposit() {
		return deposit;
	}

	public void setDeposit(float deposit) {
		this.deposit = deposit;
	}

	public boolean isPass() {
		return pass;
	}

	public void setPass(boolean pass) {
		this.pass = pass;
	}
	
	public String serialize(){
		return JsonUtil.ObjectToJsonString(this);
	}
	
	@Override
	public void serialize(DataBuffer out) {
		out.putUTF(time);
		out.put(type);
		out.putUTF(key);
		out.putUTF(pic);
		out.putUTF(deposit + "");
		out.put((byte)(pass ? 1 : 0));
	}
}
