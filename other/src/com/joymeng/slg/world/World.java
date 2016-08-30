/**
 * 
 */
package com.joymeng.slg.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import com.joymeng.Instances;
import com.joymeng.common.util.HttpClientUtil;
import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.JsonUtil;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.http.HtppOprateType;
import com.joymeng.http.HttpServer;
import com.joymeng.list.ServerStatus;
import com.joymeng.log.GameLog;
import com.joymeng.services.core.JoyServiceApp;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.slg.ServiceApp;
import com.joymeng.slg.dao.DaoData;
import com.joymeng.slg.dao.SqlData;
import com.joymeng.slg.domain.actvt.ActvtManager;
import com.joymeng.slg.domain.event.GameEvent;
import com.joymeng.slg.domain.map.MapObject;
import com.joymeng.slg.domain.market.RoleBlackMarketAgent;
import com.joymeng.slg.domain.object.IObject;
import com.joymeng.slg.domain.object.army.RoleArmyAgent;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.role.imp.RoleStatisticInfo;
import com.joymeng.slg.domain.object.task.DailyTaskAgent;
import com.joymeng.slg.domain.object.technology.RoleTechAgent;
import com.joymeng.slg.domain.shop.RoleShopAgent;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.handler.impl.feedback.FeedBackManager;
import com.joymeng.slg.net.resp.TransmissionResp;
import com.joymeng.slg.union.UnionBody;
import com.joymeng.slg.union.impl.UnionInviteInfo;

/**
 * @author Dream
 * 
 */
public class World implements Instances {

	private static final World instance = new World();
	private  Map<Class<? extends IObject>,Map<Long,IObject>> objects = new ConcurrentHashMap<Class<? extends IObject>,Map<Long,IObject>>(2048);//所有的对象
	private List<IObject> needRemoves = new CopyOnWriteArrayList<IObject>();
	
	private World() {
		
	}
	
	long superId = 0;
	long errorId = 0;
	
	public void debug(long superId,long errorId){
		this.superId = superId;
		this.errorId = errorId;
	}
	
	/**
	 * @return
	 */
	public static World getInstance() {
		return instance;
	}

	public void init() throws Exception{
		ServiceHandler.registerHandlers();
		dbMgr.init();
		nameManager.load();
		dataManager.load(true);
		RoleBlackMarketAgent.startInit();//加载黑市数据
		RoleShopAgent.load();//服务器限购数据加载
		activityManager.loadFromDataBase();
		chatDataManager.load();
		keyData.load();
		unionManager.load();
		rankManager.load();
		worldSInfo.load();
		ActvtManager.getInstance().init();
		mapWorld.load();
		FeedBackManager.getInstance().load();
		ServiceApp.FREEZE = false;
		taskPool.start();//启动系统主线程和其他功能线程
	}

	public void tick(long now) {
		for (int i = 0 ; i < needRemoves.size() ; i++){
			IObject obj = needRemoves.get(i);
			_remove(obj);
		}
		needRemoves.clear();
		Map<Long,IObject> map = objects.get(Role.class);
		if (map != null){
			for (IObject obj : map.values()){
				obj.tick(now);
			}
		}
		map = objects.get(UnionBody.class);
		if (map != null){
			for (IObject obj : map.values()){
				obj.tick(now);
			}
		}
	}
	
