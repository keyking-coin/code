/**
 * 
 */
package com.joymeng.slg.dao;



/**
 * 需要保存到数据库的接口，
 * 只用作update
 * @author Dream
 *
 */
public interface DaoData {
	
	String table();
	
	String[] wheres();
	
	boolean delete();
	
	void insertData(SqlData data);
	
	void save();
	
	void loadFromData(SqlData data);
	
	void saveToData(SqlData data);
	
	void over();
	
	boolean saving();
	
	//红警项目开始
	/**
	 * 通用字段，大部分表可能都有的
	 */
	String RED_ALERT_GENERAL_ID           = "id";
	String RED_ALERT_GENERAL_LEVEL        = "level";
	String RED_ALERT_GENERAL_NAME         = "name";
	String RED_ALERT_GENERAL_UID          = "uid";
	String RED_ALERT_GENERAL_TYPE         = "type";
	String RED_ALERT_GENERAL_CITY_ID      = "cityId";
	String RED_ALERT_GENERAL_UNION_ID     = "unionId";
	String RED_ALERT_GENERAL_CREATE_TIME  = "createTime";
	String RED_ALERT_GENERAL_FIGHT         = "fight";
	String RED_ALERT_GENERAL_POSITION      = "_position";
	String RED_ALERT_GENERAL_STATE		     = "state";
	String RED_ALERT_GENERAL_BUILD_TIMER   = "buildTimer";
	String RED_ALERT_GENERAL_OTHER		     = "other";
	String RED_ALERT_GENERAL_NUM		     = "num";
	String RED_ALERT_GENERAL_SAFETIMER     = "safeTimer";
	
	/**
	 * 玩家表
	 */
	String TABLE_RED_ALERT_ROLE         = "role";
	String RED_ALERT_ROLE_ID            = "joy_id";
	String RED_ALERT_ROLE_COUNTRY       = "countryId";
	String RED_ALERT_ROLE_SEX           = "sex";
	String RED_ALERT_ROLE_EXP           = "exp";
	String RED_ALERT_ROLE_STAMINA       = "stamina";
	String RED_ALERT_ROLE_ICON_TYPE     = "iconType";
	String RED_ALERT_ROLE_ICON_ID       = "iconId";
	String RED_ALERT_ROLE_ICON_NAME     = "iconName";
	String RED_ALERT_ROLE_MONEY		  	= "money";
	String RED_ALERT_ROLE_KRYPTON		= "krypton";
	String RED_ALERT_ROLE_GEM		  	= "gem";
	String RED_ALERT_ROLE_COPPER	  	= "copper";
	String RED_ALERT_ROLE_SLIVER	  	= "silver";
	String RED_ALERT_ROLE_VIPINFO		= "vipInfo";
	String RED_ALERT_ROLE_POSF	        = "posFavorites";
	String rED_ALERT_ROLE_GUIDEIDLIST   = "guidIdList";
	String RED_ALERT_ROLE_POINTS		= "skillPoints";
	String RED_ALERT_ROLE_LAST_JOIN	  	= "lastJoin";
	String RED_ALERT_ROLE_LAST_LOGIN    = "lastLoginTime";
	String RED_ALERT_ROLE_INCOUNTRY     = "country";
	String RED_ALERT_ROLE_LANGUAGE      = "language";
	String RED_ALERT_ROLE_UUID          = "uuid";
	String RED_ALERT_ROLE_UID_REGIS     = "uidRegisTime";
	String RED_ALERT_ROLE_UUID_REGIS    = "uuidRegisTime";
	String RED_ALERT_ROLE_MODEL         = "model";
	String RED_ALERT_ROLE_VERSION       = "version";
	String RED_ALERT_ROLE_RESOLUTION    = "resolution";
	String RED_ALERT_ROLE_MEMORY        = "memory";
	String RED_ALERT_ROLE_OPENID        = "openId";
	String RED_ALERT_ROLE_SIGNIN        = "signIn";
	String RED_ALERT_ROLE_TEGSITID      = "registrationId";
	String RED_ALERT_ROLE_LAST_LOGIN_IP = "lastLoginIp";
	String RED_ALERT_ROLE_CHATGROUPS  	= "chatGroups";
	String RED_ALERT_ROLE_TASKSTATE  	= "taskState";
	String RED_ALERT_ROLE_SEVEN_SIGNIN  = "sevenSignIn";
	String RED_ALERT_ROLE_THIRTY_SIGNIN = "thirtySignIn";
	String RED_ALERT_ROLE_ONLINE_REWARD = "onlineReward";
	String RED_ALERT_ROLE_EFFECTDATA  	= "effectData";
	String RED_ALERT_ROLE_ARMY_GROUP	= "armyGroup";
	String RED_ALERT_ROLE_TURNTABLE     = "turntable";
	String RED_ALERT_ROLE_SHOP	        = "shop";
	String RED_ALERT_BLACKLIST			= "blacklist";
	String RED_ALERT_ROEL_SETTING		= "roleSetting";
	String RED_ALERT_ROEL_MARKET		= "market";
	String RED_ALERT_ROEL_MISSIONS		= "missionDatas";
	String RED_ALERT_ROEL_HONORS		= "honorDatas";
	String RED_ALERT_ROLE_BAGDATAS		= "bagDatas";
	String RED_ALERT_ROLE_SKILLDATAS	= "skillDatas";
	String RED_ALERT_ROLE_ANTI    	    = "anti";
	String RED_ALERT_ROLE_CHANNELID     = "channelId";
	String RED_ALERT_ROLE_COPYS			= "roleCopys";
	String RED_ALERT_ROLE_RANK_INFO		= "rankInfo";
	
	
	/**
	 * 建筑数据字段部分
	 */
	String TABLE_RED_ALERT_BUILD        = "build";
	String RED_ALERT_BUILD_TYPE_ID      = "buildId";
	String RED_ALERT_BUILD_SLOT_ID      = "slotID";
	String RED_ALERT_BUILD_CITY_ID      = "cityId";
	String RED_ALERT_BUILD_TIMERS       = "timers";
	String RED_ALERT_BUILD_COMPONENTS    = "components";
	String RED_ALERT_BUILD_ARMYPOINTS	= "armypoints";
	
