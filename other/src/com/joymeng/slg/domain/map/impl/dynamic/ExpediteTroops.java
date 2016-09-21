package com.joymeng.slg.domain.map.impl.dynamic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joymeng.Const;
import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.JsonUtil;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.common.util.StringUtils;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.list.EventName;
import com.joymeng.log.GameLog;
import com.joymeng.log.LogManager;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.dao.DaoData;
import com.joymeng.slg.dao.SqlData;
import com.joymeng.slg.domain.chat.ReportType;
import com.joymeng.slg.domain.event.GameEvent;
import com.joymeng.slg.domain.map.MapObject;
import com.joymeng.slg.domain.map.MapUtil;
import com.joymeng.slg.domain.map.fight.result.FightReport;
import com.joymeng.slg.domain.map.impl.still.MapRefreshObj;
import com.joymeng.slg.domain.map.impl.still.proxy.MapProxy;
import com.joymeng.slg.domain.map.impl.still.res.MapEctype;
import com.joymeng.slg.domain.map.impl.still.role.MapBarracks;
import com.joymeng.slg.domain.map.impl.still.role.MapCity;
import com.joymeng.slg.domain.map.impl.still.role.MapFortress;
import com.joymeng.slg.domain.map.impl.still.role.MapGarrison;
import com.joymeng.slg.domain.map.impl.still.union.impl.MapUnionDefenderTower;
import com.joymeng.slg.domain.map.impl.still.union.impl.MapUnionResource;
import com.joymeng.slg.domain.map.physics.MapCell;
import com.joymeng.slg.domain.map.physics.MapCellType;
import com.joymeng.slg.domain.object.AbstractObject;
import com.joymeng.slg.domain.object.bag.ItemCell;
import com.joymeng.slg.domain.object.resource.ResourcePredatoryRatio;
import com.joymeng.slg.domain.object.resource.ResourceTypeConst;
import com.joymeng.slg.domain.object.resource.data.Resourcestype;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.task.ConditionType;
import com.joymeng.slg.domain.timer.TimerLast;
import com.joymeng.slg.domain.timer.TimerLastType;
import com.joymeng.slg.domain.timer.TimerOver;
import com.joymeng.slg.net.SerializeEntity;
import com.joymeng.slg.net.mod.AbstractClientModule;
import com.joymeng.slg.net.mod.RespModuleSet;
import com.joymeng.slg.union.UnionBody;
import com.joymeng.slg.union.impl.UnionMember;

/**
 * 玩家的出征部队,依附于建筑
 * 
 * @author tanyong
 *
 */
public class ExpediteTroops extends AbstractObject implements TimerOver, SerializeEntity {
	static final byte TROOPS_START_FIGHT = 0;
	static final byte TROOPS_END_FIGHT = 1;
	static final byte TROOPS_CHANGE = 2;
	static final byte TROOPS_DEL = 3;
	static final byte TROOPS_CALL_BACK = 4;

	private static final int FIGHT_DELAY_TIME = 5;// 5秒
	long id;// 数据库主键
	int startPosition;// 出发位置
	int targetPosition;// 目标位置
	float speed;// 移动速度
	TimerLast timer;// 行军倒计时
	List<TroopsData> teams = new ArrayList<TroopsData>();// 军团
	long clientDelayTime = -1;// 客户端播放战斗延迟时间
	List<Long> couldLooks = new ArrayList<Long>();
	int goBackQuick = 0;
	boolean mass = false;
	List<SpeedNode> speedNodes = new ArrayList<SpeedNode>();// 加速节点
	boolean callBack = false;// 召回标志
	boolean fighting = false;// 正在战斗
	boolean needDel = false;
	boolean haveGoback = false;// 防止一个部队返回两次
	String noBattleTip = I18nGreeting.MSG_FIGHT_NO_TARGET;// 未开战的原因
	public boolean isWin;// 进战场临时用，不用存档
	List<FightReport> reports = new ArrayList<FightReport>();// 和这只部队有关的战报

	public void setId(long id) {
		this.id = id;
	}

	public boolean isFighting() {
		return fighting;
	}

	public void backQuick(int time) {
		goBackQuick = time;
	}

	public void setCallBack(boolean callBack) {
		this.callBack = callBack;
	}

	public void setTeams(List<TroopsData> teams) {
		this.teams = teams;
	}

	public int getStartPosition() {
		return startPosition;
	}

	public void setStartPosition(int startPosition) {
		this.startPosition = startPosition;
	}

	public int getTargetPosition() {
		return targetPosition;
	}

