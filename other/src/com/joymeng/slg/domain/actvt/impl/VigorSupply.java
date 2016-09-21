package com.joymeng.slg.domain.actvt.impl;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.w3c.dom.Element;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.joymeng.services.utils.XmlUtils;
import com.joymeng.slg.dao.SqlData;
import com.joymeng.slg.domain.actvt.Actvt;
import com.joymeng.slg.domain.actvt.ActvtCommonState;
import com.joymeng.slg.domain.actvt.ClientMod;
import com.joymeng.slg.domain.actvt.data.ActvtCommon;
import com.joymeng.slg.domain.actvt.data.ActvtReward;
import com.joymeng.slg.domain.object.role.Role;

public class VigorSupply extends Actvt 
{
	private Map<Long, Integer> receivePlayers = new HashMap<Long, Integer>(); 
	private boolean[] receiveAbles = {false, false};
	
	private ActvtReward reward1;
	private ActvtReward reward2;
	private String time1;
	private String time2;
	private DateTime start1;
	private DateTime end1;
	private DateTime start2;
	private DateTime end2;

	@Override
	public void end()
	{
		super.end();
		
		stopInnerTimer("start1");
		stopInnerTimer("end1");
		stopInnerTimer("start2");
		stopInnerTimer("end2");
	}
	
	@Override
	public void start()
	{
		super.start();
		
		if (DateTime.now().isAfter(start1) && DateTime.now().isBefore(end1)) {
			receiveAbles[0] = true;
		}
		
		String cron = MessageFormat.format("0 {0} {1} * * ?", start1.getMinuteOfHour(), start1.getHourOfDay());
		stopInnerTimer("start1");
		startInnerTimer(cron, "start1");
		
		cron = MessageFormat.format("0 {0} {1} * * ?", end1.getMinuteOfHour(), end1.getHourOfDay());
		stopInnerTimer("end1");
		startInnerTimer(cron, "end1");
		
		if (DateTime.now().isAfter(start2) && DateTime.now().isBefore(end2)) {
			receiveAbles[1] = true;
		}
		
		cron = MessageFormat.format("0 {0} {1} * * ?", start2.getMinuteOfHour(), start2.getHourOfDay());
		stopInnerTimer("start2");
		startInnerTimer(cron, "start2");
		
		cron = MessageFormat.format("0 {0} {1} * * ?", end2.getMinuteOfHour(), end2.getHourOfDay());
		stopInnerTimer("start2");
		startInnerTimer(cron, "end2");
	}
	
	@Override
	public void load(Element element) throws Exception
	{
		super.load(element);
		
		Element eleSpecial = XmlUtils.getChildByName(element, "Special");
		
		time1 = eleSpecial.getAttribute("time1");
		String[] time1s = time1.split("-");
		start1 = parseDateTime(time1s[0]);
		end1 = parseDateTime(time1s[1]);
		if (!start1.isBefore(end1)) {
			throw new Exception("id="+getId()+" special startTime1 is not before endTime1");
		}
		
		time2 = eleSpecial.getAttribute("time2");
		String[] time2s = time2.split("-");
		start2 = parseDateTime(time2s[0]);
		end2 = parseDateTime(time2s[1]);
		if (!start2.isBefore(end2)) {
			throw new Exception("id="+getId()+" special startTime2 is not before endTime2");
		}

		reward1 = getReward(eleSpecial.getAttribute("reward1"));
		reward2 = getReward(eleSpecial.getAttribute("reward2"));
	}
	
	@Override
	public void innerTimerCB(String tag)
	{
		if (tag.equals("start1")) {
			receivePlayers.clear();
			receiveAbles[0] = true;
		}
		else if (tag.equals("end1")) {
			receiveAbles[0] = false;
		}
		else if (tag.equals("start2")) {
			receivePlayers.clear();
			receiveAbles[1] = true;
		}
		else if (tag.equals("end2")) {
			receiveAbles[1] = false;
		}
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
	
	@Override
	public boolean receiveReward(Role role, int index) 
	{
		if (!canReceive(role.getId(), index)) {
			return false;
		}
		
		if (index == 0) {
			rewardPlayer(role, reward1.getItems());
		}
		else {
			rewardPlayer(role, reward2.getItems());
		}
		receivePlayers.put(role.getId(), 1);
		return true;
	}
	
	@Override
	public void makeUpDetailModule(ClientMod module, Role role) 
	{
		ActvtCommon commonData = getCommonData();
		module.add(commonData.getType());
		module.add(commonData.getName());
		module.add(commonData.getDetailDesc());

		module.add(time1);
		module.add(reward1.getItemId(0));
		module.add(reward1.getItemNum(0));
		module.add(getRewardState(role, 0));
		
		module.add(time2);
		module.add(reward2.getItemId(0));
		module.add(reward2.getItemNum(0));
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
