package com.joymeng.log;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.joymeng.Instances;
import com.joymeng.slg.ServiceApp;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.world.GameConfig;

public class NewLogManager implements Instances {
	static Map<LoggerType,Logger> loggers = new HashMap<LoggerType,Logger>();
	public static enum LoggerType{
		LOGGER_TYPE_BASEEVENT("baseEvent"),  // 基地事件埋点
		LOGGER_TYPE_INTERFACE("interface"),  //基本界面到达埋点
		LOGGER_TYPE_GUIDE("direct"),    // 指引埋点
		LOGGER_TYPE_BUILD("buildOp"),  //基地建筑操作埋点
		LOGGER_TYPE_CHARGE("chargeOp"), //充值埋点
		LOGGER_TYPE_MAP("bigMap"), //大地图埋点
		LOGGER_TYPE_ACTIVE("active"), //活动埋点
		LOGGER_TYPE_UNION("unionOp"), //联盟埋点
		LOGGER_TYPE_GAME("game"), //游戏启动埋点	
		LOGGER_TYPE_ARMY("army"), //士兵数量变化日志
		LOGGER_TYPE_CLIENT("client"), //客户端错误信息记录日志	
		LOGGER_TYPE_MISTAKE("mistake") //服务器错误信息记录日志	
		;
		private LoggerType(String key){
			this.key = key;
		}
		private String key;
		public String getKey() {
			return key;
		}
	}
	static {
		LoggerType[] lts = LoggerType.values();
		for (int i = 0 ; i < lts.length ; i++){
			LoggerType lt = lts[i];
			Logger logger = LoggerFactory.getLogger(lt.getKey());
			loggers.put(lt,logger);
		}
	}
	
	static int serverId = ServiceApp.instanceId - GameConfig.SERVER_LIST_ID;
	static int appid = 1001;
	
	public static void logPolling() { // 0时统一添加一条日志
		for (Logger logger : loggers.values()){
			info(logger,serverId, appid);
		}
	}


	public static void baseEventLog(Role role, Object... params) { // 基地事件埋点
		Logger logger = loggers.get(LoggerType.LOGGER_TYPE_BASEEVENT);
		if (logger == null){
			return ;
		}
		long uid = role.getId();
		String channelId = role.getChannelId();
		int vipLevel = role.getVipInfo().getVipLevel();
		int level = role.getLevel();
		String reg_uid_time = role.getUidRegisTime();
		String country = role.getCountry();
		String language = role.getLanguage();
		String uuid = role.getUuid();
		String reg_uuid_time = role.getUuidRegisTime();
		StringBuffer sb = new StringBuffer();
		for (int i = 0 ; i < params.length ; i++){
			Object obj = params[i];
			sb.append(obj.toString());
			sb.append(GameLog.SPLIT_CHAR);
		}
		String newStr = sb.toString().substring(0, sb.toString().length() - 1);
		info(logger,serverId, appid, uid, channelId, vipLevel, level,
				reg_uid_time, country, language, uuid, reg_uuid_time,newStr);
	}

