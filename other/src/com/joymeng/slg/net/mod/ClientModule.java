package com.joymeng.slg.net.mod;

import com.joymeng.services.core.buffer.JoyBuffer;

public interface ClientModule {
	public static final byte DATA_TRANS_TYPE_ADD              = 0;//添加
	public static final byte DATA_TRANS_TYPE_DEL              = 1;//删除
	public static final byte DATA_TRANS_TYPE_UPDATE           = 2;//修改
	public static final short NTC_DTCD_DATA_MODULE            = 10000;//拆包数据模块
	public static final short NTC_DTCD_ENTER_SERVICE_START    = 10001;//服务器启动木块
	public static final short NTC_DTCD_USER_INFO               = 10002;//玩家数据
	public static final short NTC_DTCD_ROLE_CITY               = 10003;//城市信息
	public static final short NTC_DTCD_ROLE_BUILD              = 10004;//建筑数据
	public static final short NTC_DTCD_RESSOURCE_CAHNGE       = 10005;//资源改变模块
	public static final short NTC_DTCD_ROLE_ARMY			      = 10006;//玩家城池中的部队信息
	public static final short NTC_DTCD_ROLE_TECH			      = 10007;//玩家科技树信息
	public static final short NTC_DTCD_MESSAGE_TIP             = 20000;//文字提示
	public static final short NTC_DTCD_ROLE_BAG				  = 10008;//玩家背包
	public static final short NTC_DTCD_ROLE_ITEM_CHANGE		  = 10009;//道具数量改变
	public static final short NTC_DTCD_ROLE_EXPEDITE		      = 10010;//出征部队到达目的地
	public static final short NTC_DTCD_GEM_LIST				  = 10011;//宝石等待生产队列
	public static final short NTC_DTCD_ROLE_EQUIP			  = 10012;//玩家装备
	public static final short NTC_DTCD_MAP_OBJ_DEL			  = 10013;//地图对象删除
	public static final short NTC_DTCD_MAP_FAVORITE_POSITION  = 10014;//地图坐标收藏
	public static final short NTC_DTCD_SHOW_ITEMS			  = 10015;//显示装备/材料/物品
	public static final short NTC_DTCD_ROLE_SKILL			  = 10016;//玩家技能树信息
	public static final short NTC_DTCD_MAP_TROOPS			  = 10017;//部队信息
	public static final short NTC_DTCD_ROLE_SKILL_CHANGE	     = 10018;//玩家技能树信息
	public static final short NTC_DTCD_UNION_BODY		      = 10019;//联盟主体
	public static final short NTC_DTCD_UNION_MEMBER		  	= 10020;//联盟成员
	public static final short NTC_DTCD_UNION_MUST_EXIT	 	= 10021;//必须立马退出联盟以及联盟的子界面
	public static final short NTC_DTCD_ROLE_STAMINA		  	= 10022;//玩家体力模块
	public static final short NTC_DTCD_CHAT				  	= 10023;//聊天模块
	public static final short NTC_DTCD_ROLE_RANK		  	    = 10024;//指挥官排行榜模块
	public static final short NTC_DTCD_UNION_ASSISTANCE	  	= 10025;//联盟帮助
	public static final short NTC_DTCD_UNION_RANK		  	= 10026;//联盟排行榜模块
	public static final short NTC_DTCD_GAME_CONFIG		  	= 10027;//客户端所需游戏配置信息模块
	public static final short NTC_DTCD_UNION_TECH		  	= 10028;//联盟科技模块
	public static final short NTC_DTCD_UNION_STORE			= 10029;//联盟商店模块
	public static final short NTC_DTCD_ALL_MISSION		   	= 10030;//所有的任务
	public static final short NTC_DTCD_UPDATE_MISSION		= 10031;//已更新的任务
	public static final short NTC_DTCD_ROLE_SIGN		  	= 10032;//玩家签到模块
	public static final short NTC_DTCD_ROLE_DAILY		  	= 10033;//玩家每日在线奖励
	public static final short NTC_DTCD_UNION_RECORDS		= 10034;//联盟记录模块
	public static final short NTC_DTCD_UNION_FIGHT_NUM	     = 10035;//联盟战斗数量通知
	public static final short NTC_DTCD_UNION_TECH_PROGRESS	 = 10036;//个人联盟科技的进度
	public static final short NTC_DTCD_CITY_NEED_MOVE       = 10037;//主城需要移动到新的位置
	public static final short NTC_DTCD_ROLE_CITY_RESBUFF    = 10038;//城市资源增益信息
	public static final short NTC_DTCD_ROLE_ARMY_MOVEBUFF    = 10039;//部队行军速度buff
	public static final short NTC_DTCD_ROLE_DETAILS_INFO     = 10040;//玩家详情
	public static final short NTC_DTCD_UNION_DEFENDER_ATTACK = 10041;//联盟防御塔攻击
	public static final short NTC_DTCD_ROLE_HONOR_WALL		 = 10042;//玩家荣誉墙
	public static final short NTC_DTCD_NUCLEARSILO_WORNING	 = 10043;//核弹10秒后爆炸
	public static final short NTC_DTCD_ROLE_FREQUENT_VARIABLES	 = 10044;//用户频繁变化的参数模块
	public static final short NTC_DTCD_ROLE_ARMY_POINTS       = 10045;//士兵解锁点数
	public static final short NTC_DTCD_ROLE_VIP_INFO       = 10046;//vip信息
	public static final short NTC_DTCD_ROLE_VIEWS          = 10047;//玩家视野
	public static final short NTC_DTCD_ROLE_ARMY_GROUP        = 10048;//玩家军队分组信息模块
	public static final short NTC_DTCD_ROLE_TURNTABLE         = 10049;//玩家大转盘模块
	public static final short NTC_DTCD_ROLE_SHOP_LAYOUT       = 10050;//玩家人民币商城布局数据
	public static final short NTC_DTCD_ROLE_BLACKLIST		= 10051;//玩家黑名单模块
	public static final short NTC_DTCD_DAILY_TASK		   	= 10052;//日常任务
	public static final short NTC_DTCD_ROLE_SETTING			= 10054;//用户设置
	public static final short NTC_DTCD_COMMANDER_INFO	 = 10053;//指挥官详情中的部分字段
	public static final short NTC_DTCD_MARKET_REFRESH	 = 10055;//黑市刷新了
	public static final short NTC_DTCD_CITY_STATE     	= 10056;//城市状态信息
	public static final short NTC_DTCD_CITY_WALL_INFO 		= 10057;//城墙信息
	public static final short NTC_DTCD_UNION_SYSTEM_STORE			= 10058;//联盟系统商店模块
	public static final short NTC_DTCD_ROLE_COPYS = 10059; // 用户副本进度模块
	public static final short NTC_DTCD_CITY_BUFF			= 10060;//城池buff
	public static final short NTC_DTCD_GET_ACTVT_LIST		= 10061; // 获取活动列表
	public static final short NTC_DTCD_GET_ACTVT_DETAIL		= 10062; // 获取活动详细信息
	public static final short NTC_DTCD_GET_ACTVT_RANK_LIST	= 10063; // 获取活动排行榜
	public static final short NTC_DTCD_FIGHT_REDUCE_BY_NO_FOOD = 10064; //粮食不足影响战斗力
	public static final short NTC_DTCD_NEW_SERVER_BUFF = 10065; // 新服BUFF
	public static final short NTC_DTCD_ATTACK_CITY_SUCC = 10066; //攻城胜利
	public static final short NTC_DTCD_ACTIVITY_TIP = 10067; // 活动HUD提示和领奖提示
	public static final short NTC_DTCD_MAP_OBJ_CHANGE = 10068; //大地图有建筑编号
	public static final short NTC_DTCD_REDPACKET_ROLE_RP = 10069; //用户的红包记录
	public static final short NTC_DTCD_REBELLION_INFO = 10070; // 叛军暴乱信息
	public static final short NTC_DTCD_KICK_ROLE = 10071; //把玩家踢下线，返回登录界面
	public static final short NTC_DTCD_ADVERTISEMENT = 10072; // 广告内容
	public static final short NTC_DTCD_ECECT = 10073; // 电力调节

	/**
	 * 模块类型
	 * @return
	 */
	public short getModuleType();
	/**
	 * 串行化
	 * @param out
	 */
	public void serialize(JoyBuffer out);
}
