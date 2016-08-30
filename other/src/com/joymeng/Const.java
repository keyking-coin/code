package com.joymeng;

/**
 * @author Dream
 * 
 */
public interface Const {
	String RES_PATH = "./res/";
	String CONF_PATH = "./conf/";
	String NOC_PATH = "./notice/";
	String MARKET_SAVE_FILE_NAME = CONF_PATH + "Market.json";
	//redAlert
	long ONE_HOUR_TIME = 60*60;
	long FIVE_MINUTE = 5*60;
	float RES_CANCEL_RETURN_RATE = 0.5f;//取消各种操作，返还资源百分比
	float RES_BACK_DISMISS_ARMY = 0.6f; //解散士兵资源返还百分比
	float CURE_SOLDIER_COST_RATE = 0.5f;//治疗伤兵的资源 是训练的百分比.
	float CURE_SOLDIER_COST_TIME = 0.5f;//治疗伤兵的时间 是训练的百分比.1111111111
	
	int GEM_GRID_SIZE = 0;//材料生产建筑默认格子初始数量
	int GEM_MAX_GRID_SIZE=6;//材料生产建筑最大可解锁格子数量
	
	long GEM_PD_TIME = 2*Const.HOUR/1000;//每个材料生产时间
	
	long REPAIR_WALL_TIME = 30*60;//城墙修理一次的时间
	int REPAIR_WALL_VALUE = 50;//城墙修理一次的完成值
//	float REPAIR_WALL_MONEY_RATE = 36;//一次性完成城墙修理时消耗的金币计算  需要维修的城防值/100*3600=理论维修时间，走加速公式
	
	long CITY_FIRE_TIME = Const.HOUR / 2000;//战败后城池的失火时间
	int CITY_OUT_FIRE_MONEY = 50;//主动灭火时消耗的金币数量
	int CITY_FIRE_RED_DEFENCE = 20;//燃烧时，每隔5分钟减一次城防值
	long CITY_FIRE_INTERVAL = Const.MINUTE * 5 / 1000;
	
//	int BUY_QUEUE_COST_MONEY = 500;//购买建筑队列的金币数量
//	long BUY_QUEUE_GET_TIME = 2*Const.DAY/1000;//购买队列的持续时间
	
	long ARMY_COST_FRESH_TIME = Const.MINUTE * 10;//士兵粮食消耗的刷新时间
	
	long READ_LOG_TIME = Const.SECOND*5;//读取log文件截止提前5秒钟
	
	long  ACTICE_VIP_TIME=Const.HOUR;//激活VIP时间持续一天
	
	int BUY_STAMINA_NUM = 50; //购买体力一次增加的数量
	int MAXSTAMINA = 100;//体力上限
	int INITMAXBUYTIMES = 2;//初始化体力购买最大次数
	
	byte TASK_COND_TYPE_TIMES = 1;//累计
	byte TASK_COND_TYPE_MAX = 0;//去当前值
	byte TASK_COND_TYPE_UNION = 2;//联盟任务
	byte TASK_COND_TYPE_COUNTRY = 3;//王国任务
	
	byte ATK_MST = 0;
	byte ATK_CY = 1;
	byte MS_ATK = 2;
	byte MS_HLP_ATK = 3;
	byte DEF_CY = 4;
	byte DEF_HLP_CY = 5;
	byte OTHER_F = 99;
	
	//伤兵率初始值
	//每次资源掠夺的最大比例
	
	/*
	 * 时间单位
	 */
	long SECOND = 1000l;
	long MINUTE = 60 * SECOND;
	long HOUR = 60 * MINUTE;
	long DAY = 24 * HOUR;

	//屏蔽字
	String[] CHAT_WORDS_CLEAR = new String[] { "", "*", "**", "***", "****",
			"*****", "******", "*******", "********", "*********" , "**********" };
}