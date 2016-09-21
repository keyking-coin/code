package com.joymeng.slg.domain.chat.chatdata;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSONArray;
import com.joymeng.Const;
import com.joymeng.Instances;
import com.joymeng.common.util.JsonUtil;
import com.joymeng.slg.ServiceApp;
import com.joymeng.slg.domain.chat.ChatGroup;
import com.joymeng.slg.domain.chat.ChatMsg;
import com.joymeng.slg.domain.chat.NoticeMsg;
import com.joymeng.slg.domain.chat.RoleChatMail;
import com.joymeng.slg.domain.chat.UnionChatMsg;
import com.joymeng.slg.domain.object.redpacket.Redpacket;
import com.joymeng.slg.world.GameConfig;
import com.joymeng.slg.world.TaskPool;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
/**
 * 聊天的数据管理
 * @author houshanping
 *
 */
public class ChatDataManager implements Instances{
	
	private static ChatDataManager instance = new ChatDataManager();
	
	JedisPool redisPool;
	
	public static ChatDataManager getInstance() {
		return instance;
	}
	
	String prefix = "RedAlert_" + String.valueOf(ServiceApp.instanceId) + "_";
	String worldMsgsKey = prefix + "worldMsgs";
	String worldNoticesKey = prefix + "worldNotices";
	String unionMsgsKey = prefix + "unionMsgs";
	String groupsIdKey = prefix + "groupsId";
	String groupsKey = prefix + "groups";
	String roleMailKey = prefix + "roleMail";
	String redpacketIdKey = prefix + "roleRedpacketId";
	String returnRedpacketIdKey = prefix + "returnRedpacketId";
	String deleteRedpacketIdKey = prefix + "deleteRedpacketId";
	String redpacketKey = prefix + "roleRedpacket";
	long saveStartTime;
	long saveEndTime;
	long loadStartTime;
	long loadEndTime;
	int useDBIndex;
	
	public void load() throws Exception{
		if (redisPool == null){
			Properties properties = new Properties();
			File file = new File(Const.CONF_PATH + "redis.properties");
			properties.load(new FileInputStream(file));
			RedisConfig rc = RedisConfig.load(properties);
			JedisPoolConfig config = new JedisPoolConfig();
            //控制一个pool可分配多少个jedis实例，通过pool.getResource()来获取；  
            //如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。  
            config.setMaxActive(rc.getMaxActive());  
            //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。  
            config.setMaxIdle(rc.getMaxIdle());  
            //表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；  
            config.setMaxWait(rc.getMaxWait());  
            //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；  
            config.setTestOnBorrow(rc.isTestOnBorrow());
            useDBIndex = rc.getUseIndex();
			redisPool = new JedisPool(config,rc.getIp(),rc.getPort(),rc.getTimeOut());
		}
		loadData(); //加载聊天消息
		beginSave(); //定期保存
		beginReturnRedpacket();//开始启动红包返还刷新的时间
		beginDeleteRedpacket();//开始启动红包删除刷新的时间
	}
	
	private void beginSave() {
		taskPool.scheduleAtFixedRate(null, new Runnable() {
			@Override
			public void run() {
				save();
			}
		}, 1*TaskPool.SECONDS_PER_MINTUE,10*TaskPool.SECONDS_PER_MINTUE,TimeUnit.SECONDS);
	}
	
	private void beginReturnRedpacket() {
		taskPool.scheduleAtFixedRate(null, new Runnable() {
			@Override
			public void run() {
				rpManager.returnRedpacket();
			}
		}, TaskPool.SECONDS_PER_SECOND, GameConfig.ROLE_REDPACKET_SCAN_RETURN_TIME, TimeUnit.SECONDS);
	}

	private void beginDeleteRedpacket() {
		taskPool.scheduleAtFixedRate(null, new Runnable() {
			@Override
			public void run() {
				rpManager.deleteRedpacket();
			}
		}, TaskPool.SECONDS_PER_MINTUE, GameConfig.ROLE_REDPACKET_SCAN_DELETE_TIME, TimeUnit.SECONDS);
	}
	
	/**
	 * 获取连接
	 * @return
	 */
	private Jedis getConnectResource(){
		Jedis jedis = redisPool.getResource();
		if (jedis != null){
			jedis.select(useDBIndex);
		}
		return jedis;
	}
	
	
	/**
	 * 释放连接
	 * @param jedis
	 */
	private void release(Jedis jedis){
		if (jedis != null){
			redisPool.returnResource(jedis);
		}
	}
	
