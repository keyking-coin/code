package com.joymeng.slg.domain.chat;

import java.util.HashMap;
import java.util.Map;

import com.joymeng.Instances;
import com.joymeng.slg.world.GameConfig;
/**
 * 联盟消息体
 * @author houshanping
 *
 */
public class UnionChatMsg implements Instances {

	public long unionId;
	public Map<Long, ChatMsg> unionMsgs = new HashMap<Long, ChatMsg>(); // 联盟聊天内容

	
	public long getUnionId() {
		return unionId;
	}

	public void setUnionId(long unionId) {
		this.unionId = unionId;
	}

	public Map<Long, ChatMsg> getUnionMsgs() {
		return unionMsgs;
	}

	public void setUnionMsgs(Map<Long, ChatMsg> unionMsgs) {
		this.unionMsgs = unionMsgs;
	}

	
	/**
	 * 获取联盟聊天记录个数
	 * @return
	 */
	public int unionMsgsNum() {
		if (unionMsgs == null) {
			return 0;
		} else {
			return unionMsgs.size();
		}
	}
	
	/**
	 * 添加联盟聊天消息
	 * 
	 * @param unionId
	 * @param msg
	 */
	public synchronized void addUnionChat(Long unionId, ChatMsg msg) {		
		long key = chatKeyData.getUnionMsgsId(unionId);
		if (unionMsgsNum() >= GameConfig.UNION_CHAT_MES_MAX_NUM) {
			long removeId = chatKeyData.unionMsgHeadIncrement(unionId);
			unionMsgs.remove(removeId);
		}
		unionMsgs.put(key, msg);		
	}

	/**
	 * 获取联盟消息的最大ID
	 * 
	 * @param unionId
	 * @return
	 */
	public long getUnionMsgsIdMax(long unionId) {
		return chatKeyData.getUnionMsgsIdMax(unionId);
	}
}
