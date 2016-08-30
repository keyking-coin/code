package com.joymeng.slg.domain.object.role.signin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joymeng.Instances;
import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.MathUtils;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.common.util.StringUtils;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.log.GameLog;
import com.joymeng.log.LogManager;
import com.joymeng.log.NewLogManager;
import com.joymeng.slg.domain.data.SearchFilter;
import com.joymeng.slg.domain.event.GameEvent;
import com.joymeng.slg.domain.object.bag.ItemCell;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.role.data.Checkreward;
import com.joymeng.slg.domain.object.task.ConditionType;
import com.joymeng.slg.domain.object.task.TaskEventDelay;
import com.joymeng.slg.net.mod.AbstractClientModule;
import com.joymeng.slg.net.mod.RespModuleSet;

public class RoleThirtySignIn  implements Instances{
	public static final int MAX_RANDOM_COUNT = 30;
	private int signCount = 0; // 签到次数,大于30天，从第一天重新计算
	private long lastsigntime = 0;// 上次签到时间
	Map<Integer, String> rewardMap = new HashMap<Integer, String>();// 30次奖励
	long uid;
	
	public RoleThirtySignIn() {
		lastsigntime = 0;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public void initRewardLst(Role role) {
		if (rewardMap.size() == 0 || signCount >= MAX_RANDOM_COUNT) {
			resetRewardLst(role);
		}
	}

	private void resetRewardLst(Role role) {
		if (signCount > 0 && signCount < MAX_RANDOM_COUNT) {
			return;
		}
		if (signCount == 0 && rewardMap.size() > 0) {
			return;
		}
		rewardMap.clear();
		signCount = 0;
		for (int i = 0; i < MAX_RANDOM_COUNT; i++) {
			String rewardId = randomRewardData(role, i + 1);
			if (rewardId == null) {
				GameLog.error("randomRewardData is fail");
				continue;
			}
			rewardMap.put(i, rewardId);
		}
	}

	/**
	 * 玩家签到
	 * 
	 * @param role
	 * @return
	 */
	public boolean signIn(Role role) {
		long now = TimeUtils.nowLong();
		if (TimeUtils.isSameDay(lastsigntime, now) || now <= lastsigntime) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ROLE_HAS_SIGNED_IN_THIRTY);
			return false;
		}
		// 获取任务奖励
		if (rewardMap.size() == 0) {
			return false;
		}
		String rewardId = rewardMap.get(signCount);
		if (StringUtils.isNull(rewardId)) {
			GameLog.error("策划,表出错了.");
			return false;
		}
		Checkreward reward = dataManager.serach(Checkreward.class, rewardId);
		if (reward == null || reward.getReward() == null) {
			GameLog.error("role sign in error, reward data load error.");
			return false;
		}
		signCount++;
		lastsigntime = now;
		byte multiple = role.getVipInfo() != null && role.getVipInfo().isActive()
				&& role.getVipInfo().getVipLevel() >= reward.getVip() ? reward.getMultiple() : (byte) 1;
		List<ItemCell> cells = new ArrayList<ItemCell>();
		for (int i = 0 ; i < reward.getReward().size() ; i++){
			String strAward = reward.getReward().get(i);
			String[] awardArray = strAward.split(":");
			if (awardArray.length < 2) {
				continue;
			}
			String itemId = awardArray[0];
			int itemNum = Integer.parseInt(awardArray[1]) * multiple;
			cells.addAll(role.getBagAgent().addGoods(itemId, itemNum));
			String event = "roleThirtySignIn";
			String itemst  = itemId;
			LogManager.itemOutputLog(role, itemNum, event, itemst);
			try {
				NewLogManager.baseEventLog(role, "month_sign",signCount,itemId,itemNum);
			} catch (Exception e) {
				GameLog.info("埋点错误");
			}
		}
		RespModuleSet rms = new RespModuleSet();
		role.getBagAgent().sendItemsToClient(rms, cells);
		sendSignInDataToClient(rms);
		MessageSendUtil.sendModule(rms, role.getUserInfo());
		//任务事件
		role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.C_SIGN_CNT, 0);
		return true;
	}

	public void sendSignInDataToClient(RespModuleSet rms) {
		AbstractClientModule module = new AbstractClientModule() {
			@Override
			public short getModuleType() {
				return NTC_DTCD_ROLE_SIGN;
			}
		};
		module.add(1);
		module.add(signCount);// 签到次数，int
		long now = TimeUtils.nowLong();
//		module.add((byte)0);
		module.add(TimeUtils.isSameDay(lastsigntime, now) ? (byte) 1 : (byte) 0);
		module.add(rewardMap.size());// 奖励列表大小,int
		for (Map.Entry<Integer, String> entry : rewardMap.entrySet()) {
			module.add(entry.getKey());// 奖励id,int
			module.add(entry.getValue());// 奖励的固化表id,String
		}
		rms.addModule(module);
	}

	/**
	 * 随机生成签到奖励
	 * 
	 * @return
	 */
	private String randomRewardData(Role role,final int count) {
		final byte roleLv = role.getCity(0).getCityCenterLevel();
		List<Checkreward> rewardLst = dataManager.serachList(Checkreward.class, new SearchFilter<Checkreward>() {
			@Override
			public boolean filter(Checkreward data) {
				byte lvMin = Byte.parseByte(data.getCommanderlv().get(0));
				byte lvMax = Byte.parseByte(data.getCommanderlv().get(1));
				if (data.getData() == count && data.getType() == 2 && roleLv >= lvMin && roleLv <= lvMax) {
					return true;
				}
				return false;
			}
		});
		if (rewardLst == null || rewardLst.size() == 0) {
			GameLog.error("role " + uid + " checkin reward data error.. ps:Thirty");
			return null;
		}
		Checkreward reward = MathUtils.randomOne(rewardLst);
		if (reward == null) {
			reward = rewardLst.get(0);
		}
		return reward.getId();
	}

	public void deserialize(String data) {
		if (StringUtils.isNull(data)) {
			return;
		}
		String[] strArray = data.split(",");
		String[] strarr = strArray[0].split(":");
		signCount = Integer.parseInt(strarr[1]);
		strarr = strArray[1].split(":");
		lastsigntime = Long.parseLong(strarr[1]);
		for (int i = 2; i < strArray.length; i++) {
			strarr = strArray[i].split(":");
			rewardMap.put(Integer.parseInt(strarr[0]), strarr[1]);
		}
	}

	public String serialize() {
		String str = "SignCount:" + signCount + "," + "lasSignTime:" + lastsigntime;
		for (Map.Entry<Integer, String> entry : rewardMap.entrySet()) {
			str += "," + entry.getKey() + ":" + entry.getValue();
		}
		return str;
	}

	public int getSignCount() {
		return signCount;
	}

	public void setSignCount(int signCount) {
		this.signCount = signCount;
	}

	public long getLastsigntime() {
		return lastsigntime;
	}

	public void setLastsigntime(long lastsigntime) {
		this.lastsigntime = lastsigntime;
	}

}
