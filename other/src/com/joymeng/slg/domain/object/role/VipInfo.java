package com.joymeng.slg.domain.object.role;

import java.util.List;

import com.joymeng.Instances;
import com.joymeng.common.util.JsonUtil;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.common.util.StringUtils;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.log.GameLog;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.domain.event.GameEvent;
import com.joymeng.slg.domain.object.role.data.Vipbufftype;
import com.joymeng.slg.domain.object.role.data.Viplevel;
import com.joymeng.slg.domain.object.task.ConditionType;
import com.joymeng.slg.domain.object.task.TaskEventDelay;
import com.joymeng.slg.domain.timer.TimerLast;
import com.joymeng.slg.domain.timer.TimerLastType;
import com.joymeng.slg.domain.timer.TimerOver;
import com.joymeng.slg.net.mod.AbstractClientModule;
import com.joymeng.slg.net.mod.RespModuleSet;

public class VipInfo implements TimerOver, Instances {
	private long uid;
	private byte vipLevel;
	private int vipExp;
	private boolean isActive;
	private TimerLast timer;

	public VipInfo(){
		vipLevel = 1;
		vipExp = 0;
		isActive = false;
	}

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public byte getVipLevel() {
		return vipLevel;
	}

	public void setVipLevel(byte vipLevel) {
		this.vipLevel = vipLevel;
	}

	public int getVipExp() {
		return vipExp;
	}

	public void setVipExp(int vipExp) {
		this.vipExp = vipExp;
	}

	public TimerLast getTimer() {
		return timer;
	}

	public void setTimer(TimerLast timer) {
		this.timer = timer;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public boolean isActive() {
		return isActive;
	}

	public void deserialize(String data) {
		if (StringUtils.isNull(data)) {
			return;
		}
		String[] strText = data.split(";");
		vipLevel = Byte.parseByte(strText[0]);
		vipExp = Integer.parseInt(strText[1]);
		if (strText.length > 2) {
			TimerLast timer = JsonUtil.JsonToObject(strText[2], TimerLast.class);
			if (!timer.over()) {
				timer.registTimeOver(this);
				this.timer = timer;
				isActive = true;
			} else {
				isActive = false;
			}
		}
		
	}

	public String serialize() {
		String str = vipLevel + ";";
		str += vipExp;
		if (isActive) {
			str += ";" + JsonUtil.ObjectToJsonString(timer);
		}
		return str;
	}
	
	public boolean levelUp() {
		Viplevel vip = dataManager.serach(Viplevel.class, (int) vipLevel + 1);
		if (vip == null) {
			GameLog.error("dont read Viplevel of viplevel = " + (vipLevel + 1));
			return true;
		}
		if (vipExp >= vip.getExp()) {
			vipLevel += 1;
			vipExp -= vip.getExp();
			levelUp();
			return true;
		}
		return false;
	}

	public void addExp(Role role, int value) {
		vipExp += value;
		boolean bSuc = levelUp();
		if (bSuc) {
			role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.COND_VIP, vipLevel);
		}
		// vip等级提升,更新vipbuff内容
		if (bSuc && isActive) {
			// 更新用户体力最大值
			role.getRoleStamina().updateBuyMaxTimes();
			role.getEffectAgent().removeVipBuffs();
			long timerLast = timer.getStart() + timer.getLast() - TimeUtils.nowLong() / 1000;
			if (timerLast > 0) {
				addVipBuff(role, timerLast);
			}
		}
	}

	public void sendVipToClient(RespModuleSet rms) {
		AbstractClientModule module = new AbstractClientModule() {
			@Override
			public short getModuleType() {
				return NTC_DTCD_ROLE_VIP_INFO;
			}
		};
		module.add(vipLevel);// byte
		module.add(vipExp);// int
		module.add(isActive ? (byte) 1 : (byte) 0);// byte
		if (isActive) {
			timer.sendToClient(module.getParams());// 倒计时
		}
		rms.addModule(module);
	}
	
	public void serialize(JoyBuffer out) {
		out.put(vipLevel); // byte Vip 等级
		byte vipState = isActive == true ? (byte) 1 : (byte) 0;
		out.put(vipState); // byte Vip 状态 1:激活 0:表示未激活
	}
	