	/**
	 *   城市表
	 */
	String TABLE_RED_ALERT_CITY            = "city";
	String RED_ALERT_CITY_RESOURCES        = "resources";
	String RED_ALERT_CITY_BUILD_QUEUE      = "buildQueue";
	String RED_ALERT_CITY_RESEARCH_QUEUE   = "researchQueue";
	String RED_ALERT_CITY_STATE		       = "state";
	String RED_ALERT_CITY_TIMERS           = "timers";
	String RED_ALERT_CITY_RESSYNCTIME      = "resSyncTime";
	String RED_ALERT_CITY_MESS             = "mass";
	String RED_ALERT_CITY_STATUS		   = "cityState";
	String RED_ALERT_CITY_LANDIDS		   = "landIds";
	String RED_ALERT_CITY_ARMYS			   = "armyDatas";
	String RED_ALERT_CITY_BUILDS		   = "buildDatas";
	String RED_ALERT_CITY_LEVEL			   = "centerLevel";
	String RED_ALERT_CITY_RADARLVL		   = "radarLevel";
	String RED_ALERT_CITY_MAXBUILDKEY 	   = "maxBuildKey";
	String RED_ALERT_CITY_TECHDATAS		   = "techDatas";
	String RED_ALERT_CITY_WALLINFO		   = "wallInfo";
	String RED_ALERT_CITY_HELPER_NUM	   = "helperNum";
	/**
	 * 玩家军队
	 */
	String TABLE_RED_ALERT_ARMY			   = "roleArmy";
	String RED_ALERT_ARMY_TYPE_ID		       = "armyId";
	String RED_ALERT_ARMY_NUM			       = "armyNum";
	String RED_ALERT_ARMY_STATE			   = "armyState";
	
	/**
	 * 玩家科技表
	 */
	String TABLE_RED_ALERT_TECH		       ="technology";
	String RED_ALERT_TECH_TYPE_ID		   ="techId";
	String RED_ALERT_TECH_LEVEL			   ="level";
	String RED_ALERT_TECH_BUFF			   ="buff";
	
	/**
	 * 玩家技能表
	 */
	String TABLE_RED_ALERT_SKILL	       ="skill";
	String RED_ALERT_SKILL_ID		   	   ="skillId";
	String RED_ALERT_SKILL_BRANCHID	   	   ="branchId";
	String RED_ALERT_SKILL_LEVEL		   ="level";
	String RED_ALERT_SKILL_TYPE			   ="isActive";
	String RED_ALERT_SKILL_STATE		   ="state";
	String RED_ALERT_SKILL_TIME		   	   ="time";
	
	/**
	 * 背包表
	 */
	String TABLE_RED_ALERT_BAG		       = "bag";
	String RED_ALERT_BAG_KEY		       = "keyid";
	
