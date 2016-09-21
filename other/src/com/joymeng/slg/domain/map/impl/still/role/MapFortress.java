package com.joymeng.slg.domain.map.impl.still.role;

import java.util.ArrayList;
import java.util.List;

import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.JsonUtil;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.list.EventName;
import com.joymeng.log.GameLog;
import com.joymeng.log.LogManager;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.slg.dao.SqlData;
import com.joymeng.slg.domain.event.GameEvent;
import com.joymeng.slg.domain.map.MapObject;
import com.joymeng.slg.domain.map.MapUtil;
import com.joymeng.slg.domain.map.data.Worldbuildinglevel;
import com.joymeng.slg.domain.map.fight.result.ReportTitleType;
import com.joymeng.slg.domain.map.impl.MapRoleInfo;
import com.joymeng.slg.domain.map.impl.dynamic.ExpediteTroops;
import com.joymeng.slg.domain.map.impl.dynamic.GarrisonTroops;
import com.joymeng.slg.domain.map.impl.dynamic.GridType;
import com.joymeng.slg.domain.map.impl.dynamic.TroopsData;
import com.joymeng.slg.domain.map.impl.still.proxy.MapProxy;
import com.joymeng.slg.domain.map.physics.MapCell;
import com.joymeng.slg.domain.map.physics.MapCellType;
import com.joymeng.slg.domain.map.spyreport.data.SpyType;
import com.joymeng.slg.domain.object.IObject;
import com.joymeng.slg.domain.object.build.BuildName;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.task.ConditionType;
import com.joymeng.slg.domain.object.task.TaskEventDelay;
import com.joymeng.slg.domain.timer.TimerLast;
import com.joymeng.slg.domain.timer.TimerLastType;
import com.joymeng.slg.domain.timer.TimerOver;
import com.joymeng.slg.net.mod.RespModuleSet;
import com.joymeng.slg.world.GameConfig;

/**
 * 玩家要塞
 * @author tanyong
 *
 */
public class MapFortress extends MapObject implements TimerOver{
	protected String name = "atlas_fortd";//要塞名字
	protected byte level = 1;//要塞等级
	protected TimerLast buildTimer;//建筑倒计时
	protected List<GridType> grids = new ArrayList<GridType>();//格子
	
	@Override
	public int getLevel() {
		return level;
	}

	public void setLevel(byte level) {
		this.level = level;
	}

	public TimerLast getBuildTimer() {
		return buildTimer;
	}

