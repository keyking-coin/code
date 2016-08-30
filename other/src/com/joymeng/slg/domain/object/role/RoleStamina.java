package com.joymeng.slg.domain.object.role;

import com.joymeng.Const;
import com.joymeng.Instances;
import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.common.util.StringUtils;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.log.GameLog;
import com.joymeng.slg.domain.data.SearchFilter;
import com.joymeng.slg.domain.object.role.data.Viplevel;
import com.joymeng.slg.domain.shop.data.Shop;
import com.joymeng.slg.net.mod.AbstractClientModule;
import com.joymeng.slg.net.mod.RespModuleSet;
import com.joymeng.slg.world.GameConfig;

public class RoleStamina implements Instances{
	int curStamina;// 当前体力值
	int buyTimes;// 已购买次数
	int buyMaxTimes;// 当前体力最大购买次数
	long refreshBuyTime;// 体力购买次数刷新时间
	boolean isCanBuy = true;
	long staminaResumeTime;//上一次体力恢复时间
	long staminaResumeSpeed;// 体力恢复速度
	float speedBuff = 0;
	Role role;

	public RoleStamina(){
		
	}
	
	public void initStamina(Role role){
		this.role = role;
		buyTimes = 0;
		updateBuyMaxTimes();
		curStamina = Const.MAXSTAMINA;
		refreshBuyTime = TimeUtils.nowLong();
		staminaResumeTime = TimeUtils.nowLong();
		staminaResumeSpeed = GameConfig.STAMINA_RESUME_TIME * Const.MINUTE;
	}
	
	/**
	 * 更新购买的最大次数 
	 * @param role
	 */
	public void updateBuyMaxTimes() {
		int extraTimes = 0;
		if (role.getVipInfo().getVipLevel() == 0) {
			extraTimes = 0;
		} else {
			Viplevel viplevel = dataManager.serach(Viplevel.class, (int) role.getVipInfo().getVipLevel());
			if (viplevel == null) {
				GameLog.error("read viplevel is fail");
				return;
			}
			extraTimes = viplevel.getPhybuynumber();
		}
		buyMaxTimes = extraTimes + Const.INITMAXBUYTIMES;
		if(buyTimes < buyMaxTimes){
			isCanBuy = true;
		}
		sendToClient(null);
	}
	
	public short getCurStamina() {
		return (short) curStamina;
	}
	
	public void setCurStamina(int curStamina) {
		this.curStamina = curStamina;
	}
	
	public float getSpeedBuff(){
		return speedBuff;
	}
	
	public int getBuyTimes() {
		return buyTimes;
	}

	public void setBuyTimes(int buyTimes) {
		this.buyTimes = buyTimes;
	}

	public int getBuyMaxTimes() {
		return buyMaxTimes;
	}

	public void setBuyMaxTimes(int buyMaxTimes) {
		this.buyMaxTimes = buyMaxTimes;
	}

	public boolean isCanBuy() {
		return isCanBuy;
	}

	public void setCanBuy(boolean isCanBuy) {
		this.isCanBuy = isCanBuy;
	}
	
	public void updateStaminaSpeed(boolean isRemove, float value){
		if(!isRemove){
			speedBuff += value;
		}else{
			speedBuff -= value;
		}
		long staticSpeed = GameConfig.STAMINA_RESUME_TIME * Const.MINUTE;
		staminaResumeSpeed = (long) (staticSpeed * ((1.0f - speedBuff) <= 0 ? 0.01f : (1.0f - speedBuff)));
		sendToClient(null);
	}
	
	public void updateBuyTimes(int num){
		buyMaxTimes += num;
		if(buyMaxTimes > 0 && buyMaxTimes > buyTimes){
			isCanBuy = true;
		}
		sendToClient(null);
	}
	
	public void ResetBuyTimes(){
		if(!TimeUtils.isSameDay(refreshBuyTime, TimeUtils.nowLong())){
			refreshBuyTime = TimeUtils.nowLong();
			buyTimes = 0;
		}
		updateBuyMaxTimes();
		sendToClient(null);
	}

	public boolean buyStamina(Role role){
		Shop good = dataManager.serach(Shop.class, new SearchFilter<Shop>() {
			@Override
			public boolean filter(Shop data) {
				return data.getItemid().equals("add_physicalStrength_50");
			}
		});
		if (good == null) {
			GameLog.error("read shop is fail from ps:add_physicalStringth_50");
			return false;
		}
		if (role.getRoleStamina().getCurStamina() >= Const.MAXSTAMINA) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ROLE_STAMINA_IS_MAX);
			return false;
		}