	/**
	 *  资源点
	 */
	String TABLE_RED_ALERT_RESOURCES	               = "resources";
	String RED_ALERT_RESOURCES_REFRESHID	           = "refreshId";
	String RED_ALERT_RESOURCES_OUTPUT	               = "output";
	String RED_ALERT_RESOURCES_COLLECT_SPEED	       = "collectSpeed";
	String RED_ALERT_RESOURCES_COLLECT_EFFECT	       = "collectEffect";
	String RED_ALERT_RESOURCES_COLLECT_TIME	       = "collectTime";
	String RED_ALERT_RESOURCES_COLLECT_NUM	       = "collectNum";
	String RED_ALERT_RESOURCES_COLLECT_LISTENERS	   = "listeners";
	
	/**
	 * 怪物
	 */
	String TABLE_RED_ALERT_MONSTER	                   = "monster";
	String RED_ALERT_MONSTER_AUTO_DIE	               = "autoDie";
	
	/**
	 * 驻防者表
	 */
	String TABLE_RED_ALERT_GARRISON	    = "roleGarrison";
	String RED_ALERT_GARRISON_TIMER		= "timer";
	String RED_ALERT_GARRISON_TROOPS		= "troops";
	
	/***
	 * 代理对象,据点 等
	 */
	String TABLE_RED_ALERT_MAPOBJ	             = "mapObj";
	String RED_ALERT_MAP_DELETE	             = "delTimer";
	
	/***
	 * 玩家出征部队
	 */
	String TABLE_RED_ALERT_ROLEEXPEDITE	        = "roleExpedite";
	String RED_ALERT_ROLEEXPEDITE_STARTPOSITION	= "startPosition";
	String RED_ALERT_ROLEEXPEDITE_TARGETPOSITION	= "targetPosition";
	String RED_ALERT_ROLEEXPEDITE_SPEED           = "speed";
	String RED_ALERT_ROLEEXPEDITE_TEAMS           = "teams";
	String RED_ALERT_ROLEEXPEDITE_MASS            = "mass";
	String RED_ALERT_ROLEEXPEDITE_NODES           = "nodes";
	
	/**
	 * 要塞
	 */
	String TABLE_RED_ALERT_FORTRESS	              = "mapFortress";
	String RED_ALERT_FORTRESS_INFO                  = "info";
	String RED_ALERT_FORTRESS_GRIDS                 = "grids";
	
	/**
	 * 迁城点
	 */
	String TABLE_RED_ALERT_CITY_MOVE	              = "cityMove";
	
	/**
	 * 军营
	 */
	String TABLE_RED_ALERT_BARRACKS	              = "mapBarracks";
	String RED_ALERT_BARRACKS_BUILDKEY              = "buildkey";
	
	/**
	 * 联盟
	 */
	String TABLE_RED_ALERT_UNION	                 = "unionBody";
	String RED_ALERT_UNION_ICON                    = "icon";
	String RED_ALERT_UNION_SHORT_NAME              = "shortName";
	String RED_ALERT_UNION_LANGUAGE					="language";
	String RED_ALERT_UNION_NOTICE                  = "notice";
	String RED_ALERT_UNION_SCORE                   = "score";
	String RED_ALERT_UNION_SCOREDAILY              = "scoreDaily";
	String RED_ALERT_UNION_SCOREWEEKLY             = "scoreWeekly";
	String RED_ALERT_UNION_SCORE_RECODE            = "scoreRecord";
	String RED_ALERT_UNION_DONATEDAILY			   = "donateDaily";
	String RED_ALERT_UNION_DONATEWEEKLY			   = "donateWeekly";
	String RED_ALERT_UNION_DONATE_RECORD		   = "donateRecord";
	String RED_ALERT_UNION_DONATE_TYPE				="donateType";
	String RED_ALERT_UNION_DONATE_TIMER				="donateTimer";
	String RED_ALERT_UNION_RECRUITS                = "recruits";
	String RED_ALERT_UNION_TECH					   = "unionTech";
	String RED_ALERT_UNION_TIMERS                  = "timers";
	String RED_ALERT_UNION_UPGRADETECHID		   = "upgradeTechId";
	String RED_ALERT_UNION_SYSTEM_STORE 		   = "sysStore";
	String RED_ALERT_UNION_STORE				   = "store";
	String RED_ALERT_UNION_TITLE				   = "unionTitle";
	String RED_ALERT_UNION_RECORDS  			   = "unionRecords";
	String RED_ALERT_UNION_BALLTE_RECORDS  		   = "unionBattleRecords";
	String RED_ALERT_UNION_GENERAL_RECORDS		   = "unionGeneralRecords";
	String RED_ALERT_UNION_STATIC_INFO			   = "unionStaticInfo";
	String RED_ALERT_UNION_FIGHT_RECORD            = "unionFightRecord";
	
