package com.joymeng.slg.union;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.joymeng.Instances;
import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.MathUtils;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.list.EventName;
import com.joymeng.log.GameLog;
import com.joymeng.log.LogManager;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.dao.DaoData;
import com.joymeng.slg.dao.SqlData;
import com.joymeng.slg.domain.data.SearchFilter;
import com.joymeng.slg.domain.evnt.EvntManager;
import com.joymeng.slg.domain.map.MapUtil;
import com.joymeng.slg.domain.map.impl.dynamic.ExpediteTroops;
import com.joymeng.slg.domain.map.impl.dynamic.GarrisonTroops;
import com.joymeng.slg.domain.map.impl.dynamic.GridType;
import com.joymeng.slg.domain.map.impl.dynamic.MassTroops;
import com.joymeng.slg.domain.map.impl.dynamic.TroopsData;
import com.joymeng.slg.domain.map.impl.still.role.MapCity;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.timer.TimerLastType;
import com.joymeng.slg.net.mod.ClientModule;
import com.joymeng.slg.net.mod.RespModuleSet;
import com.joymeng.slg.union.data.Alliancemembers;
import com.joymeng.slg.union.impl.UnionInviteInfo;
import com.joymeng.slg.union.impl.UnionMember;
import com.joymeng.slg.world.TaskPool;

/**
 * 联盟管理
 * @author tanyong
 *
 */
public class UnionManager implements Instances{
	
	private static UnionManager instance = new UnionManager();
	
	public static UnionManager getInstance(){
		return instance;
	}
	
	public void load(){
		List<Map<String,Object>> datas = dbMgr.getGameDao().getDatasByTableName(DaoData.TABLE_RED_ALERT_UNION);
		for (Map<String,Object> data : datas){
			UnionBody union = new UnionBody();
			union.loadFromData(new SqlData(data));
			union.loadMembers();
			world.addObject(UnionBody.class,union);
		}
		beginRefreshMemberScore();
	}
	
