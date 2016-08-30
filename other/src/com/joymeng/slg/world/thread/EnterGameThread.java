package com.joymeng.slg.world.thread;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.joymeng.Instances;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.log.GameLog;
import com.joymeng.slg.ServiceApp;
import com.joymeng.slg.domain.actvt.ActvtManager;
import com.joymeng.slg.domain.actvt.impl.NewServerBuff;
import com.joymeng.slg.domain.event.GameEvent;
import com.joymeng.slg.domain.evnt.EvntManager;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.mod.AbstractClientModule;
import com.joymeng.slg.net.mod.RespModuleSet;
import com.joymeng.slg.union.UnionBody;

public class EnterGameThread extends Thread implements Instances {
	ConcurrentLinkedQueue<Role> enterList = new ConcurrentLinkedQueue<Role>();
	
	public void enterIn(Role role) {
		if (role == null){
			return;
		}
		if (!enterList.contains(role)) {
			enterList.add(role);
		}
	}
	
	@Override
	public void run() {
		while (!ServiceApp.FREEZE) {
			try {
				long delay = 1000;
				if (enterList.size() > 0){
					delay = 0;
				}
				if (delay > 0){
					Thread.sleep(delay);
				}
				Role role = enterList.poll();
				if (role != null){
					if (role.isOnline()){
						RespModuleSet rms = role.sendToClient(2);
						UnionBody union = unionManager.search(role.getUnionId());
						if (union != null){
							union.sendToClient(rms);//发送联盟消息
							union.sendUnionTech(role,rms);//发送商店科技
							union.sendUnionStore(role,rms);//发送联盟商店
							union.sendUnionRecordsToClient(rms,union.getAllUnionRecords()); //发送联盟记录
							union.sendMemberTechProgress(role,rms); //发送个人对应联盟科技的捐赠按钮
						}
						role.sendGameConfigToClient(rms);//发送客户端所需要的游戏配置
						//role.sendViews(rms,false);//下发玩家视野
						role.sendRoleCopysToClient(rms, false);//下发副本信息
						role.sendArmyMobiBuff(rms);//发送必要的buff数据
						activityManager.sendShopLayoutToClient(rms,role);//下发玩家人民币商城
						//粮食不足的战力降低的模块
						AbstractClientModule module = new AbstractClientModule(){
							@Override
							public short getModuleType() {
								return NTC_DTCD_FIGHT_REDUCE_BY_NO_FOOD;
							}
						};
						module.add(role.getEffectAgent().checkHaveBuff(RoleCityAgent.buffName) ? 1 : 2);//1表示不正常,2表示恢复正常
						rms.addModule(module);
						AbstractClientModule nsbMod = NewServerBuff.getNewServerBuffMod(false);
						if (nsbMod != null) {
							rms.addModule(nsbMod);
						}
						role.handleEvent(GameEvent.TROOPS_SEND);
						role.handleEvent(GameEvent.UNION_FIGHT_CHANGE,true);
						role.getHonorAgent().honorWallTask(role);
						role.sendFrequentVariables();//下发用户战斗力
						role.sendCommanderInfo();//下发指挥官的部分数据
						chatMgr.firstSendMsgs(role);//下发各种聊天消息
						MessageSendUtil.sendModule(rms,role.getUserInfo());
						EvntManager.getInstance().Notify("roleEnter",String.valueOf(role.getId()));
						ActvtManager.getInstance().sendActvtTip(role.getId());
					}else{
						enterIn(role);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				GameLog.error(e);
			}	
		}
	}
}
