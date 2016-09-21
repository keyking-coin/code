package com.joymeng.slg.domain.actvt.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

import com.joymeng.services.utils.XmlUtils;
import com.joymeng.slg.dao.DaoData;
import com.joymeng.slg.dao.SqlData;
import com.joymeng.slg.domain.actvt.Actvt;
import com.joymeng.slg.domain.actvt.ClientMod;
import com.joymeng.slg.domain.actvt.data.ActvtCommon;
import com.joymeng.slg.domain.actvt.data.ActvtReward;
import com.joymeng.slg.domain.object.role.Role;

public class FirstCharge extends Actvt 
{	
	private static final int STAGE_NUM = 4;
	private static final int RECEIVED = -1;
	
	private List<List<Integer>> stages = new ArrayList<List<Integer>>();
	private List<ActvtReward> rewards = new ArrayList<ActvtReward>();
	
	private Map<Long,Integer> chargeFlags = new HashMap<Long,Integer>();
	
	@Override
	public void load(Element element) throws Exception {
		super.load(element);
		
		Element eleSpecial = XmlUtils.getChildByName(element, "Special");
		String str = eleSpecial.getAttribute("stages");
		String[] strs = str.split("#");
		for (int i = 0; i < strs.length; i++) {
			String[] strss = strs[i].split(",");
			List<Integer> stagess = new ArrayList<Integer>();
			for (int j = 0; j < strss.length; j++) {
				stagess.add(Integer.parseInt(strss[j]));
			}
			stages.add(stagess);
		}
		if (stages.size() != STAGE_NUM) {
			throw new Exception("FirstCharge stages num is not " + STAGE_NUM);
		}
		
		for (int i = 1; i <= STAGE_NUM; i++) {
			ActvtReward reward = getReward(String.valueOf(i));
			rewards.add(reward);
		}
	}
	
	@Override
	public void start()
	{
		super.start();
		
	}
	
	@Override
	public void end()
	{
		super.end();
	}
	
	@Override
	public void makeUpDetailModule(ClientMod module, Role role)
	{
		ActvtCommon commonData = getCommonData();
		module.add(commonData.getType());
		module.add(commonData.getName());
		module.add(commonData.getDetailDesc());
		
		module.add(getFlag(role.getId()));
		
		for (int i = 0; i < STAGE_NUM; i++) {
			module.add(stages.get(i).size());
			for (int j = 0; j < stages.get(i).size(); j++) {
				module.add(stages.get(i).get(j));
			}
			module.add(rewards.get(i).getItems());
		}
	}
	
	private int getMaxCharge(long joyId) {
		try {
			List<SqlData> list = dbMgr.getGameDao().getDatas(DaoData.TABLE_RED_ALERT_CHARGE_ORDER, DaoData.RED_ALERT_CHARGE_JOYID, joyId);
			if (list == null) {
				return 0;
			}
			int max = 0;
			for (int i = 0; i < list.size(); i++) {
				int value = list.get(i).getInt(DaoData.RED_ALERT_CHARGE_VALUE);
				if (max < value) {
					max = value;
				}
			}
			return max;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	private int getStage(int value) {
		if (value <=0 ){
			return 0;
		}
		
		int stage = 0;
		for (int i = 0; i < stages.size(); i++) {
			List<Integer> stagess = stages.get(i);
			for (int j = 0; j < stagess.size(); j++) {
				if (value >= stagess.get(j)) {
					stage = i+1;
					break;
				}
			}
		}
		return stage;
	}
	
	private int getFlag(long joyId) {
		int flag = 0;
		if (chargeFlags.containsKey(joyId)) {
			flag = chargeFlags.get(joyId);
			if (flag != 0) {
				return flag;
			}
		}
		int max = getMaxCharge(joyId);
		int stage = getStage(max);
		chargeFlags.put(joyId, stage);
		return stage;
	}
	
	@Override
	public boolean receiveReward(Role role, int index) {
		if (index < 0 || index >= STAGE_NUM) {
			return false;
		}
		int flag = getFlag(role.getId());
		if (flag == RECEIVED) {
			return false;
		}
		ActvtReward reward = rewards.get(index);
		rewardPlayer(role, reward.getItems());
		chargeFlags.put(role.getId(), RECEIVED);
		return true;
	}

	@Override
	public void loadFromData(SqlData data) {}
}