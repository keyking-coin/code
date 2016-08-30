package com.joymeng.slg.domain.object.role;

import com.joymeng.Const;
import com.joymeng.Instances;
import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.JsonUtil;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.dao.DaoData;
import com.joymeng.slg.dao.SqlData;
import com.joymeng.slg.domain.chat.ChatSystemContent;
import com.joymeng.slg.domain.chat.MsgTextColorType;
import com.joymeng.slg.world.GameConfig;

/**
 * 防沉迷类
 * @author tanyong
 *
 */
public class RoleAntiAddiction implements Instances{
	long preTime = 0;//临时时间
	long online;//已在线时间
	long outTime;//被踢出的时间或者是下线的时间
	long tipTime;//提示时间
	int  userType = 1;
	private static long THREED_TIME_LIMIT = 3 * Const.HOUR;//3小时时限
	private static long FIVE_TIME_LIMIT   = 5 * Const.HOUR;//5小时时限
	private static long TIP_TIME_LIMIT    = 15 * Const.MINUTE;//15分钟提醒
	
	
	public void setUserType(int userType) {
		this.userType = userType;
	}

	public void tick(Role role , long now){
		if (!GameConfig.ANTI_ADDICTION_FLAG /*|| !role.getChannelId().equals(ChannelKey.CHANNEL_ID_360.getKey())*/ || userType == 1){
			return;
		}
		if (preTime > 0){
			online += now - preTime;
		}
		if (online >= FIVE_TIME_LIMIT){//需要踢出玩家
			MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_ANTI_LOGIN_TIP);
			outTime = now;
			role.kick();
		}else if (online >= THREED_TIME_LIMIT){//提示
			if (tipTime == 0 || now > tipTime + TIP_TIME_LIMIT){
				String tipMsg        = null;
				ChatSystemContent csc = null;
				if (online >= THREED_TIME_LIMIT + Const.HOUR){
					MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_ANTI_RUN_TIP,4);
					csc = new ChatSystemContent(I18nGreeting.MSG_ANTI_RUN_TIP, String.valueOf(4));
				}else{
					MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_ANTI_RUN_TIP,3);
					csc = new ChatSystemContent(I18nGreeting.MSG_ANTI_RUN_TIP, String.valueOf(3));
				}
				tipMsg = JsonUtil.ObjectToJsonString(csc);
				chatMgr.generateSystemMsgsToRole(tipMsg,MsgTextColorType.COLOR_RED,role);
				tipTime = now;
			}
		}
		preTime = now;
	}
	
	public boolean couldLogin(){
		if (outTime > 0 && TimeUtils.isSameDay(outTime)){
			long now = TimeUtils.nowLong();
			if (now > outTime + FIVE_TIME_LIMIT){
				return true;
			}
		}
		return true;
	}
	
	public void serialize(SqlData data){
		JoyBuffer buffer = JoyBuffer.allocate(1024);
		buffer.putInt(userType);
		buffer.putLong(online);
		buffer.putLong(outTime);
		buffer.putLong(tipTime);
		data.put(DaoData.RED_ALERT_ROLE_ANTI,buffer.arrayToPosition());
	}
	
	public void deserialize(byte[] datas){
		if(datas == null){
			return;
		}
		JoyBuffer buffer = JoyBuffer.wrap(datas);
		userType = buffer.getInt();
		online   = buffer.getLong();
		outTime  = buffer.getLong();
		tipTime  = buffer.getLong();
	}
}