	/**
	 * 联盟成员
	 */
	String TABLE_RED_ALERT_UNION_MEMBER	         = "unionMember";
	String RED_ALERT_UNION_MEMBER_JOIN_TIME       = "joinTime";
	String RED_ALERT_UNION_MEMBER_ALLIANCE_KEY    = "allianceKey";
	String RED_ALERT_UNION_MEMBER_PERMISSIONS     ="permissions";
	String RED_ALERT_UNION_MEMBER_TECH_PROGRESS	  ="techProgress";
	
	/**
	 * 聊天群组
	 */
	String TABLE_RED_ALERT_CHATGROUP        	 = "chat_group";
	String RED_ALERT_CHATGROUP_NAME				 = "name";
	String RED_ALERT_CHATGROUP_CREATOR			 = "creator_id";
	String RED_ALERT_CHATGROUP_MSG_KEY 			 = "msg_key";
	String RED_ALERT_CHATGROUP_ROLES			 = "roles";
	String RED_ALERT_CHATGROUP_MSGS				 = "msgs";
	String RED_ALERT_CHATGROUP_UPDATEDATE		 = "last_update_date";
	String RED_ALERT_CHATGROUP_CREATEDATE		 = "create_date";
	String RED_ALERT_CHATGROUP_ROLESIZE			 = "roles_size";

	/**
	 * 玩家每日聊天记录
	 */
	String TABLE_RED_LAERT_ROLE_DAILYACT 		 = "player_dailyact";
	String RED_ALERT_PLAYER_DAILYACT_ID 		 = "id"; 
	String RED_ALERT_PLAYER_DAILYACT_CAHT 		 = "chat";
	
	String TABLE_RED_ALERT_NPC_CITY		       	= "npc_city";
	String RED_ALERT_NPC_CITY_KEY		       	    = "cityKey";
	String RED_ALERT_NPC_CITY_GIVEUP              = "giveUpTimer";
	String RED_ALERT_NPC_CITY_BUILDS              = "builds";
	String RED_ALERT_NPC_CITY_CONQUERER           = "conquerer";
	
	/**
	 * 玩家任务
	 */
	String TABLE_RED_ALERT_MISSION			="roleMission";
	String RED_ALERT_MISSION_ID				="missionId";
	String RED_ALERT_MISSION_CONDTYPE		="conditionType";
	String RED_ALERT_MISSION_TYPE			="missionType";
	String RED_ALERT_MISSION_UNIONTYPE		="missionUnionType";
	String RED_ALERT_MISSION_CONDITION		="conditions";
	String RED_ALERT_MISSION_SCHEDULE		="schedule";
	String RED_ALERT_MISSION_MAX_SCHEDULE	="maxSchedule";
	String RED_ALERT_MISSION_STATE			="state";
	String RED_ALERT_MISSION_ISACTIVE		="isActive";
	String RED_ALERT_MISSION_UPDATETIME		="updateTime";
	
	/**
	 * 荣誉任务
	 */
	String TABLE_RED_ALERT_HONOR			="roleHonor";
	String RED_ALERT_MISSION_HONORID        ="honorId";
	String RED_ALERT_MISSION_TASKNUM        ="taskNum";
	String RED_ALERT_MISSION_STARNUM        ="starNum";
	/**
	 * 玩家任务索引
	 */
	String TABLE_RED_ALERT_TASKINDEX		="taskIndex";
	String RED_ALERT_TASK_TYPE_ID			="taskTypeId";
	String RED_ALERT_TASK_TYPE_INDEX		="taskIndex";
	/**
	 * 每日任务
	 */
	String TABLE_RED_ALERT_DAILYTASK		="dailyTask";
	String RED_ALERT_TASK_SCHEDULE			="schedule";
	String RED_ALERT_TASK_GET_INDEX			="rewardState";
	String RED_ALERT_TASK_UPDATE_TIMER		="timer";
	String RED_ALERT_TASK_MAP				="missions";

