package com.joymeng.slg.world;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.Properties;

import com.joymeng.Const;
import com.joymeng.log.GameLog;

public class GameConfig {
	public static long SYSTEM_TRANFOEM_ID = 1850812;//服务器列表传输数据的用户编号
	public static int SERVER_LIST_ID = 0x3001;//服务器列表实例号
	public static int MAP_WIDTH    = 1201;//世界地图宽
	public static int MAP_HEIGHT   = 1201;//世界地图高
	public static float MAP_CELL_WIDTH  = 25.6f;//世界地图格子宽
	public static float MAP_CELL_HEIGHT = 12.8f;//世界地图格子高
	public static int MAP_SAVE_TIME = 15;//大地图保存间隔时间
	public static String LANGUAGE_NAME = "CH";//国际化语言类型
	public static String CITYINITIALIZE_ID ="1";
	public static int HEART_TIME   = 5;
	public static int STAMINA_RESUME_TIME = 10;//每5分钟体力恢复一点
	public static int STAMINA_MAX_NUM =100 ;//体力上限
	public static boolean OPEN_JMX_FLAG = true;
	public static byte BATTLE_FIELD_COL = 1;
	public static byte BATTLE_FIELD_ROW = 5;
	public static int BASE_TRAIN_SPACE = 10;//基础训练空间
	public static int UNION_CHAT_MES_MAX_NUM = 1000;//联盟聊天内容最大保存数量
	public static int WORLD_CHAT_MES_MAX_NUM = 1000;//世界聊天内容最大保存数量
	public static int GROUP_CHAT_ROLE_MAX_NUM = 100;//群组人数最大数量
	public static long CHAT_MULTI_OLD_CLEAR = 24*60*60*1000;//多人聊天保存记录时间
	public static long CHAT_PRIVATE_OLD_CLEAR = 24*60*60*1000;//私聊保存记录时间
	public static long CHAT_INTERVAL_SECOND = 3;//聊天间隔3秒
	public static int BUILD_ASSISTANCE_MAX_NUM = 5;//玩家帮组次数
	public static int BUILD_ASSISTANCE_EFFECT = 60;//玩家帮组的效果
	public static int UNION_NAME_MIN = 4;			//联盟名字的最小字符数
	public static int UNION_NAME_MAX = 12;			//联盟名字的最大字符数
	public static int UNION_SHORTNAME_LIMIT = 3;	//联盟简称的限制字符数
	public static int UNION_CHANGE_FLAG_PRICE = 1000;//修改联盟旗帜的价格
	public static int UNION_TITLE_MIN = 2;			//联盟称谓的最小字符数
	public static int UNION_TITLE_MAX = 10;			//联盟称谓的最大字符数
	public static float MAP_SPEED_SLOW = 0.5f;    //减速因子
	public static float EXPEDITE_SPEED_EFFECT = 0.0008f;//行军速度因子
	public static int GROUP_NAME_MAX = 16;		//讨论的名称字符限制
	public static short CHAT_MIN_LEVEL = 1;		//聊天的最低等级限制
	public static int FORTRESS_LEVEL_MAX = 5;	//要塞的最高等级
	public static short EXPEDITE_TROOPS_NUM = 2;		//出征队伍数量初始值
	public static short EXPEDITE_SOLDIER_NUM = 0;   //出征士兵数量初始值
	public static float LOGISTICS_CENTER_LOOT_RATE = 0.3f;//仓库掠夺率
	public static int EXPEDITE_SPY_COST = 50;//侦查所需的粮食(因子)
	public static double SPY_NUM_FLUCTUATE_FACTOR = 0.2;//侦查时使用的大致数量的波动系数(<1)
	public static int UNION_RECORD_MAX = 200;// 联盟记录队列的最大值
	public static int BUY_QUEUE_COST_MONEY = 40;//购买建筑队列的金币数量
	public static int BUY_QUEUE_GET_TIME = 8;//购买队列的持续时间(天数)
	public static boolean USE_DATA_EDIT_DB = true;//使用数据编辑器数据库
	public static int NOTICE_INVITE_PRICE = 200;//公告邀请价格
	public static String CITY_WAL_ARMY_ID = "Fence_00";//玩家城墙兵种编号
	public static int EXPEDITE_MONSTER_NEED_STAMINA = 10;//去打怪需要体力
	public static int EXPEDITE_ECTYPE_NEED_STAMINA = 10;//去副本需要体力
	public static int ROLE_ARMY_GROUP_MAX = 3;//军队分组最大个数
	public static int TURNTABLE_PRICE = 1000;//大转盘的消耗
	public static int BLACK_MARKET_REFRESH_TIME = 28800;//黑市刷新倒数计时间
	public static int BLACK_MARKET_CELL_NUM = 4;//黑市刷新格子数
	public static int CHANGE_UNION_NAME_PRICE = 500;//修改联盟名称所需金币
	public static int CHANGE_UNION_SHORTNAME_PRICE = 100;//修改联盟简称所需金币
	public static boolean BIG_MAP_USE_NEW_MONSTER = false;//使用新的刷新规则
	public static long WORLD_NOTICE_REFRESH_TIME = 20;//世界公告消息刷新的时长 (s秒)
	public static long UNION_DONATE_MAX_NUM = 20;// 联盟捐赠进入休息的最大次数
	public static long UNION_DONATE_TIME_PER = 300;// 单次捐赠休息的时长
	public static String APK_VERSION  = "1.0.1";//apk版本号
	public static String CODE_VERSION = "5";//代码版本号
	public static boolean ANTI_ADDICTION_FLAG = false;//防沉迷系统开启标志
	public static boolean SEND_REALTIME_DATA = true;//是否向后台发送实时数据
	public static int RELIC_MAX_FREE_RESET_NUM = 1;// 最大副本免费重置次数
	public static int RELIC_MAX_ITEM_RESET_NUM = 3;// 最大副本道具重置次数
	public static int FORTRESS_NAME_MIN = 2;// 要塞名字的最小
	public static int FORTRESS_NAME_MAX = 10;// 要塞名字的最大
	public static String CHARGE_SHOP_TIP = "msg_rechargeShopView_no_open";//充值商店是否开启提示语
	public static boolean ATTACK_CITY_MUST_WIN = false;
	
	public static void load() throws Exception{
		Properties properties = new Properties();
		File file = new File(Const.CONF_PATH + "game.properties");
		properties.load(new FileInputStream(file));
		Field[] fields = GameConfig.class.getDeclaredFields();
		for (Field field : fields) {
			String str = properties.getProperty(field.getName());
			if (str == null) {
				continue;
			}
			loadOneProperty(field,str,null);
		}
		GameLog.info("Game Properties loaded!");
	}
	
	public static void loadOneProperty(Field f , String val , Object obj) throws Exception {
		if (f.getType() == byte.class) {
			f.set(obj, Byte.parseByte(val));
		}else if (f.getType() == int.class) {
			f.set(obj, Integer.parseInt(val));
		}else if (f.getType() == short.class) {
			f.set(obj, Short.parseShort(val));
		}else if (f.getType() == long.class) {
			f.set(obj, Long.parseLong(val));
		}else if (f.getType() == float.class) {
			f.set(obj, Float.parseFloat(val));
		}else if (f.getType() == boolean.class) {
			f.set(obj, Boolean.parseBoolean(val));
		}else if (f.getType() == String.class) {
			f.set(obj, val);
		}else if (f.getType() == Timestamp.class) {
			f.set(obj, Timestamp.valueOf(val));
		}
	}
}
