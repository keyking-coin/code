package com.joymeng.slg.net;

import java.util.concurrent.atomic.AtomicLong;

import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.slg.net.mod.ClientModule;

public class DataModule implements ClientModule {
	
	long id;
	UserInfo userInfo = null;
	byte[] data = null;
	static AtomicLong idCreater = new AtomicLong(0);
	
	public DataModule(byte[] data,UserInfo userInfo){
		this.data = data;
		this.userInfo = userInfo;
		id = idCreater.incrementAndGet();
	}
	
	@Override
	public short getModuleType() {
		return NTC_DTCD_DATA_MODULE;
	}

	@Override
	public void serialize(JoyBuffer out) {
		out.putLong(id);
		out.putInt(data.length);
		out.put(data);
	}

	public long getId() {
		return id;
	}

	public UserInfo getUserInfo() {
		return userInfo;
	}
}