	public static void buildLog(Role role, Object... params) { // 基地建筑操作埋点
		Logger logger = loggers.get(LoggerType.LOGGER_TYPE_BUILD);
		if (logger == null){
			return ;
		}
		long uid = role.getId();
		String channelId = role.getChannelId();
		int vipLevel = role.getVipInfo().getVipLevel();
		int level = role.getLevel();
		String reg_uid_time = role.getUidRegisTime();
		String country = role.getCountry();
		String language = role.getLanguage();
		String uuid = role.getUuid();
		String reg_uuid_time = role.getUuidRegisTime();
		StringBuffer sb = new StringBuffer();
		for (int i = 0 ; i < params.length ; i++){
			Object obj = params[i];
			sb.append(obj.toString());
			sb.append(GameLog.SPLIT_CHAR);
		}
		String newStr = sb.toString().substring(0, sb.toString().length() - 1);
		info(logger,serverId, appid, uid, channelId, vipLevel, level,
				reg_uid_time, country, language, uuid, reg_uuid_time,newStr);
	}
	
	
	public static void interfaceLog(Role role,String eventID ,String parameter) { // 基本界面到达埋点
		Logger logger = loggers.get(LoggerType.LOGGER_TYPE_INTERFACE);
		if (logger == null){
			return ;
		}
		long uid = role.getId();
		String channelId = role.getChannelId();
		int vipLevel = role.getVipInfo().getVipLevel();
		int level = role.getLevel();
		String reg_uid_time = role.getUidRegisTime();
		String country = role.getCountry();
		String language = role.getLanguage();
		String uuid = role.getUuid();
		String reg_uuid_time = role.getUuidRegisTime();
		
		info(logger,serverId, appid, uid, channelId, vipLevel, level,
				reg_uid_time, country, language, uuid, reg_uuid_time,eventID,parameter);
	}

	public static void guideLog(Role role,String eventID ,String parameter) { // 指引埋点
		Logger logger = loggers.get(LoggerType.LOGGER_TYPE_GUIDE);
		if (logger == null){
			return ;
		}
		long uid = role.getId();
		String channelId = role.getChannelId();
		int vipLevel = role.getVipInfo().getVipLevel();
		int level = role.getLevel();
		String reg_uid_time = role.getUidRegisTime();
		String country = role.getCountry();
		String language = role.getLanguage();
		String uuid = role.getUuid();
		String reg_uuid_time = role.getUuidRegisTime();
		
		info(logger,serverId, appid, uid, channelId, vipLevel, level,
				reg_uid_time, country, language, uuid, reg_uuid_time,eventID,parameter);
	}
	
	public static void chargeLog(Role role,String eventID ,int money) { // 充值埋点
		Logger logger = loggers.get(LoggerType.LOGGER_TYPE_CHARGE);
		if (logger == null){
			return ;
		}
		long uid = role.getId();
		String channelId = role.getChannelId();
		int vipLevel = role.getVipInfo().getVipLevel();
		int level = role.getLevel();
		String reg_uid_time = role.getUidRegisTime();
		String country = role.getCountry();
		String language = role.getLanguage();
		String uuid = role.getUuid();
		String reg_uuid_time = role.getUuidRegisTime();
		
		info(logger,serverId, appid, uid, channelId, vipLevel, level,
				reg_uid_time, country, language, uuid, reg_uuid_time,eventID,money);
	}
	
	public static void mapLog(Role role,Object... params) { // 大地图埋点
		Logger logger = loggers.get(LoggerType.LOGGER_TYPE_MAP);
		if (logger == null){
			return ;
		}
		long uid = role.getId();
		String channelId = role.getChannelId();
		int vipLevel = role.getVipInfo().getVipLevel();
		int level = role.getLevel();
		String reg_uid_time = role.getUidRegisTime();
		String country = role.getCountry();
		String language = role.getLanguage();
		String uuid = role.getUuid();
		String reg_uuid_time = role.getUuidRegisTime();
		StringBuffer sb = new StringBuffer();
		for (int i = 0 ; i < params.length ; i++){
			Object obj = params[i];
			sb.append(obj.toString());
			sb.append(GameLog.SPLIT_CHAR);
		}
		String newStr = sb.toString().substring(0, sb.toString().length() - 1);
		info(logger,serverId, appid, uid, channelId, vipLevel, level,
				reg_uid_time, country, language, uuid, reg_uuid_time,newStr);
	}
	
