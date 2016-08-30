package com.joymeng.slg.union.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.joymeng.Instances;
import com.joymeng.common.util.JsonUtil;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.common.util.StringUtils;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.log.GameLog;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.dao.DaoData;
import com.joymeng.slg.dao.SqlData;
import com.joymeng.slg.domain.data.SearchFilter;
import com.joymeng.slg.domain.event.GameEvent;
import com.joymeng.slg.domain.map.MapUtil;
import com.joymeng.slg.domain.map.impl.still.role.MapCity;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.role.RoleIcon;
import com.joymeng.slg.domain.timer.TimerLast;
import com.joymeng.slg.domain.timer.TimerOver;
import com.joymeng.slg.net.SerializeEntity;
import com.joymeng.slg.net.mod.AbstractClientModule;
import com.joymeng.slg.net.mod.RespModuleSet;
import com.joymeng.slg.net.resp.CommunicateResp;
import com.joymeng.slg.union.UnionBody;
import com.joymeng.slg.union.data.Alliancemembers;
import com.joymeng.slg.union.data.UnionPostType;
/**
 * 联盟成员
 * @author tanyong
 *
 */
public class UnionMember implements Comparable<UnionMember>, DaoData, Instances, SerializeEntity, TimerOver {
	long uid;// 玩家编号
	long unionId;//联盟id
	String name;//名称
	RoleIcon icon = new RoleIcon();//头像
	int fight;//战斗力
	int level;//等级
	long score;//个人贡献
	long scoreDaily;//每日贡献度
	long scoreWeekly;//每周贡献度
	long scoreRecord;//历史贡献度
	long donateDaily;//每日捐献
	long donateWeekly;//每周捐献
	long donateRecord;//历史捐献
	String joinTime;//计入时间
	String allianceKey;//官职
	List<String> permissions = new ArrayList<String>();//权限编号列表
	boolean deleteFlag = false;
	Map<String, UnionMemberTechProgress> techProgresses = new HashMap<String, UnionMemberTechProgress>();
	int donateType = 0;//0:正常状态 1:倒计时
	TimerLast timer;// 捐赠倒计时
	boolean savIng = false;
	
	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}
	
	public long getUnionId() {
		return unionId;
	}

	public TimerLast getTimer() {
		return timer;
	}

	public int getDonateType() {
		return donateType;
	}

	public void setDonateType(int donateType) {
		this.donateType = donateType;
	}

	public void setTimer(TimerLast timer) {
		this.timer = timer;
	}

	public void setUnionId(long unionId) {
		this.unionId = unionId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getFight() {
		return fight;
	}

	public void setFight(int fight) {
		this.fight = fight;
	}

	public long getScore() {
		return score;
	}

	public void setScore(long score) {
		this.score = score;
	}

	public long getScoreDaily() {
		return scoreDaily;
	}

	public Map<String, UnionMemberTechProgress> getTechProgresses() {
		return techProgresses;
	}

	public void setTechProgresses(
			Map<String, UnionMemberTechProgress> techProgresses) {
		this.techProgresses = techProgresses;
	}

	public void setScoreDaily(long scoreDaily) {
		this.scoreDaily = scoreDaily;
	}

	public long getScoreWeekly() {
		return scoreWeekly;
	}

	public void setScoreWeekly(long scoreWeekly) {
		this.scoreWeekly = scoreWeekly;
	}

	public long getScoreRecord() {
		return scoreRecord;
	}

	public void setScoreRecord(long scoreRecord) {
		this.scoreRecord = scoreRecord;
		//更新用户的称谓
		Alliancemembers alliancemember = dataManager.serach(Alliancemembers.class,allianceKey);
		if (alliancemember == null) {
			GameLog.error("alliancemember base date read fail");
			return;
		}
		if (alliancemember.getType() == 2) {
			updateUnionMemberTitle();
		}
	}

	/**
	 * 更新用户的称谓
	 */
	public void updateUnionMemberTitle() {
		final long allPerContr = getScoreRecord();
		Alliancemembers alliancemember = dataManager.serach(Alliancemembers.class, new SearchFilter<Alliancemembers>() {
			@Override
			public boolean filter(Alliancemembers data) {
				long persContrMin = Integer.valueOf(data.getPersContr().get(0));
				long persContrMax = Integer.valueOf(data.getPersContr().get(1));
				return allPerContr >= persContrMin && allPerContr <= persContrMax && data.getType() == 2;
			}
		});
		if (alliancemember == null) {
			return;
		}
		setAllianceKey(String.valueOf(alliancemember.getRank()));//设置用户军衔
		setPermissions(alliancemember.getJurisdiction());//设置用户的权限
	}

	public long getDonateDaily() {
		return donateDaily;
	}

	public void setDonateDaily(long donateDaily) {
		this.donateDaily = donateDaily;
	}

	public long getDonateWeekly() {
		return donateWeekly;
	}

	public void setDonateWeekly(long donateWeekly) {
		this.donateWeekly = donateWeekly;
	}

	public long getDonateRecord() {
		return donateRecord;
	}

	public void setDonateRecord(long donateRecord) {
		this.donateRecord = donateRecord;
	}

	public boolean isDeleteFlag() {
		return deleteFlag;
	}

	public String getJoinTime() {
		return joinTime;
	}

	public void setJoinTime(String joinTime) {
		this.joinTime = joinTime;
	}

	public String getAllianceKey() {
		return allianceKey;
	}

	public void setAllianceKey(String allianceKey) {
		this.allianceKey = allianceKey;
	}
	
	public void setDeleteFlag(boolean deleteFlag) {
		this.deleteFlag = deleteFlag;
	}

	public List<String> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<String> permissions) {
		this.permissions = permissions;
	}

	public RoleIcon getIcon() {
		return icon;
	}

	public void setIcon(RoleIcon icon) {
		this.icon = icon;
	}

	@Override
	public int compareTo(UnionMember o) {
		Alliancemembers data1 = getData();
		Alliancemembers data2 = o.getData();
		if (data1.getRank() == data2.getRank()){//官职相同，按贡献排
			if (score < o.score){
				return 1;
			}else if (score == o.score){//如果贡献也相同，按加入先后排
				long time1 = TimeUtils.getTimes(joinTime);
				long time2 = TimeUtils.getTimes(o.joinTime);
				if (time1 < time2){
					return -1;
				}else if (time1 > time2){
					return 1;
				}else{
					return 0;
				}
			}else{
				return -1;
			}
		}else if (data1.getRank() < data2.getRank()){
			return -1;
		}else{
			return 1;
		}
	}
	
	public void tick(long now) {
		if (timer != null && timer.over(now)) {
			timer.die();
		}
	}

	@Override
	public String table() {
		return TABLE_RED_ALERT_UNION_MEMBER;
	}

	@Override
	public String[] wheres() {
		return new String[]{RED_ALERT_GENERAL_UID,RED_ALERT_GENERAL_UNION_ID};
	}

	@Override
	public boolean delete() {
		return deleteFlag;
	}

	@Override
	public void insertData(SqlData data) {
		saveToData(data);
	}

	@Override
	public void save() {
		if (savIng){
			return;
		}
		savIng = true;
		taskPool.saveThread.addSaveData(this);
	}

	@Override
	public void loadFromData(SqlData data) {
		uid          = data.getLong(RED_ALERT_GENERAL_UID);
		unionId      = data.getLong(RED_ALERT_GENERAL_UNION_ID);
		name         = data.getString(RED_ALERT_GENERAL_NAME);
		level        = data.getInt(RED_ALERT_GENERAL_LEVEL);
		fight        = data.getInt(RED_ALERT_GENERAL_FIGHT);
		score        = data.getLong(RED_ALERT_UNION_SCORE);
		scoreDaily	 = data.getLong(RED_ALERT_UNION_SCOREDAILY);
		scoreWeekly  = data.getLong(RED_ALERT_UNION_SCOREWEEKLY);
		scoreRecord  = data.getLong(RED_ALERT_UNION_SCORE_RECODE);
		donateDaily	 = data.getLong(RED_ALERT_UNION_DONATEDAILY);
		donateWeekly  = data.getLong(RED_ALERT_UNION_DONATEWEEKLY);
		donateRecord  = data.getLong(RED_ALERT_UNION_DONATE_RECORD);
		donateType	= data.getInt(RED_ALERT_UNION_DONATE_TYPE);
		//String timerData      = data.getString(RED_ALERT_UNION_DONATE_TIMER);
		//if (!StringUtils.isNull(timerData)) {
		//	TimerLast timer = JsonUtil.JsonToObject(timerData,TimerLast.class);
		//	timer.registTimeOver(this);
		//}
		Timestamp ts = data.getTimestamp(RED_ALERT_UNION_MEMBER_JOIN_TIME);
		joinTime     = ts.toString().substring(0,19);
		allianceKey  = data.getString(RED_ALERT_UNION_MEMBER_ALLIANCE_KEY);
		String str   = data.getString(RED_ALERT_UNION_MEMBER_PERMISSIONS);
		if (!StringUtils.isNull(str)) {
			permissions  = JsonUtil.JsonToObjectList(str,String.class);
		}
		String techPro   = data.getString(RED_ALERT_UNION_MEMBER_TECH_PROGRESS);
		if (!StringUtils.isNull(techPro)) {
			techProgresses = JSON.parseObject(techPro,new TypeReference<Map<String,UnionMemberTechProgress>>(){});
		}
		str        = data.getString(RED_ALERT_UNION_ICON);
		if (!StringUtils.isNull(str)) {
			icon = JsonUtil.JsonToObject(str,RoleIcon.class);
		}
 	}

	@Override
	public void saveToData(SqlData data) {
		data.put(RED_ALERT_GENERAL_UID,uid);
		data.put(RED_ALERT_GENERAL_UNION_ID,unionId);
		data.put(RED_ALERT_GENERAL_NAME,name);
		data.put(RED_ALERT_GENERAL_LEVEL,level);
		data.put(RED_ALERT_GENERAL_FIGHT,fight);
		data.put(RED_ALERT_UNION_SCORE,score);
		data.put(RED_ALERT_UNION_SCOREDAILY, scoreDaily);
		data.put(RED_ALERT_UNION_SCOREWEEKLY, scoreWeekly);
		data.put(RED_ALERT_UNION_SCORE_RECODE,scoreRecord);
		data.put(RED_ALERT_UNION_DONATEDAILY, donateDaily);
		data.put(RED_ALERT_UNION_DONATEWEEKLY, donateWeekly);
		data.put(RED_ALERT_UNION_DONATE_RECORD,donateRecord);
		data.put(RED_ALERT_UNION_DONATE_TYPE, donateType);
		String timerData   = JsonUtil.ObjectToJsonString(timer);
		data.put(RED_ALERT_UNION_DONATE_TIMER,timerData);
		data.put(RED_ALERT_UNION_MEMBER_JOIN_TIME,joinTime);
		data.put(RED_ALERT_UNION_MEMBER_ALLIANCE_KEY,allianceKey);
		String str   = JsonUtil.ObjectToJsonString(permissions);
		data.put(RED_ALERT_UNION_MEMBER_PERMISSIONS,str);
		String techPro = JsonUtil.ObjectToJsonString(techProgresses);
		data.put(RED_ALERT_UNION_MEMBER_TECH_PROGRESS, techPro);
		str  = JsonUtil.ObjectToJsonString(icon);
		data.put(RED_ALERT_UNION_ICON,str);
	}

	@Override
	public void serialize(JoyBuffer out) {
		out.putLong(uid);//long 成员编号
		out.putLong(unionId);//long 隶属联盟编号
		out.putPrefixedString(name,JoyBuffer.STRING_TYPE_SHORT);//string 成员名称
		out.putInt(level);//int 成员等级
		out.putLong(score);//long 成员个人贡献
		out.putLong(scoreDaily);	//long 日贡献度 ★
		out.putLong(scoreWeekly);	//long 周贡献度 ★
		out.putLong(scoreRecord);	//long 历史贡献度 ★
		out.putLong(donateDaily);	//long 日捐献度 ★
		out.putLong(donateWeekly);	//long 周捐献度 ★
		out.putLong(donateRecord);	//long 历史捐献度 ★
		//TODO 2
//		out.putInt(donateType);		//int 捐赠按钮的状态 0:正常 1:倒计时
//		if (timer != null){
//			out.putInt(1);
//			timer.serialize(out);
//		}else{
//			out.putInt(0);
//		}
		Alliancemembers data = getData();
		String title = getTitle(data);
		out.putPrefixedString(title,JoyBuffer.STRING_TYPE_SHORT);//String 官职名称
		out.putInt(data.getRank());//int 军衔
		MapCity mapcty = mapWorld.searchMapCity(uid,0);
		out.putInt(mapcty.getPosition());//int 位置
		out.putInt(getFight());//int 战斗力
		out.putInt(permissions.size());//int 拥有权限个数
		for (int i = 0 ; i < permissions.size() ; i++){
			String permission = permissions.get(i);
			out.putPrefixedString(permission,JoyBuffer.STRING_TYPE_SHORT);//string 权限编号
		}
		icon.serialize(out);
	}


	public Alliancemembers getData(){
		Alliancemembers data = dataManager.serach(Alliancemembers.class,allianceKey);
		return data;
	}

	public String getTitle(Alliancemembers alliancemembers ) {
		UnionBody unionBody = unionManager.search(unionId);
		if (unionBody.getUnionTitle() == null || unionBody.getUnionTitle().size() == 0) {
			GameLog.error("UnionTitle is fail,reset init title");
			unionBody.initUnionTitle();
		}
		return unionBody.getUnionTitle().get(alliancemembers.getRank());
	}
	
	public RespModuleSet sendToClient(byte type){
		RespModuleSet rms = new RespModuleSet();
		sendToClient(rms,type);
		return rms;
	}
	
	public void sendToClient(RespModuleSet rms , byte type){
		AbstractClientModule module = new AbstractClientModule(){
			@Override
			public short getModuleType() {
				return NTC_DTCD_UNION_MEMBER;
			}
		};
		module.add(type);//操作类型 0 添加，1删除，2修改
		module.add(this);
		rms.addModule(module);
	}
	
	/**
	 * 发送用户对应联盟科技捐赠按钮
	 * @param tempTechProgresses
	 * @param rms
	 */
	public void sendMemberTechProgress(List<UnionMemberTechProgress> tempTechProgresses ,RespModuleSet rms) {
		AbstractClientModule module = new AbstractClientModule(){
			@Override
			public short getModuleType() {
				return NTC_DTCD_UNION_TECH_PROGRESS;
			}
		};
		if (tempTechProgresses == null) {
			GameLog.error("techProgresses is null");
			return;
		}
		int allSize = tempTechProgresses.size();
		module.add(allSize);	//int
		for (int i = 0; i < allSize; i++) {			
			UnionMemberTechProgress techPro = tempTechProgresses.get(i);
			if (techPro == null || techPro.getTechProgresses() == null|| techPro.getTechProgresses().size() < 3) {
				GameLog.error("read techProgress is fail");
				return;
			}
			module.add(techPro.getTechId()); //String
			int size = techPro.getTechProgresses().size();
			module.add(size);	//int 
			for (int j = 0; j < size; j++) {
				module.add(techPro.getTechProgresses().get(j).getDonateId());	//int			
			}	
		}
		rms.addModule(module);
	}
	
	public boolean isLeader(){
		return getData().getRank() == 1;
	}

	public void destroy(Object... params) {
		Role role = (Role)params[0];
		if (role != null){
			role.handleEvent(GameEvent.UNION_EXIT);
			unionManager.roleExitUnion(role);
			MapUtil.updateUnionBuidlBuff(role,unionId,false);
			if (role.isOnline()){
				RespModuleSet rms = new RespModuleSet();
				AbstractClientModule module = new AbstractClientModule(){
					@Override
					public short getModuleType() {
						return NTC_DTCD_UNION_MUST_EXIT;
					}
				};
				module.add("you have must close this UI of union");
				rms.addModule(module);
				role.sendRoleToClient(rms);
				if (params != null && params.length > 1){
					Object[] objs = null;
					if (params.length > 2){
						objs = new Object[params.length -2];
						System.arraycopy(params,1,objs,0,objs.length);
					}
					MessageSendUtil.tipModule(rms,MessageSendUtil.TIP_TYPE_NORMAL,params[1].toString(),objs);
				}
				MessageSendUtil.sendModule(rms,role.getUserInfo());
			}
		}
		deleteFlag = true;
		save();
	}
	
	public boolean checkPost(UnionPostType postType){
		final int rank = Integer.valueOf(getAllianceKey());
		Alliancemembers alliancemember = dataManager.serach(Alliancemembers.class, new SearchFilter<Alliancemembers>() {
			@Override
			public boolean filter(Alliancemembers data) {
				return rank == data.getRank();
			}
		});
		if (alliancemember == null) {
			GameLog.error("read alliancemember is fail ");
			return false;
		}
		List<String> permissions = alliancemember.getJurisdiction();
		if (permissions == null) {
			GameLog.error("read alliancemember is fail ");
			return false;
		}
		for (int i = 0 ; i < permissions.size() ; i++){
			String permission = permissions.get(i);
			int id = Integer.parseInt(permission) - 1;
			if (id == postType.ordinal()){
				return true;
			}
		}
		return false;
	}

	public void resetPermission(Alliancemembers t_alliance) {
		allianceKey = String.valueOf(t_alliance.getRank());
		permissions.clear();
		permissions.addAll(t_alliance.getJurisdiction());
	}

	public int getOfficerLevel() {
		Alliancemembers data = getData();
		return data.getRank();
	}

	
	public void listResp(CommunicateResp resp) {
		resp.add(uid);		//成员Uid long
		resp.add(unionId);	//联盟id long
		resp.add(name);	//名称	String
		resp.add(fight);	//战斗力 int
		resp.add(level);	//等级	int
		resp.add(score);	//个人贡献 long
		resp.add(scoreRecord);//贡献历史记录 long
		resp.add(joinTime);	//加入时间 String
		resp.add(allianceKey);//军衔 String
		resp.add(permissions.size());	//权限列表大小 int
		for (int i = 0 ; i < permissions.size() ; i++){
			String perString = permissions.get(i);
			resp.add(perString);//权限ID String
		}		
	}

	@Override
	public void over() {
		savIng = false;
	}

	
	@Override
	public boolean saving() {
		return savIng ;
	}

	@Override
	public void finish() {
		donateType = 0;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName()  + "_" + uid;
	}
}
