package com.keyking.coin.service.domain.friend;

import com.keyking.coin.service.domain.data.EntitySaver;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.resp.module.Module;
import com.keyking.coin.service.net.resp.module.ModuleResp;

public class Friend extends EntitySaver{
	long uid;//我的编号
	long fid;//朋友编号
	byte pass;//是否通过 0 申请,1通过。
	String time;//申请时间,或者是通过实践
	String other;//验证信息
	
	public long getUid() {
		return uid;
	}
	
	public void setUid(long uid) {
		this.uid = uid;
	}
	
	public long getFid() {
		return fid;
	}
	
	public void setFid(long fid) {
		this.fid = fid;
	}
	
	public byte getPass() {
		return pass;
	}
	
	public void setPass(byte pass) {
		this.pass = pass;
	}
	
	public String getTime() {
		return time;
	}
	
	public void setTime(String time) {
		this.time = time;
	}
	
	public String getOther() {
		return other;
	}
	
	public void setOther(String other) {
		this.other = other;
	}

	@Override
	public void serialize(DataBuffer out) {
		out.putLong(uid);
		out.putLong(fid);
		UserCharacter user = CTRL.search(fid);
		out.putUTF(user.getAccount());
		out.putUTF(user.getNikeName());
		out.putUTF(user.getFace());
		out.put(pass);
		out.putUTF(time == null ? "" : time);
		out.putUTF(other == null ? "" : other);
	}
	
	public ModuleResp clientMessage(byte type){
		Module module = new Module();
		module.setCode(Module.MODULE_CODE_FRIEND);
		module.setFlag(type);
		module.add(this);
		ModuleResp modules = new ModuleResp();
		modules.addModule(module);
		return modules;
	}
	
	public ModuleResp clientMessage(ModuleResp modules , byte type){
		Module module = new Module();
		module.setCode(Module.MODULE_CODE_FRIEND);
		module.setFlag(type);
		module.add(this);
		modules.addModule(module);
		return modules;
	}

	public void save() {
		if (needSave){
			DB.getFriendDao().save(this);
			needSave = false;
		}
	}

	public void del() {
		DB.getFriendDao().delete(uid,fid);
	}
}