	boolean tryToLogin(){
		try {
			String url  = "http://netuser.joymeng.com/user/login?uname=" + ServiceApp.service_account + "&password=" + ServiceApp.service_pwd;
			HttpResponse resp = HttpClientUtil.getHttpResponse(new HttpGet(url));
			byte[] datas = HttpClientUtil.readFromStream(resp.getEntity().getContent());
			String str = new String(datas,"UTF-8");
			ServerLogin result = JsonUtil.JsonToObject(str,ServerLogin.class);
			if (result.status == 1){
				return true;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void notifyList(ServerStatus statu){
		if (ServiceApp.instanceId == GameConfig.SERVER_LIST_ID){
			return;
		}
		//if (!tryToLogin()){
		//	return;
		//}
		if (ServiceApp.FREEZE){
			return;
		}
		UserInfo targetInfo = new UserInfo();
		targetInfo.setUid(ServiceApp.service_uid);
		targetInfo.setCid(GameConfig.SERVER_LIST_ID);
		int protocolId = 0x00000002;//玩家信息
		TransmissionResp resp = new TransmissionResp();
		resp.setUserInfo(targetInfo);
		resp.getParams().put(protocolId);//指令编号
		resp.getParams().put(HtppOprateType.HTPP_OPRATE_RESPONSE.ordinal());
		resp.getParams().put(ServiceApp.instanceId);//从哪里来的
		resp.getParams().put(statu.getKey());//状态
		JoyServiceApp.getInstance().sendMessage(resp);
	}
	
	public void gameServerShutDown() {
		if (ServiceApp.FREEZE){
			return;
		}
		GameLog.info("FREEZE EXCUTING");
		HttpServer.getInstance().stop();
		notifyList(ServerStatus.SERVER_STATUS_CLOSE);
		ServiceApp.FREEZE = true;
		RoleBlackMarketAgent.closeSave();
		save();
		taskPool.getSaveThread().gameShutDown();
	}
	
	public void save(){
		saveAllRole();
		saveAllUnion();
		chatDataManager.save();
		mapWorld.save();
		taskPool.stop();
		ActvtManager.getInstance().save();
	}
	
	public void remove(IObject obj){
		obj.removing();
		needRemoves.add(obj);
	}
	
	private void saveAllRole(){
		List<Role> roles = getListObjects(Role.class);
		for (int i = 0 ; i < roles.size() ; i++){
			Role role = roles.get(i);
			role.save();
		}
	}
	
	private void saveAllUnion(){
		List<UnionBody> unions = getListObjects(UnionBody.class);
		for (int i = 0 ; i < unions.size() ; i++){
			UnionBody union = unions.get(i);
			union.save();
		}
	}
	
	private Role getRoleFromDB(long joyId) {
		try {
			SqlData data = dbMgr.getGameDao().getData(DaoData.TABLE_RED_ALERT_ROLE,DaoData.RED_ALERT_ROLE_ID,joyId);
		    return loadRole(data);
		} catch (Exception e) {
			GameLog.error("load Role[joyId = " + joyId + "] from db is error...",e);
			return null;
		}
	}
	/*
	 * 玩家删号
	 */
	public boolean removeRole(long uid) {
		boolean succeed = false;
		Role role = getRole(uid);
		if (role == null) {
			succeed = false;
		}
		if (!role.isOnline()) {
			role.handleEvent(GameEvent.REMOVE_ROLE);
			UnionBody union = unionManager.search(role.getUnionId());
			if (union != null) {
				if (union.checkLeader(role.getId())) {
					if (union.getMembers().size() > 1) {
						// 需禅让
						long roleId = union.getMemberDemise();
						union.tryToAppoint(role, roleId, 1);
					} else {
						union.dissolve();
					}
				} else {
					union.memberExit(role.getId());
				}
			}
			StringBuffer rolebuffer = new StringBuffer(256);
			rolebuffer.append("delete  from role where role.joy_id =" + uid);
			String rolesq = rolebuffer.toString();
			dbMgr.getGameDao().getSimpleJdbcTemplate().update(rolesq, new HashMap<>());

			StringBuffer citybuffer = new StringBuffer(256);
			citybuffer.append("delete  from city where city.uid =" + uid);
			String citysq = citybuffer.toString();
			dbMgr.getGameDao().getSimpleJdbcTemplate().update(citysq, new HashMap<>());
			
			StringBuffer dailybuffer = new StringBuffer(256);
			dailybuffer.append("delete  from dailyTask where dailyTask.uid =" + uid);
			String dailysq = citybuffer.toString();
			dbMgr.getGameDao().getSimpleJdbcTemplate().update(dailysq, new HashMap<>());
			
			StringBuffer rankbuffer = new StringBuffer(256);
			rankbuffer.append("delete  from rank where rank.uid =" + uid);
			String ranksq = rankbuffer.toString();
			dbMgr.getGameDao().getSimpleJdbcTemplate().update(ranksq, new HashMap<>());
			
	        GameLog.info("删除 player"+uid+"完成");
		}
		return succeed;    
	}
	
	/*
	 *读取用户表内容
	 */
	public List<Role> getRolesFromDB() {
		List<Role> roleList = new ArrayList<>();
		try {
			List<SqlData> data = dbMgr.getGameDao().getDatas(DaoData.TABLE_RED_ALERT_ROLE);
			for (int i = 0 ; i < data.size() ; i++){
				SqlData sql = data.get(i);
				roleList.add(loadRole(sql));
			}

		} catch (Exception e) {
			GameLog.error("load Roles from db is error...", e);
			return null;
		}
		return roleList;
	}
	
	
	private Role loadRole(SqlData data) {
		if (data == null){
			return null;
		}
		Role role = new Role();
		role.loadFromData(data);
		return role;
	}
	
	public boolean kick(long id){
		Role role = getObject(Role.class,id);
		if (role != null){
			role.kick();
			return true;
		}
		return false;
	}
	
	public Role getRole(long id){
		if (id == 0){
			return null;
		}
		if (superId > 0 && errorId > 0){
			if (id == superId){
				id = errorId;
			}
		}
		Role role = getObject(Role.class,id);
		if (role == null){//内存里面没有
			role = getRoleFromDB(id);
			if (role != null){
				role.handleEvent(GameEvent.ROLE_HEART);//第一个心跳
				role.handleEvent(GameEvent.LOAD_FROM_DB);
				role.addSelf();
				role.getEffectAgent().handleAllEffectEvent(role);
				for(RoleCityAgent city : role.getCityAgents()){
					RoleCityAgent agent = role.getCity(city.getId());
					agent.grainConsumption(role,false);//部队离线粮食消耗计算
				}
			}
		}
		return role;
	}
	
	public Role createNewRole(long uid, String chinnelId, String country,
			String language, String uuid, String uuidRegisTime, String model,
			String version, String resolution, int memory,String registrationId) {
		Role role = new Role();
		role.setId(uid);
		role.setName("新兵-" + uid);
		role.setChannelId(chinnelId);
		role.setUidRegisTime(TimeUtils.nowStr());
		role.setCountry(country);
		role.setLanguage(language);
		role.setUuid(uuid);
		role.setUuidRegisTime(uuidRegisTime);
		role.setModel(model);
		role.setVersion(version);
		role.setResolution(resolution);
		role.setMemory(memory);
		role.setRegistrationId(registrationId);
		role.setCountryId(ServiceApp.instanceId);
		role.handleEvent(GameEvent.ROLE_HEART);
		role.handleEvent(GameEvent.ROLE_CREATE);
		role.addSelf();
		role.save();
		GameLog.info("new player<" + uid + "> regist");
		return role;
	}
	
	public List<Role> getOnlineRoles(){
		List<Role> result = getListObjects(Role.class);
		for (int i = 0 ; i < result.size();){
			Role role = result.get(i);
			if (!role.isOnline()){
				result.remove(i);
			}else{
				i++;
			}
		}
		return result;
	}
	
	public Role getOnlineRole(long id){
		Role role = getObject(Role.class,id);
		if (role != null && role.isOnline()){
			return role;
		}
		return null;
	}
	
	public void addObject(Class<? extends IObject> clazz , IObject obj){
		if (obj == null){
			return;
		}
		Map<Long,IObject> map = objects.get(clazz);
		if (map == null){
			map = new ConcurrentHashMap<Long,IObject>();
			objects.put(clazz,map);
		}
		map.put(obj.getId(),obj);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends IObject> T getObject(Class<? extends T> clazz , long id){
		Map<Long,? extends IObject> map = objects.get(clazz);
		if (map != null){
			IObject obj = map.get(id);
			if (obj == null || obj.isRemoving()){
				return null;
			}
			return (T)obj;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends IObject> List<T> getListObjects(Class<? extends T> clazz){
		List<T> result = new ArrayList<T>();
		Map<Long,IObject> map = objects.get(clazz);
		if (map != null){
			for (IObject obj : map.values()){
				if (obj.isRemoving()){
					continue;
				}
				result.add((T)obj);
			}
		}
		return result;
	}
	
	public List<MapObject> getMapObjects(long uid){
		List<MapObject> result = new ArrayList<MapObject>();
		for (Class<?> type : objects.keySet()){
			if (MapObject.class.isAssignableFrom(type)){
				Collection<IObject> values = objects.get(type).values();
				for (IObject obj : values){
					MapObject mo = (MapObject)obj;
					if (mo.getInfo().getUid() == uid && !mo.isRemoving()){
						result.add(mo);
					}
				}
			}
		}
		return result;
	}

	public void loadCitys(Role role){
		List<SqlData> datas = dbMgr.getGameDao().getDatas(DaoData.TABLE_RED_ALERT_CITY,DaoData.RED_ALERT_GENERAL_UID,role.getId());
		if (datas == null){
			return;
		}
		for (int i = 0 ; i < datas.size() ; i++){
			SqlData data = datas.get(i);
			RoleCityAgent buildAgent = new RoleCityAgent();
			role.addCity(buildAgent);
			buildAgent.loadFromData(data);
			buildAgent.initBuildComponent(role);
		}
	}
	
	public void loadArmysFromDB(Role role){
		List<SqlData> datas = dbMgr.getGameDao().getDatas(DaoData.TABLE_RED_ALERT_ARMY,DaoData.RED_ALERT_GENERAL_UID,role.getId());
		if (datas == null){
			return;
		}
		for (int i = 0 ; i < datas.size() ; i++){
			SqlData data = datas.get(i);
			int cityId = data.getInt(DaoData.RED_ALERT_GENERAL_CITY_ID);
			RoleCityAgent cityAgent = role.getCity(cityId);
			RoleArmyAgent armyAgent = cityAgent.getCityArmys();
			armyAgent.loadFromData(data);
		}
	}
	
	public void loadTechFromDB(Role role){
		List<SqlData> datas = dbMgr.getGameDao().getDatas(DaoData.TABLE_RED_ALERT_TECH,DaoData.RED_ALERT_GENERAL_UID,role.getId());
		if (datas == null){
			return;
		}
		for (int i = 0 ; i < datas.size() ; i++){
			SqlData data = datas.get(i);
			int cityId = data.getInt(DaoData.RED_ALERT_GENERAL_CITY_ID);
			RoleTechAgent techAgent = role.getCity(cityId).getTechAgent();
			techAgent.loadFromData(data);
		}
	}
	
	private void _remove(IObject obj){
		Map<Long,IObject> map = objects.get(obj.getClass());
		if (map != null){
			map.remove(obj.getId());
		}
	}
	
	public void loadDailyTasks(Role role){
		List<SqlData> datas = dbMgr.getGameDao().getDatas(DaoData.TABLE_RED_ALERT_DAILYTASK,DaoData.RED_ALERT_GENERAL_UID,role.getId());
		DailyTaskAgent taskAgent = role.getDailyTaskAgent();
		if (datas == null){
			role.getDailyTaskAgent().setUid(role.getId());
			role.getDailyTaskAgent().initDailyTask(role);
			return;
		}
		for (int i = 0 ; i < datas.size() ; i++){
			SqlData data = datas.get(i);
			if(taskAgent.checkDailyTime(role, data)){
				taskAgent.loadFromData(data);
			}
		}
	}
	
	public void loadRoleStaticInfo(Role role){
		List<SqlData> datas = dbMgr.getGameDao().getDatas(DaoData.TABLE_RED_ALERT_STATIC,DaoData.RED_ALERT_GENERAL_UID,role.getId());
		RoleStatisticInfo agent = role.getRoleStatisticInfo();
		if (datas == null){
			return;
		}
		for (int i = 0 ; i < datas.size() ; i++){
			SqlData data = datas.get(i);
			agent.loadFromData(data);
		}
	}
	
	public void tryToShutDown(){
		new Thread() {
			@Override
			public void run() {
				long end = TimeUtils.nowLong() + 20 * 1000;
				while (true) {
					long now = TimeUtils.nowLong();
					long left = (end - now) / 1000;
					if (left <= 0) {
						try {
							JoyServiceApp.getInstance().stop();
						} catch (Exception e) {
							e.printStackTrace();
						}
						break;
					}
					if (left < 5 || (left > 5 && left % 5 == 0)){
						MessageSendUtil.sendMessageToOnlineRole(MessageSendUtil.TIP_TYPE_NORMAL,I18nGreeting.MSG_SERVICE_CLOSE_TIP,left);
					}
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
						
					}
				}
			}
		}.start();
	}
	
	public List<UnionInviteInfo> fuzzySearchRole(Role role, String name) {
		name = name.toUpperCase();
		List<UnionInviteInfo> result = new ArrayList<UnionInviteInfo>();
		List<Long> uids = new ArrayList<>();
		//查询内存
		List<Role> momeryDatas = getOnlineRoles();
		for (int i = 0 ; i < momeryDatas.size() ; i++){
			Role r = momeryDatas.get(i);
			String rName = r.getName().toUpperCase();
			if (rName.contains(name)) {
				UnionInviteInfo temp = new UnionInviteInfo();
				temp.init(r);
				result.add(temp);
				uids.add(temp.getUid());
			}
		}
		//查询数据库
		StringBuffer sb = new StringBuffer();
		sb.append("select role.joy_id as joy_id,role.name as name,");
		sb.append("role.iconType as iconType,role.iconId as iconId,");
		sb.append("role.iconName as iconName ");
		sb.append("from role ");
		sb.append("where (Upper(role.name) like '%" + name + "%' or ");
		sb.append("role.name like '" + name + "%' or ");
		sb.append("role.name like '%" + name + "') ");
//		if (role.getUnionId() != 0) {
//			sb.append(" and role.unionId !=" + role.getUnionId());
//		}
		List<Map<String,Object>> datas = dbMgr.getGameDao().getDatasBySql(sb.toString());
		if (datas != null){
			for (Map<String,Object> data : datas){
				UnionInviteInfo info = new UnionInviteInfo();
				info.load(data);
				if(uids.contains(info.getUid())){
					continue;
				}
				result.add(info);
			}
		}
		
		return result;
	}
	
	static class ServerLogin{
		byte status;
		String msg;
		LoginResult content;
		public byte getStatus() {
			return status;
		}
		public void setStatus(byte status) {
			this.status = status;
		}
		public String getMsg() {
			return msg;
		}
		public void setMsg(String msg) {
			this.msg = msg;
		}
		public LoginResult getContent() {
			return content;
		}
		public void setContent(LoginResult content) {
			this.content = content;
		}
	};
	static class LoginResult{
		long uid;
		String uname;
		String nname;
		String password;
		String reg_date;
		String token;
		public long getUid() {
			return uid;
		}
		public void setUid(long uid) {
			this.uid = uid;
		}
		public String getUname() {
			return uname;
		}
		public void setUname(String uname) {
			this.uname = uname;
		}
		public String getNname() {
			return nname;
		}
		public void setNname(String nname) {
			this.nname = nname;
		}
		public String getPassword() {
			return password;
		}
		public void setPassword(String password) {
			this.password = password;
		}
		public String getReg_date() {
			return reg_date;
		}
		public void setReg_date(String reg_date) {
			this.reg_date = reg_date;
		}
		public String getToken() {
			return token;
		}
		public void setToken(String token) {
			this.token = token;
		}
	}
}
