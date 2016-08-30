package com.joymeng.common.util;

import com.joymeng.slg.domain.data.DataManager;
import com.joymeng.slg.domain.str.Stringcontent;

public class I18nGreeting {
	
	public static final String MSG_SERVICE_CLOSE_TIP      = "msg_service_close_tip";
	//服务器暂时还未开启服务
	public static final String MSG_SERVICE_NOT_START      = "msg_service_not_start";
	//需求{0}的{1}。
	public static final String MSG_NO_RESOURCE             = "msg_no_resource";
	//注册失败。
	public static final String MSG_ROLE_REGIST_FAIL        = "msg_role_regist_fail";
	//您已掉线,请重新登录
	public static final String MSG_ROLE_NOT_FIND           = "msg_role_not_find";
	//{0}已被其他玩家使用
	public static final String MSG_ROLE_NAME_REPEAT         = "msg_role_name_repeat";
	//{0}用户名不合法
	public static final String MSG_ROLE_NAME_ILLEGAL         = "msg_role_name_illegal";
	//{0}只能建造{1}个。
	public static final String MSG_BUILD_MUST_ONLY          = "msg_build_must_only";
	//不能拆除该建筑{0}
	public static final String MSG_BUILD_CANNOT_REMOVE      = "msg_build_cannot_remove";
	//拆除后电力不足,不能拆除该电厂
	public static final String MSG_POWERPLANT_CANNOT_REMOVE = "msg_powerplant_cannot_remove";
	//找不到编号是{0}的建筑。
	public static final String MSG_BUILD_NOT_FIND           = "msg_build_not_find";
	//操作失败,需要{0}级建筑{0}。
	public static final String MSG_BUILD_NEED_BUILD         = "msg_build_need_build";
	//没有可用建筑队列
	public static final String MSG_NO_BUILD_QUEUE           = "msg_no_build_queue";
	//建筑队列时间不足
	public static final String MSG_TIME_BUILD_QUEUE         = "msg_time_build_queue";
	//操作失败,建筑{0}已满级。
	public static final String MSG_BUILD_LEVEL_MAX	        = "msg_build_level_max";
	//建筑{0}时间已被占用。
	public static final String MSG_BUILD_TIMER_UNUSED       = "msg_build_timer_unusable";
	//迁城失败。
	public static final String MSG_MOVE_CITY_FAIL           = "msg_move_city_fail";
	//槽{0}已经被占用。
	public static final String MSG_BUILD_SLOT_UNUSED  	    ="msg_build_slot_unusable";
	//金币不足,需求金币{0}。
	public static final String MSG_ROLE_NO_MONEY		    = "msg_role_no_money";
	//金币不足,需求氪晶{0}。
	public static final String MSG_ROLE_NO_KRYPTON		    = "msg_role_no_krypton";
	//金币不足,需求宝石{0}。
	public static final String MSG_ROLE_NO_GEM		    	= "msg_role_no_gem";
	// 金币不足,需求宝石{0}。
	public static final String MSG_ROLE_NO_COPPER 			= "msg_role_no_copper";
	// 金币不足,需求宝石{0}。
	public static final String MSG_ROLE_NO_SILVER 			= "msg_role_no_silver";
	//联盟捐献不存在
	public static final String MSG_UNION_DONATE_INEXIST		    = "msg_union_donate_inexist";
	//电力不足
	public static final String MSG_BUILD_NO_POWER		    = "msg_build_no_power";
	//建筑忙碌中
	public static final String MSG_BUILD_STATE_WRONG		= "msg_build_state_wrong";
	//建筑{0}训练未完成，不能收取该部队
	public static final String MSG_TRAIN_NO_FINISH		    = "msg_train_no_finish";
	//时间已完成
	public static final String MSG_BUILD_TIMER_OVER			="msg_build_timer_over";
	//建筑{0}状态不正确，不能加速
	public static final String MSG_BUILD_UNACCELERATE	 	="msg_build_unaccelerate";
	//物品{0}数量不足{1}
	public static final String MSG_ITEM_NOT_ENOUGH		 	="msg_item_not_enough";
	//时间类型不正确，不能免费
	public static final String MSG_TIMER_TYPE_NOT_RIGHT		="msg_timer_type_not_right";
	//剩余时间还不能免费
	public static final String MSG_TIMER_NOT_FREE			="msg_timer_not_free";
	//请先升级科技{0}
	public static final String MSG_TECH_NO_PRETECH       	= "msg_tech_no_pretech";
	//科技{0}等级已满
	public static final String MSG_TECH_LEVEL_MAX        = "msg_tech_level_max";
	//体力不足
	public static final String MSG_ROLE_NO_STAMINA       = "msg_role_no_stamina";
	//可出征队伍不足
	public static final String MSG_ROLE_NO_EXPEDITE_NUM  = "msg_role_no_expedite_num";
	//出征失败，找不到目标
	public static final String MSG_ROLE_EXPEDITE_FAIL    = "msg_role_expedite_fail";
	//兵力不足
	public static final String MSG_ROLE_NO_SOLDIER       = "msg_role_no_soldier";
	//没有空位可以调拨
	public static final String MSG_ROLE_NO_GRID          = "msg_role_no_grid";
	//数据无效。
	public static final String MSG_CUREARMY_DATA_INVALID = "msg_curearmy_data_invalid";
	//科技{0}解锁条件不足
	public static final String MSG_TECH_LIMITED          = "msg_tech_limited";
    //{0}装备不存在
	public static final String MSG_EQUIP_UNUSED          = "msg_equip_unused";
    //{0}材料不存在
	public static final String MSG_MATERIAL_UNUSED          = "msg_material_unused";
	//单次只能出征{0}的部队
	public static final String MSG_EXPEDITE_NUM_ONE       = "msg_expedite_num_one";
	//这里无法建造要塞
	public static final String MSG_MAP_FORTRESS_MUST_AT_NONE   = "msg_map_fortress_must_at_none";
	//这个个驻防点不是你，无法建造要塞
	public static final String MSG_CREATE_FORTRESS_NOT_YOUR_GARRSION   = "msg_create_fortress_not_your_garrsion";
	//正在建造
	public static final String MSG_MAP_FORTRESS_CREATE_ING = "msg_map_fortress_create_ing";
	//正在升级
	public static final String MSG_MAP_FORTRESS_LEVEL_ING = "msg_map_fortress_level_ing";
	//正在拆除
	public static final String MSG_MAP_FORTRESS_REMOVE_ING = "msg_map_fortress_remove_ing";
	//最大等级
	public static final String MSG_MAP_FORTRESS_MAX_LEVEL = "msg_map_fortress_max_level";
	//您目前只能建造{0}个要塞
	public static final String MSG_MAP_FORTRESS_LIMITE    = "msg_map_fortress_limite";
	//收藏点坐标未找到
	public static final String MSG_MAP_FAVORITE_POSITION_NOT_FIND    = "msg_map_favorite_position_not_find";
	//材料不足
	public static final String MSG_MATERIAL_INSUFFICIENT    = "msg_material_insufficient";
	//材料类型不同,请重新选择
	public static final String MSG_MATERIAL_TYPE_NOT_SAME    = "msg_material_not_same";
	//替换需要炼化属性不能为空
	public static final String MSG_REFINE_BUFF_NULL			= "msg_refine_buff_null";
	//装备穿上/卸下失败
	public static final String MSG_EQUIP_WIELD_OR_UNWIELD_FAIL ="msg_wield_or_unwield_fail";
	//装备已经满级,不可升级
	public static final String MSG_EQUIP_UNUPGRADE			="msg_equip_unupgrade";
	//找不到撤退目标
	public static final String MSG_MAP_RETREAT_NOT_FIND    = "msg_map_retreat_not_find";
	//这里无法迁城
	public static final String MSG_MAP_BASE_MUST_AT_NONE   = "msg_map_base_must_at_none";
	//你正在迁城中,无法迁城
	public static final String MSG_MAP_BASE_ONLY_ONE       = "msg_map_base_only_one";
	//材料已经最高级,不可升级
	public static final String MSG_MATERIAL_UNUPGRADE      = "msg_material_unupgrade";
	//联盟<{0}>名称已被使用
	public static final String MSG_UNION_CREATE_NAME_HAVE_USED  = "msg_union_create_name_have_used";
	//联盟<{0}>简称已被使用
	public static final String MSG_UNION_CREATE_SHORT_NAME_HAVE_USED  = "msg_union_create_short_name_have_used";
	//您目前尚且是别的联盟成员!
	public static final String MSG_UNION_CREATE_ROLE_HAVE_IN    = "msg_union_create_role_have_in";
	//联盟{0}创建成功
	public static final String MSG_UNION_CREATE_SUCCESS    = "msg_union_create_success";
	//指挥官的等级不足,无法创建联盟！
	public static final String MSG_UNION_CREATE_ROLE_LEVEL_NO   = "msg_union_create_role_level_no";
	//联盟内还有其他成员，无法解散联盟
	public static final String MSG_UNION_DISSOLVE_FAIL   = "msg_union_dissolve_fail";
	//城池{0}状态不正确，无法进行当前操作
	public static final String MSG_CITY_STATE_NOT_RIGHT   = "msg_city_state_not_right";
	//系统未找到指定的联盟
	public static final String MSG_UNION_NOT_FIND         = "msg_union_not_find";
	// 未加入联盟
	public static final String MSG_NO_JOIN_UNION = "msg_no_join_union";
	//联盟各称谓不可相同
	public static final String MSG_UNION_TITLE_NOT_SAME       = "msg_union_title_not_same";
	//联盟仓库未建造
	public static final String MSG_UNION_STORAGE_NOT_FIND         = "msg_union_storage_not_find";
	//物品不可出售
	public static final String MSG_ITEM_NOT_SELL		="msg_item_not_sell";
	//此旗帜不存在
	public static final String MSG_UNION_FLAG_NOT_FIND         = "msg_union_flag_not_find";
	//恭喜您已经成为联盟<{0}>的下士
	public static final String MSG_UNION_NEW_MEMBER_IN     = "msg_union_new_member_in";
	//您的权限不足
	public static final String MSG_UNION_NO_OPERAT_PERMISSION     = "msg_union_no_operat_permission";
	//该用户不在邀请列表内
	public static final String MSG_UNION_NO_ON_APPLY    = "msg_union_no_on_apply";		
	//联盟已最高等级
	public static final String MSG_UNION_MAX_LEVEL     = "msg_union_max_level";
	//联盟积分不足
	public static final String MSG_UNION_NO_SCORE           = "msg_union_no_score";
	//找不到目标联盟成员
	public static final String MSG_UNION_MEMBER_NOT_FIND     = "msg_union_member_not_find";
	//您已被{0}>踢出联盟<{1}>了
	public static final String MSG_UNION_KICK_MEMBER_OUT    = "msg_union_kick_member_out";
	//军衔已任命满
	public static final String MSG_UNION_NO_OFFICER_NUM    = "msg_union_no_officer_num";
	//联盟已满员
	public static final String MSG_UNION_MEMBER_FULL    = "msg_union_member_full";
	//已操作过联盟邀请
	public static final String MSG_UNION_INVITE_HAVE_OPERATED    = "msg_union_invite_have_operated";
	//你已申请过加入该联盟
	public static final String MSG_UNION_ROLE_HAS_APPLIED    = "msg_union_role_has_applied";
	//你不满足当前联盟的招募条件
	public static final String MSG_UNION_NOT_MEET_REQUIREMENT    = "msg_union_not_meet_requirement";
	//你已申请成功,等待同意
	public static final String MSG_UNION_APPLY_SUC   = "msg_union_apply_suc";
	//帮助失败,请重试
	public static final String MSG_UNION_HELP_FAIL   = "msg_union_help_fail";
	//帮助已被拒绝(自己不能帮助自己)
	public static final String MSG_UNION_HELP_FAIL_NO_HELP_SELF   = "msg_union_help_fail_no_help_self";
	//任务{0}不存在
	public static final String MSG_MISSION_NOT_EXSIT		="msg_mission_not_exsit";
	//任务{0}奖励不存在
	public static final String MSG_MISSION_REWARD_NOT_EXSIT		="msg_mission_reward_not_exsit";
	//任务{0}未完成
	public static final String MSG_MISSION_NOT_OVER		= "msg_mission_not_over";
	//.......................................................
	//讨论组已经超出用户上限
	public static final String CHAT_GROUP_BEYOND_ROLE_NUM_LIMIT = "chat_group_beyond_role_num_limit";
	//讨论组不包含用户
	public static final String CHAT_GROUP_EXCLUDE_ROLE = "chat_group_exclude_role";
	//讨论组名称不合法(敏感字符)
	public static final String CHAT_GROUP_NAME_ILLEGALITY_SENSITIVE	= "chat_group_name_illegality_sensitive";
	//讨论组名称不合法(字符过长)
	public static final String CHAT_GROUP_NAME_ILLEGALITY_LENGTH = "chat_group_name_illegality_length";
	//创建讨论组
	public static final String CHAT_GROUP_CREATE	="chat_group_create";
	//聊天组创建失败人数不足
	public static final String CHAT_GROUP_CREATE_FAIL_LAST_NEED_ONE = "chat_group_create_fail_last_need_one";
	//讨论组创建失败人数超出上限
	public static final String CHAT_GROUP_CREATE_FAIL_MAX_NUM = "chat_group_create_fail_max_num";
	//添加聊天组失败
	public static final String CHAT_GROUP_CREATE_FAIL_OPERATE = "chat_group_create_fail_operate";
	//聊天组不存在
	public static final String CHAT_GROUP_NOT_EXIST = "chat_group_not_exist";
	//不允许群名称
	public static final String CHAT_GROUP_NO_CHANGE_NAME = "chat_no_change_name";
	//添加成员失败，至少需要选择一个
	public static final String CHAT_GROUP_ADD_ROLE_FAIL_NEED_ONE="chat_group_add_role_fail_need_one";
	//私聊创建失败，聊天对象不存在
	public static final String CHAT_PRIVATE_CREATE_FAIL_PLAYER_NOT_EXIT="chat_private_create_fail_player_not_exit";
	//创建玩家失败，名字长度不符合标准
	public static final String CREATE_ROLE_FAIL_NAME_LENGTH_NOT_TRUE = "create_role_fail_name_length_not_true";
	//创建玩家失败，名字不可用
	public static final String CREATE_ROLE_FAIL_NAME_NOT_VALID = "create_role_fail_name_not_valid";
	//禁止世界聊天
	public static final String CHAT_WORLD_FAIL_IS_FORBID = "chat_world_fail_is_forbid";
	//亲！慢点发
	public static final String CHAT_WORLD_TOO_FAST="chat_world_too_fast";
	//世界聊天等级不足
	public static final String CHAT_WORLD_MIN_LEVEL="chat_world_min_level";
	public static final String CHAT_GUILD_NEED_JION="chat_guild_need_jion";
	//聊天消息发送失败
	public static final String CHAT_CHANNEL_CAN_NOT_SEND="chat_channel_can_not_send";
	public static final String CHAT_PRIVATE_FAIL_IN_MY_BLCAKS="chat_private_fail_in_my_blcaks";
	public static final String CHAT_PRIVATE_FAIL_IN_OTHER_BLCAKS="chat_private_fail_in_other_blcaks";
	public static final String CHAT_PRIVATE_FAIL_NEED_PLAYER_ID="chat_private_fail_need_player_id";
	public static final String PLAYER_NO_ONLINE = "player_no_online";
			