	/**
	 * 加载消息
	 */
	public void loadData() {
		Jedis jedis = getConnectResource();
		//世界消息的加载
		Set<String> worldMsgIds = jedis.hkeys(worldMsgsKey);		
		List<String> worldMsgListIds = new ArrayList<String>(worldMsgIds);
		Collections.sort(worldMsgListIds, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				long num1 = Long.valueOf(o1);
				long num2 = Long.valueOf(o2);
				return num1 == num2 ? 0 : (num1 < num2 ? -1 : 1);
			}
		});		
		//公告消息的加载
		List<NoticeMsg> datas = JSONArray.parseArray(jedis.get(worldNoticesKey),NoticeMsg.class);
		if (datas != null) {
			for (int i = 0 ; i < datas.size() ; i++){
				NoticeMsg noticeMsg = datas.get(i);
				chatMgr.addWorldNotice(noticeMsg);
			}
		}
		//世界消息的加载
		for (int i = 0 ; i < worldMsgListIds.size() ; i++){
			String string = worldMsgListIds.get(i);
			ChatMsg chatMsg =  JsonUtil.JsonToObject(jedis.hget(worldMsgsKey, string), ChatMsg.class);
			chatMgr.addWorldChat(chatMsg);
		}
		//联盟消息的加载
		Set<String> unionIds = jedis.hkeys(unionMsgsKey);
		for (String string : unionIds) {
			long unionId = Long.valueOf(string);
			UnionChatMsg unionChatMsg =  JsonUtil.JsonToObject(jedis.hget(unionMsgsKey, string), UnionChatMsg.class);
			if (unionChatMsg.getUnionMsgs().size() > 0) {
				Set<Long> unionMsgIds = unionChatMsg.getUnionMsgs().keySet();
				List<Long> unionMsgListIds = new ArrayList<Long>(unionMsgIds);
				Collections.sort(unionMsgListIds, new Comparator<Long>() {
					@Override
					public int compare(Long o1, Long o2) {
						return o1 == o2 ? 0 : (o1 < o2 ? -1 : 1);
					}
				});
				for (int i = 0 ; i < unionMsgListIds.size() ; i++){
					Long unionMsgId = unionMsgListIds.get(i);
					ChatMsg chatMsg = unionChatMsg.getUnionMsgs().get(unionMsgId);
					chatMgr.addUnionChat(unionId, chatMsg);	
				}
			}
		}
		//群组的Id的加载
		String groupIdStr = jedis.get(groupsIdKey);
		chatKeyData.setGroupId(Long.valueOf(groupIdStr == null ? "1" : groupIdStr));
		//组消息的加载
		Set<String> groupIds = jedis.hkeys(groupsKey);
		for (String string : groupIds) {
			long groupId = Long.valueOf(string);
			ChatGroup chatGroup =  JsonUtil.JsonToObject(jedis.hget(groupsKey, string), ChatGroup.class);
			chatMgr.addGroup(groupId, chatGroup);
		}
		//个人邮箱的加载
		Set<String> roleMailIds = jedis.hkeys(roleMailKey);
		for (String string : roleMailIds) {
			long uid = Long.valueOf(string);
			RoleChatMail roleChatMail =  JsonUtil.JsonToObject(jedis.hget(roleMailKey, string), RoleChatMail.class);
			if (roleChatMail == null) {
				continue;
			}
			RoleChatMail ResultRoleChatMail = new RoleChatMail();
			ResultRoleChatMail.setRoleChatMailId(roleChatMail.getRoleChatMailId());
			if (roleChatMail.getRoleChatMails().size() > 0) {
				Set<Long> roleChatMailIds = roleChatMail.getRoleChatMails().keySet();
				List<Long> roleChatMailListIds = new ArrayList<Long>(roleChatMailIds);
				Collections.sort(roleChatMailListIds, new Comparator<Long>() {
					@Override
					public int compare(Long o1, Long o2) {
						return o1 == o2 ? 0 : (o1 < o2 ? -1 : 1);
					}
				});
				for (int i = 0 ; i < roleChatMailListIds.size() ; i++){
					Long roleChatMailId = roleChatMailListIds.get(i);
					ChatMsg chatMsg = roleChatMail.getRoleChatMails().get(roleChatMailId);
					ResultRoleChatMail.firstAddMail(chatMsg);
				}
				chatMgr.addRoleMail(uid, ResultRoleChatMail);
			}
		}
		// 用户红包的Id的加载
		String redpacketIdStr = jedis.get(redpacketIdKey);
		rpManager.setRedpacketId(Long.valueOf(redpacketIdStr == null ? "1" : redpacketIdStr));
		String returnRedpacketIdStr = jedis.get(returnRedpacketIdKey);
		rpManager.setReturnRedpacketId(Long.valueOf(returnRedpacketIdStr == null ? "1" : returnRedpacketIdStr));
		String deleteRedpacketIdStr = jedis.get(deleteRedpacketIdKey);
		rpManager.setDeleteRedpacketId(Long.valueOf(deleteRedpacketIdStr == null ? "1" : deleteRedpacketIdStr));
		// 用户红包
		Set<String> redpacketIds = jedis.hkeys(redpacketKey);
		for (String string : redpacketIds) {
			long uid = Long.valueOf(string);
			Redpacket redpacket = JsonUtil.JsonToObject(jedis.hget(redpacketKey, string), Redpacket.class);
			if (redpacket == null) {
				continue;
			}
			rpManager.firstAddRoleRedpacket(uid, redpacket);
		}
		release(jedis);
	}
	
	/**
	 * 保存数据
	 */
	public void save() {
		Jedis jedis = getConnectResource();
		Map<Long, ChatMsg> worldMsgs = chatMgr.getWorldMsgs();
		Queue<NoticeMsg> worldNotices = chatMgr.getWorldNotices();
		Map<Long, UnionChatMsg> unionMsgs = chatMgr.getUnionMsgs();
		Map<Long, ChatGroup> groups = chatMgr.getGroups();
		Map<Long, RoleChatMail> roleMail = chatMgr.getRoleMail();
		Map<Long, Redpacket> roleRPs = rpManager.getAllRedpacket();
		//公告
		List<NoticeMsg> datas = new ArrayList<>();
		for (NoticeMsg noticeMsg : worldNotices) {
			if (noticeMsg.getPriorityLevel() > 0) {
				continue;
			}
			datas.add(noticeMsg);
		}
		jedis.set(worldNoticesKey, JsonUtil.ObjectToJsonString(datas));
		//世界聊天信息
		for (long hKey : worldMsgs.keySet()) {
			String hashKey = String.valueOf(hKey);
			hashKey = String.valueOf(Long.valueOf(hashKey) - chatKeyData.getWorldMsgHead() + 1);
			String hashValue = JsonUtil.ObjectToJsonString(worldMsgs.get(hKey));
			jedis.hset(worldMsgsKey, hashKey, hashValue);
		}
		//联盟聊天信息
		for (long hKey : unionMsgs.keySet()) {
			String hashKey = String.valueOf(hKey);
			String hashValue = JsonUtil.ObjectToJsonString(unionMsgs.get(hKey));
			jedis.hset(unionMsgsKey, hashKey, hashValue);
		}
		//写入群组的ID
		jedis.set(groupsIdKey, String.valueOf(chatKeyData.getGroupId()));
		//群组的对象
		for (long hKey : groups.keySet()) {
			String hashKey = String.valueOf(hKey);
			String hashValue = JsonUtil.ObjectToJsonString(groups.get(hKey));
			jedis.hset(groupsKey, hashKey, hashValue);
		}
		//用户的邮箱
		for (long hKey : roleMail.keySet()) {
			String hashKey = String.valueOf(hKey);
			String hashValue = JsonUtil.ObjectToJsonString(roleMail.get(hKey));
			jedis.hset(roleMailKey, hashKey, hashValue);
		}
		//写入红包的ID
		jedis.set(redpacketIdKey, String.valueOf(rpManager.getRedpacketId()));
		jedis.set(returnRedpacketIdKey, String.valueOf(rpManager.getReturnRedpacketId()));
		jedis.set(deleteRedpacketIdKey, String.valueOf(rpManager.getDeleteRedpacketId()));
		//所有红包数据
		for (long hKey : roleRPs.keySet()) {
			String hashKey = String.valueOf(hKey);
			String hashValue = JsonUtil.ObjectToJsonString(roleRPs.get(hKey));
			jedis.hset(redpacketKey, hashKey, hashValue);
		}
		release(jedis);
	}	
	
	/**
	 * 更新用户邮箱
	 * @param uid
	 */
	public void updataRoleMail(long uid) {
		Jedis jedis = getConnectResource();
		Map<Long, RoleChatMail> roleMail = chatMgr.roleMail;
		if (roleMail != null) {
			String hashKey = String.valueOf(uid);
			String hashValue = JsonUtil.ObjectToJsonString(roleMail.get(uid));
			jedis.hset(roleMailKey, hashKey, hashValue);
		}
		release(jedis);
	}
}
