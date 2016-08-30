package com.joymeng.log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.joymeng.Instances;
import com.joymeng.list.BuildOperation;
import com.joymeng.list.EquipPosRarQue;
import com.joymeng.list.EventName;
import com.joymeng.list.ItemType;
import com.joymeng.list.RealtimeData;
import com.joymeng.slg.ServiceApp;
import com.joymeng.slg.dao.DaoData;
import com.joymeng.slg.dao.SqlData;
import com.joymeng.slg.domain.map.MapUtil;
import com.joymeng.slg.domain.object.bag.data.Item;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.union.UnionBody;
import com.joymeng.slg.world.GameConfig;
import com.joymeng.slg.world.thread.HourRunable;

public class LogManager implements Instances {
	static Map<LoggerType,Logger> loggers = new HashMap<LoggerType,Logger>();
	public static enum LoggerType{
		LOGGER_TYPE_CHARGE("charge"),  // 玩家充值日志
		LOGGER_TYPE_GOLD_CS("goldCsm"),  // 金币消耗日志
		LOGGER_TYPE_GOLD_OPT("goldOpt"),  // 金币产出日志
		LOGGER_TYPE_ITEM_CS("itemCsm"),  // 物品消耗日志
		LOGGER_TYPE_ITEM_OPT("itemOpt"),  // 物品产出日志
		LOGGER_TYPE_LEAVE("leave"),  // 退出游戏日志
		LOGGER_TYPE_LOGIN("login"),  // 登录游戏日志
		LOGGER_TYPE_ONLINE("online"),  // 在线日志
		LOGGER_TYPE_SHOP("shop"),  // 商城日志
		LOGGER_TYPE_TASK("task"),  // 任务日志
		LOGGER_TYPE_BUILD("build"),  // 建筑日志
		LOGGER_TYPE_UNION("union"),  // 联盟日志
		LOGGER_TYPE_MAP("map"),  // 大地图日志
		LOGGER_TYPE_CHAT("chat"),  // 聊天日志
		LOGGER_TYPE_MAIL("mail"),  // 邮件日志
		LOGGER_TYPE_TURN_TABLE("turntable"),  // 大转盘日志
		LOGGER_TYPE_EQUIP("equip"),  // 装备日志
		LOGGER_TYPE_GUIDE("guide"),  // 指引日志
		LOGGER_TYPE_PVP("pvp"),  // pvp日志
		LOGGER_TYPE_PVE("pve")// pve日志
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

	public static void loginLog(Role role) { // 登录日志
		Logger logger = loggers.get(LoggerType.LOGGER_TYPE_LOGIN);
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
		String name = role.getName();
		int money = 0;
		int gold = role.getMoney();
		int fight = role.getRoleStatisticInfo().getRoleFight();
		int troops = role.getRoleStatisticInfo().getRoleArmyFight();
		String osversion = role.getVersion();
		int memory = role.getMemory();
		info(logger, serverId, appid, uid, channelId, vipLevel, level, reg_uid_time, country, language, uuid,
				reg_uuid_time, name, money, gold, fight, troops, osversion, memory);
	}

