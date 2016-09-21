package com.joymeng.slg.domain.object.redpacket;

import java.util.HashMap;
import java.util.Map;

import com.joymeng.Instances;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.log.GameLog;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.world.GameConfig;

public class RedpacketManager implements Instances {
	private static RedpacketManager instance = new RedpacketManager();

	public static RedpacketManager getInstance() {
		return instance;
	}

	public Long redpacketId = new Long(1L);// 红包的ID
	public Long returnRedpacketId = new Long(1L);// 返还红包的起始ID
	public Long deleteRedpacketId = new Long(1L);// 删除红包的起始ID
	public Map<Long, Redpacket> allRedpacket = new HashMap<Long, Redpacket>();// 所有的红包

	public Long getRedpacketId() {
		return redpacketId;
	}

	public synchronized void setRedpacketId(Long redpacketId) {
		this.redpacketId = redpacketId;
	}

	public Long getReturnRedpacketId() {
		return returnRedpacketId;
	}

	public synchronized void setReturnRedpacketId(Long returnRedpacketId) {
		this.returnRedpacketId = returnRedpacketId;
	}

	public Long getDeleteRedpacketId() {
		return deleteRedpacketId;
	}

	public synchronized void setDeleteRedpacketId(Long deleteRedpacketId) {
		this.deleteRedpacketId = deleteRedpacketId;
	}

	public Map<Long, Redpacket> getAllRedpacket() {
		return allRedpacket;
	}

	public void setAllRedpacket(Map<Long, Redpacket> allRedpacket) {
		this.allRedpacket = allRedpacket;
	}

	/**
	 * 创建一个红包
	 * 
	 * @param role
	 * @param itemId
	 * @param state
	 * @param type
	 * @param unionId
	 * @param greetings
	 * @param redpacketGold
	 * @param redpacketNum
	 * @return
	 */
	public synchronized Redpacket createRedpacket(Role role, String itemId, RedpacketState state, byte type,
			long unionId, String greetings, int redpacketGold, int redpacketNum) {
		Redpacket redpacket = new Redpacket(redpacketId, role, itemId, state, type, unionId, greetings, redpacketGold,
				redpacketNum);
		allRedpacket.put(redpacketId, redpacket);
		redpacketId++;
		return redpacket;
	}

	/**
	 * 第一次加载数据
	 * 
	 * @param uid
	 * @param redpacket
	 */
	public synchronized void firstAddRoleRedpacket(Long uid, Redpacket redpacket) {
		synchronized (allRedpacket) {
			allRedpacket.put(uid, redpacket);
		}
	}

	/**
	 * 查找对应ID的红包
	 * 
	 * @param uid
	 * @param redpacketId
	 * @return
	 */
	public synchronized Redpacket searchRedPacketById(long redpacketId) {
		Redpacket redpacket = allRedpacket.get(redpacketId);
		if (redpacket == null) {
			return null;
		}
		return redpacket;
	}

	/**
	 * 返还红包
	 */
	public synchronized void returnRedpacket() {
		Redpacket redpacket = allRedpacket.get(returnRedpacketId);
		while (redpacket != null
				&& TimeUtils.nowLong() - redpacket.getTime() >= GameConfig.ROLE_REDPACKET_RETURN_TIME) {
			GameLog.info("returnRedpacket redpacketId = " + returnRedpacketId);
			// 返还该红包剩余金额
			redpacket.returnRest();
			redpacket.setState(RedpacketState.INVALID);// 设置红包的状态为失效状态
			// 处理下一个
			returnRedpacketId++;
			redpacket = allRedpacket.get(returnRedpacketId);
		}
	}

	/**
	 * 删除红包
	 */
	public synchronized void deleteRedpacket() {
		Redpacket redpacket = allRedpacket.get(deleteRedpacketId);
		while (redpacket != null
				&& TimeUtils.nowLong() - redpacket.getTime() >= GameConfig.ROLE_REDPACKET_DELETE_TIME) {
			GameLog.info("deleteRedpacket redpacketId = " + deleteRedpacketId);
			// 删除该红包
			allRedpacket.remove(redpacket.getId());
			// 处理下一个
			deleteRedpacketId++;
			redpacket = allRedpacket.get(deleteRedpacketId);
		}
	}
}
