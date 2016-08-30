package com.joymeng.slg.domain.object.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joymeng.Instances;
import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.log.GameLog;
import com.joymeng.log.LogManager;
import com.joymeng.log.NewLogManager;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.dao.DaoData;
import com.joymeng.slg.dao.SqlData;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.task.data.Honorwall;
import com.joymeng.slg.domain.object.task.data.RoleHonor;
import com.joymeng.slg.domain.object.task.data.Task2;
import com.joymeng.slg.net.mod.AbstractClientModule;
import com.joymeng.slg.net.mod.RespModuleSet;

public class HonorMissionAgent implements Instances {

	Map<String, RoleHonor> falgMap = new HashMap<>(); // 数据库荣誉榜信息读取存为Map

	public HonorMissionAgent() {

	}

	public Map<String, RoleHonor> getFalgMap() {
		return falgMap;
	}

	public void setFalgMap(Map<String, RoleHonor> falgMap) {
		this.falgMap = falgMap;
	}
    
	/*
	 * 获取玩家勋章收集个数
	 */
	
	public int getMedalCount(boolean isAll) {
		int count = 0;
		if (isAll) {
			count = falgMap.values().size();
		} else {
			for (RoleHonor rh : falgMap.values()) {
				if (rh.getStarNum() == 5) {
					count++;
				}
			}
		}
		return count;
	}
	
	public List<String> getMedalList(){
		List<String> medalList = new ArrayList<String>();
		for (RoleHonor rh : falgMap.values()) {
			if (rh.getStarNum() == 5) {
				medalList.add(rh.getId());
			}
		}
		return medalList;
	}
	
