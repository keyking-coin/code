package com.joymeng.slg.domain.object.redpacket;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.joymeng.common.util.JsonUtil;
import com.joymeng.log.GameLog;
import com.joymeng.slg.net.mod.AbstractClientModule;
import com.joymeng.slg.net.mod.RespModuleSet;
import com.joymeng.slg.world.GameConfig;

/**
 * 用户红包集合体
 * 
 * @author houshanping
 *
 */
public class RoleRedpacket {
	long uid;// 用户Uid
	long cumulativeSendGold = 0;// 累计发出金币
	long cumulativeGotGold = 0;// 累计领取金币
	int cumulativeSendNum = 0;// 累计发出的次数
	int cumulativeGotNum = 0;// 累计领取次数
	long daySendGold = 0;// 今天发出金币
	long dayGotGold = 0;// 今天领取金币
	int daySendNum = 0;// 今天发出的次数
	int dayGotNum = 0;// 今天领取次数
	Queue<RedpacketMsg> sendedRedpacket = new ConcurrentLinkedQueue<>();// 发出去的红包Id列表
	List<RedpacketMsg> gotRedpacket = new ArrayList<>();// 领取的红包Id列表
	long saveTime = 0;//存储是时间

	public RoleRedpacket() {
	}

	public RoleRedpacket(long uid) {
		this.uid = uid;
	}

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public long getSaveTime() {
		return saveTime;
	}

	public void setSaveTime(long saveTime) {
		this.saveTime = saveTime;
	}

	public long getCumulativeSendGold() {
		return cumulativeSendGold;
	}

	public void setCumulativeSendGold(long cumulativeSendGold) {
		this.cumulativeSendGold = cumulativeSendGold;
	}

	public long getCumulativeGotGold() {
		return cumulativeGotGold;
	}

	public void setCumulativeGotGold(long cumulativeGotGold) {
		this.cumulativeGotGold = cumulativeGotGold;
	}

	public int getCumulativeSendNum() {
		return cumulativeSendNum;
	}

	public void setCumulativeSendNum(int cumulativeSendNum) {
		this.cumulativeSendNum = cumulativeSendNum;
	}

	public int getCumulativeGotNum() {
		return cumulativeGotNum;
	}

	public void setCumulativeGotNum(int cumulativeGotNum) {
		this.cumulativeGotNum = cumulativeGotNum;
	}

	public long getDaySendGold() {
		return daySendGold;
	}

	public void setDaySendGold(long daySendGold) {
		this.daySendGold = daySendGold;
	}

	public long getDayGotGold() {
		return dayGotGold;
	}

	public void setDayGotGold(long dayGotGold) {
		this.dayGotGold = dayGotGold;
	}

	public int getDaySendNum() {
		return daySendNum;
	}

	public void setDaySendNum(int daySendNum) {
		this.daySendNum = daySendNum;
	}

	public int getDayGotNum() {
		return dayGotNum;
	}

	public void setDayGotNum(int dayGotNum) {
		this.dayGotNum = dayGotNum;
	}

	public Queue<RedpacketMsg> getSendedRedpacket() {
		return sendedRedpacket;
	}

	public void setSendedRedpacket(Queue<RedpacketMsg> sendedRedpacket) {
		this.sendedRedpacket = sendedRedpacket;
	}

	public List<RedpacketMsg> getGotRedpacket() {
		return gotRedpacket;
	}

	public void setGotRedpacket(List<RedpacketMsg> gotRedpacket) {
		this.gotRedpacket = gotRedpacket;
	}

	public void sendClient(RespModuleSet rms) {
		AbstractClientModule module = new AbstractClientModule() {
			@Override
			public short getModuleType() {
				return NTC_DTCD_REDPACKET_ROLE_RP;
			}
		};
		module.add(cumulativeSendGold);// 累计发出的金币 long
		module.add(cumulativeGotGold);// 累计获取的金币 long
		List<RedpacketMsg> temp = new ArrayList<>();
		for (RedpacketMsg redpacketMsg : sendedRedpacket) {
			if (redpacketMsg == null) {
				continue;
			}
			temp.add(redpacketMsg);
			if(temp.size() >= GameConfig.ROLE_REDPACKET_RECORD_MAX){
				break;
			}
		}
		module.add(JsonUtil.ObjectToJsonString(temp));// 发出去的红包记录
		module.add(JsonUtil.ObjectToJsonString(gotRedpacket));// 领取的红包记录
		rms.addModule(module);
	}

	public void addDayAndCumulativeSendGold(long add) {
		cumulativeSendGold += add;
		daySendGold += add;
	}

	public void addDayAndCumulativeGotGold(long add) {
		cumulativeGotGold += add;
		dayGotGold += add;
	}

	public void addDayAndCumulativeSendNum() {
		cumulativeSendNum += 1;
		daySendNum += 1;
	}

	public void addDayAndCumulativeGotNum() {
		cumulativeGotNum += 1;
		dayGotNum += 1;
	}

	public void addGotRedpacketRole(RedpacketMsg redpacketMsg) {
		gotRedpacket.add(redpacketMsg);
	}

	/**
	 * 发一个红包
	 * 
	 * @param time
	 */
	public void addRoleSendRedpacket(Redpacket redpacket) {
		addDayAndCumulativeSendGold(redpacket.getRedpacketGold());// 增加每日和累计发出金币的数量
		addDayAndCumulativeSendNum();// 增加每日和累计发出金币的次数
		RedpacketMsg redpacketMsg = new RedpacketMsg(redpacket.getItemId(), redpacket.getId());
		if (sendedRedpacket.size() >= GameConfig.ROLE_REDPACKET_RECORD_MAX) {
			RedpacketMsg temp = sendedRedpacket.poll();
			GameLog.info("remove roleRedpacket by itemId = " + temp.getItemId() + " redpacketId = "
					+ temp.getRedpacketId());
		}
		sendedRedpacket.add(redpacketMsg);// 把红包加入用户已发送的红包列表
	}

	/**
	 * 重置每日是数据
	 */
	public void resetDailyData() {
		setDayGotGold(0L);
		setDayGotNum(0);
		setDaySendGold(0L);
		setDaySendNum(0);
	}
}
