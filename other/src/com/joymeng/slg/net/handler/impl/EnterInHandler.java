package com.joymeng.slg.net.handler.impl;
import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.list.RecordServers;
import com.joymeng.log.GameLog;
import com.joymeng.log.LogManager;
import com.joymeng.log.NewLogManager;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.ServiceApp;
import com.joymeng.slg.domain.event.GameEvent;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.rank.RoleRank;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.role.channel.ChannelKey;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.mod.RespModuleSet;
import com.joymeng.slg.net.resp.CommunicateResp;
import com.joymeng.slg.world.GameConfig;
import com.joymeng.slg.world.thread.OnlineRunnable;

public class EnterInHandler extends ServiceHandler {
	
	@Override
	public void _deserialize(JoyBuffer in,ParametersEntity params) {
		String ip = in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
		params.put(ip);
		String apkVersion      =  in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
		String codeVersion     =  in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
		String chinnelId       =  in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
		int userType           =  in.getInt();//1成年用户,2未成年用户,3未知用户
		String country         =  in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
		String language        =  in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
		String uuid            =  in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
		String uuidRegisTime   =  in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
		String model           =  in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
		String version         =  in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
		String resolution      =  in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
		int memory             =  in.getInt();
		String registrationId  =  in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
		params.put(apkVersion);
		params.put(codeVersion);
		params.put(chinnelId);
		params.put(userType);	
		params.put(country);
		params.put(language);
		params.put(uuid);
		params.put(uuidRegisTime);
		params.put(model);
		params.put(version);
		params.put(resolution);
		params.put(memory);
		params.put(registrationId);
	}
	
	@Override
	public JoyProtocol handle(UserInfo info,ParametersEntity params) throws Exception{
		CommunicateResp resp = newResp(info);
		//String apkVersion  = params.get(1);
		String codeVersion = params.get(2);
		String chinnelId   = params.get(3);
		int userType       = params.get(4);
		String country     = params.get(5);
		String language    = params.get(6);
		String uuid        = params.get(7);
		String uuidRegisTime   = params.get(8);
		String model = params.get(9);
		String version = params.get(10);
		String resolution = params.get(11);
		int memory = params.get(12);
		String registrationId = params.get(13);
		Role role = world.getRole(info.getUid());
		if (role == null){
			role = world.createNewRole(info.getUid(),chinnelId, country,language,uuid, uuidRegisTime,model,version,resolution,memory,registrationId);
			role.getRoleAnti().setUserType(userType);
			RecordServers.sendRecord(info.getUid(), String.valueOf(ServiceApp.instanceId));
		}
		role.setChannelId(chinnelId);
		role.setCountry(country);
		role.setLanguage(language);
		role.setUuid(uuid);
		role.setUuidRegisTime(uuidRegisTime);
		role.setModel(model);
		role.setVersion(version);
		role.setResolution(resolution);
		role.setMemory(memory);
		role.setRegistrationId(registrationId);
		for (RoleCityAgent city : role.getCityAgents()) {
			RoleCityAgent agent = role.getCity(city.getId());
			long time = TimeUtils.nowLong();
			if (TimeUtils.isSameDay(agent.getResSyncTime(), time)) {
				role.setSignIn(0);
			}
		}
		if (!codeVersion.equals(GameConfig.CODE_VERSION)){
			resp.add(I18nGreeting.MSG_VERSION_ERROR);
			resp.fail();
			return resp;
		}
		if (chinnelId.equals(ChannelKey.CHANNEL_ID_360.getKey())){
			if (!role.getRoleAnti().couldLogin()){
				resp.add(I18nGreeting.MSG_ANTI_LOGIN_TIP);
				resp.fail();
				return resp;
			}
		}
		String ip = params.get(0);
		role.setLastLoginIp(ip);
		if(!role.isOnline()){
			OnlineRunnable.recordTime(role,(byte)1);
		}
		role.setUserInfo(info);//设置玩家在线
		RespModuleSet rms = role.sendToClient(1);
		role.handleEvent(GameEvent.ROLE_HEART);
		RoleRank roleRank = rankManager.getRoleRankByRoleUid(role.getId());
		if (roleRank == null) {
			role.initRoleRank();
		}
		MessageSendUtil.sendModule(rms,info);
		role.setLastLoginTime(TimeUtils.nowLong());
		taskPool.enterThread.enterIn(role);
		GameLog.info(info.getUid() + " login at " + TimeUtils.chDate(TimeUtils.nowLong()));
		try {
			NewLogManager.baseEventLog(role, "game_start");
		} catch (Exception e) {
			GameLog.info("埋点错误");
		}
		LogManager.loginLog(role);
		return resp;
	}
}