	public void setTargetPosition(int targetPosition) {
		this.targetPosition = targetPosition;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public TimerLast getTimer() {
		return timer;
	}

	public void setTimer(TimerLast timer) {
		this.timer = timer;
	}

	public List<TroopsData> getTeams() {
		return teams;
	}

	public List<Long> getCouldLooks() {
		return couldLooks;
	}

	public void setCouldLooks(List<Long> couldLooks) {
		this.couldLooks = couldLooks;
	}

	public void setNoBattleTip(String noBattleTip) {
		this.noBattleTip = noBattleTip;
	}

	public List<FightReport> getReports() {
		return reports;
	}

	public void registTimer(TimerLast timer) {
		if (timer == null) {
			return;
		}
		this.timer = timer;
		timer.registTimeOver(this);
		taskPool.mapTread.addObj(this, timer);
	}

	public float getSpeed(long time) {
		if (speedNodes.size() > 0) {
			long stand = timer.getStart() + time;
			for (int i = 0; i < speedNodes.size(); i++) {
				SpeedNode node = speedNodes.get(i);
				long ct = node.getTime();
				if (stand < ct) {
					return node.getSpeed();
				}
				SpeedNode next = i < speedNodes.size() - 1 ? speedNodes.get(i + 1) : null;
				if (next != null) {
					long nt = next.getTime();
					if (stand >= ct && stand < nt) {
						return next.getSpeed();
					}
				}
			}
		}
		return speed;
	}

	public void reset() {
		callBack = false;
		goBackQuick = 0;
		speedNodes.clear();
		isWin = false;
		mass = false;
	}

	@Override
	public String table() {
		return TABLE_RED_ALERT_ROLEEXPEDITE;
	}

	@Override
	public void loadFromData(SqlData data) {
		id = data.getLong(DaoData.RED_ALERT_GENERAL_ID);
		startPosition = data.getInt(RED_ALERT_ROLEEXPEDITE_STARTPOSITION);
		targetPosition = data.getInt(RED_ALERT_ROLEEXPEDITE_TARGETPOSITION);
		speed = data.getFloat(RED_ALERT_ROLEEXPEDITE_SPEED);
		String str = data.getString(RED_ALERT_ROLEEXPEDITE_TEAMS);
		teams = JsonUtil.JsonToObjectList(str, TroopsData.class);
		str = data.getString(RED_ALERT_GARRISON_TIMER);
		TimerLast timer = JsonUtil.JsonToObject(str, TimerLast.class);
		registTimer(timer);
		mass = data.getByte(RED_ALERT_ROLEEXPEDITE_MASS) == 1;
		str = data.getString(RED_ALERT_ROLEEXPEDITE_NODES);
		if (!StringUtils.isNull(str)) {
			speedNodes = JsonUtil.JsonToObjectList(str, SpeedNode.class);
		}
		needDel = true;
	}

	@Override
	public void saveToData(SqlData data) {
		data.put(DaoData.RED_ALERT_GENERAL_ID, id);
		data.put(RED_ALERT_ROLEEXPEDITE_STARTPOSITION, startPosition);
		data.put(RED_ALERT_ROLEEXPEDITE_TARGETPOSITION, targetPosition);
		data.put(RED_ALERT_ROLEEXPEDITE_SPEED, speed);
		String str = JsonUtil.ObjectToJsonString(timer);
		data.put(RED_ALERT_GARRISON_TIMER, str);
		str = JsonUtil.ObjectToJsonString(teams);
		data.put(RED_ALERT_ROLEEXPEDITE_TEAMS, str);
		data.put(RED_ALERT_ROLEEXPEDITE_MASS, mass ? 1 : 0);
		str = JsonUtil.ObjectToJsonString(speedNodes);
		data.put(RED_ALERT_ROLEEXPEDITE_NODES, str);
		needDel = true;
	}

	@Override
	public void registerAll() {

	}

	@Override
	public void _tick(long now) {
		if (callBack || goBackQuick > 0) {
			goBackToCome();
			sendToClient(TROOPS_CALL_BACK);
			for (int i = 0; i < teams.size(); i++) {
				TroopsData troops = teams.get(i);
				Role role = world.getRole(troops.getInfo().getUid());
				role.handleEvent(GameEvent.TROOPS_SEND);
				if (troops.isLeader() && !mass) {
					if (timer.getType() == TimerLastType.TIME_EXPEDITE_FIGHT) {
						MapObject target = mapWorld.searchObject(targetPosition);
						if (target != null) {
							boolean emenyFlag = target instanceof MapRefreshObj || target instanceof MapEctype;
							if (!emenyFlag) {
								role.handleEvent(GameEvent.UNION_FIGHT_CHANGE, false);
							}
						}
					}
				}
			}
			return;
		}
		if (timer.over(now)) {// 时间到了
			timer.die();
		}
	}

	@Override
	public void remove() {
		super.remove();
		MapCell cell = mapWorld.getMapCell(startPosition);
		cell.removeExpedite(id);
		cell = mapWorld.getMapCell(targetPosition);
		cell.removeExpedite(id);
		if (needDel) {
			setDeleteFlag(true);
			save();
		}
		sendToClient(TROOPS_DEL);
	}

	@Override
	public void serialize(JoyBuffer out) {
		out.putLong(id);// long 部队主键编号
		out.putInt(teams.size());// 集团军的数量
		for (int i = 0; i < teams.size(); i++) {
			TroopsData data = teams.get(i);
			data.serialize(out);
		}
		out.putInt(mass ? 1 : 0);// 是不是集结行军
		out.putInt(startPosition);// int 出发位置
		out.putInt(targetPosition);// int 目标位置
		out.putPrefixedString(String.valueOf(speed), JoyBuffer.STRING_TYPE_SHORT);// string
																					// 行军速度
		out.putInt(speedNodes.size());// 加速节点
		for (int i = 0; i < speedNodes.size(); i++) {
			SpeedNode node = speedNodes.get(i);
			node.serialize(out);
		}
		timer.serialize(out);
	}

	public void sendToClient(byte type) {
		RespModuleSet rms = new RespModuleSet();
		AbstractClientModule module = new AbstractClientModule() {
			@Override
			public short getModuleType() {
				return NTC_DTCD_ROLE_EXPEDITE;
			}
		};
		module.add(type);// 接下来干什么事情，0 开始战斗，1结束战斗需要删除,2更新。
		module.add(this);
		rms.addModule(module);
		synchronized (couldLooks) {
			for (int i = 0; i < couldLooks.size(); i++) {
				long rid = couldLooks.get(i).longValue();
				Role role = world.getOnlineRole(rid);
				if (role != null) {
					MessageSendUtil.sendModule(rms, role.getUserInfo());
				}
			}
		}
	}

	public TroopsData getLeader() {
		for (int i = 0; i < teams.size(); i++) {
			TroopsData data = teams.get(i);
			if (data.isLeader()) {
				return data;
			}
		}
		return null;
	}

	public void startFight(MapObject obj, long now) {
		clientDelayTime = now;
		fighting = true;
		sendToClient(TROOPS_START_FIGHT);
		Role role = world.getRole(this.getLeader().getInfo().getUid());
		if (role != null) {
			LogManager.mapLog(role, startPosition, targetPosition, id, EventName.startFighting.getName());
		}
	}

	/**
	 * 出发未战
	 */
	public void goBackNoFight() {
		goBackToCome();
		for (int i = 0; i < teams.size(); i++) {
			TroopsData troops = teams.get(i);
			Role role = world.getRole(troops.getInfo().getUid());
			FightReport report = new FightReport();
			report.setType((byte) 1);
			report.setPosition(targetPosition);
			report.setNoBattleTip(noBattleTip);
			report.setTime(TimeUtils.nowStr());
			String battleReport = JsonUtil.ObjectToJsonString(report);
			chatMgr.creatBattleReportAndSend(battleReport, ReportType.TYPE_BATTLE_REPORT, null, role);
		}
		if (mass) {
			StringBuffer sb = new StringBuffer();
			sb.append("expediteTroops : " + id + "[" + teams.get(0).getInfo().getName());
			for (int i = 1; i < teams.size(); i++) {
				TroopsData data = teams.get(i);
				sb.append("," + data.getInfo().getName());
			}
			sb.append("] go back do nothing");
			GameLog.info(sb.toString());
		} else {
			TroopsData leader = getLeader();
			GameLog.info("expediteTroops : " + id + leader.getInfo().getName() + " go back do nothing");
		}
		sendToClient(TROOPS_DEL);
	}

	@Override
	public void finish() {
		boolean isReturn = false;
		MapCell targetCell = null;
		try {
			long now = TimeUtils.nowLong();
			if (clientDelayTime > 0) {// 延迟时间
				if (now >= clientDelayTime + FIGHT_DELAY_TIME * Const.SECOND) {
					clientDelayTime = 0;
				}
				isReturn = true;
				return;
			}
			boolean needBack = true;// 部队需要原路返回
			targetCell = mapWorld.getMapCell(targetPosition);
			if (timer.getType() == TimerLastType.TIME_ARMY_BACK) {// 回城
				if (targetCell.getTypeKey() == MapCity.class) {
					// 实际目标也是玩家主城
					MapCity mapCity = mapWorld.searchObject(targetCell);
					if (mapCity != null) {
						mapCity.troopsArrive(this);
					}
				} else {
					goBackCity();
				}
				needBack = false;
			} else if (timer.getType() == TimerLastType.TIME_ARMY_BACK_FORTRESS) {
				// 回要塞或者兵营
				if (targetCell.getTypeKey() == MapFortress.class || targetCell.getTypeKey() == MapBarracks.class) {
					MapFortress fortress = mapWorld.searchObject(targetCell);
					if (fortress != null) {
						fortress.troopsArrive(this);
					}
				} else {// 生成新的回家路线
					goBackCity();
				}
				needBack = false;
			} else if (timer.getType() == TimerLastType.TIME_EXPEDITE_CREATE_FORTRESS
					|| timer.getType() == TimerLastType.TIME_EXPEDITE_CREATE_MOVE) {
				// 建造要塞,迁城点
				if (targetCell.getTypeKey() == MapProxy.class) {
					MapProxy proxy = mapWorld.searchObject(targetCell);
					if (proxy != null) {
						proxy.troopsArrive(this);
						needBack = false;
					}
				} else if (targetCell.getTypeKey() == null) {// 如果这里是空地
					MapProxy proxy = new MapProxy(true);
					proxy.setPosition(targetPosition);
					proxy.troopsArrive(this);
					needBack = false;
				} else if (targetCell.getTypeKey() == MapGarrison.class) {
					MapGarrison garrison = mapWorld.searchObject(targetCell);
					garrison.troopsArrive(this);
					needBack = false;
				}
			} else if (timer.getType() == TimerLastType.TIME_EXPEDITE_GARRISON) {
				MapObject obj = mapWorld.searchObject(targetCell);
				TroopsData leader = getLeader();
				if (obj == null) {// 如果是空地
					MapGarrison garrison = mapWorld.create(MapGarrison.class, false);
					if (mapWorld.checkPosition(garrison, targetPosition)) {
						mapWorld.insertObj(garrison);
						mapWorld.updatePosition(garrison, targetPosition);
						obj = garrison;
					}
				}
				if (obj.couldGarrison(leader.getInfo().getUnionId())) {
					obj.troopsArrive(this);
					needBack = false;
				}
			} else if (timer.getType() == TimerLastType.TIME_EXPEDITE_FIGHT
					|| timer.getType() == TimerLastType.TIME_MONSTER_ATTACK) {
				MapObject obj = mapWorld.searchObject(targetCell);
				if (obj != null) {
					if (clientDelayTime == -1) {// 没有播放过战斗动画
						if (!obj.couldAttack(this)) {// 不能被攻击
							goBackNoFight();
							needBack = false;
						} else if (!fighting) {
							startFight(obj, now);
							isReturn = true;
							return;
						}
					}
					if (fighting) {// 已播放完动画
						if (obj.getFight() > 0 && obj.getFight() != id) {
							// 正在和别人战斗,等待别人打完后在打
							isReturn = true;
							return;
						}
						if (!obj.couldAttack(this)) {
							goBackNoFight();
							needBack = false;
						} else {
							obj.setFight(id);
							obj.troopsArrive(this);
							sendToClient(TROOPS_END_FIGHT);
							sendReport();
							needBack = false;
						}
					}
				} else {// 发送未战战报
					goBackNoFight();
					needBack = false;
				}
				
				if (timer.getType() == TimerLastType.TIME_MONSTER_ATTACK) {
					remove();
					needBack = false;
				}
			} else if (timer.getType() == TimerLastType.TIME_EXPEDITE_SPY) {
				MapObject obj = mapWorld.searchObject(targetCell);
				if (obj != null) {
					obj.troopsArrive(this);
				}
				sendToClient(TROOPS_DEL);
			} else if (timer.getType() == TimerLastType.TIME_EXPEDITE_STATION) {
				if (targetCell.getTypeKey() == MapFortress.class || targetCell.getTypeKey() == MapBarracks.class) {
					// 调拨
					MapFortress fortress = mapWorld.searchObject(targetCell);
					if (fortress != null) {
						fortress.troopsArrive(this);
						needBack = false;
					}
				}
			} else if (timer.getType() == TimerLastType.TIME_EXPEDITE_MASS) {
				// 去别人主城集结
				if (targetCell.getTypeKey() == MapCity.class) {
					MapCity city = mapWorld.searchObject(targetCell);
					if (city != null) {
						city.troopsArrive(this);
						needBack = false;
					}
				}
			} else if (timer.getType() == TimerLastType.TIME_EXPEDITE_UNION_RES_COLLECT) {
				if (targetCell.getTypeKey() == MapUnionResource.class) {
					MapUnionResource unionRes = mapWorld.searchObject(targetCell);
					if (unionRes != null) {
						unionRes.troopsArrive(this);
						needBack = false;
					}
				}
			} else if (timer.getType() == TimerLastType.TIME_BACK_SPY) {// 侦查回城
				remove();// 移除行军数据
				needBack = false;
			} else if (timer.getType() == TimerLastType.TIME_GO_TO_ECTYPE) {// 去副本的部队
				MapEctype ectype = mapWorld.searchObject(targetCell);
				ectype.troopsArrive(this);
				needBack = false;
			}
			if (needBack) {
				goBackToCome();
			}
		} catch (Exception e) {
			if (timer.getType() == TimerLastType.TIME_EXPEDITE_FIGHT) {// 去战斗
				GameLog.error("fight erorr", e);
				// 需要加错误日志
			} else {
				GameLog.error("expedite finish erorr", e);
				// 需要加错误日志
			}
			goBackToCome();// 结束异常后就让部队返回,防止丢部队
		} finally {
			if (!isReturn) {
				for (int i = 0; i < teams.size(); i++) {
					TroopsData troops = teams.get(i);
					Role role = world.getRole(troops.getInfo().getUid());
					if (role != null) {
						role.handleEvent(GameEvent.TROOPS_SEND);
						if (troops.isLeader()) {
							role.handleEvent(GameEvent.UNION_FIGHT_CHANGE, false);
						}
					}
				}
				MapObject target = mapWorld.searchObject(targetCell);
				if (target != null) {
					if (fighting && target.getFight() == id) {
						target.setFight(0);// 解锁因为战斗报错的对象
					}
					UnionBody union = unionManager.search(target.getInfo().getUnionId());
					if (union != null) {
						UnionMember lm = union.getLeader();
						if (lm != null) {
							Role role = world.getRole(lm.getUid());
							if (role != null) {
								role.handleEvent(GameEvent.UNION_FIGHT_CHANGE, false);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 加入部队数据
	 * 
	 * @param troops
	 * @param needNew
	 */
	public TroopsData addTroops(TroopsData troops) {
		for (int i = 0; i < teams.size(); i++) {
			TroopsData data = teams.get(i);
			if (data.getInfo().getUid() == troops.getInfo().getUid()) {
				return null;
			}
		}
		teams.add(troops);
		return troops;
	}

	public float computeWeight(Role role) {
		return getLeader().computeWeight(role);
	}

	/***
	 * 3秒回城
	 * 
	 * @param oldPos
	 */
	public void tryToBackQuick(int pos, int time) {
		if (targetPosition != pos && startPosition != pos) {
			// 部队的起点和终点都不是主城的，不做处理
			return;
		}
		backQuick(time);
	}

	private void turnBack() {
		// 掉头逻辑
		int temp = startPosition;
		startPosition = targetPosition;
		targetPosition = temp;
		reset();
	}

	/**
	 * //TODO xufangliang 计算掠夺的资源
	 * 
	 * @Title: goBackToCome
	 * @Description:资源塞入行军部队的包裹
	 * 
	 * @return void
	 * @param resources
	 */
	public void goBackToCome(Map<String, Integer> resources) {
		if (haveGoback) {
			return;
		}
		haveGoback = true;
		long now = TimeUtils.nowLong() / 1000;
		MapCell startCell = mapWorld.getMapCell(targetPosition);
		List<TroopsData> troopsDatas = new ArrayList<TroopsData>();
		MapCell targetCell = mapWorld.getMapCell(startPosition);
		TimerLastType type = null;
		if (timer.getType() == TimerLastType.TIME_EXPEDITE_SPY) {
			type = TimerLastType.TIME_BACK_SPY;
		} else if (targetCell.getType() == MapCellType.MAP_CELL_TYPE_FORTRESS
				|| targetCell.getType() == MapCellType.MAP_CELL_TYPE_BARRACKS) {
			type = TimerLastType.TIME_ARMY_BACK_FORTRESS;
		} else {
			type = TimerLastType.TIME_ARMY_BACK;
		}
		if (callBack) {// 召回逻辑
			TroopsData leader = getLeader();
			Role role = world.getRole(leader.getInfo().getUid());
			long go = now - timer.getStart();// 以走过的时间
			long left = timer.getLast() - go;// 剩余时间
			float ld = left * speed;
			float gd = 0;
			if (speedNodes.size() > 0) {// 如果加过速
				long preTime = timer.getStart();
				for (int j = 0; j < speedNodes.size(); j++) {
					SpeedNode node = speedNodes.get(j);
					long time = node.getTime() - preTime;
					preTime += time;
					gd += time * node.getSpeed();
				}
				long time = now - preTime;
				gd += time * speed;
			} else {
				gd = go * speed;
			}
			computMoveSpeed(leader, role);
			left = (long) (ld / speed);
			long last = left + (long) (gd / speed);
			timer.setStart(now - left);
			timer.setLast(last);
			boolean flag = false;
			if (mass) {// 集结的行军部队召回要解散集结数据
				MapCity mc = mapWorld.searchObject(startPosition);
				if (mc != null && mc.getMass() != null) {
					mc.massEnd();
				}
				flag = true;
			}
			if (timer.getType() == TimerLastType.TIME_EXPEDITE_CREATE_MOVE
					|| timer.getType() == TimerLastType.TIME_EXPEDITE_CREATE_FORTRESS) {
				MapProxy proxy = mapWorld.searchObject(targetPosition);
				if (proxy != null) {
					proxy.remove();
				}
			}
			if (timer.getType() == TimerLastType.TIME_EXPEDITE_MASS) {
				// 去参加集结的过程整
				MapCity mc = mapWorld.searchObject(targetPosition);
				if (mc != null && mc.getMass() != null) {
					mc.getMass().removeGrid(this);// 移除行军格子
				}
				flag = true;
			}
			if (flag) {
				role.handleEvent(GameEvent.UNION_FIGHT_CHANGE, false);// 联盟战斗变化
			}
			timer.setType(type);
		} else if (goBackQuick > 0) {// 快速召回
			if (timer.getType() == TimerLastType.TIME_EXPEDITE_MASS) {
				// 去参加集结的过程整
				MapCity mc = mapWorld.searchObject(targetPosition);
				if (mc != null && mc.getMass() != null) {
					mc.getMass().removeGrid(this);// 移除行军格子
				}
				Role role = world.getRole(getLeader().getInfo().getUid());
				role.handleEvent(GameEvent.UNION_FIGHT_CHANGE, false);// 联盟战斗变化
			}
			timer.setStart(now);
			timer.setLast(goBackQuick);
			timer.setType(type);
		} else {// 普通的返回
			TroopsData leader = null;
			for (int i = 0; i < teams.size();) {
				TroopsData troops = teams.get(i);
				if (troops.isLeader()) {
					i++;
					leader = troops;
					continue;
				}
				ExpediteTroops expedite = new ExpediteTroops();
				expedite.addTroops(troops);
				Role role = world.getRole(troops.getInfo().getUid());
				troops.setLeader(true);
				troopsDatas.add(troops);
				expedite.startPosition = targetPosition;
				expedite.targetPosition = troops.getComePosition();
				expedite.computMoveSpeed(troops, role);
				long castTime = MapUtil.computeCastTime(expedite.startPosition, expedite.targetPosition,
						expedite.speed);// 行军需要的时间
				TimerLast newTimer = new TimerLast(now, castTime, type);
				expedite.setId(troops.getId());
				expedite.registTimer(newTimer);// 计入行军倒计时
				targetCell.expedite(troops.getId());// 终点格子加入回去行军
				startCell.expedite(troops.getId());// 起点格子加入回去行军
				expedite.addSelf();// 添加行军到列表
				expedite.addLook(troops.getInfo().getUid());
				teams.remove(i);// 各自回家的部队从军团长的编号队列移除
			}
			troopsDatas.add(leader);
			Role role = world.getRole(leader.getInfo().getUid());
			computMoveSpeed(leader, role);
			long last = MapUtil.computeCastTime(targetPosition, startPosition, speed);// 行军需要的时间
			timer.setStart(now);
			timer.setLast(last);
			timer.setType(type);
		}
		turnBack();

		/**
		 * 更新为攻击基地胜利后资源掠夺按照比例（16:16:4:1）进行，某类资源不够时按照食品、金属、石油和合金的顺序优先级掠夺；
		 * 可掠夺的资源>=负重时：每项资源的掠夺量=负重量*比例系数/37；单项资源不够时按照资源优先级顺序进行弥补；
		 * 可掠夺的资源<负重时：全部掠夺
		 * 
		 * if (resources != null && resources.size() > 0){//计算每个成员获得战利品 float
		 * totalWeight = 0;//总的负重 List<Float> weights = new ArrayList<Float>();
		 * List<Role> roles = new ArrayList<Role>(); for (int i = 0 ; i <
		 * troopsDatas.size() ; i++){ TroopsData troops = troopsDatas.get(i);
		 * Role role = world.getRole(troops.getInfo().getUid());
		 * roles.add(role); float weight = troops.computeWeight(role);
		 * totalWeight += weight; weights.add(weight); } long group_id = 0; if
		 * (startCell.getTypeKey() == MapCity.class){ MapCity city =
		 * mapWorld.searchObject(startCell); group_id =city.getInfo().getUid();
		 * }
		 * 
		 * int start = ResourceTypeConst.RESOURCE_TYPE_FOOD.ordinal(); int end =
		 * ResourceTypeConst.RESOURCE_TYPE_ALLOY.ordinal(); int count = 0;//
		 * 统计是否掠夺到资源 for (int k = start ; k <= end ; k++){ ResourceTypeConst rtc
		 * = ResourceTypeConst.search(k); Resourcestype resType =
		 * dataManager.serach(Resourcestype.class,rtc.getKey()); if
		 * (!resources.containsKey(rtc.getKey())){//没有这种资源了 continue; } int have
		 * = resources.get(rtc.getKey()).intValue(); int save = have; for (int i
		 * = 0 ; i < troopsDatas.size() ; i++){ //TroopsData old = teams.get(i);
		 * TroopsData troops = troopsDatas.get(i); float weight =
		 * weights.get(i); Role role = roles.get(i); float nowWeight =
		 * troops.computeWeight(role); if (nowWeight > 0 && have >
		 * 0){//自己还有负重，并且资源还没被瓜分完 float rate = weight /
		 * totalWeight;//按自己负重在总负重战的比例来算 int getNum = (int)(save *
		 * resType.getWeight() * rate); getNum =
		 * Math.min(getNum,(int)nowWeight);//取获得和自己负重的最小值 getNum /=
		 * resType.getWeight();//实际抢到的资源 have -= getNum;
		 * troops.addSomethingToPackage(ExpeditePackageType.PACKAGE_TYPE_RESOURCE,rtc.getKey(),getNum);
		 * //这里是给攻城时下发战报用的
		 * //old.addSomethingToPackage(ExpeditePackageType.PACKAGE_TYPE_RESOURCE,rtc.getKey(),getNum);
		 * // 任务事件 if (getNum > 0) {
		 * role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(),
		 * ConditionType.C_RESS_ROB, rtc, getNum); } count++; if (getNum != 0) {
		 * if (troopsDatas.size() > 1) { LogManager.pvpLog(role, group_id,
		 * EventName.AllianceWar.getName(),
		 * EventName.OffensivePlayerCity.getName(), (byte) 1, rtc.getKey(),
		 * getNum); } else { LogManager.pvpLog(role, group_id,
		 * EventName.PersonalChallenge.getName(),
		 * EventName.OffensivePlayerCity.getName(), (byte) 1, rtc.getKey(),
		 * getNum); } } } } have = Math.max(0,have);
		 * resources.put(rtc.getKey(),have);//负重不够剩余的资源数量 } if (count == 0) { if
		 * (troopsDatas.size() > 1) { for (int i = 0; i < troopsDatas.size();
		 * i++) { TroopsData troops = troopsDatas.get(i); Role role =
		 * world.getRole(troops.getInfo().getUid()); LogManager.pvpLog(role,
		 * group_id,
		 * EventName.AllianceWar.getName(),EventName.OffensivePlayerCity.getName(),
		 * (byte) 1, "0", 0); } } else { for (int i = 0; i < troopsDatas.size();
		 * i++) { TroopsData troops = troopsDatas.get(i); Role role =
		 * world.getRole(troops.getInfo().getUid()); LogManager.pvpLog(role,
		 * group_id,
		 * EventName.PersonalChallenge.getName(),EventName.OffensivePlayerCity.getName(),
		 * (byte) 1, "0", 0); } } } }
		 */
		if (resources != null && resources.size() > 0) {// 计算每个成员获得战利品
			predatory(resources, troopsDatas, startCell);
		} 
	}


	/**
	 * 
	 * @Title: predatory
	 * @Description: 掠夺资源
	 * @author xufangliang
	 * 
	 *         更新为攻击基地胜利后资源掠夺按照比例（16:16:4:1）进行，某类资源不够时按照食品、金属、石油和合金的顺序优先级掠夺；
	 *         可掠夺的资源>=负重时：每项资源的掠夺量=负重量*比例系数/37；单项资源不够时按照资源优先级顺序进行弥补；
	 *         可掠夺的资源<负重时：全部掠夺
	 * @return void
	 * @param resources
	 *            可以掠夺的资源
	 * @param troopsDatas
	 *            部队信息
	 * @param startCell
	 *            地图各自信息 log日志使用
	 */
	public void predatory(Map<String, Integer> resources, List<TroopsData> troopsDatas, MapCell startCell) {
		long group_id = 0;
		if (startCell.getTypeKey() == MapCity.class) {
			MapCity city = mapWorld.searchObject(startCell);
			group_id = city.getInfo().getUid();
		}
		StringBuffer sb = new StringBuffer("------------------------predatory----------------------------group_id="
				+ group_id +  "\n");
		// 计算每个成员获得战利品
		float totalWeight = 0;// 总的负重
		List<Float> weights = new ArrayList<Float>();
		Map<Integer, Float> nowWeights = new HashMap<Integer, Float>();
		List<Role> roles = new ArrayList<Role>();
		for (int i = 0; i < troopsDatas.size(); i++) {
			TroopsData troops = troopsDatas.get(i);
			Role role = world.getRole(troops.getInfo().getUid());
			roles.add(role);
			float weight = troops.computeWeight(role);
			totalWeight += weight;
			weights.add(weight);
			nowWeights.put(i, weight);
			sb.append("troops=" + troops.getId() + "|weight=" + weight + "\n");
		}
		
		sb.append("totalWeight=" + totalWeight + "\n");
		sb.append("allNum=" + troopsDatas.size() + "\n");

		int count = 0;
		while (true) {
			double all = thisResourcePredatoryRatio(resources)*1.0;
			sb.append("*****************************count="+count+"****allNum="+all+"****************\n");
			boolean isdResourceCarveUp=  isdResourceCarveUp(resources);
			boolean isOverWeight = isOverWeight(nowWeights, resources);
			if (!isdResourceCarveUp || !isOverWeight || count > 50) {
				sb.append("isdResourceCarveUp || isOverWeight break count=" + count + "\n");
				break;
			}
			int start = ResourceTypeConst.RESOURCE_TYPE_FOOD.ordinal();
			int end = ResourceTypeConst.RESOURCE_TYPE_ALLOY.ordinal();
			for (int i = 0; i < troopsDatas.size(); i++) {
				TroopsData troops = troopsDatas.get(i);
				Role role = roles.get(i);
				//剩余空间
				double nowWeight = troops.computeWeight(role);
				nowWeights.put(i, (float) nowWeight);
				for (int k = start; k <= end; k++) {
					ResourceTypeConst rtc = ResourceTypeConst.search(k);
					Resourcestype resType = dataManager.serach(Resourcestype.class, rtc.getKey());
					if (resType == null) {// 固话数据错误
						continue;
					}
					if (!resources.containsKey(rtc.getKey())) {// 没有这种资源了
						continue;
					}
					int have = resources.get(rtc.getKey()).intValue();
					int troopsCount = troopsDataCount(nowWeights, resType.getWeight());
					int save = have;
					if (have <= 0 || troopsCount <= 0) {
						sb.append("RESOURCE_TYPE = " + k + "|value=" + have + "|troopsCount=" + troopsCount  + " continue \n");
						continue;
					}
					
					double resuRat = ResourcePredatoryRatio.searchProportion(rtc.getKey()) * 1.0;
					sb.append("RESOURCE_TYPE = " + k + "|value=" + have + "|troopsCount=" + troopsCount + "|resuRat="
							+ resuRat + "\n");
					sb.append("RESOURCE_TYPE = " + k + "|calb_residue=" + have + "\n");
					have = Math.max(0, have);
					resources.put(rtc.getKey(), have);// 负重不够剩余的资源数量
					sb.append("RESOURCE_TYPE = " + k + "|cala_residue=" + have + "\n");
					
					double rate = 1.0 / (troopsCount * 1.0);
					//分配的空间
					double myCanInWeighe = nowWeight * resuRat * resType.getWeight() / all;
					//实际容量
					double thisNowWeight = troops.computeWeight(role);
					//剩余物品占据的空间
					double canResourceWeight = save * resType.getWeight() * rate;
					//最后实际可用空间
					double calWeight = Math.min(Math.min(myCanInWeighe, thisNowWeight), canResourceWeight);
					//可用数量
					int getNum = (int) Math.ceil(calWeight / resType.getWeight());
					getNum = Math.min(have,getNum);
					
					getNum = Math.min((int)(thisNowWeight / resType.getWeight()),getNum);
					sb.append("troops=" + troops.getId() + "|rate=" + rate + "|nowWeight=" + nowWeight
							+ "|myCanInWeighe=" + myCanInWeighe + "|canResourceWeight=" + canResourceWeight
							+ "|calWeight=" + calWeight + "|getNum=" + getNum +"|thisNowWeight="+thisNowWeight+ "\n");
					if (thisNowWeight > 0 && have > 0 && getNum > 0 && thisNowWeight >= resType.getWeight()) {
						// 减去装的数量
						have -= getNum;
						troops.addSomethingToPackage(ExpeditePackageType.PACKAGE_TYPE_RESOURCE, rtc.getKey(), getNum);
						if (getNum > 0) {
							role.handleEvent(GameEvent.TASK_CHECK_EVENT, ConditionType.C_RESS_ROB, rtc, getNum);
						}
						if (getNum != 0) {
							if (troopsDatas.size() > 1) {
								LogManager.pvpLog(role, group_id, EventName.AllianceWar.getName(),
										EventName.OffensivePlayerCity.getName(), (byte) 1, rtc.getKey(), getNum);
							} else {
								LogManager.pvpLog(role, group_id, EventName.PersonalChallenge.getName(),
										EventName.OffensivePlayerCity.getName(), (byte) 1, rtc.getKey(), getNum);
							}
						}
						sb.append("troops=" + troops.getId() + "|1getNum=" + getNum + "\n");
					} else {
						sb.append("troops=" + troops.getId() + "|2getNum=" + getNum + "\n");
					}
					nowWeights.put(i, (float) (thisNowWeight-resType.getWeight() * getNum));
				}
			}
			count++;
			sb.append("***************************************************************\n");
		}
		if (count == 0) { // pvp日志 什么都没掠夺到 写入一条行为日志
			String dung_type = troopsDatas.size() > 1 ? EventName.AllianceWar.getName()
					: EventName.PersonalChallenge.getName();
			for (int i = 0; i < troopsDatas.size(); i++) {
				TroopsData troops = troopsDatas.get(i);
				Role role = world.getRole(troops.getInfo().getUid());
				LogManager.pvpLog(role, group_id, dung_type, EventName.OffensivePlayerCity.getName(), (byte) 1, "0", 0);
			}
		}
		GameLog.info(sb.append("--------------------------\n").toString());
	}

	/**
	 * 
	 * @Title: TroopsDataCount
	 * @Description: 可分的队伍
	 * 
	 * @return int
	 * @param nowWeights
	 * @param weight
	 * @return
	 */
	public int troopsDataCount(Map<Integer, Float> nowWeights, int weight) {
		int count = 0;
		for (int i = 0; i < nowWeights.size(); i++) {
			if (nowWeights.get(i) >= weight) {
				count++;
			}
		}
		return count;
	}
	
	/**
	 * 资源总和
	* @Title: thisResourcePredatoryRatio 
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* 
	* @return int
	* @param resources
	* @return
	 */
	public int thisResourcePredatoryRatio(Map<String, Integer> resources) {
		int all = 0;
		for (int i = 0; i < resources.size(); i++) {
			ResourceTypeConst rtc = ResourceTypeConst.search(i);
			Resourcestype resType = dataManager.serach(Resourcestype.class, rtc.getKey());
			if (resType == null) {// 固话数据错误
				continue;
			}
			if (!resources.containsKey(rtc.getKey())) {// 没有这种资源了
				continue;
			}
			int have = resources.get(rtc.getKey()).intValue();
			if(have > 0)
				all += ResourcePredatoryRatio.searchProportion(rtc.getKey());
		}
		return all;
	}


	/**
	 * 
	 * @Title: isdResourceCarveUp
	 * @Description: 是否还有资源
	 * 
	 * @return void
	 */
	public boolean isdResourceCarveUp(Map<String, Integer> resources) {
		int all = 0;
		int start = ResourceTypeConst.RESOURCE_TYPE_FOOD.ordinal();
		int end = ResourceTypeConst.RESOURCE_TYPE_ALLOY.ordinal();
		for (int k = start; k <= end; k++) {
			ResourceTypeConst rtc = ResourceTypeConst.search(k);
			Resourcestype resType = dataManager.serach(Resourcestype.class, rtc.getKey());
			if (resType == null || !resources.containsKey(rtc.getKey())) {
				continue;
			} else {
				all += resources.get(rtc.getKey());
			}
		}
		return all > 0;
	}

	/**
	 * 
	 * @Title: isOverWeight
	 * @Description: 剩余军队的是否还能装下
	 * 
	 * @return boolean
	 * @param nowWeights
	 * @return
	 */
	public boolean isOverWeight(Map<Integer, Float> nowWeights, Map<String, Integer> resources) {
		boolean isAdd = false;
		loop: for (int i = 0; i < nowWeights.size(); i++) {
			int start = ResourceTypeConst.RESOURCE_TYPE_FOOD.ordinal();
			int end = ResourceTypeConst.RESOURCE_TYPE_ALLOY.ordinal();
			for (int k = start; k <= end; k++) {
				ResourceTypeConst rtc = ResourceTypeConst.search(k);
				Resourcestype resType = dataManager.serach(Resourcestype.class, rtc.getKey());
				if (resType == null) {// 固话数据错误
					continue;
				}
				if (!resources.containsKey(rtc.getKey())) {// 没有这种资源了
					continue;
				}
				int have = resources.get(rtc.getKey()).intValue();
				if (have <= 0)
					continue;
				if (nowWeights.get(i) >= resType.getWeight()) {
					isAdd = true;
					break loop;
				}

			}
		}

		return isAdd;
	}

	/**
	 * 命令集结中的一支部队返回
	 * 
	 * @param troops
	 * @param role
	 */
	public void orderTroopsBack(TroopsData troops, Role role) {
		ExpediteTroops expedite = new ExpediteTroops();
		TroopsData leader = expedite.addTroops(troops);
		leader.setLeader(true);
		expedite.startPosition = targetPosition;
		expedite.targetPosition = leader.getComePosition();
		expedite.setId(troops.getId());
		TimerLastType type = TimerLastType.TIME_ARMY_BACK;
		expedite.startPosition = targetPosition;
		expedite.targetPosition = leader.getComePosition();
		long now = TimeUtils.nowLong() / 1000;
		long go = now - timer.getStart();// 以走过的时间
		long left = timer.getLast() - go;// 剩余时间
		float ld = left * speed;
		float gd = 0;
		if (speedNodes.size() > 0) {// 如果加过速
			long preTime = timer.getStart();
			for (int j = 0; j < speedNodes.size(); j++) {
				SpeedNode node = speedNodes.get(j);
				long time = node.getTime() - preTime;
				preTime += time;
				gd += time * node.getSpeed();
			}
			long time = now - preTime;
			gd += time * speed;
		} else {
			gd = go * speed;
		}
		expedite.computMoveSpeed(leader, role);
		float nSpeed = expedite.getSpeed();
		left = (long) (ld / nSpeed);
		long last = left + (long) (gd / nSpeed);
		TimerLast newTimer = new TimerLast(now - left, last, type);
		expedite.registTimer(newTimer);// 计入行军倒计时
		mapWorld.getMapCell(startPosition).expedite(troops.getId());// 终点格子加入回去行军
		mapWorld.getMapCell(targetPosition).expedite(troops.getId());// 起点格子加入回去行军
		expedite.addSelf();// 添加行军到列表
		expedite.addLook(troops.getInfo().getUid());
		MapCell comeCell = mapWorld.getMapCell(troops.getComePosition());
		if (comeCell.getTypeKey() == MapFortress.class || comeCell.getTypeKey() == MapBarracks.class) {
			MapFortress fortress = mapWorld.searchObject(comeCell);
			if (fortress.getInfo().getUid() == leader.getInfo().getUid()) {
				fortress.changeGrid(this, expedite);
			}
		}
		role.handleEvent(GameEvent.UNION_FIGHT_CHANGE, false);// 联盟战斗变化
		role.handleEvent(GameEvent.TROOPS_SEND);
	}

	public void goBackToCome() {
		goBackToCome(null);
	}

	public void goBackCity() {
		long now = TimeUtils.nowLong() / 1000;
		MapCell startCell = mapWorld.getMapCell(targetPosition);
		for (int i = 0; i < teams.size(); i++) {
			TroopsData troops = teams.get(i);
			MapCity mc = mapWorld.searchMapCity(troops.getInfo());
			int rolePos = mc.getPosition();
			Role role = world.getRole(troops.getInfo().getUid());
			if (rolePos == targetPosition) {
				// 主城就在这里
				RespModuleSet rms = new RespModuleSet();
				// 部队回营
				armyBack(rms, role);
				packageBack(rms, role);
				MessageSendUtil.sendModule(rms, role.getUserInfo());
			} else {
				MapCell targetCell = mapWorld.getMapCell(rolePos);
				computMoveSpeed(troops, role);
				startPosition = targetPosition;
				targetPosition = rolePos;
				long castTime = MapUtil.computeCastTime(startPosition, targetPosition, speed);// 行军需要的时间
				timer.setStart(now);
				timer.setLast(castTime);
				timer.setType(TimerLastType.TIME_ARMY_BACK);
				targetCell.expedite(id);// 终点格子加入回去行军
				startCell.expedite(id);// 起点格子加入回去行军
				sendToClient(TROOPS_CHANGE);
			}
		}
	}

	public void packageBack(RespModuleSet rms, Role role) {
		for (int i = 0; i < teams.size(); i++) {
			TroopsData troops = teams.get(i);
			if (troops.getInfo().getUid() != role.getId()) {
				continue;
			}
			List<ItemCell> changes = new ArrayList<ItemCell>();
			List<Object> objs = new ArrayList<Object>();
			troops.addResourceToCity(role, changes, objs);
			if (changes.size() > 0) {
				role.getBagAgent().sendItemsToClient(rms, changes);// 背包变化的道具修改
			}
			if (objs.size() > 0) {
				role.addResourcesToCity(true, rms, troops.getInfo().getCityId(), objs.toArray());
			}
			role.sendRoleToClient(rms);
		}
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public String[] wheres() {
		String[] result = new String[1];
		result[0] = RED_ALERT_GENERAL_ID;
		return result;
	}

	@Override
	public void insertData(SqlData data) {
		saveToData(data);
	}

	public void addLook(long joy_id) {
		synchronized (couldLooks) {
			if (!couldLooks.contains(joy_id)) {
				couldLooks.add(joy_id);
			}
		}
	}

	public void computMoveSpeed(Role role) {
		TroopsData leader = getLeader();
		computMoveSpeed(leader, role);
	}

	public void computMoveSpeed(TroopsData leader, Role role) {
		if (leader != null) {
			speed = leader.computMoveSpeed(role);
		}
	}

	public void setMass(boolean mass) {
		this.mass = mass;
	}

	public boolean isMass() {
		return mass;
	}

	public boolean relevance(long id) {
		for (int i = 0; i < teams.size(); i++) {
			TroopsData troops = teams.get(i);
			if (troops.getInfo().getUid() == id) {
				return true;
			}
		}
		return false;
	}

	public GarrisonTroops occuper(MapObject obj) {
		haveGoback = true;// 驻防了就不能返回了。
		GarrisonTroops occupyer = new GarrisonTroops();
		occupyer.setId(id);// 驻防部队还是用行军的部队编号
		occupyer.setTroops(getLeader());
		occupyer.setPosition(targetPosition);
		occupyer.addSelf();
		if (timer.getType() != TimerLastType.TIME_EXPEDITE_STATION) {// 非调拨部队
			MapCell comeCell = mapWorld.getMapCell(startPosition);
			if (comeCell.getTypeKey() == MapFortress.class || comeCell.getTypeKey() == MapBarracks.class) {
				MapFortress fortress = mapWorld.searchObject(comeCell);
				if (fortress.getInfo().getUid() == occupyer.getTroops().getInfo().getUid()) {
					if (obj instanceof MapBarracks) {
						fortress.removeGrid(this);
					} else {
						fortress.changeGrid(this, occupyer);
					}
				}
			}
		}
		remove();// 部队任务完成,移除部队
		return occupyer;
	}

	public void armyBack(RespModuleSet rms, Role role) {
		for (int i = 0; i < teams.size(); i++) {
			TroopsData troops = teams.get(i);
			if (troops.getInfo().getUid() == role.getId()) {
				troops.armyBack(rms, role);
			}
		}
	}

	/**
	 * 获取下一个可以出战的部队
	 * 
	 * @param excepts
	 * @return
	 */
	public TroopsData getFightTroops(List<Long> excepts) {
		for (int i = 0; i < teams.size(); i++) {
			TroopsData troops = teams.get(i);
			if (!excepts.contains(troops.getInfo().getUid()) && troops.couldFight()) {
				return troops;
			}
		}
		return null;
	}

	public int getAliveNum() {
		int num = 0;
		for (int i = 0; i < teams.size(); i++) {
			TroopsData troops = teams.get(i);
			num += troops.getAliveNum();
		}
		return num;
	}

	public List<TroopsData> getAllAliveTroopses() {
		List<TroopsData> troopses = new ArrayList<TroopsData>();
		for (int i = 0; i < teams.size(); i++) {
			TroopsData troops = teams.get(i);
			if (troops.couldFight()) {
				troopses.add(troops);
			}
		}
		return troopses;
	}

	public void trySpeedUp(float rate) {
		if (speed > 100000) {
			return;
		}
		long now = TimeUtils.nowLong() / 1000;
		long desTime = Math.max(0, now - timer.getStart());
		float left = Math.max(0, timer.getLast() - desTime);
		if (left == 0) {
			return;
		}
		SpeedNode node = null;
		SpeedNode tail = speedNodes.size() > 0 ? speedNodes.get(speedNodes.size() - 1) : null;
		long newLast = 0;
		if (left <= 5) {// 小于5秒的加速直接到达
			newLast = desTime;
		} else {
			newLast = (long) (desTime + left * (1 - rate));
			if (rate < 1) {
				if (tail != null && tail.getTime() - now == 0) {// 在同一秒加速的
					node = tail;
				} else {
					node = new SpeedNode();
				}
				node.setTime(now);
				node.setSpeed(speed);
				speed = speed * (1 + rate);
			}
		}
		timer.setLast(newLast);
		if (node != null && !node.equals(tail)) {
			speedNodes.add(node);
		}
		List<MapUnionDefenderTower> towers = world.getListObjects(MapUnionDefenderTower.class);
		if (towers.size() > 0) {
			for (int i = 0; i < towers.size(); i++) {
				MapUnionDefenderTower tower = towers.get(i);
				tower.tryToLock(this);
			}
		}
		sendToClient(TROOPS_CHANGE);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "_" + id;
	}

	public String getHeader() {
		StringBuffer sb = new StringBuffer();
		sb.append(id + "[" + teams.get(0).getInfo().getName());
		for (int i = 1; i < teams.size(); i++) {
			sb.append("," + teams.get(i).getInfo().getName());
		}
		sb.append("]");
		return sb.toString();
	}

	/**
	 * 下发所有的战报
	 */
	public void sendReport() {
		for (int i = 0; i < reports.size(); i++) {
			FightReport report = reports.get(i);
			report.send(this);// 添加奖励
		}
	}

	public void addReport(FightReport report) {
		if (report == null)
			return;
		GameLog.info("[report=== type=" + report.getType() + "|time=" + report.getTime() + "|position="
				+ report.getPosition() + "]" + JsonUtil.ObjectToJsonString(report));
		reports.add(report);
	}

	public static boolean isA(Map<Integer, Integer> map) {
		for (Integer value : map.values()) {
			if (value > 0)
				return true;
		}
		return false;
	}

}