	/**
	 * 世界聊天
	 */
	String TABLE_RED_ALERT_CHATWORLD        ="worldChat";
	String RED_ALERT_CHATWORLD_UID          ="uid";
	String RED_AlERT_CHATWORLD_KEY          ="key";
	String RED_ALERT_CHATWORLD_MSG          ="msg";
	String RED_ALERT_CHATWORLD_CREATETIME   ="createtime";
	
	String TABLE_RED_ALERT_UNION_BUILD    = "unionBuild";
	String RED_ALERT_UNION_BUILD_KEY	    = "buildKey";
	String RED_ALERT_UNION_CITY_KEY	    = "citykey";
	String RED_ALERT_UNION_BUILD_SUNS	    = "unionStr";
	
	/**
	 * 世界统计表
	 */
	String TABLE_RED_ALERT_WD_ST = "worldStaticData";
	String RED_ALERT_MONSTER_ID = "monsterId";
	String RED_ALERT_MONSTER_NUM = "killNum";
	/**
	 * 玩家统计数据表
	 */
	String TABLE_RED_ALERT_STATIC = "statisticData";
//	uid
	String RED_ALERT_S_RF = "roleFight";
	String RED_ALERT_S_RBF = "roleBuildFight";
	String RED_ALERT_S_RTF = "roleTechFight";
	String RED_ALERT_S_RAF = "roleArmyFight";
	String RED_ALERT_S_FWT = "fightWinTimes";
	String RED_ALERT_S_FFT = "fightFailTimes";
	String RED_ALERT_S_KMS = "killMsterSold";
	String RED_ALERT_S_AWT = "attackWinTimes";
	String RED_ALERT_S_AFT = "attackFailTimes";
	String RED_ALERT_S_DWT = "defenceWinTimes";
	String RED_ALERT_S_DFT = "defenceFailTimes";
	String RED_ALERT_S_HDWT = "helpDefenceWinTimes";
	String RED_ALERT_S_HDKN = "helpDefenceKillNum";
	String RED_ALERT_S_MKN = "massKillNum";
	String RED_ALERT_S_HMWT = "helpMassWinTimes";
	String RED_ALERT_S_ST = "spyTimes";
	String RED_ALERT_S_KSN = "killSoldsNum";
	String RED_ALERT_S_DSN = "deadSoldNum";
	String RED_ALERT_S_AHT = "alliHelpTimes";
	String RED_ALERT_S_BFN = "buildFortNum";
	String RED_ALERT_S_RT = "researchTimes";
	String RED_ALERT_S_EUT = "equipUpTimes";
	String RED_ALERT_S_ELT = "equiplhTimes";
	String RED_ALERT_S_EFT = "equipfjTimes";
	String RED_ALERT_S_MN = "materialNum";
	String RED_ALERT_S_ESM = "equipsMap";
	String RED_ALERT_S_KSM = "killsMap";
	String RED_ALERT_S_TSM = "trainsMap";
	String RED_ALERT_S_CSM = "curesMap";
	String RED_ALERT_S_HSM = "harvestsMap";
	String RED_ALERT_S_CLTSM = "collectsMap";
	String RED_ALERT_S_RSM = "robsMap";
	
	/**
	 * 服务器道具限购表
	 */
	String TABLE_RED_ALERT_SHOP_LIMIT = "shopLimit";
	
	
	String TABLE_RED_ALERT_ACTIVITY         = "activity";
	String RED_ALERT_ACTIVITY_DESCRIPTION   = "description";
	String RED_ALERT_ACTIVITY_FILENAME      = "fileName";
	String RED_ALERT_ACTIVITY_DESENO        = "deseno";
	String RED_ALERT_ACTIVITY_STARTDATE     = "startDate";
	String RED_ALERT_ACTIVITY_ENDDATE       = "endDate";
	
	
	String TABLE_RED_ALERT_CHARGE_ORDER     = "chargeOrder";
	
	String RED_ALERT_CHARGE_ORDER_ID         = "orderId";
	String RED_ALERT_CHARGE_JOYID             = "joyId";
	String RED_ALERT_CHARGE_VALUE             = "_value";
	String RED_ALERT_CHARGE_ORDERTYPE         = "orderType";
	String RED_ALERT_CHARGE_ORDER_PRODUCTID   = "productId";
	String RED_ALERT_CHARGE_ORDER_REWARD      = "reward";
	String RED_ALERT_CHARGE_ORDER_TIME        = "_time";
	
	/*
	 * 活动相关
	 */
	String TABLE_RED_ALERT_ACTVT = "actvt";
}