	/**
	 * 激活Vip
	 * 
	 * @param lastTime
	 */
	public boolean ActiveVip(Role role,long lastTime){
		if(role == null){
			return false;
		}
		if (isActive) {
			timer.setLast(timer.getLast() + lastTime);
			//add vip buff
			if(vipLevel > 0){
				role.getEffectAgent().updateVipBuffTime(lastTime);
			}
		} else {
			isActive = true;
			timer = new TimerLast(TimeUtils.nowLong() / 1000, lastTime, TimerLastType.TIME_VIP);
			timer.registTimeOver(this);
			// add vip buff
			if(vipLevel > 0){
				addVipBuff(role,lastTime);
			}
		}
		RespModuleSet rms = new RespModuleSet();
		sendVipToClient(rms);
		MessageSendUtil.sendModule(rms, role.getUserInfo());
		//任务事件
		role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.C_VIP_ACTIVE, 0);
		//更新用户体力最大值
		role.getRoleStamina().updateBuyMaxTimes();
		return true;
	}

	public void tick(Role role,long now) {
		if (timer != null && timer.over(now)) {
			timer.die();
			timer = null;
			//更新用户体力最大值
			role.getRoleStamina().updateBuyMaxTimes();
			RespModuleSet rms = new RespModuleSet();
			role.sendRoleToClient(rms);
			MessageSendUtil.sendModule(rms, role.getUserInfo());
		}
	}

	@Override
	public void finish() {
		isActive = false;
		Role role = world.getOnlineRole(uid);
		if (role != null) {
			role.getEffectAgent().removeVipBuffs();
			RespModuleSet rms = new RespModuleSet();
			role.getVipInfo().sendVipToClient(rms);// 下发VIP消息
			MessageSendUtil.sendModule(rms, role.getUserInfo());
		}
	}

	private void addVipBuff(Role role, long lastTime) {
		if (role != null) {
			Viplevel vl = dataManager.serach(Viplevel.class, (int) vipLevel);
			if (vl == null) {
				GameLog.error("find vip level buff data error where level= " + vipLevel);
				return;
			}
			List<Vipbufftype> vTypeLst = dataManager.serachList(Vipbufftype.class);
			if (vTypeLst.size() == 0) {
				return;
			}
			for (int i = 0; i < vTypeLst.size(); i++) {
				Vipbufftype vbt = vTypeLst.get(i);
				float value = Float.MIN_VALUE;
				switch (vbt.getVipBufftype()) {
				case "FoodProd":
					value = vl.getFoodProd();
					break;
				case "MetalProd":
					value = vl.getMetalProd();
					break;
				case "BuildSpeed":
					value = vl.getBuildSpeed();
					break;
				case "ResSpeed":
					value = vl.getResSpeed();
					break;
				case "phyBuyNumber":// 体力购买次数增加
					value = vl.getPhybuynumber();
					break;
				case "SProdLimit":
					value = vl.getSProdLimit();
					break;
				case "ImpMobi":
					value = vl.getImpMobi();
					break;
				case "ImpVitSp":
					value = vl.getImpVitSp();
					break;
				case "ImpColl":
					value = vl.getImpColl();
					break;
				case "ImpOilProd":
					value = vl.getImpOilProd();
					break;
				case "ImpAtk":
					value = vl.getImpAtk();
					break;
				case "ImpDef":
					value = vl.getImpDef();
					break;
				case "ReduBearDMGAll":
					value = vl.getReduBearDMGAll();
					break;
				case "ImpAlloyProd":
					value = vl.getImpAlloyProd();
					break;
				case "TroopsLimit":
					value = vl.getTroopsLimit();
					break;
				case "SoldLimit":
					value = vl.getSoldLimit();
					break;
				case "ReduProdTime":
					value = vl.getReduProdTime();
					break;
				case "FreeBuildSpeed":
					value = vl.getFreeBuildSpeed();
					break;
				case "FreeResearchSpeed":
					value = vl.getFreeResearchSpeed();
					break;
				case "ReduHospTime":
					value = vl.getReduHospTime();
					break;
				case "ReduRepaTime":
					value = vl.getReduRepaTime();
					break;
				case "ReduHospRes":
					value = vl.getReduHospRes();
					break;
				case "ReduRepaRes":
					value = vl.getReduRepaRes();
					break;
				case "AddHospCapa":
					value = vl.getAddHospCapa();
					break;
				case "AddRepaCapa":
					value = vl.getAddRepaCapa();
					break;
				default:
					break;
				}
				if (value == 0 || value == Float.MIN_VALUE) {
					continue;
				}
				role.getEffectAgent().addVipBuffs(role,vbt.getBuffID(),value,lastTime);
			}
			role.handleEvent(GameEvent.ROLE_RES_BUFF_CHANGE);
			role.handleEvent(GameEvent.TROOPS_SEND);
		}
	}
}