//		if (Const.BUY_STAMINA_NUM + role.getRoleStamina().getCurStamina() > Const.MAXSTAMINA) {
//			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_BUG_STAMINA_NUM_BEYOND_MAX);
//			return false;
//		}
		if (role.getMoney() < good.getNormalPrice()) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ROLE_NO_MONEY,good.getNormalPrice());
			return false;
		}
		if(!role.getVipInfo().isActive() && buyTimes >= Const.INITMAXBUYTIMES){
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_HAVE_NO_BUY_STAMINA_TIMES);
			return false;
		}
		if(!isCanBuy || buyTimes >= buyMaxTimes){
			return false;
		}
		buyTimes ++;
		if(buyTimes >= buyMaxTimes){
			isCanBuy = false;
		}
		updateCurStamina(Const.BUY_STAMINA_NUM);
		if (!role.redRoleMoney(good.getNormalPrice())) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ROLE_NO_MONEY,good.getNormalPrice());
			return false;
		}
		RespModuleSet rms = new RespModuleSet();
		role.sendRoleToClient(rms);
		MessageSendUtil.sendModule(rms,role.getUserInfo());
		return true;
	}
	
	public synchronized void updateCurStamina(int value){
		curStamina += value;
		curStamina = Math.min(Const.MAXSTAMINA,curStamina);
		curStamina = Math.max(0,curStamina);
		//TODO 预备取消
		if (role.isOnline()){
			RespModuleSet rms = new RespModuleSet();
			sendToClient(rms);
			MessageSendUtil.sendModule(rms, role.getUserInfo());
		}
	}
	
	private synchronized void stamina_logic(long now){
		if (curStamina >= Const.MAXSTAMINA){
			staminaResumeTime = now;
			return;
		}
		if (staminaResumeTime == 0){//开始恢复
			staminaResumeTime = now;
		}
		boolean isAdd = false;
		while (staminaResumeTime + staminaResumeSpeed < now){
			if (curStamina >= Const.MAXSTAMINA){
				staminaResumeTime = now;
				break;
			}
			curStamina ++;
			isAdd = true;
			staminaResumeTime += staminaResumeSpeed;
		}
		if(isAdd){
			sendToClient(null);
		}
	}
	
	public void sendToClient(RespModuleSet rms){
		AbstractClientModule module = new AbstractClientModule() {
			@Override
			public short getModuleType() {
				return NTC_DTCD_ROLE_STAMINA;
			}
		};
		module.add(curStamina); //当前体力值  	int
		module.add(buyTimes);   //当天已购买次数   int
		module.add(buyMaxTimes);//当前最大购买次数 int
		module.add((byte)(isCanBuy ? 1 : 0));//当前是否可购买 byte
		module.add(staminaResumeSpeed);//当前体力恢复速度, long
		if (rms == null) {
			rms = new RespModuleSet();
			rms.addModule(module);
			MessageSendUtil.sendModule(rms, role.getUserInfo());
		} else {
			rms.addModule(module);
		}
	}
	
	public void tick(long time){
		stamina_logic(time);
	}

	public void deserialize(Role role, byte level, String str){
		this.role = role;
		if (StringUtils.isNull(str)){
			return;
		}
		int index=1;
		String[] strText = str.split(":");
		curStamina = Short.parseShort(strText[index++]);
		buyTimes = Short.parseShort(strText[index++]);
		staminaResumeTime = Long.parseLong(strText[index++]);
		if(strText.length > index){
			staminaResumeSpeed = Long.parseLong(strText[index++]);
		}else{
			staminaResumeSpeed = GameConfig.STAMINA_RESUME_TIME * Const.MINUTE;
		}
		if(strText.length > index){
			buyMaxTimes = Integer.parseInt(strText[index++]);
			refreshBuyTime = Long.parseLong(strText[index]);
			ResetBuyTimes();
		}
		//计算离线时间的体力增长和新的体力恢复速度
		if(curStamina < Const.MAXSTAMINA){
			long leave = TimeUtils.nowLong() - staminaResumeTime;
			int num = (int) (leave/staminaResumeSpeed);
			if(num > 0){
				staminaResumeTime = TimeUtils.nowLong();
				curStamina = (curStamina+num) > Const.MAXSTAMINA ? Const.MAXSTAMINA : (curStamina+num);
			}
		}
	}
	
	public String serialize(){
		String str="Stamina:";
		str += curStamina + ":" + buyTimes + ":" + staminaResumeTime + ":" + staminaResumeSpeed + ":" + buyMaxTimes + ":" + refreshBuyTime;
		return str;
	}
}
