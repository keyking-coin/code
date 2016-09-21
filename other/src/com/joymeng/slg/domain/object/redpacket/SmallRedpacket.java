package com.joymeng.slg.domain.object.redpacket;

/**
 * 领取红包的用户
 * 
 * @author houshanping
 *
 */
public class SmallRedpacket {
	long uid;// 领取用户ID
	String name;// 领取用户的名称
	long time;// 领取的时间
	long redpacketUid;// 红包的用户UId
	long redpacketId;// 红包的ID
	int gotGold;// 领取到的奖励数量

	public SmallRedpacket() {
	}

	public SmallRedpacket(long uid, String name, long time, long redpacketUid, long redpacketId, int gotGold) {
		this.uid = uid;
		this.name = name;
		this.time = time;
		this.redpacketUid = redpacketUid;
		this.redpacketId = redpacketId;
		this.gotGold = gotGold;
	}

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public long getRedpacketUid() {
		return redpacketUid;
	}

	public void setRedpacketUid(long redpacketUid) {
		this.redpacketUid = redpacketUid;
	}

	public long getRedpacketId() {
		return redpacketId;
	}

	public void setRedpacketId(long redpacketId) {
		this.redpacketId = redpacketId;
	}

	public int getGotGold() {
		return gotGold;
	}

	public void setGotGold(int gotGold) {
		this.gotGold = gotGold;
	}
}
