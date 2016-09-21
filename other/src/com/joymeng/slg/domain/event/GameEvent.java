package com.joymeng.slg.domain.event;

import com.joymeng.Instances;
import com.joymeng.slg.domain.object.IObject;

public interface GameEvent extends Instances {
	public static final short ADD_LIST = 0;// 添加到列表
	public static final short REMOVE_LIST = 1;// 从列表移除
	public static final short SAVE_MYSELF = 2;// 保存自己
	public static final short ROLE_CREATE = 3;// 创建角色
	public static final short LOAD_FROM_DB = 4;// 需要加载
	public static final short CENTER_LEVE_UP = 5;// 主城升级
	public static final short ROLE_HEART = 6;// 角色心跳
	public static final short ARMY_CHANGE = 7;// 部队数量变化
	public static final short TROOPS_SEND = 8;// 下发部队信息
	public static final short UNION_LOAD = 9;// 联盟加载
	public static final short UNION_JOIN = 10;// 加入联盟
	public static final short UNION_EXIT = 11;// 退出联盟
	public static final short ROLE_BUILD_TIME_ROVER = 12;// 玩家的帮组类型的倒计时结束
	public static final short ROlE_CHANGE_BASE_INFO = 13;// 玩家修改基础信息(包括:姓名\等级)
	// 任务检测事件
	public static final short TASK_CHECK_EVENT = 16;//
	// 任务状态变更事件
	public static final short TASK_CHECK_STATE_EVENT = 17;// 任务进度等状态
	public static final short EFFECT_UPDATE = 15;// 更新buff
	// 玩家统计信息更新
	public static final short FIGHT_WIN_EVENT = 20;
	public static final short FIGHT_FAIL_EVENT = 21;
	public static final short ATTACK_WIN_EVENT = 22;
	public static final short DEFENCE_WIN_EVENT = 23;
	public static final short DEFENCE_FAIL_EVENT = 24;
	public static final short SPY_EVENT = 25;
	// 兵营建筑升级，解锁士兵
	public static final short ARMY_FACT_CREATE = 26;
	public static final short ARMY_FACT_LEVEL_UP = 27;// 获取技能点
	public static final short UNION_WAR_RECORD = 28;// 联盟战争记录
	public static final short UNION_FIGHT_CHANGE = 165;// 联盟战斗有变化
	public static final short RANK_ROLE_FIGHT_CHANGE = 166;// 排行榜个人战斗力发生变化
	public static final short RANK_ROLEKILLENEMY_CHANGE = 167;// 排行榜个人击杀部队数发生变化
	public static final short RANK_ROLECITYLEVEL_CHANGE = 168;// 排行榜个人城市等级发生变化
	public static final short RANK_ROLEHEROLEVEL_CHANGE = 169;// 排行榜个人英雄等级发生变化
	
	public static final short ROLE_RES_BUFF_CHANGE = 170;//资源采集buff发生变化
	public static final short ROLE_RES_BUFF_CHANGE_1 = 173;//资源采集buff发生变化 不先采集一部分
	public static final short ACTIVITY_EVENTS = 171; // 活动相关的事件
	
	public static final short REMOVE_ROLE = 172; // 删除玩家的事件
	
	/**
	 * 时间片段处理
	 */
	public void tick();

	/**
	 * 处理具体的某个事件
	 * 
	 * @param trigger
	 * @param params
	 */
	public void handle(IObject trigger, Object[] params);

}
