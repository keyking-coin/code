package com.joymeng.slg.domain.object.daily;

import java.util.ArrayList;
import java.util.List;

import com.joymeng.Instances;
import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.JsonUtil;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.common.util.StringUtils;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.log.GameLog;
import com.joymeng.log.LogManager;
import com.joymeng.log.NewLogManager;
import com.joymeng.slg.domain.event.GameEvent;
import com.joymeng.slg.domain.object.bag.ItemCell;
import com.joymeng.slg.domain.object.daily.data.Onlinereward;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.task.ConditionType;
import com.joymeng.slg.domain.object.task.TaskEventDelay;
import com.joymeng.slg.domain.timer.TimerLastType;
import com.joymeng.slg.domain.timer.TimerOver;
import com.joymeng.slg.domain.timer.TimerLast;
import com.joymeng.slg.net.mod.AbstractClientModule;
import com.joymeng.slg.net.mod.RespModuleSet;

public class OnlineAgent implements TimerOver, Instances{
	String id;//奖励索引
	TimerLast timer;
	boolean isOver;//是否可领取
	long uid;
	int gmTime;
	
	public OnlineAgent(){
		isOver = true;
	}
	public void setUid(long uid){
		this.uid = uid;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public boolean isOver() {
		return isOver;
	}
	
	public void setGmTime(int gmTime) {
		this.gmTime = gmTime;
	}
	
	public TimerLast getTimer() {
		return timer;
	}
	
	public void init(){
		if(timer == null){
			id = "1";
			Onlinereward reward = dataManager.serach(Onlinereward.class, id);
			if(reward != null){
				timer = new TimerLast(TimeUtils.nowLong()/1000,reward.getTime(),TimerLastType.TIME_DAILY_REWARD);
				timer.registTimeOver(this);
				isOver = false;
			}
		}
	}
	
	public boolean getDailyReward(Role role) {
		Onlinereward reward = dataManager.serach(Onlinereward.class, id);
		if (reward == null) {
			return false;
		}
		// 发送奖励
		List<ItemCell> cells = new ArrayList<ItemCell>();
		for (String str : reward.getReward()) {
			String[] wards = str.split(":");
			if (wards.length < 2) {
				continue;
			}
			String itemId = wards[0];
			int itemNum = Integer.parseInt(wards[1]);
			cells.addAll(role.getBagAgent().addGoods(itemId, itemNum));
			String event = "getDailyReward";
			String itemst = itemId;
			LogManager.itemOutputLog(role, itemNum, event, itemst);
			try {
				NewLogManager.baseEventLog(role, "receive_supplies",itemId,itemNum);
			} catch (Exception e) {
				GameLog.info("埋点错误");
			}
		}
		RespModuleSet rms = new RespModuleSet();
		role.getBagAgent().sendItemsToClient(rms, cells);
		MessageSendUtil.sendModule(rms, role.getUserInfo());
		// 任务事件
		role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.C_ONLINE_CNT, 0);
		// 设置下一个
		String newIndex = reward.getNextid();
		if (!TimeUtils.isSameDay(timer.getStart() * 1000)) {
			newIndex = "1";
		}
		if (newIndex.equals("0")) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ONLINE_GOT_ALL);
			id = "0";
		} else {
			Onlinereward newReward = dataManager.serach(Onlinereward.class, newIndex);
			if (newReward == null) {
				GameLog.error("read Onlinereward is fail");
				return false;
			}
			long last = gmTime > 0 ? gmTime : newReward.getTime();
			id = newIndex;
			timer = new TimerLast(TimeUtils.nowLong() / 1000,last,TimerLastType.TIME_DAILY_REWARD);
			timer.registTimeOver(this);
			isOver = false;
		}
		RespModuleSet rms2 = new RespModuleSet();
		sendToClient(rms2);
		MessageSendUtil.sendModule(rms2, role.getUserInfo());
		return true;
	}
	
	public void deserialize(String data){
		if(StringUtils.isNull(data)){
			init();//防止玩家数据载入失败
			return;
		}
		String[] strArray = data.split(";");
		if(strArray.length < 2){
			init();//防止玩家数据载入失败
			return;
		}
		id = strArray[0];
		timer = JsonUtil.JsonToObject(strArray[1], TimerLast.class);
		if(timer.getLast() + timer.getStart() <= TimeUtils.nowLong()/1000){
			isOver = true;
		}else{
			timer.registTimeOver(this);
			isOver = false;
		}
	}
	
	public String serialize(){
		String str=id + ";";
		str += JsonUtil.ObjectToJsonString(timer);
		return str;
	}
	
	public void tick(long now){
		if(!isOver){
			if (timer != null && timer.over(now)){
				timer.die();
			}
		}
	}
	
	public void sendToClient(RespModuleSet rms) {
		AbstractClientModule module = new AbstractClientModule() {
			@Override
			public short getModuleType() {
				return NTC_DTCD_ROLE_DAILY;
			}
		};
		module.add(id);// 当前奖励索引 string
		timer.sendToClient(module.getParams());// 倒计时
		module.add(isOver ? (byte) 1 : (byte) 0);// 是否可领取
		rms.addModule(module);
	}
	@Override
	public void finish() {
		Role role = world.getObject(Role.class, uid);
		if(role == null){
			return;
		}
		isOver = true;
		RespModuleSet rms = new RespModuleSet();
		sendToClient(rms);
		MessageSendUtil.sendModule(rms, role.getUserInfo());
	}
}