	public static void unionLog(Role role,Object... params) { // 联盟埋点
		Logger logger = loggers.get(LoggerType.LOGGER_TYPE_UNION);
		if (logger == null){
			return ;
		}
		long uid = role.getId();
		String channelId = role.getChannelId();
		int vipLevel = role.getVipInfo().getVipLevel();
		int level = role.getLevel();
		String reg_uid_time = role.getUidRegisTime();
		String country = role.getCountry();
		String language = role.getLanguage();
		String uuid = role.getUuid();
		String reg_uuid_time = role.getUuidRegisTime();
		StringBuffer sb = new StringBuffer();
		for (int i = 0 ; i < params.length ; i++){
			Object obj = params[i];
			sb.append(obj.toString());
			sb.append(GameLog.SPLIT_CHAR);
		}
		String newStr = sb.toString().substring(0, sb.toString().length() - 1);
		info(logger,serverId, appid, uid, channelId, vipLevel, level,
				reg_uid_time, country, language, uuid, reg_uuid_time,newStr);
	}
	
	public static void activeLog(Role role,Object... params) { // 活动埋点
		Logger logger = loggers.get(LoggerType.LOGGER_TYPE_ACTIVE);
		if (logger == null){
			return ;
		}
		long uid = role.getId();
		String channelId = role.getChannelId();
		int vipLevel = role.getVipInfo().getVipLevel();
		int level = role.getLevel();
		String reg_uid_time = role.getUidRegisTime();
		String country = role.getCountry();
		String language = role.getLanguage();
		String uuid = role.getUuid();
		String reg_uuid_time = role.getUuidRegisTime();
		StringBuffer sb = new StringBuffer();
		for (int i = 0 ; i < params.length ; i++){
			Object obj = params[i];
			sb.append(obj.toString());
			sb.append(GameLog.SPLIT_CHAR);
		}
		String newStr = sb.toString().substring(0, sb.toString().length() - 1);
		info(logger,serverId, appid, uid, channelId, vipLevel, level,
				reg_uid_time, country, language, uuid, reg_uuid_time,newStr);
	}
	
	public static void gameLog(Object... params) { // 游戏启动埋点
		Logger logger = loggers.get(LoggerType.LOGGER_TYPE_GAME);
		if (logger == null){
			return ;
		}
		info(logger,params);
	}
	
	public static void clientLog(Object... params) {  //客户端错误信息记录
		Logger logger = loggers.get(LoggerType.LOGGER_TYPE_CLIENT);
		if (logger == null){
			return ;
		}
		info(logger,params);
	}
	
	public static void misTakeLog(Object... params) {  //服务器错误信息记录
		Logger logger = loggers.get(LoggerType.LOGGER_TYPE_MISTAKE);
		if (logger == null){
			return ;
		}
		info(logger,params);
	}
	
	public static void armyLog(Role role, Object... params) { // 士兵数量变化日志
		Logger logger = loggers.get(LoggerType.LOGGER_TYPE_ARMY);
		if (logger == null) {
			return;
		}
		long uid = role.getId();
		String channelId = role.getChannelId();
		int vipLevel = role.getVipInfo().getVipLevel();
		int level = role.getLevel();
		String reg_uid_time = role.getUidRegisTime();
		String country = role.getCountry();
		String language = role.getLanguage();
		String uuid = role.getUuid();
		String reg_uuid_time = role.getUuidRegisTime();
		StringBuffer sb = new StringBuffer();
		for (int i = 0 ; i < params.length ; i++){
			Object obj = params[i];
			sb.append(obj.toString());
			sb.append(GameLog.SPLIT_CHAR);
		}
		String newStr = sb.toString().substring(0, sb.toString().length() - 1);
		info(logger, serverId, appid, uid, channelId, vipLevel, level, reg_uid_time, country, language, uuid,
				reg_uuid_time, newStr);
	}
	public static void info(Logger logger,Object... params) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0 ; i < params.length ; i++){
			Object obj = params[i];
			sb.append(obj.toString());
			sb.append(GameLog.SPLIT_CHAR);
		}
		String newStr = sb.toString().substring(0, sb.toString().length() - 1);
		logger.info(newStr);
	}
}