	public synchronized UnionBody create(Role role,String name,String shortName){
		if (role.getUnionId() != 0) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_JOINED_IN_UNION); // 提示创建成功
			return null;
		}
		UnionBody union = new UnionBody();
		union.setName(name);
		union.setShortName(shortName);
		union.setPosition(role.getCity(0).getPosition());
		union.setLanguage(role.getRoleSetting().getLanguage());
		String time = TimeUtils.nowStr();
		union.setCreateTime(time);
		long unionId = keyData.key(DaoData.TABLE_RED_ALERT_UNION);
		union.setId(unionId);
		union.init();
		world.addObject(UnionBody.class,union);
		Alliancemembers alliance = dataManager.serach(Alliancemembers.class,new SearchFilter<Alliancemembers>(){
			@Override
			public boolean filter(Alliancemembers data) {
				return data.getRank() == 1;
			}
		});
		RespModuleSet rms = new RespModuleSet();
		if (!union.tryToAddmember(rms,role,alliance)){
			return union;
		}
		if (role.isOnline()){
			MessageSendUtil.sendModule(rms,role.getUserInfo());
		}
		String parameter = role.getId() + "|" + name + "|" + shortName;
		LogManager.unionLog(role, name, EventName.creatUnion.getName(), parameter);
	    EvntManager.getInstance().Notify("createUnion", "");
		return union;
	}
	
	public UnionBody search(long unionID){
		if (unionID == 0){
			return null;
		}
		UnionBody union = world.getObject(UnionBody.class,unionID);
		if (union != null && !union.isRemoving()){
			return union;
		}
		return null;
	}
	
	public UnionBody search(String name){
		List<UnionBody> unions = world.getListObjects(UnionBody.class);
		for (int i = 0 ; i < unions.size() ; i++){
			UnionBody union = unions.get(i);
			if (union.getName().equals(name) && !union.isRemoving()){
				return union;
			}
		}
		return null;
	}
	
	public int searchUnionsNum(int memSize){
		int num = 0;
		List<UnionBody> unions = world.getListObjects(UnionBody.class);
		for (int i = 0 ; i < unions.size() ; i++){
			UnionBody union = unions.get(i);
			if (union.getMembers().size() >= memSize && !union.isRemoving()){
				num ++;
			}
		}
		return num;
	}
	
	public int getUnionFightsNum(long cFight){
		int num = 0;
		List<UnionBody> unions = world.getListObjects(UnionBody.class);
		for (int i = 0 ; i < unions.size() ; i++){
			UnionBody union = unions.get(i);
			if (union.getUsInfo().getUnionFight() >= cFight && !union.isRemoving()){
				num ++;
			}
		}
		return num;
	}
	
	public List<UnionBody> search(final int myPosition, List<Long> ids, int max) {
		List<UnionBody> result = new ArrayList<UnionBody>();
		List<UnionBody> full = new ArrayList<UnionBody>();
		List<UnionBody> temp = world.getListObjects(UnionBody.class);
		Collections.sort(temp, new Comparator<UnionBody>() {
			@Override
			public int compare(UnionBody o1, UnionBody o2) {
				float a = MapUtil.computePointsDistance(myPosition, o1.getPosition());
				float b = MapUtil.computePointsDistance(myPosition, o2.getPosition());
				return a == b ? 0 : a > b ? 1 : -1;
			}
		});
		for (int i = 0; i < max && i < temp.size();) {
			if (temp.get(i).isFull()) {
				full.add(temp.get(i++));
				continue;
			}
			result.add(temp.get(i++));
		}
		int index = temp.size();
		int j = 0;
		while (index < max && j < full.size()) {
			if (full.get(j) == null) {
				continue;
			}
			result.add(full.get(j++));
			index++;
		}
//		int t = 0;
//		while (temp.size() > 0){
//			int index = MathUtils.random(temp.size());
//			UnionBody union = temp.get(index);
//			if (t < 3 && union.isFull()) {
//				continue;
//			}
//			t++;
//			temp.remove(index);
//			if (ids.contains(union.getId()) || union.isRemoving() || union.getMembers().size() < 1){
//				continue;
//			}
//			result.add(union);
//			if (result.size() == max){
//				break;
//			}
//		}
		return result;
	}

	public UnionBody searchShortName(String name) {
		List<UnionBody> unions = world.getListObjects(UnionBody.class);
		for (int i = 0 ; i < unions.size() ; i++){
			UnionBody union = unions.get(i);
			if (union.getShortName().equals(name) || union.isRemoving()){
				return union;
			}
		}
		return null;
	}

	/**
	 * 模糊查找
	 * @param name
	 * @return
	 */
	public List<UnionBody> fuzzyBodySearch(String name) {
		List<UnionBody> result = world.getListObjects(UnionBody.class);
		String low = name.toLowerCase();
		for (int i = 0 ; i < result.size() ;){
			UnionBody union = result.get(i);
			if (union.getMembers().size() < 1 || union.isRemoving() || (!union.getName().toLowerCase().contains(low)
					&& !union.getShortName().toLowerCase().contains(low))) {
				result.remove(i);
			} else {
				i++;
			}
		}
		return result;
	}
	
	public List<UnionInviteInfo> searchInvite(List<Long> ids,int max){
		List<UnionInviteInfo> infos = new ArrayList<UnionInviteInfo>();
		List<Role> roles = world.getOnlineRoles();		
		while (max > 0 && roles.size() > 0){
			int index = MathUtils.random(roles.size());
			Role role = roles.get(index);
			roles.remove(index);
			if (ids.contains(role.getId()) || role.getUnionId() != 0){
				continue;
			}
			UnionInviteInfo info = new UnionInviteInfo();
			info.init(role);
			infos.add(info);
			max--;
		}
		return infos;
	}
	
	public List<UnionInviteInfo> fuzzyInviteSearch(String name) {
		List<UnionInviteInfo> result = new ArrayList<UnionInviteInfo>();
		StringBuffer sb = new StringBuffer();
		sb.append("select role.joy_id as joy_id,role.unionId as unionId,role.name as name,");
		sb.append("role.iconType as iconType,role.iconId as iconId,");
		sb.append("role.iconName as iconName ");
		sb.append("from role ");
		sb.append("where (role.name like '%" + name + "%' or ");
		sb.append("role.name like '" + name + "%' or ");
		sb.append("role.name like '%" + name + "')");
		List<Map<String,Object>> datas = dbMgr.getGameDao().getDatasBySql(sb.toString());
		if (datas != null){
			for (int i = 0 ; i < datas.size() ; i++){
				Map<String,Object> data = datas.get(i);
				UnionInviteInfo info = new UnionInviteInfo();
				info.load(data);
				result.add(info);
			}
		}
		return result;
	}
	
	private void beginRefreshMemberScore() {
		//日更新
		long delay = MathUtils.getSecondsToClock(23,59) + 61;//第二天的第一秒
		taskPool.scheduleAtFixedRate(null,new Runnable() {
			@Override
			public void run() {
				refreshScoreDaily();
				//System.out.println("refreshScoreDaily");
			}
		},delay,TaskPool.SECONDS_PER_DAY,TimeUnit.SECONDS);
		//周更新
		int nowDay = TimeUtils.getWeek(); //周几
		long delayWeek = MathUtils.getSecondsToClock(23, 59) + 61 + TaskPool.SECONDS_PER_DAY * (7 - nowDay);
		//long delayWeek = (7 * 24 * 3600) - Math.abs(MathUtils.getSecondesToDayAndClock(1970, 1, 4, 23, 59, 59)) % (7 * 24 * 3600);
		taskPool.scheduleAtFixedRate(null,new Runnable() {
			@Override
			public void run() {
				refreshWeeklyDaily();
				//System.out.println("refreshScoreWeekly");
			}
		},delayWeek,7*TaskPool.SECONDS_PER_DAY,TimeUnit.SECONDS);		
	}
	
	/**
	 * 日刷新处理
	 */
	private void refreshScoreDaily() {
		List<UnionBody> allUnion = world.getListObjects(UnionBody.class);
		for (int i = 0 ; i < allUnion.size() ; i++){
			UnionBody unionBody = allUnion.get(i);
			synchronized (unionBody) {
				//清楚邀请列表
				unionBody.getInvites().clear();
				//更新用户日贡献数值
				List<UnionMember> members = unionBody.getMembers();
				for (int j = 0 ; j < members.size() ; j++){
					UnionMember member = members.get(j);
					member.setScoreDaily(0L);
					member.setDonateDaily(0L);
					unionBody.sendMemberToAllMembers(member, ClientModule.DATA_TRANS_TYPE_UPDATE);
				}
				unionBody.sendMeToAllMembers(0);
			}
		}
	}

	/**
	 * 周刷新处理
	 */
	private void refreshWeeklyDaily() {
		List<UnionBody> allUnion = world.getListObjects(UnionBody.class);
		for (int i = 0 ; i < allUnion.size() ; i++){
			UnionBody unionBody = allUnion.get(i);
			synchronized (unionBody) {
				if (unionBody == null) {
					continue;
				}
				//更新用户周贡献数值
				List<UnionMember> members = unionBody.getMembers();
				for (int j = 0 ; j < members.size() ; j++){
					UnionMember member = members.get(j);
					member.setScoreWeekly(0L);
					member.setDonateWeekly(0L);
					unionBody.sendMemberToAllMembers(member, ClientModule.DATA_TRANS_TYPE_UPDATE);
				}
				//更新联盟系统商店
				unionBody.initSysStore();
				unionBody.sendMeToAllMembers(0);
			}
		}
	}

	public void serializeSimple(long unionId, JoyBuffer out) {
		UnionBody union = search(unionId);
		if (union != null){
			out.putPrefixedString(union.getName(),JoyBuffer.STRING_TYPE_SHORT);//string 联盟名字
			out.putPrefixedString(union.getShortName(),JoyBuffer.STRING_TYPE_SHORT);//string 联盟短名称
			out.putPrefixedString(union.getIcon(),JoyBuffer.STRING_TYPE_SHORT);//string 联盟图标
		}else{
			out.putPrefixedString("",JoyBuffer.STRING_TYPE_SHORT);//string 联盟名字
			out.putPrefixedString("",JoyBuffer.STRING_TYPE_SHORT);//string 联盟短名称
			out.putPrefixedString("",JoyBuffer.STRING_TYPE_SHORT);//string 联盟图标
		}
	}
	
	/**
	 * 玩家退出联盟，这里为啥不在UNION_EXIT事件一起处理，主要是为了防止事件里面再触发事件
	 * @param role
	 */
	public void roleExitUnion(Role role){
		long uid = role.getId();
		List<ExpediteTroops> expedites = world.getListObjects(ExpediteTroops.class);
		for (int i = 0 ; i < expedites.size() ; i++){
			ExpediteTroops expedite = expedites.get(i);
			TroopsData myTroops = null;
			for (int j = 0 ; j < expedite.getTeams().size() ; j++){
				TroopsData troops = expedite.getTeams().get(j);
				if (troops.getInfo().getUid() == uid){
					troops.getInfo().setUnionId(0);
					myTroops = troops;
					break;
				}
			}
			if (expedite.getTimer().getType() == TimerLastType.TIME_EXPEDITE_MASS){
				//去参加集结的部队,需要回家
				expedite.setCallBack(true);
			}
			if (expedite.isMass() && myTroops != null){
				//已经出发的集结部队
				if (myTroops.isLeader()){//全部召回
					expedite.setCallBack(true);
				}else{//召回一支
					expedite.orderTroopsBack(myTroops,role);
				}
			}
		}
		List<MapCity> citys = world.getListObjects(MapCity.class);
		for (int i = 0 ; i < citys.size() ; i++){
			MapCity city = citys.get(i);
			MassTroops mass = city.getMass();
			if (mass != null){
				if (mass.getTargetInfo().getUid() == uid){
					//我是被集结的目标
					mass.getTargetInfo().setUnionId(0);
				}
				if (!mass.isExpedite()){//正在行军
					if (city.getInfo().getUid() == uid){//我是集结发起者
						city.massCancle(role);
					}else{//我是参与者
						GridType[] grids = mass.getGrids();
						for (int j = 0 ; j < grids.length ; j++){
							GridType grid = grids[j];
							if (grid != null){
								//来参加集结的部队已经处理过了
								if (grid.getType() == GarrisonTroops.class){
									GarrisonTroops troops = grid.object();
									if (troops != null){
										if (troops.getTroops().getInfo().getUid() == uid){
											troops.die();
										}
									}
								}
							}
						}
					}
				}
			}
		}
		role.setUnionId(0);
		role.sendViews(new RespModuleSet(),true);
		GameLog.info("uid is " + role.getId() + " exit union");
	}
}
