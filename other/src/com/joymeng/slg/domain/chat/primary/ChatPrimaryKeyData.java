package com.joymeng.slg.domain.chat.primary;

import java.util.HashMap;
import java.util.Map;
import com.joymeng.Instances;
/**
 * 主键管理
 * @author houshanping
 *
 */
public class ChatPrimaryKeyData implements Instances {
	
	private static ChatPrimaryKeyData instance = new ChatPrimaryKeyData();
	public static ChatPrimaryKeyData getInstance() {
		return instance;
	}
	
	private Long worldMsgsId = new Long(1L); // 世界聊天记录的Id
	private Map<Long, Long> unionMsgsId = new HashMap<Long, Long>(); // 联盟的Id
	private Long groupId = new Long(1L); 	// 记录组的Id
	
	private Long worldMsgHead = new Long(1L);	//世界聊天的
	private Map<Long, Long> unionMsgHead = new HashMap<Long, Long>(); // 联盟的Id
	
	public long getWorldMsgHead() {
		synchronized (worldMsgHead) {
			return worldMsgHead;			
		}
	}
//	
//	public long getUnionMsgHead(long unionId) {
//		synchronized (unionMsgHead) {
//			if (unionMsgHead.size() == 0 || unionMsgHead.get(unionId) == null) {
//				unionMsgHead.put(unionId, (long) 1);
//			}
//			long temp = unionMsgHead.get(unionId);
//			return temp;
//		}
//	}

	public long worldMsgHeadIncrement() {
		synchronized (worldMsgHead) {
			if ((worldMsgHead + 1) <= worldMsgsId) {
				worldMsgHead++;
				return worldMsgHead - 1;
			} else {
				return -1;
			}
		}
	}
	
	public long unionMsgHeadIncrement(long unionId){
		synchronized (unionMsgHead) {
			if (unionMsgHead.get(unionId) == null) {
				unionMsgHead.put(unionId, (long) 1);
			}
			long tempUnionMsgHead = unionMsgHead.get(unionId);
			if ((tempUnionMsgHead + 1) <= unionMsgsId.get(unionId)) {
				unionMsgHead.put(unionId, tempUnionMsgHead + 1);
				return tempUnionMsgHead;
			}else {
				return -1;
			}
		}
	}
	
	public long getWorldMsgsId() {
		synchronized (worldMsgsId) {
			return worldMsgsId++;
		}
	}

	public long getWorldMsgsIdMax() {
		return worldMsgsId - 1;
	}

	public long getUnionMsgsId(long unionId) {
		synchronized (unionMsgsId) {
			if (unionMsgsId.get(unionId) == null) {
				unionMsgsId.put(unionId, (long) 1);
			}
			long tempUnionMsgsId = unionMsgsId.get(unionId);
			if (tempUnionMsgsId == 0 || unionMsgsId.get(unionId) == null) {
				tempUnionMsgsId = 1;
			}
			unionMsgsId.put(unionId, tempUnionMsgsId + 1);
			return tempUnionMsgsId;
		}
	}
	
	public long getUnionMsgsIdMax(long unionId){
		return unionMsgsId.get(unionId) == null ? 1 : unionMsgsId.get(unionId) - 1;
	}
	
	public long getGroupId(){	
		synchronized (groupId) {
			return groupId++;
		}		
	}
	
	public void setGroupId(Long groupId){	
		synchronized (this.groupId) {
			this.groupId = groupId;
		}		
	}
}