	public void setBuildTimer(TimerLast buildTimer) {
		this.buildTimer = buildTimer;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Worldbuildinglevel getDataLevel(){
		Worldbuildinglevel wbl = dataManager.serach(Worldbuildinglevel.class, BuildName.MAP_FORTRESS.getKey() + level);
		return wbl;
	}
	
	/**
	 * 建造和驻扎的都是自己的
	 * @param uid
	 * @return
	 */
	public boolean isMyMaster(long uid){
		return info.getUid() == uid;
	}
	
	@Override
	public void _tick(long now) {
		if (buildTimer != null){
			if (buildTimer.over(now)){
				buildTimer.die();
			}
		}
	}

	@Override
	public void serialize(JoyBuffer out) {
		info.serialize(out);
		out.putPrefixedString(name,JoyBuffer.STRING_TYPE_SHORT);//string 要塞名字
		out.put(level);//byte 要塞等级
		if (buildTimer != null){
			 out.putInt(1);
			 buildTimer.serialize(out);
		}else{
			 out.putInt(0);
		}
		super.serialize(out);
		int size = gridNum();
		out.putInt(size);
		for (int i = 0 ; i < size ; i++){
			GridType grid = grids.get(i);
			if (grid.getType() == GarrisonTroops.class){
				GarrisonTroops obj = grid.object();
				if (obj == null){
					continue;
				}
				if (obj.getPosition() == position){
					out.putInt(0);
					out.putLong(grid.getId());
				}else{
					out.putInt(1);
					obj.serialize(out);
				}
			}else{
				out.putInt(2);
				ExpediteTroops obj = grid.object();
				if (obj == null){
					continue;
				}
				obj.serialize(out);
			}
		}
	}

	@Override
	public MapCellType cellType() {
		return MapCellType.MAP_CELL_TYPE_FORTRESS;
	}

	@Override
	public void troopsArrive(ExpediteTroops expedite) throws Exception{
		synchronized (expedite) {
			if (expedite.getTimer().getType() == TimerLastType.TIME_ARMY_BACK_FORTRESS){
				RespModuleSet rms = new RespModuleSet();
				Role role = world.getRole(info.getUid());
				//资源加到玩家主城
				expedite.packageBack(rms,role);
				//部队驻扎
				GarrisonTroops occuper = expedite.occuper(this);
				TimerLast timer = new TimerLast(TimerLastType.TIME_MAP_STATION);
				occuper.registTimer(timer);
				changeGrid(expedite,occuper);
				MessageSendUtil.sendModule(rms,role.getUserInfo());
			}else if (expedite.getTimer().getType() == TimerLastType.TIME_EXPEDITE_GARRISON){
				//驻防部队
				garrison(expedite);
			}else if (expedite.getTimer().getType() == TimerLastType.TIME_EXPEDITE_FIGHT){
				List<GarrisonTroops> defenders = getDefencers();//防守者
				boolean isWin = attackDefenders(defenders,expedite,ReportTitleType.TITLE_TYPE_A_FORTRESS,ReportTitleType.TITLE_TYPE_D_FORTRESS);
				if (isWin){
					remove();//移除要塞
					GameLog.info(expedite.getLeader().getInfo().getUid() + " attack " + info.getUid() + "'s fortress successful and destroy it at " + position);
				}else{
					GameLog.info(expedite.getLeader().getInfo().getUid() + " attack " + info.getUid() + "'s fortress fail at " + position);
				}
				Role role = world.getRole(expedite.getLeader().getInfo().getUid());
				role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(),ConditionType.C_ATK_WIN, isWin);
				expedite.goBackToCome();
			}else if (expedite.getTimer().getType() == TimerLastType.TIME_EXPEDITE_STATION){
				//驻扎部队
				GarrisonTroops garrison = expedite.occuper(this);
				TimerLast timer = new TimerLast(TimeUtils.nowLong()/1000,0,TimerLastType.TIME_MAP_STATION);
				garrison.registTimer(timer);
				Role role  = world.getRole(expedite.getLeader().getInfo().getUid());
				role.handleEvent(GameEvent.TROOPS_SEND);
				addGrid(garrison);
			}else if (expedite.getTimer().getType() == TimerLastType.TIME_EXPEDITE_SPY){
				//要塞侦查结果报告
				MapUtil.spyResport(SpyType.SPY_TYPE_FORTRESS, expedite,this);
				logSpy(expedite);
			}
		}
	}

	private GarrisonTroops getCreater(){
		List<GarrisonTroops> occupyers = getDefencers();
		if (occupyers.size() > 0){
			for (int i = 0 ; i < occupyers.size() ; i++){
				GarrisonTroops occupyer = occupyers.get(i);
				if (occupyer.getTimer().getType().ordinal() == TimerLastType.TIME_CREATE.ordinal()){
					return occupyer;
				}
			}
		}
		return null;
	}
	
