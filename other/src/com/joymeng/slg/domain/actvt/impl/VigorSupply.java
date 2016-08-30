package com.joymeng.slg.domain.actvt.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.joymeng.log.GameLog;
import com.joymeng.log.NewLogManager;
import com.joymeng.slg.dao.SqlData;
import com.joymeng.slg.domain.actvt.Actvt;
import com.joymeng.slg.domain.actvt.ActvtCommonState;
import com.joymeng.slg.domain.actvt.ClientMod;
import com.joymeng.slg.domain.actvt.DTManager.SearchFilter;
import com.joymeng.slg.domain.actvt.data.Activity;
import com.joymeng.slg.domain.actvt.data.Activity_reward;
import com.joymeng.slg.domain.actvt.data.Activity_vigorsupply;
import com.joymeng.slg.domain.object.role.Role;

public class VigorSupply extends Actvt {

	private Map<Long, Integer> receivePlayers = new HashMap<Long, Integer>(); 
	private boolean[] receiveAbles = {false, false};
	
	private Activity_vigorsupply vigorSupply;
	private Activity_reward reward1;
	private Activity_reward reward2;
	
	@Override
	public boolean init(Activity activity)
	{
		if (!super.init(activity)) {
			return false;
		}
		load();
		
		return true;
	}
	
	public int getRewardState(Role role, int index)
	{
		if (!isRuning()) {
			return ActvtCommonState.NOT_FINISH.ordinal();
		}
		if (!receiveAbles[index]) {
			return ActvtCommonState.NOT_FINISH.ordinal();
		}
		if (!receivePlayers.containsKey(role.getId())) {
			return ActvtCommonState.FINISH.ordinal();
		}
		return ActvtCommonState.RECEIVED.ordinal();
	}

	public boolean canReceive(long joyId, int index) 
	{
		if (!isRuning()) {
			return false;
		}
		return receiveAbles[index] && !receivePlayers.containsKey(joyId);
	}
	
	public void checkValideReward(String value, String data)
	{
		
	}
	
	public void valideReward(String value, String data) {
		GameLog.info("valideReward, " + value + ", " + data);
		
		String[] values = value.split("#");
		if (values.length < 1) {
			return; //SN 错误处理
		}
		
		try {
			int index = Integer.valueOf(values[0]);
			int valide = Integer.valueOf(values[1]);
			receiveAbles[index-1] = (valide == 1);
		}
		catch (Exception e) {
			//SN 异常报错的LOG记录
		}
	}
	
	public void clearReceiveStats(String value, String data) {
		GameLog.info("clearReceiveStats, " + value + ", " + data);
		
		receivePlayers.clear();
	}
	
	public void receiveStats(String value, String data) {
		GameLog.info("receiveStats, " + value + ", " + data);
		Role role = getRole(data);
		if (role != null) {
			receivePlayers.put(role.getId(), 1);
		}
		else {
			//SN 错误处理
		}
	}
	
	@Override
	public boolean receiveReward(Role role, int index) {
		if (!canReceive(role.getId(), index)) {
			return false;
		}
      
		// TEST11  定时领取 	String sID; 	int num;
	
		if (index == 0) {
			rewardPlayer(role, reward1.toString());
			String str = reward1.getsID()+"|"+reward1.getNum();
			NewLogManager.activeLog(role, "activity_timing_reward",str);
		}
		else {
			rewardPlayer(role, reward2.toString());
			String str = reward2.getsID()+"|"+reward2.getNum();
			NewLogManager.activeLog(role, "activity_timing_reward",str);
		}

		receivePlayers.put(role.getId(), 1);
		return true;
	}
	
	@Override
	public void makeUpDetailModule(ClientMod module, Role role) {
		Activity activity = getActivity();
		module.add(activity.getType());
		module.add(activity.getName());
		module.add(activity.getDetailDesc());

		module.add(vigorSupply.getStartTime1()+"-"+vigorSupply.getEndTime1());
		module.add(reward1.getsID());
		module.add(reward1.getNum());
		module.add(getRewardState(role, 0));
		
		module.add(vigorSupply.getStartTime2()+"-"+vigorSupply.getEndTime2());
		module.add(reward2.getsID());
		module.add(reward2.getNum());
		module.add(getRewardState(role, 1));
//		System.out.println(module.getParams().toString());
	}
	
	@Override
	public String getStateStr() {
		return JSON.toJSONString(receivePlayers);
	}

	@Override
	public void loadFromData(SqlData data) {
		String[] strs = getStateStrs(data);
		receivePlayers = JSON.parseObject(strs[0], new TypeReference<Map<Long, Integer>>(){});
	}

	@Override
	public void load()
	{
//		vigorSupply = actvtMgr.serach(Activity_vigorsupply.class, "1");
		vigorSupply = actvtMgr.serach(Activity_vigorsupply.class, new SearchFilter<Activity_vigorsupply>() {
			@Override
			public boolean filter(Activity_vigorsupply data) {
				return data.getTypeId().equals(getActivity().getTypeId());
			}
		});
		
		List<Activity_reward> rewardList = actvtMgr.serachList(Activity_reward.class, new SearchFilter<Activity_reward>() {
			@Override
			public boolean filter(Activity_reward data) {
				return data.getrID().equals(getActivity().getTypeId()+"_r1");
			}
		});
		reward1 = rewardList.get(0);
		
		rewardList = actvtMgr.serachList(Activity_reward.class, new SearchFilter<Activity_reward>() {
			@Override
			public boolean filter(Activity_reward data) {
				return data.getrID().equals(getActivity().getTypeId()+"_r2");
			}
		});
		reward2 = rewardList.get(0);
		
//		reward1 = actvtMgr.getReward(vigorSupply.getReward1()).get(0);
//		reward2 = actvtMgr.getReward(vigorSupply.getReward2()).get(0);
		
		DateTime dt1 = parseDateTime(vigorSupply.getStartTime1());
		DateTime dt2 = parseDateTime(vigorSupply.getEndTime1());
		if (DateTime.now().isAfter(dt1) && DateTime.now().isBefore(dt2)) {
			receiveAbles[0] = true;
		}
		else {
			dt1 = parseDateTime(vigorSupply.getStartTime2());
			dt2 = parseDateTime(vigorSupply.getEndTime2());
			if (DateTime.now().isAfter(dt1) && DateTime.now().isBefore(dt2)) {
				receiveAbles[1] = true;
			}
		}
//		vigorSupply.getStartTime1()
	}
	
	private DateTime parseDateTime(String timestr)
	{
		DateTimeFormatter formatter = DateTimeFormat.forPattern("HH:mm");
		DateTime dt = formatter.parseDateTime(timestr);
		dt = DateTime.now().withHourOfDay(dt.getHourOfDay()).withMinuteOfHour(dt.getMinuteOfHour()).withSecondOfMinute(0);
		return dt;
	}
	
	@Override
	public int getReceiveableNum(long joyId)
	{
		int num = 0;
		if (canReceive(joyId, 0)) {
			num++;
		}
		if (canReceive(joyId, 1)) {
			num++;
		}
		return num;
	}
}
