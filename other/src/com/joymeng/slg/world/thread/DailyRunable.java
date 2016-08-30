package com.joymeng.slg.world.thread;

import com.joymeng.Instances;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.list.RealtimeData;
import com.joymeng.log.GameLog;
import com.joymeng.log.LogManager;
import com.joymeng.slg.domain.map.impl.still.copy.RoleRelic;
import com.joymeng.slg.domain.market.RoleBlackMarketAgent;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.role.signin.RoleSevenSignIn;
import com.joymeng.slg.domain.object.role.signin.RoleThirtySignIn;
import com.joymeng.slg.net.mod.RespModuleSet;
import com.joymeng.slg.world.GameConfig;

public class DailyRunable implements Runnable, Instances {	
	@Override
	public void run() {
		GameLog.info("do job at 00:00:01 every day");
		for(Role role : world.getOnlineRoles()){
			if (role == null) {
				continue;
			}
			RespModuleSet rms = new RespModuleSet();
			RoleSevenSignIn sevenSignIn = role.getSevenSignIn();
			RoleThirtySignIn thirtySignIn = role.getThirtySignIn();
			if (sevenSignIn == null || thirtySignIn == null) {
				continue;
			}
			//七日签到更新
			long yesterday = TimeUtils.nowLong() - 86_400_000;
			if (sevenSignIn.getLastsigntime() != 0 && !TimeUtils.isSameDay(sevenSignIn.getLastsigntime(), yesterday)) {
				sevenSignIn.resetSevenSignIn(role);
			}
			sevenSignIn.initRewardLst(role);
			sevenSignIn.sendSignInDataToClient(role,rms);
			//三十日签到更新
			thirtySignIn.initRewardLst(role);
			thirtySignIn.sendSignInDataToClient(rms);
			//体力购买次数更新
			role.getRoleStamina().ResetBuyTimes();
			role.getRoleStamina().sendToClient(null);
			//每日任务更新
			role.getDailyTaskAgent().refreshDailyTask(role);
			role.setSignIn(0);//微信每日签到清空
			//重置大转盘ID
			role.getTurntableBody().updateTurntableId(role); //更新大转盘的数据 00:00
			role.updateTurntableTurnSum(0);//设置转动次数为0次
			role.getTurntableBody().setSaveTime(TimeUtils.nowLong() + 1000);//设置保存时间为当前(确保不会在登陆的时候再操作一次)
			role.getTurntableBody().sendTurntableToClient(rms);//下发
			//重置所有在线用户的副本的重置次数
			if (role.getRoleCopys() != null && role.getRoleCopys().size() > 0) {
				for (RoleRelic roleRelic : role.getRoleCopys().values()) {
					roleRelic.resetRelicResetNum();
				}
			}
			role.sendRoleCopysToClient(rms, false);
			MessageSendUtil.sendModule(rms, role.getUserInfo());
		}
		RoleBlackMarketAgent.refreshDailyDiscounts(false);
		if (GameConfig.SEND_REALTIME_DATA) {
			RealtimeData.vipLevel(); // 全区等级分布
			RealtimeData.propStock(); // 道具库存
			RealtimeData.gameAlly(); // 工会数量及人数
			RealtimeData.levelLoss();// 流失玩家等级分布
			RealtimeData.levelOther(); // 其他道具物品等级分布
			RealtimeData.levelType();// 类型分布
		}
		LogManager.logPolling(); //日志每日轮询
	}
}