	@Override
	public void finish() {
		GarrisonTroops creater = getCreater();
		Role role = world.getRole(info.getUid());
		if (creater != null){//建造时间到了
			TimerLast timer = creater.getTimer();
			timer.setType(TimerLastType.TIME_MAP_STATION);//修改部队状态
			timer.setLast(0);
			timer.removeTimeOver(this);
			LogManager.mapLog(role, role.getCity(0).getPosition(), creater.getPosition(), creater.getId(),EventName.buildFortComplete.getName());
		}else if (buildTimer != null){
			if (buildTimer.getType() == TimerLastType.TIME_LEVEL_UP){//升级
				level ++;
				setMapThreadFlag(true);
			}else if (buildTimer.getType() == TimerLastType.TIME_REMOVE){//拆除
				List<GarrisonTroops> garrisons = getDefencers();
				for (int i = 0 ; i < garrisons.size() ; i++){
					GarrisonTroops garrison = garrisons.get(i);
					garrison.die();
				}
				if (this instanceof MapBarracks){//军营的放弃
					info.clear();
					safeTimer = null;
					save();
				}else{
					remove();
				}
			}
			buildTimer = null;
		}
		if (role != null){
			role.handleEvent(GameEvent.TROOPS_SEND);
			//任务事件
			role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.COND_FORT, 1);
			role.sendViews(new RespModuleSet(),true);
		}
		sendChange();
	}

	@Override
	public String table() {
		return TABLE_RED_ALERT_FORTRESS;
	}

	@Override
	public void loadFromData(SqlData data) {
		super.loadFromData(data);
		String str = data.getString(RED_ALERT_GENERAL_SAFETIMER);
		safeTimer = JsonUtil.JsonToObject(str,TimerLast.class);
		name   = data.getString(RED_ALERT_GENERAL_NAME);
		level  = data.getByte(RED_ALERT_GENERAL_LEVEL);
		GarrisonTroops creater = getCreater();
		if (creater != null){
			creater.registTimer(creater.getTimer(),this);//注册建造倒计时结束逻辑
		}
		str  = data.getString(RED_ALERT_GENERAL_BUILD_TIMER);
		buildTimer = JsonUtil.JsonToObject(str,TimerLast.class);
		if (buildTimer != null){
			buildTimer.registTimeOver(this);
			taskPool.mapTread.addObj(this,buildTimer);
		}
		str = data.getString(RED_ALERT_FORTRESS_INFO);
		info = JsonUtil.JsonToObject(str,MapRoleInfo.class);
		//str = data.getString(RED_ALERT_FORTRESS_GRIDS);
		//grids = JsonUtil.JsonToObjectList(str,GridType.class);
		loadCheck();
	}
	
    void loadCheck(){
    	List<GarrisonTroops> gts = world.getListObjects(GarrisonTroops.class);
		for (int i = 0 ; i < gts.size() ; i++){
			GarrisonTroops troops = gts.get(i);
			if (troops.getTroops().getComePosition() == position ||
				(troops.getTimer().getType() == TimerLastType.TIME_MAP_STATION &&
				troops.getPosition() == position)){
				addGrid(troops);
			}
		}
    	List<ExpediteTroops> ets = world.getListObjects(ExpediteTroops.class);
		for (int i = 0 ; i < ets.size() ; i++){
			ExpediteTroops troops = ets.get(i);
			boolean insert = false;
			for (int j = 0 ; j < troops.getTeams().size() ; j++){
				TroopsData td = troops.getTeams().get(j);
				if (td.getComePosition() == position){
					insert = true;
					break;
				}
			}
			if (insert){
				addGrid(troops);
			}
		}
    }
    
	protected void checkGridsEmpty(){
		synchronized (grids) {
			for (int i = 0 ; i < grids.size() ; ){
				GridType grid = grids.get(i);
				if (grid == null || grid.object() == null){
					grids.remove(i);
					continue;
				}
				i++;
			}
		}
	}
	
	@Override
	public void saveToData(SqlData data) {
		super.saveToData(data);
		String str = JsonUtil.ObjectToJsonString(safeTimer);
		data.put(RED_ALERT_GENERAL_SAFETIMER,str);
		data.put(RED_ALERT_GENERAL_NAME,name);
		data.put(RED_ALERT_GENERAL_LEVEL,level);
		str = JsonUtil.ObjectToJsonString(buildTimer);
		data.put(RED_ALERT_GENERAL_BUILD_TIMER,str);
		str = JsonUtil.ObjectToJsonString(info);
		data.put(RED_ALERT_FORTRESS_INFO,str);
		str = JsonUtil.ObjectToJsonString(grids);
		data.put(RED_ALERT_FORTRESS_GRIDS,str);
	}

	public void registSafeTimer(TimerLast timer) {
		safeTimer = timer;
	}
	
	public boolean completeCreate(Role role, byte type, String datas){
		GarrisonTroops creater = getCreater();
		if (creater != null){
			if (creater.getTroops().getInfo().getUid() != role.getId()){
				GameLog.error("when " + role.getId() + " tried to finish the fortress , but he is not the owner of the it");
				return false;
			}
			long now = TimeUtils.nowLong() / 1000;
			long leftTime = creater.getTimer().getStart() +  creater.getTimer().getLast() - now;
			if (leftTime <= 0){
				return false;
			}
			if (type == 0){
				int costMoney = role.timeChgMoney(leftTime,(byte)0);
				if (!role.redRoleMoney(costMoney)){
					MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_ROLE_NO_MONEY,costMoney);
					return false;
				}
				LogManager.goldConsumeLog(role, costMoney, EventName.completeCreate.getName());
				creater.die();
				if (role.isOnline()){
					RespModuleSet rms = new RespModuleSet();
					role.sendRoleToClient(rms);
					MessageSendUtil.sendModule(rms,role.getUserInfo());
				}
			}else{
				
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean relevance(long id) {
		if (super.relevance(id)){
			return true;
		}
		return info.getUid() == id;
	}
	
	public boolean cancle(UserInfo info) {
		if (buildTimer == null){
			return false;
		}
		setMapThreadFlag(true);
		buildTimer = null;
		return true;
	}
	
	public boolean levelUp(Worldbuildinglevel wbl,Role role) {
		GarrisonTroops creater = getCreater();
		if (creater != null){
			MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_MAP_FORTRESS_CREATE_ING);
			return false;
		}
		if (buildTimer != null){//建筑队列被占用
			if (buildTimer.getType().ordinal() == TimerLastType.TIME_LEVEL_UP.ordinal()){
				MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_MAP_FORTRESS_LEVEL_ING);
			}else{
				MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_MAP_FORTRESS_REMOVE_ING);
			}
			return false;
		}
		//开始升级倒计时
		long now = TimeUtils.nowLong() / 1000;
		long time = role.getGmFortressLevelUpTime() > 0 ? role.getGmFortressLevelUpTime() : wbl.getTime();
		buildTimer = new TimerLast(now,time,TimerLastType.TIME_LEVEL_UP);
		buildTimer.registTimeOver(this);
		taskPool.mapTread.addObj(this,buildTimer);
		return true;
	}
	
	public boolean drop(Role role) {
		GarrisonTroops creater = getCreater();
		if (creater != null) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_MAP_FORTRESS_CREATE_ING);
			return false;
		}
		if (buildTimer != null){//建筑队列被占用
			if (buildTimer.getType().ordinal() == TimerLastType.TIME_LEVEL_UP.ordinal()){
				MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_MAP_FORTRESS_LEVEL_ING);
			}else{
				MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_MAP_FORTRESS_REMOVE_ING);
			}
			return false;
		}
		long now  = TimeUtils.nowLong() / 1000;
		long time = role.getGmFortressDropTime() > 0 ? role.getGmFortressDropTime() : 3600;
		buildTimer = new TimerLast(now,time,TimerLastType.TIME_REMOVE);
		buildTimer.registTimeOver(this);
		taskPool.mapTread.addObj(this,buildTimer);
		return true;
	}

	public ExpediteTroops tryToMove(Role role, int target, byte type,long troopId) {
		if (target < 0 || target > GameConfig.MAP_WIDTH * GameConfig.MAP_HEIGHT){
			return null;
		}
		MapCell targetCell = mapWorld.getMapCell(target);
		if (targetCell.getType().ordinal() == MapCellType.MAP_CELL_TYPE_RESIST.ordinal()){
			MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_ROLE_EXPEDITE_FAIL);
			return null;
		}
		TimerLastType expediteType = null;
		if (type == 0){//建造要塞
			expediteType = TimerLastType.TIME_EXPEDITE_CREATE_FORTRESS;
		}else if (type == 1){//建筑迁城点
			expediteType = TimerLastType.TIME_EXPEDITE_CREATE_MOVE;
		}else if (type == 4 || type == 6 || type == 7){
			return null;
		}else if (type == 3){
			expediteType = TimerLastType.TIME_EXPEDITE_GARRISON;
		}else if (type == 5){//调拨
			MapObject targetObj = mapWorld.searchObject(targetCell);
			if (targetObj == null || !(targetObj instanceof MapFortress)){
				GameLog.error("目的地不是要塞或者军营");
				return null;
			}
			MapFortress fortress = (MapFortress)targetObj;
			if (fortress.getEmptyNum() == 0){
				MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_ROLE_NO_GRID);
				return null;
			}
			expediteType = TimerLastType.TIME_EXPEDITE_STATION;
		}else if (type == 8){//联盟采集
			expediteType = TimerLastType.TIME_EXPEDITE_UNION_RES_COLLECT;
		}else if (type == 9){//去副本的路上
			expediteType = TimerLastType.TIME_GO_TO_ECTYPE;
		}else{
			if (targetCell.getType() == MapCellType.MAP_CELL_TYPE_UINON_CITY && role.getUnionId() == 0){
				MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_ROLE_NO_UNION,role.getName());
				return null;
			}
			expediteType = mapWorld.getExpediteOutType(targetCell,info.getUnionId());
		}
		if (targetCell.getTypeKey() == null && type == 4){
			MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_ROLE_EXPEDITE_FAIL);
			return null;
		}else if (targetCell.getTypeKey() == null && type <= 1){//去建造的时候选中的是空格子
			MapProxy proxy = null;
			if (expediteType.ordinal() == TimerLastType.TIME_EXPEDITE_CREATE_FORTRESS.ordinal()){
				proxy = mapWorld.create(false,MapCellType.MAP_CELL_TYPE_FORTRESS_PROXY);
			}else if (expediteType.ordinal() == TimerLastType.TIME_EXPEDITE_CREATE_MOVE.ordinal()){
				proxy = mapWorld.create(false,MapCellType.MAP_CELL_TYPE_MOVE_PROXY);
			}
			if (proxy != null && mapWorld.checkPosition(proxy,target)){//这个代理类能放下
				mapWorld.insertObj(proxy);
				mapWorld.updatePosition(proxy,target);
			}else{
				MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_ROLE_EXPEDITE_FAIL);
				return null;
			}
		}
		GarrisonTroops selectTroops = null;
		List<GarrisonTroops> troopses = getDefencers();
		for (int i = 0 ; i < troopses.size() ; i++){
			GarrisonTroops troops = troopses.get(i);
			if (troops.getId() == troopId){
				selectTroops = troops;
			}
		}
		if (selectTroops == null){
			GameLog.error("客户端传的部队编号错误");
			return null;
		}
		if (selectTroops.getAliveNum() == 0){
			MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_STATION_TROOPS_DIE_ALL);
			return null;
		}
		ExpediteTroops expedite = selectTroops.tryToMove(role,target,expediteType);
		if (type == 5 || type <= 1){//调拨出去了就把格子让出来
			removeGrid(selectTroops);
		}else{//不是调拨出去的就修改格子
			changeGrid(selectTroops,expedite);
		}
		return expedite;
	}
	
	public int getEmptyNum(){
		return level - grids.size();
	}
	
	public int gridNum() {
		synchronized (grids) {
			int count = 0;
			for (int i = 0 ; i < grids.size() ; i++){
				GridType grid = grids.get(i);
				if (grid.object() != null){
					count ++;
				}
			}
			return count;
		}
	}
	
	public void addGrid(IObject obj) {
		if (grids.size() > level){
			return;
		}
		GridType grid = new GridType();
		grid.setType(obj.getClass());
		grid.setId(obj.getId());
		grids.add(grid);
	}
	
	public void changeGrid(IObject pre,IObject obj) {
		//checkGridsEmpty();
		if (grids.size() == 0){
			addGrid(obj);
		}else{
			GridType target = null;
			for (int i = 0 ; i < grids.size() ; i++){
				GridType grid = grids.get(i);
				if (grid.getId() == pre.getId()){
					target = grid;
					break;
				}
			}
			if (target != null){
				target.setType(obj.getClass());
				target.setId(obj.getId());
			}
		}
	}
	
	public void removeGrid(IObject obj) {
		for (int i = 0 ; i < grids.size() ; i++){
			GridType grid = grids.get(i);
			if (grid.getId() == obj.getId() && grid.getType() == obj.getClass()){
				grids.remove(grid);
				return;
			}
		}
	}
	
	@Override
	public boolean _couldAttack(ExpediteTroops expedite){
		long unionId = info.getUnionId();
		if (unionId != 0 && info.getUnionId() == expedite.getLeader().getInfo().getUnionId()){
			return false;
		}
		return true;
	}

	/**
	 * 获取我的视野
	 * @param views
	 */
	public void getViews(List<Integer> views) {
		Worldbuildinglevel wbl = getDataLevel();
		List<String> lis = wbl.getParamList();
		int w = Integer.parseInt(lis.get(0));
		int h = Integer.parseInt(lis.get(1));
		List<Integer> temp = MapUtil.computeIndexs(position,w,h);
		for (int i = 0 ; i < temp.size() ; i++){
			Integer pos = temp.get(i);
			if (!views.contains(pos)){
				views.add(pos);
			}
		}
	}
	
	@Override
	public boolean destroy(String buffStr){
		return super.destroy(buffStr);
		//视野的逻辑不要了
		//Role role = world.getOnlineRole(info.getUid());
		//if (role != null){
		//	role.sendViews(new RespModuleSet(),true);
		//}
		//return false;
	}
	
}