	/*
	 * 荣誉任务奖励
	 */
	public boolean getHonorReward(Role role, String honorMId, int count) {
		
		Honorwall honor = dataManager.serach(Honorwall.class, honorMId); // 读取固化表内容
		List<String> hreward = honor.getRewardNum();
		if (hreward == null || hreward.size() == 0) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_MISSION_REWARD_NOT_EXSIT, honorMId);
			return false;
		}
		for (RoleHonor rh : falgMap.values()) {
			if (rh.getId().equals(honorMId)) {
				rh.setReNum(rh.getReNum() + 1);  	// 改变金币奖励领取的次数
				break;
			}
		}
		honorWallTask(role);
		String reward = hreward.get(count-1);
		String[] rw = reward.split(":");
		int num = Integer.parseInt(rw[1]);
		if (num > 0) {
			role.addRoleMoney(num);
			RespModuleSet rms = new RespModuleSet();
			role.sendRoleToClient(rms);
			MessageSendUtil.sendModule(rms, role.getUserInfo());
			LogManager.goldOutputLog(role, num, "getHonorReward");
			try {
				NewLogManager.baseEventLog(role, "get_task_reward",honorMId,num);
			} catch (Exception e) {
				GameLog.info("埋点错误");
			}
			return true;
		} else {
			GameLog.error("奖励具体内容错误，或不存在~");
		}
		return false;
	}

	/*
	 * 荣誉墙任务展示
	 */
	public boolean honorWallTask(Role role) {
		RespModuleSet rms = new RespModuleSet();
		sendHonorToClient(rms);
		MessageSendUtil.sendModule(rms, role.getUserInfo());
		return true;
	}

	/*
	 * 发送荣誉墙任务详情到客户端
	 */

	public void sendHonorToClient(RespModuleSet rms) {
		AbstractClientModule module = new AbstractClientModule() {
			@Override
			public short getModuleType() {
				return NTC_DTCD_ROLE_HONOR_WALL; // 返回给客户端协议号
			}
		};
		module.add(falgMap.size()); // 需要发送的内容：荣誉任务Id 子任务完成个数 领取了金币奖励的个数等
		for (RoleHonor rh : falgMap.values()) {
			module.add(rh.getId());
			module.add(rh.getStarNum());
			module.add(rh.getReNum());
			module.add(rh.getSchedule());
		}
		rms.addModule(module);
	}

	// 重写代码，从数据库读取数据，然后发送给客户端

	public void loadHonorFromDB(SqlData data) {

		String id = data.getString(DaoData.RED_ALERT_MISSION_HONORID);
		long uid = data.getLong(DaoData.RED_ALERT_GENERAL_UID);
		int starNum = data.getInt(DaoData.RED_ALERT_MISSION_TASKNUM);
		int reNum = data.getInt(DaoData.RED_ALERT_MISSION_STARNUM);
		int schedule = data.getInt(DaoData.RED_ALERT_MISSION_SCHEDULE);
		RoleHonor honor = new RoleHonor();
		honor.setId(id);
		honor.setUid(uid);
		honor.setStarNum(starNum);
		honor.setReNum(reNum);
		honor.setSchedule(schedule);
		falgMap.put(id, honor);

	}
	
	public void sendMsTohonor(String id, byte awardStatus, int schedule) { //荣誉任务Id 状态 进度
       Task2 task=  dataManager.serach(Task2.class, id);
       String jumpo = task.getJumpto();
       Honorwall hwall =dataManager.serach(Honorwall.class, jumpo);
       List<String> honorLink = hwall.getHonorLink();
       List<String> list=hwall.getStagetype();
       String[] str = list.get(0).split(":");
		if (awardStatus == 1) { // 任务完成，状态-1,累计进度存为0，星星数量加1
			for (RoleHonor rh : falgMap.values()) {
				if (rh.getId().equals(jumpo)) {
					if(str[0].equals("0")){
						rh.setSchedule(0);	
					}else{
						rh.setSchedule(schedule);
					}
					rh.setStarNum(honorLink.indexOf(task.getId())+1);
					//TODO 发送玩家荣誉任务进度更新
					Role role = world.getOnlineRole(rh.getUid());
					if(role != null){
						role.sendCommanderInfo();
						honorWallTask(role);
					}
					break;
				}
			}
		} else {
			for (RoleHonor rh : falgMap.values()) { // 任务未完成，状态-0，进度为改变后的进度，星星数量不变
				if (rh.getId().equals(jumpo)) {
					rh.setSchedule(schedule);
					rh.setStarNum(honorLink.indexOf(task.getId()));
					break;
				}
			}
		}
	}

	public void initHonors(Role role) {
		List<Honorwall> list = dataManager.serachList(Honorwall.class); // 读取固化表内容,荣誉任务列表
		if (list == null) {
			GameLog.error(" have no data~");
		} else {
			for (int i = 0 ; i < list.size() ; i++){
				Honorwall hw = list.get(i);
				List<String> str = hw.getStagetype();
				String[] s = str.get(0).split(":");
				RoleHonor honor = new RoleHonor();
				honor.setId(hw.getId());
				honor.setUid(role.getId());
				honor.setStarNum(0);
				honor.setReNum(0);
				if (s[1].equals("0")) {
					honor.setSchedule(0);
				} else {
					honor.setSchedule(1);
				}
				falgMap.put(hw.getId(), honor);
			}
		}
	}
	
	public void serialize(SqlData data){
		JoyBuffer out = JoyBuffer.allocate(4096);
		out.putInt(falgMap.size());
		for(RoleHonor honor : falgMap.values()){
			out.putPrefixedString(honor.getId(),JoyBuffer.STRING_TYPE_SHORT);
			out.putLong(honor.getUid());
			out.putInt(honor.getStarNum());
			out.putInt(honor.getReNum());
			out.putInt(honor.getSchedule());
		}
		data.put(DaoData.RED_ALERT_ROEL_HONORS, out.arrayToPosition());
	}
	
	public void deserialize(Object data){
		if(data == null){
			return;
		}
		JoyBuffer buffer = JoyBuffer.wrap((byte[])data);
		int size = buffer.getInt();
		for(int i = 0; i < size; i++){
			String id = buffer.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
			long uid = buffer.getLong();
			int starNum = buffer.getInt();
			int reNum = buffer.getInt();
			int schedule = buffer.getInt();
			RoleHonor honor = new RoleHonor();
			honor.setId(id);
			honor.setUid(uid);
			honor.setStarNum(starNum);
			honor.setReNum(reNum);
			honor.setSchedule(schedule);
			falgMap.put(id, honor);
		}
	}
	
}