	//对方已加入其它联盟
	public static final String MSG_UNION_MEMBER_HAVE_IN_OTHER   = "msg_union_member_have_in_other";
	//已邀请对方加入联盟,等待对方同意
	public static final String MSG_UNION_HAS_INVITED_MEMBER  = "msg_union_has_invite_member";
	//建筑{0}
	public static final String DES_ASSISTANCE_BUILD_CREATE    = "des_assistance_type_create";
	//升级{0}级{1}
	public static final String DES_ASSISTANCE_BUILD_LEVEL_UP    = "des_assistance_build_level_up";
	//治疗伤兵
	public static final String DES_ASSISTANCE_BUILD_CURE    = "des_assistance_build_cure";
	//维修机械
	public static final String DES_ASSISTANCE_BUILD_REPAIR    = "des_assistance_build_repair";
	//研究{0}{1}级
	public static final String DES_ASSISTANCE_BUILD_RESEARCH    = "des_assistance_build_research";
	//你未加入联盟，无法寻求帮助
	public static final String MSG_MEMBER_NO_ASSISTANCE_PERMISSIONS   = "msg_member_no_assistance_permissions";
	//{0}已拒绝你的邀请
	public static final String MSG_OTHER_ROLE_REFUSE_INVITE   = "msg_other_role_has_refused_invite";
	//你已加入联盟
	public static final String MSG_JOINED_IN_UNION   = "msg_joined_in_union";
	//用户{0}未加入联盟
	public static final String MSG_ROLE_NO_UNION = "msg_role_no_union";
	//联盟科技未达到升级条件
	public static final String MSG_UNION_TECH_NO_UPGRADE = "msg_union_tech_no_upgrade";
	//有联盟科技正在升级中
	public static final String MSG_UNION_TECH_UPGRADING = "msg_union_tech_upgrading";
	//科技{0}已经是最高等级
	public static final String MSG_UNION_TECH_MAX_LELEL = "msg_union_tech_max_level";
	//用户{0}没有科技升级的权限
	public static final String  MSG_UNION_TECH_NO_PERMISSION = "msg_union_tech_no_permission";
	//联盟科技等级已满
	public static final String  MSG_UNION_TECH_EXP_FULL = "msg_union_tech_exp_full";
	//联盟积分不足
	public static final String MSG_UNION_SCORE_SHORTAGE = "msg_union_score_shortage";
	//个人积分不足
	public static final String MSG_ROLE_SCORE_SHORTAGE = "msg_role_score_shortage";
	//联盟等级不够
	public static final String MSG_UNION_LEVEL_SHORTAGE = "msg_union_level_shortage";
	//联盟商店的{0}物品不足
	public static final String MSG_UNION_STORE_ITEM_SHORTAGE = "msg_union_store_item_shortage";
	//联盟称谓{0}不合法(包含敏感字符)
	public static final String MSG_UNION_TITLE_ILLEGALITY_SENSITIVE = "msg_union_title_illegality_sensitive";
	//联盟称谓{0}不合法(长度过长)
	public static final String MSG_UNION_TITLE_ILLEGALITY_LENGTH = "msg_union_title_illegality_length";
	//联盟名字非法长度
	public static final String MSG_UNION_NAME_ILLEGALITY_LENGTH = "msg_union_name_illegality_length";
	//联盟缩写非法长度
	public static final String MSG_UNION_SHORTNAME_ILLEGALITY_LENGTH = "msg_union_shortname_illegality_length";
	//联盟名字/简称不合法(含有敏感字符)
	public static final String MSG_UNION_NAME_OR_SHORTNAME_ILLEGALITY_SENSITIVE = "msg_union_name_or_shortname_illegality_sensitive";
	//联盟宣言含有敏感字符
	public static final String  MSG_UNION_NOTICE_ILLEGALITY_SENSITIVE = "msg_union_notice_illegality_sensitive";
	//此城市不是您的联盟攻打下来的，无法占领
	public static final String  MSG_UNION_OCCUPY_CITY_NO_YOUR = "msg_union_occupy_city_no_your";
	//此效果已存在无法重复使用
	public static final String MSG_ITEM_USE_REPITATION  = "msg_item_use_repitation";
	//城墙完好，当前不需要修理
	public static final String MSG_CITY_WALL_FULL  = "msg_city_wall_full";
	//城墙正在修理中，请稍后再试
	public static final String MSG_CITY_WALL_REPAIRING = "msg_city_wall_repariring";
	//目标位置不能驻防
	public static final String  MSG_MAP_CANT_NOT_GARRISON = "msg_map_cant_not_garrison";
	//你正在集结，无法再发起新的集结
	public static final String  MSG_MAP_CITY_MASS_ING = "msg_map_city_mass_ing";
	//你已参加了此处的集结无法
	public static final String  MSG_MAP_CITY_MASS_JOIN = "msg_map_city_mass_join";
	//没有战争大厅，无法发起集结
	public static final String  MSG_MAP_CITY_MASS_NOT_BUILD = "msg_map_city_mass_not_build";
	//集结已满，没有空的队列
	public static final String  MSG_MAP_CITY_MASS_NO_GRID = "msg_map_city_mass_no_grid";
	//资源交换失败,数量不匹配
	public static final String  MSG_ROLE_CHG_RES_ERROR = "msg_role_can_not_change_resource";
	//没有空闲的格子了，请先解锁
	public static final String MSG_BUILD_GEM_NO_GRAD	= "msg_build_gem_no_grad";
	//部队数量过多:{0}/{1},您派出的数量是{2}。
	public static final String  MSG_MAP_CITY_MASS_NO_SOLIDER_NUM = "msg_map_city_mass_no_solider_num";
	//粮食数量不足，无法维持部队消耗
	public static final String  MSG_FOOD_SHORTAGE_FORCE_ARMY ="msg_food_short_unable_force_consumption";
	//此城市不是你的联盟占领的
	public static final String  MSG_UNION_CITY_NOT_OWNER = "msg_union_city_not_owner";
	//此城市不是联盟领地
	public static final String  MSG_UNION_CITY_NOT_OCCUPY = "msg_union_city_not_occupy";
	//需求联盟城市{0}级
	public static final String  MSG_NEED_UNION_CITY = "msg_need_union_city";
	//当前联盟城市无法建造{0}
	public static final String  MSG_UNION_CITY_NOT_BUILD = "msg_union_city_not_build";
	//当前联盟城市只能建造{0}个{1}
	public static final String  MSG_UNION_CITY_ONLY_BUILD = "msg_union_city_only_build";
	//选择的位置无法建造联盟建筑{0}
	public static final String  MSG_UNION_BUILD_IN_ERROR_POS = "msg_union_build_in_error_pos";
	//联盟建筑{0}正在建造
	public static final String  MSG_UNION_BUILD_CREATING = "msg_union_build_creating";
	//联盟建筑{0}正在升级
	public static final String  MSG_UNION_BUILD_LEVELING = "msg_union_build_leveling";
	//联盟建筑{0}正在拆除
	public static final String  MSG_UNION_BUILD_REMOVING = "msg_union_build_removing";
	//建造位置超出城市范围
	public static final String  MSG_UNION_OUT_RANGE_POS = "msg_union_out_range_pos";
	//联盟资源只能建造一个
	public static final String  MSG_UNION_RESOURCE_ONLY_ONE = "msg_union_resource_only_one";
	//驻扎部队已重伤,无法出征
	public static final String  MSG_STATION_TROOPS_DIE_ALL = "msg_station_troops_die_all";
	//请先升级技能{0}
	public static final String MSG_SKILL_NO_PRETECH = "msg_skill_no_pretech";
	//技能{0}解锁条件不足
	public static final String MSG_SKILL_LIMITED = "msg_skill_limited";
	//玩家技能点剩余数量不足
	public static final String MSG_SKILLPOINTS_NOT_ENOUGH = "msg_skillpoints_not_enough";
	//技能等级已满，不能再升级
	public static final String MSG_SKILL_LEVELUP_MAX = "msg_skill_max_level";
	//有技能生效中，无法重置，请稍后再试
	public static final String MSG_SKILL_ACTIVE_RESET = "msg_skill_active_reset";
	//您的部队被{0}的{1}级{2}攻击了
	public static final String MSG_UNION_TOWNER_ATTACK = "msg_union_towner_attack";
	//搜索用户名不能为空
	public static final String MSG_GROUP_SEARCH_ROLE_NAME_ISNT_NULL = "msg_group_search_role_name_isnt_null";
	//兵种技能Id错误
	public static final String MSG_ROLE_ARMY_SKILL_ERROR = "msg_role_army_skill_error";
	//兵种技能升级条件不足，请先升级{0}
	public static final String MSG_ROLE_ARMY_SKILL_NO_PRECEDING = "msg_role_army_skill_no_preceding";
	//剩余技能点数不足
	public static final String MSG_ROLE_ARMY_SKILL_NO_POINTS = "msg_role_army_skill_no_points";
	//行军已经结束无法加速
	public static final String MSG_ROLE_EXPEDITE_OVERED = "msg_role_expedite_overed";
	//物品{0}使用条件不足
	public static final String MSG_ROLE_ITEM_USE_LMT = "msg_role_item_use_limitation";
	//不是集结的军团长,操作失败
	public static final String MSG_ROLE_EXPEDITE_NOT_LEADER = "msg_role_expedite_not_leader";
	//喇叭不足,请前往商城购买
	public static final String MSG_ROLE_HORN_INSUFFICIENT = "msg_role_horn_insufficient";
	//解锁该地块需要等级{0}
	public static final String MSG_UNLOCK_LAND_NEED_ROLE_LEVEL = "msg_unlock_land_need_role_level";
	//该地块已解锁
	public static final String MSG_ROLE_UNLOCKED = "msg_role_unlocked";
	//今天<<7天连续签>>已签到
	public static final String MSG_ROLE_HAS_SIGNED_IN_SEVEN = "msg_role_has_sign_in_seven";
	//今<<30天签>>已签到
	public static final String MSG_ROLE_HAS_SIGNED_IN_THIRTY = "msg_role_has_sign_in_thirty";
	//铜币不足,需要{0}
	public static final String MSG_ROLE_COPPER_INSUFFICIENT = "msg_role_copper_insufficient";
	//未搜索到符合条件的联盟
	public static final String MSG_HAVE_NO_UNION = "msg_have_no_union";
	//九宫格错误位置
	public static final String MSG_SUDOKU_ERROR_POS = "msg_sudoku_error_pos";
	//该卡牌已经翻起过
	public static final String MSG_SUDOKU_POS_IS_OPENED = "msg_sudoku_pos_is_opened";
	//玩家不存在
	public static final String MSG_ROLE_INEXISTENCE = "msg_role_inexistence";
	//宝石币不足,需要{0}
	public static final String MSG_ROLE_GEM_INSUFFICIENT = "msg_role_gem_insufficient";
	//剩余数量不足,服务器限购{0}个
	public static final String MSG_SHOP_SERVICE_LIMITE = "msg_shop_service_limite";
	//剩余数量不足,个人限购{0}个
	public static final String MSG_SHOP_PERSON_LIMITE = "msg_shop_person_limite";
	//已过期,无法购买
	public static final String MSG_SHOP_OUT_DATE = "msg_shop_out_date";
	//需求VIP{0}级
	public static final String MSG_VIP_LEVEL_NOT = "msg_vip_level_not";
	//指挥官等级不足,无法穿戴
	public static final String MSG_ROLE_LEVEL_INSUFFICIENT = "msg_role_level_insufficient";
	//已加入屏蔽列表
	public static final String MSG_ADD_ROLE_BLACKLIST_SUC = "msg_add_role_blacklist_suc";
	//已移除屏蔽列表
	public static final String MSG_REMOVE_ROLE_BLACKLIST_SUC = "msg_remove_role_blacklist_suc";
	//已在屏蔽列表
	public static final String MSG_HAS_BEEN_IN_BLACKLIST = "msg_has_been_in_blacklist";
	//需求粮食{0}
	public static final String MSG_MUST_NEED_FOOD = "msg_must_need_food";
	//需求金属{0}
	public static final String MSG_MUST_NEED_METAL = "msg_must_need_metal";
	//需求石油{0}
	public static final String MSG_MUST_NEED_OIL = "msg_must_need_oil";
	//需求钛合金{0}
	public static final String MSG_MUST_NEED_ALLOY = "msg_must_need_alloy";
	//不在视野，无法出征
	public static final String MSG_EXPEDITE_NOT_IN_VIEWS = "msg_expedite_not_in_views";
	//部队正在战斗，无法召回
	public static final String MSG_EXPEDITE_FIGHTING = "msg_expedite_fighting";
	//未搜索到符合条件的用户
	public static final String MSG_NO_SEARCH_MEMBER = "msg_no_search_member";
	//需求高级行军召回令
	public static final String MSG_EXPEDITE_CALL_BACK_NEED_HIGH = "msg_expedite_call_back_need_high";
	//您不是军团长,无法召回
	public static final String MSG_EXPEDITE_CALL_BACK_NEED_LEADER = "msg_expedite_call_back_need_leader";
	//返回部队,无法召回
	public static final String MSG_EXPEDITE_IS_BACK = "msg_expedite_is_back";
	//已超出当前体力购买次数
	public static final String MSG_HAVE_NO_BUY_STAMINA_TIMES = "msg_have_no_stamina_times";
	//体力购买成功
	public static final String MSG_BUY_STAMINA_SUC = "msg_stamina_times_suc";	
	//全体邮件成功发送
	public static final String MSG_SEND_ALL_MEMBERS_MSG_SUC = "msg_send_all_members_suc";
	//未查询到符合条件的用户
	public static final String MSG_DONT_SEARCH_ROLES = "msg_dont_search_roles";
	//军营
	public static final String MSG_BIG_MAP_BARRACK = "msg_big_map_barrack";
	//要塞
	public static final String MSG_BIG_MAP_FORTRESS = "msg_big_map_fortress";
	//物品不足,不能兑换
	public static final String MSG_UNION_SYS_STORE_INSUFFICIENT = "msg_union_sys_store_insufficient";
	//您的版本过低
	public static final String MSG_VERSION_ERROR = "msg_version_error";
	//你已在线超过{0}小时，请注意游戏时间。
	public static final String MSG_ANTI_RUN_TIP = "msg_anti_run_tip";
	//你已在线超过5小时，请至少休息5小时或者次日再登录。
	public static final String MSG_ANTI_LOGIN_TIP = "msg_anti_login_tip";
	//目标正处于保护时间,无法攻击
	public static final String MSG_MAP_NO_FIGHT_IN_SAFE_TIME = "msg_map_no_fight_in_safe_time";
	//目标是您的盟友，无法攻击
	public static final String MSG_MAP_NO_FIGHT_MEMBER = "msg_map_no_fight_member";
	//处于保护罩状态,无法攻击
	public static final String MSG_MAP_NO_FIGHT_ISNOWAR = "msg_map_no_fight_isnowar";
	//选择的目标不是副本
	public static final String MSG_MAP_TARGET_ISNT_ECTYPE = "msg_map_target_isnt_ectype";
	//您已经在这个副本上了
	public static final String MSG_MAP_YOU_WERE_IN_ECTYPE = "msg_map_you_were_in_ectype";
	//您正在去此副本或者从此副本回来的路上
	public static final String MSG_MAP_MOVING_ECTYPE = "msg_map_moving_ectype";
	//关卡已全部通关
	public static final String MSG_ALL_SCENES_HAVE_FINISH = "msg_all_scenes_have_finish";
	//已超出免费重置的次数
	public static final String MSG_BEYOND_FREE_RESET_MAX = "msg_beyond_free_reset_max";
	//已超出道具重置的次数
	public static final String MSG_BEYOND_ITEM_RESET_MAX = "msg_beyond_item_reset_max";
	//当前军队已经全部阵亡
	public static final String MSG_ARMYS_ALL_DIE_OUT = "msg_armys_all_die_out";
	//关卡未通关
	public static final String MSG_RELIC_NO_FINISH = "msg_relic_no_finish";
	//已领取过奖励
	public static final String MSG_RELIC_REWARD_HAS_GOT = "msg_relic_reward_has_got";
	//联盟NPCCity不满足
	public static final String MSG_NOT_UNION_NPC_CITY = "msg_not_union_npc_city";
	//目标不存在
	public static final String  MSG_FIGHT_NO_TARGET = "msg_fight_no_target";
	// 内容不合法
	public static final String MSG_DATA_NAME_ILLEGAL = "msg_name_illegal";
	// 长度不合法
	public static final String MSG_DATA_LENGTH_ILLEGAL = "msg_data_length_illegal";
	//当前体力已满无法继续购买
	public static final String MSG_ROLE_STAMINA_IS_MAX = "msg_role_stamina_is_max";
	//当前体力已满无法继续使用
	public static final String MSG_ROLE_STAMINA_NOUSE_MAX = "msg_role_stamina_nouse_max";
	//已全部领取
	public static final String MSG_ONLINE_GOT_ALL = "msg_online_got_all";
	// 兑换码使用失败
	public static final String MSG_USE_CODE_FAIL = "msg_use_code_fail";
	// 兑换码不存在
	public static final String MSG_USE_CODE_NOEXIST = "msg_use_code_noexist";
	// 兑换码已过期
	public static final String MSG_USE_CODE_OUTDATA = "msg_use_code_outdata";
	// 兑换码已使用
	public static final String MSG_USE_CODE_USED = "msg_use_code_used";
	// 不支持该渠道
	public static final String MSG_USE_CODE_SUPPORT = "msg_use_code_support";
	// 已使用过该类型激活码
	public static final String MSG_USE_CODE_NOTYPE = "msg_use_code_notype";
	// 兑换码使用成功
	public static final String MSG_USE_CODE_SUCCEED = "msg_use_code_succeed";
	//士兵不足
	public static final String MSG_ROLE_ARMY_INSUFFICIENT = "msg_role_army_insufficient";
	//建筑升级所需的物品不足
	public static final String MSG_BUILD_UP_ITEM_INSUFFICIENT = "msg_build_up_item_insufficient";
	//需要消耗粮食
	public static final String MSG_MAINTAIN_UNIT_CONSUM ="maintain_unit_consumption";
	//邮件附件领取成功
	public static final String MSG_GET_MAIL_ITEMS_SUCC ="msg_get_mail_items_succ";
	//{0}正在正在攻击城市{1}
	public static final String MSG_UNION_ATTCK_CITY_ING = "msg_union_attck_city_ing";
	//恭喜联盟{0}攻陷城市{1}。\n杀敌排行:\n第一名、{2}{3}\n第二名、{4}{5}\n第三名、{6}{7}
	public static final String MSG_UNION_ATTCK_CITY_SUCC = "msg_union_attck_city_succ";
	//{0}正在正在攻击{1}
	public static final String MSG_UNION_ATTCK_UNION_ING = "msg_union_attck_union_ing";
	// 购买体力超出最大值,请合理使用
	public static final String MSG_BUG_STAMINA_NUM_BEYOND_MAX = "msg_bug_stamina_num_beyond_max";
	//您的围墙空间不足
	public static final String MSG_TRAIN_HOOK_NOT_HAVE_SPACE = "msg_train_hook_not_have_space";
	// 恭喜编号为{0}的玩家在大转盘中获得{1}个{2}
	public static final String TURNTABLE_GOT_REWARD = "turntable_got_reward";
	//已废弃，无法采集
	public static final String  MSG_UNION_BUILD_COULD_NOT_USE = "msg_union_build_could_not_use";
	//没有联盟，没法攻击城市
	public static final String  MSG_ATTACK_CITY_NO_UNION = "msg_attack_city_no_union";
	
	public static String search(String key,Object... params){
		Stringcontent vaule = DataManager.getInstance().serach(Stringcontent.class,key);
		if (vaule == null){
			return key;
		}
		String result = vaule.getContent();
		if (params != null) {
			for (int i = 0 ; i < params.length ; i++) {
				Object parma = params[i];
				String regix = "{" + i + "}";
				result = result.replace(regix,parma.toString());
			}
		}
		return result;
	}
}