	public static void leaveLog(Role role) { // 下线日志
		Logger logger = loggers.get(LoggerType.LOGGER_TYPE_LEAVE);
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
		String name = role.getName();
		int money = 0;
		int gold = role.getMoney();
		int fight = role.getRoleStatisticInfo().getRoleFight();
		int troops = role.getRoleStatisticInfo().getRoleArmyFight();
		String osversion = role.getVersion();
		int memory = role.getMemory();
		info(logger, serverId, appid, uid, channelId, vipLevel, level, reg_uid_time, country, language, uuid,
				reg_uuid_time, name, money, gold, fight, troops, osversion, memory);
	}
	
	
	public static void chargeLog(Role role, int orderType, int chargeGold) { // 充值日志
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
		String from_uid = String.valueOf(uid);
		List<SqlData> list = new ArrayList<SqlData>();;
		try {
			list = dbMgr.getGameDao().getDatas(
					DaoData.TABLE_RED_ALERT_CHARGE_ORDER, DaoData.RED_ALERT_CHARGE_JOYID,uid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		byte first_charge;
		if (list.size()==0) {  
			first_charge = 0;  //首冲
		} else {
			first_charge = 1;  //非首冲
		}
		info(logger,serverId, appid, uid, channelId, vipLevel, level,
				reg_uid_time, country, language, uuid, reg_uuid_time,
				orderType, chargeGold, from_uid, first_charge);
	}
	
	public static void onlineLog(Role role,long online) { // 在线时长日志
		Logger logger = loggers.get(LoggerType.LOGGER_TYPE_ONLINE);
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
		info(logger,serverId, appid, uid, channelId, vipLevel,level, reg_uid_time,
				country, language, uuid, reg_uuid_time, online);

	}

	public static void goldOutputLog(Role role, int money, String event) { // 金币产出日志
		Logger logger = loggers.get(LoggerType.LOGGER_TYPE_GOLD_OPT);
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
		int propnum = money;
		String type_name = getTypeName(event);
		HourRunable.recordProduce(channelId,type_name,money,(byte)1);
		info(logger,serverId, appid, uid, channelId, vipLevel, level,
				reg_uid_time, country, language, propnum, type_name);

	}

	public static void goldConsumeLog(Role role, int money, String event) { // 金币消耗日志
		Logger logger = loggers.get(LoggerType.LOGGER_TYPE_GOLD_CS);
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
		int propnum = money;
		String type_name = getTypeName(event);
		info(logger,serverId, appid, uid, channelId, vipLevel, level,
				reg_uid_time, country, language, propnum, type_name);
	}

	public static void itemOutputLog(Role role, long num, String event,String itemName) { // 其他道具产出日志
		Logger logger = loggers.get(LoggerType.LOGGER_TYPE_ITEM_OPT);
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
		long propnum = num;
		String type_name = getTypeName(event);
		String item_type = getItemType(itemName);
		String item_name = getCHName(itemName);
		byte type = RealtimeData.getType(itemName);
		HourRunable.recordProduce(channelId,type_name,(int)num,type);
		info(logger,serverId, appid, uid, channelId, vipLevel, level,
				reg_uid_time, country, language, propnum, type_name, item_type,
				item_name);
	}

	public static void itemConsumeLog(Role role, long num, String event,String itemName) { // 其他道具消耗日志
		Logger logger = loggers.get(LoggerType.LOGGER_TYPE_ITEM_CS);
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
		long propnum = num;
		String type_name = getTypeName(event);
		String item_type = getItemType(itemName);
		String item_name = getCHName(itemName);
		info(logger,serverId, appid, uid, channelId, vipLevel, level,
				reg_uid_time, country, language, propnum, type_name, item_type,
				item_name);
	}

	public static void shopLog(Role role, long num, int price, String type,String item,String shop) { // 玩家商城日志
		Logger logger = loggers.get(LoggerType.LOGGER_TYPE_SHOP);
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
		long propnum = num;
		int money = price;
		String item_name = item;
		String buy_type =getBuyType(type);
		String shop_name = shop;
		info(logger,serverId, appid, uid, channelId, vipLevel, level,
				reg_uid_time, country, language, propnum, money, item_name,buy_type,
				shop_name);
	}

	public static void taskLog(Role role, byte taskCome,String missType, String missName,boolean starEnd) { // 玩家任务日志
		Logger logger = loggers.get(LoggerType.LOGGER_TYPE_TASK);
		if (logger == null){
			return ;
		}
		long uid = role.getId();
		String channelId = role.getChannelId();
		int vipLevel = role.getVipInfo().getVipLevel();
		int level = role.getLevel();
		String reg_uid_time = role.getUidRegisTime();
		String beginOrend;
		String taskFrom = null;
		switch (taskCome) {
		case 0:
			taskFrom = "任务系统";
			break;
		case 1:
			taskFrom = "日常任务";
			break;
		default:
			break;
		}
		if (starEnd) {
			beginOrend = "接受任务";
		} else {
			beginOrend = "完成任务";
		}
		info(logger,serverId, appid, uid, channelId, vipLevel, level,
				reg_uid_time,taskFrom, missType, missName, beginOrend);
	}

	public static void buildLog(Role role, String slotID,String buildId,byte lv, byte operation) { // 玩家内城建筑相关日志
		Logger logger = loggers.get(LoggerType.LOGGER_TYPE_BUILD);
		if (logger == null){
			return ;
		}
		long uid = role.getId();
		String channelId = role.getChannelId();
		String country = role.getCountry();
		String language = role.getLanguage();
		int vipLevel = role.getVipInfo().getVipLevel();
		int level = role.getLevel();
		String reg_uid_time = role.getUidRegisTime();
		String slot ="建筑槽_"+slotID;
	    String op=getBuildOp(operation);
		info(logger,serverId, appid, uid, channelId,country,language, vipLevel, level,
				reg_uid_time, slot,buildId,lv,op);
	}
	
	public static void unionLog(Role role, String name, String event,String parameter) { // 玩家联盟日志
		Logger logger = loggers.get(LoggerType.LOGGER_TYPE_UNION);
		if (logger == null){
			return ;
		}
		long uid = role.getId();
		String channelId = role.getChannelId();
		String country = role.getCountry();
		String language = role.getLanguage();
		int vipLevel = role.getVipInfo().getVipLevel();
		int level = role.getLevel();
		String reg_uid_time = role.getUidRegisTime();
		String type_name = getTypeName(event);
		info(logger,serverId, appid, uid, channelId,country,language, vipLevel, level,
				reg_uid_time, name, type_name, parameter);
	}
	
	public static void mapLog(Role role, int startPos, int endPos,long id,String event) { // 大地图日志
		Logger logger = loggers.get(LoggerType.LOGGER_TYPE_MAP);
		if (logger == null){
			return ;
		}
		long uid = role.getId();
		String channelId = role.getChannelId();
		String country = role.getCountry();
		String language = role.getLanguage();
		int vipLevel = role.getVipInfo().getVipLevel();
		int level = role.getLevel();
		String reg_uid_time = role.getUidRegisTime();
		String start = MapUtil.getStrPosition(startPos);
		String  end  = MapUtil.getStrPosition(endPos);
		String type_name = getTypeName(event);
		info(logger,serverId, appid, uid, channelId,country,language,vipLevel, level,reg_uid_time, start, end,id, type_name);
	}
	
	public static void chatLog(Role role,String groupType,byte msgType,String msg) { // 聊天日志
		Logger logger = loggers.get(LoggerType.LOGGER_TYPE_CHAT);
		if (logger == null){
			return ;
		}
		long uid = role.getId();
		String name = role.getName();
		String channelId = role.getChannelId();
		String country = role.getCountry();
		String language = role.getLanguage();
		int vipLevel = role.getVipInfo().getVipLevel();
		int level = role.getLevel();
		String reg_uid_time = role.getUidRegisTime();
		String group_type = getGroupType(groupType);
		String getMsgType = getMsgType(msgType);
		String message  = msg.substring(1, msg.length());
		info(logger,serverId, appid, uid,name, channelId,country,language, vipLevel, level,
				reg_uid_time,group_type,getMsgType,message);
	}
	
	public static void mailLog(Role role,String mailType, String action,String sender,String recipient) { // 邮件日志
		Logger logger = loggers.get(LoggerType.LOGGER_TYPE_MAIL);
		if (logger == null){
			return ;
		}
		long uid = role.getId();
		String name = role.getName();
		String channelId = role.getChannelId();
		String country = role.getCountry();
		String language = role.getLanguage();
		int vipLevel = role.getVipInfo().getVipLevel();
		int level = role.getLevel();
		String reg_uid_time = role.getUidRegisTime();
		String str = sender+"/"+recipient;
		info(logger,serverId, appid, uid,name, channelId,country,language, vipLevel, level,
				reg_uid_time,mailType,action,str);
	}
	
	public static void turnTableLog(Role role,String rate,String item) { // 大转盘使用日志
		Logger logger = loggers.get(LoggerType.LOGGER_TYPE_TURN_TABLE);
		if (logger == null){
			return ;
		}
		long uid = role.getId();
		String channelId = role.getChannelId();
		String country = role.getCountry();
		String language = role.getLanguage();
		int vipLevel = role.getVipInfo().getVipLevel();
		int level = role.getLevel();
		String reg_uid_time = role.getUidRegisTime();
		info(logger,serverId, appid, uid, channelId,country,language, vipLevel, level,
				reg_uid_time,rate,item);
	}
	
	public static void equipLog(Role role,int equipType,String equipId, String action) { // 装备日志
		Logger logger = loggers.get(LoggerType.LOGGER_TYPE_EQUIP);
		if (logger == null){
			return ;
		}
		long uid = role.getId();
		String channelId = role.getChannelId();
		String country = role.getCountry();
		String language = role.getLanguage();
		int vipLevel = role.getVipInfo().getVipLevel();
		int level = role.getLevel();
		String reg_uid_time = role.getUidRegisTime();
		String position = EquipPosRarQue.getEquipPosition(equipType);
		info(logger,serverId, appid, uid, channelId,country,language, vipLevel, level,
				reg_uid_time,position,equipId,action);
	}
	
	public static void guideLog(Role role,String guideId) { // 指引日志
		Logger logger = loggers.get(LoggerType.LOGGER_TYPE_GUIDE);
		if (logger == null){
			return ;
		}
		int serverId = ServiceApp.instanceId - GameConfig.SERVER_LIST_ID;
		int appid = 1001;
		long uid = role.getId();
		String channelId = role.getChannelId();
		String country = role.getCountry();
		String language = role.getLanguage();
		int vipLevel = role.getVipInfo().getVipLevel();
		int level = role.getLevel();
		String reg_uid_time = role.getUidRegisTime();
		info(logger,serverId, appid, uid, channelId, country, language, vipLevel, level, reg_uid_time, guideId);
	}
	
	public static void pvpLog(Role role, long group_id, byte result, String prop_name, int number) { // pvp日志
		Logger logger = loggers.get(LoggerType.LOGGER_TYPE_PVP);
		if (logger == null) {
			return;
		}
		int serverId = ServiceApp.instanceId - GameConfig.SERVER_LIST_ID;
		int appid = 1001;
		long uid = role.getId();
		String channelId = role.getChannelId();
		String country = role.getCountry();
		String language = role.getLanguage();
		int vipLevel = role.getVipInfo().getVipLevel();
		int level = role.getLevel();
		String reg_uid_time = role.getUidRegisTime();
		String dung_name = "个人挑战";
		String dung_type = "个人挑战";
		String name = getCHName(prop_name);
		long ally_id;
		byte ally_type;
		UnionBody unionBody = unionManager.search(role.getUnionId());
		if (unionBody == null) {
			ally_id = 0;
			ally_type = 0;
		} else {
			ally_id = unionBody.getId();
			ally_type = 1;
		}
		info(logger, serverId, appid, uid, channelId, vipLevel, level,reg_uid_time, country, language, dung_name, dung_type,
				group_id, result, "进攻玩家城市", name, number, ally_id,ally_type);
	}
	
	public static void pveLog(Role role, String dung_name,String dung_type,byte result, String prop_name, long number) { // pvp日志
		Logger logger = loggers.get(LoggerType.LOGGER_TYPE_PVE);
		if (logger == null) {
			return;
		}
		int serverId = ServiceApp.instanceId - GameConfig.SERVER_LIST_ID;
		int appid = 1001;
		long uid = role.getId();
		String channelId = role.getChannelId();
		String country = role.getCountry();
		String language = role.getLanguage();
		int vipLevel = role.getVipInfo().getVipLevel();
		int level = role.getLevel();
		String reg_uid_time = role.getUidRegisTime();
		int pass_star = -1;
		long ally_id;
		byte ally_type;
		String name;
		if(prop_name.equals("0")){
			name = prop_name;
		}else{
			Item item = dataManager.serach(Item.class, prop_name);
			name = item.getBeizhuname();
		}
		UnionBody unionBody = unionManager.search(role.getUnionId());
		if (unionBody == null) {
			ally_id = 0;
			ally_type = 0;
		} else {
			ally_id = unionBody.getId();
			ally_type = 1;
		}
		info(logger, serverId, appid, uid, channelId, vipLevel, level,
				reg_uid_time, country, language, dung_name, dung_type, result,
				pass_star, "废墟副本", name, number, ally_id, ally_type);
	}
	
	public static String getBuildOp(byte operation) {
		String op = "";
		switch (operation) {
		case 0:
			op = BuildOperation.creatBuild.getName();
			break;
		case 1:
			op = BuildOperation.uplevelBuild.getName();
			break;
		case 2:
			op = BuildOperation.removeBuild.getName();
			break;
		case 3:
			op = BuildOperation.moveBuild.getName();
			break;
		case 4:
			op = BuildOperation.cancleCreBuild.getName();
			break;
		case 5:
			op = BuildOperation.cancleUpBuild.getName();
			break;
		case 6:
			op = BuildOperation.cancleReBuild.getName();
			break;
		case 7:
			op = BuildOperation.createFinish.getName();
			break;
		case 8:
			op = BuildOperation.upLevelFinish.getName();
			break;
		case 9:
			op = BuildOperation.removeFinish.getName();
			break;
		default:
			op = "未知操作";
			break;
		}
		return op;
	}
	
	public  static String getGroupType(String groupType){
		String group_type = "";
		switch (groupType) {
		case "world":
			group_type="世界频道";
			break;
		case "guild":
			group_type="联盟频道";
			break;
		case "group":
			group_type="个人群组";
			break;	
		default:
			group_type="未知频道";
			break;
		}
		return group_type;
	}
	
	public  static String getMsgType(byte msgType){
		String msg_type = "";
		switch (msgType) {
		case 0:
			msg_type="普通文字聊天";
			break;
		case 1:
			msg_type="语音消息";
			break;
		case 2:
			msg_type="系统消息";
			break;
		case 3:
			msg_type="喇叭消息";
			break;
		case 4:
			msg_type="联盟全体邮件";
			break;
		case 6:
			msg_type="系统聊天";
			break;
		case 99:
			msg_type="报告分享";
			break;
		default:
			msg_type="未知聊天类型";
			break;
		}
		return msg_type;
	}
	
	
	
	public static String getTypeName(String event) {
		String type_name = "";
		switch (event) {
		case "Garrison":
			type_name = EventName.Garrison.getName();
			break;
		case "Expedite":
			type_name = EventName.Expedite.getName();
			break;
		case "BackCity":
			type_name = EventName.BackCity.getName();
			break;
		case "BackFortress":
			type_name = EventName.BackFortress.getName();
			break;
		case "HttpGmRoleMgr":
			type_name = EventName.HttpGmRoleMgr.getName();
			break;
		case "addResourceToCity":
			type_name = EventName.addResourceToCity.getName();
			break;
		case "useItem":
			type_name = EventName.useItem.getName();
			break;
		case "getHonorReward":
			type_name = EventName.getHonorReward.getName();
			break;
		case "getAwordFromMission":
			type_name = EventName.getAwordFromMission.getName();
			break;
		case "ModifyBasicInfo":
			type_name = EventName.ModifyBasicInfo.getName();
			break;
		case "completeCreate":
			type_name = EventName.completeCreate.getName();
			break;
		case "resetArmysPoints":
			type_name = EventName.resetArmysPoints.getName();
			break;
		case "secondKill":
			type_name = EventName.secondKill.getName();
			break;
		case "BuildLevelup":
			type_name = EventName.BuildLevelup.getName();
			break;
		case "createBuild":
			type_name = EventName.createBuild.getName();
			break;
		case "removeBuild":
			type_name = EventName.removeBuild.getName();
			break;
		case "trainArmy":
			type_name = EventName.trainArmy.getName();
			break;
		case "cureArmys":
			type_name = EventName.cureArmys.getName();
			break;
		case "repairDefenseArmys":
			type_name = EventName.repairDefenseArmys.getName();
			break;
		case "buyItemSize":
			type_name = EventName.buyItemSize.getName();
			break;
		case "upgradeTech":
			type_name = EventName.upgradeTech.getName();
			break;
		case "resetSkills":
			type_name = EventName.resetSkills.getName();
			break;
		case "tryToBuySomeThing":
			type_name = EventName.tryToBuySomeThing.getName();
			break;
		case "UnlockLandIdHandler":
			type_name = EventName.UnlockLandIdHandler.getName();
			break;
		case "UnionCreateHandler":
			type_name = EventName.UnionCreateHandler.getName();
			break;
		case "tryToChangeFlag":
			type_name = EventName.tryToChangeFlag.getName();
			break;
		case "unionDonate":
			type_name = EventName.unionDonate.getName();
			break;
		case "packageBack":
			type_name = EventName.packageBack.getName();
			break;
		case "troopsArrive":
			type_name = EventName.troopsArrive.getName();
			break;
		case "disMissArmy":
			type_name = EventName.disMissArmy.getName();
			break;
		case "equipDecompose":
			type_name = EventName.equipDecompose.getName();
			break;
		case "materialSynthesis":
			type_name = EventName.materialSynthesis.getName();
			break;
		case "cancelBuildLevelup":
			type_name = EventName.cancelBuildLevelup.getName();
			break;
		case "cancelCreateBuild":
			type_name = EventName.cancelCreateBuild.getName();
			break;
		case "cancelTrainArmy":
			type_name = EventName.cancelTrainArmy.getName();
			break;
		case "EquipUpgradeOver":
			type_name = EventName.EquipUpgradeOver.getName();
			break;
		case "getGems":
			type_name = EventName.getGems.getName();
			break;
		case "collectResource":
			type_name = EventName.collectResource.getName();
			break;
		case "cancelUpgradeTech":
			type_name = EventName.cancelUpgradeTech.getName();
			break;
		case "getDailyReward":
			type_name = EventName.getDailyReward.getName();
			break;
		case "sudokuOpen":
			type_name = EventName.sudokuOpen.getName();
			break;
		case "roleSevenSignIn":
			type_name = EventName.roleSevenSignIn.getName();
			break;
		case "roleThirtySignIn":
			type_name = EventName.roleThirtySignIn.getName();
			break;
		case "ModifyAddItem":
			type_name = EventName.ModifyAddItem.getName();
			break;
		case "ModifyPlayerItem":
			type_name = EventName.ModifyPlayerItem.getName();
			break;
		case "StartTurntableHandler":
			type_name = EventName.StartTurntableHandler.getName();
			break;
		case "buyUnionGoods":
			type_name = EventName.buyUnionGoods.getName();
			break;
		case "equipRefine":
			type_name = EventName.equipRefine.getName();
			break;
		case "getOutlineConsumption":
			type_name = EventName.getOutlineConsumption.getName();
			break;
		case "grainConsumption":
			type_name = EventName.grainConsumption.getName();
			break;
		case "redCostResource":
			type_name = EventName.redCostResource.getName();
			break;
		case "upgradeEquipment":
			type_name = EventName.upgradeEquipment.getName();
			break;
		case "tryToBuyCell":
			type_name = EventName.tryToBuyCell.getName();
			break;
		case "tryToRefresh":
			type_name = EventName.tryToRefresh.getName();
			break;	
		case "creatUnion":
			type_name = EventName.creatUnion.getName();
			break;	
		case "applyJoinUnion":
			type_name = EventName.applyJoinUnion.getName();
			break;
		case "exitUnion":
			type_name = EventName.exitUnion.getName();
			break;
		case "memberAppoint":
			type_name = EventName.memberAppoint.getName();
			break;
		case "changeUnionName":
			type_name = EventName.changeUnionName.getName();
			break;	
		case "changeRecruit":
			type_name = EventName.changeRecruit.getName();
			break;	
		case "changeFlag":
			type_name = EventName.changeFlag.getName();
			break;
		case "levelUpUnion":
			type_name = EventName.levelUpUnion.getName();
			break;
		case "kickMember":
			type_name = EventName.kickMember.getName();
			break;
		case "invitMemberSearch":
			type_name = EventName.invitMemberSearch.getName();
			break;
		case "inviteMemberIn":
			type_name = EventName.inviteMemberIn.getName();
			break;
		case "addAssistance":
			type_name = EventName.addAssistance.getName();
			break;
		case "techDonate":
			type_name = EventName.techDonate.getName();
			break;
		case "techUpgrade":
			type_name = EventName.techUpgrade.getName();
			break;
		case "convertGoods":
			type_name = EventName.convertGoods.getName();
			break;
		case "buyGoods":
			type_name = EventName.buyGoods.getName();
			break;
		case "changeTitle":
			type_name = EventName.changeTitle.getName();
			break;
		case "acceptJionIn":
			type_name = EventName.acceptJionIn.getName();
			break;
		case "removeItemTo":
			type_name = EventName.removeItemTo.getName();
			break;
		case "changeUnionShort":
			type_name = EventName.changeUnionShort.getName();
			break;
		case "changeUnionDeclar":
			type_name = EventName.changeUnionDeclar.getName();
			break;
		case "dissolveUnion":
			type_name = EventName.dissolveUnion.getName();
			break;
		case "buildFortress":
			type_name = EventName.buildFortress.getName();
			break;
		case "buildCity":
			type_name = EventName.buildCity.getName();
			break;
		case "others":
			type_name = EventName.others.getName();
			break;
		case "garrison":
			type_name = EventName.garrison.getName();
			break;
		case "spy":
			type_name = EventName.spy.getName();
			break;
		case "allocation":
			type_name = EventName.allocation.getName();
			break;
		case "aggregation":
			type_name = EventName.aggregation.getName();
			break;
		case "toAggregation":
			type_name = EventName.toAggregation.getName();
			break;
		case "collection":
			type_name = EventName.collection.getName();
			break;	
		case "getAwardDailyTask":
			type_name = EventName.getAwardDailyTask.getName();
			break;
		case "_buyOk":
			type_name = EventName._buyOk.getName();
			break;	
		case "toEctype":
			type_name = EventName.toEctype.getName();
			break;
		case "arriveAtGarrisonPoint":
			type_name = EventName.arriveAtGarrisonPoint.getName();
			break;
		case "InvestCompletion":
			type_name = EventName.InvestCompletion.getName();
			break;
		case "endOfBattle":
			type_name = EventName.endOfBattle.getName();
			break;
		case "startCollecting":
			type_name = EventName.startCollecting.getName();
			break;
		case "endOfCollection":
			type_name = EventName.endOfCollection.getName();
			break;
		case "startBuildFortres":
			type_name = EventName.startBuildFortres.getName();
			break;
		case "startBuildCity":
			type_name = EventName.startBuildCity.getName();
			break;
		case "moveCityComplete":
			type_name = EventName.moveCityComplete.getName();
			break;
		case "buildFortComplete":
			type_name = EventName.buildFortComplete.getName();
			break;
		case "withdrawalForce":
			type_name = EventName.withdrawalForce.getName();
			break;
		case "investConsumption":
			type_name = EventName.investConsumption.getName();
			break;
		case "startFighting":
			type_name = EventName.startFighting.getName();
			break;
			
		case "creatUnionBuild":
			type_name = EventName.creatUnionBuild.getName();
			break;
		case "levelUpunionBuild":
			type_name = EventName.levelUpunionBuild.getName();
			break;
		case "removeUnionBuild":
			type_name = EventName.removeUnionBuild.getName();
			break;
		case "cRemoveUnionBuild":
			type_name = EventName.cRemoveUnionBuild.getName();
			break;
		case "ruinsHarvest":
			type_name = EventName.ruinsHarvest.getName();
			break;
		case "roleTradeCityResource":
			type_name = EventName.roleTradeCityResource.getName();
			break;	
		case "fortressAnd":
			type_name = EventName.fortressAnd.getName();
			break;
		case "FortressLevelUp":
			type_name = EventName.FortressLevelUp.getName();
			break;
		case "MailItemsHandler":
			type_name = EventName.MailItemsHandler.getName();
			break;
		default:
			type_name = "尚未记录事件";
			break;
		}
		return type_name;
	}

	public static String getItemType(String itemName) {
		String item_type = "";
		Item it = dataManager.serach(Item.class, itemName);
		if (it != null) {
			byte itemType = it.getItemType();
			switch (itemType) {
			case 1:
				item_type = ItemType.fouresources.getName();
				break;
			case 2:
				item_type = ItemType.speedprop.getName();
				break;
			case 3:
				item_type = ItemType.buffprop.getName();
				break;
			case 4:
				item_type = ItemType.chest.getName();
				break;
			case 5:
				item_type = ItemType.movecity.getName();
				break;
			case 6:
				item_type = ItemType.viptime.getName();
				break;
			case 7:
				item_type = ItemType.vipexp.getName();
				break;
			case 8:
				item_type = ItemType.functionprop.getName();
				break;
			case 9:
				item_type = ItemType.equipmater.getName();
				break;
			case 10:
				item_type = ItemType.gold.getName();
				break;
			case 11:
				item_type = ItemType.gem.getName();
				break;
			case 12:
				item_type = ItemType.copper.getName();
				break;
			case 13:
				item_type = ItemType.sliver.getName();
				break;
			case 14:
				item_type = ItemType.krypton.getName();
				break;
			case 15:
				item_type = ItemType.roleexp.getName();
				break;
			case 16:
				item_type = ItemType.stamina.getName();
				break;
			case 17:
				item_type = ItemType.drawing.getName();
				break;
			case 18:
				item_type = ItemType.turntable.getName();
				break;
			case 99:
				item_type = ItemType.rate.getName();
				break;
			default:
				item_type = "物品类型遗漏";
				break;
			}
		} else {
			String[] str=itemName.split("_");
			String s =str[0];
			switch (s) {
			case "food":
				item_type = "食品";
				break;
			case "metal":
				item_type = "金属";
				break;
			case "oil":
				item_type = "石油";
				break;
			case "alloy":
				item_type = "合金";
				break;
			case "krypton":
				item_type = "氪晶";
				break;
			case "copper":
				item_type = "银筹码";
				break;
			case "silver":
				item_type = "银币";
				break;
			case "gem":
				item_type = "金筹码";
				break;
			case "Equip":
				item_type = "装备";
				break;
			default:
				item_type = "其他";
				break;
			}
		}
		return item_type;
	}
	
	
	public static String getBuyType(String type) {
		String buy_type = "";
		switch (type) {
		case "gold":
			buy_type = "金币";
			break;
		case "food":
			buy_type = "食品";
			break;
		case "metal":
			buy_type = "金属";
			break;
		case "oil":
			buy_type = "石油";
			break;
		case "alloy":
			buy_type = "合金";
			break;
		default:
			buy_type = "其他";
			break;
		}
		return buy_type;
	}
		
	/*
	 * 物品中文对照
	 */
	public static String getCHName(String item) {
		String name = "";
		Item it = dataManager.serach(Item.class, item);
		if (it != null) {
			name = it.getBeizhuname();
		} else {
			switch (item) {
			case "food":
				name = "食品";
				break;
			case "metal":
				name = "金属";
				break;
			case "oil":
				name = "石油";
				break;
			case "alloy":
				name = "合金";
				break;
			case "time":
				name = "时间";
				break;
			case "userexp":
				name = "经验";
				break;
			case "goldcoin":
				name = "金币";
				break;
			case "power":
				name = "电力";
				break;
			case "material":
				name = "材料";
				break;
			case "item":
				name = "道具";
				break;
			case "equip":
				name = "装备";
				break;
			case "allianceContr":
				name = "联盟积分";
				break;
			case "persContr":
				name = "成员贡献度";
				break;
			case "copper":
				name = "银筹码";
				break;
			case "krypton":
				name = "氪晶";
				break;
			case "gem":
				name = "金筹码";
				break;
			case "silver":
				name = "银币";
				break;
			case "monthCard":
				name = "月卡";
				break;
			case "point":
				name = "活跃点数";
				break;
			case "stamina":
				name = "玩家体力";
				break;
			default:
				name = "未知";
				break;
			}
		}
		return name;
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
