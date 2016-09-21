package com.joymeng.slg.domain.forbidden;

import com.joymeng.Instances;
import com.joymeng.slg.dao.DaoData;
import com.joymeng.slg.dao.SqlData;

public class Forbidden implements DaoData, Instances {
	long uid;// 玩家uid
	byte type;// 类型 1-角色禁言 2-角色封号 3-帐号等号 4-设备封号
	String startTime; // 开始时间
	String endTime; // 持续时间
	String uuid;// 设备禁封专用

	boolean savIng = false;

	public Forbidden(long uid, byte type, String startTime, String endTime, String uuid) {
		this.uid =uid;
		this.type =type;
		this.startTime =startTime;
		this.endTime =endTime;
		this.uuid =uuid;
	}
	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@Override
	public String table() {
		return TABLE_RED_ALERT_PLAY_BAN;
	}

	@Override
	public String[] wheres() {
		String[] result = new String[2];
		result[0] = DaoData.RED_ALERT_GENERAL_UID;
		result[1] = DaoData.RED_ALERT_FORBIDDEN_TYPE;
		return result;
	}

	@Override
	public boolean delete() {
		return false;
	}

	@Override
	public void insertData(SqlData data) {
		saveToData(data);
	}

	@Override
	public void save() {
		if (savIng) {
			return;
		}
		savIng = true;
		taskPool.saveThread.addSaveData(this);
	}

	@Override
	public void loadFromData(SqlData data) {

	}

	@Override
	public void saveToData(SqlData data) {
		data.put(DaoData.RED_ALERT_GENERAL_UID, uid);
		data.put(DaoData.RED_ALERT_FORBIDDEN_TYPE, type);
		data.put(DaoData.RED_ALERT_STARTTIME, startTime);
		data.put(DaoData.RED_ALERT_ENDTIME, endTime);
		data.put(DaoData.RED_ALERT_ROLE_UUID, uuid);
	}

	@Override
	public void over() {
		savIng = false;
	}

	@Override
	public boolean saving() {
		return savIng;
	}

}
