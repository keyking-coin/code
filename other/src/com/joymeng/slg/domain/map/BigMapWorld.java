
package com.joymeng.slg.domain.map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import javax.imageio.ImageIO;

import com.joymeng.Const;
import com.joymeng.Instances;
import com.joymeng.common.util.JsonUtil;
import com.joymeng.common.util.MathUtils;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.log.GameLog;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.dao.DaoData;
import com.joymeng.slg.dao.SqlData;
import com.joymeng.slg.domain.data.SearchFilter;
import com.joymeng.slg.domain.map.data.CampDistributionData;
import com.joymeng.slg.domain.map.data.DistributionData;
import com.joymeng.slg.domain.map.data.FubenDistributionData;
import com.joymeng.slg.domain.map.data.Monster;
import com.joymeng.slg.domain.map.data.Monsterrefresh;
import com.joymeng.slg.domain.map.data.NPCDistributionData;
import com.joymeng.slg.domain.map.data.Npccity;
import com.joymeng.slg.domain.map.data.Resourcerefresh;
import com.joymeng.slg.domain.map.data.UserDistribution;
import com.joymeng.slg.domain.map.impl.MapRoleInfo;
import com.joymeng.slg.domain.map.impl.dynamic.ExpediteTroops;
import com.joymeng.slg.domain.map.impl.dynamic.GarrisonTroops;
import com.joymeng.slg.domain.map.impl.dynamic.MassTroops;
import com.joymeng.slg.domain.map.impl.dynamic.TroopsData;
import com.joymeng.slg.domain.map.impl.still.copy.Scene;
import com.joymeng.slg.domain.map.impl.still.copy.data.Ruinscheckpoin;
import com.joymeng.slg.domain.map.impl.still.moster.MapMonster;
import com.joymeng.slg.domain.map.impl.still.moster.MonsterRefreshAble;
import com.joymeng.slg.domain.map.impl.still.proxy.MapProxy;
import com.joymeng.slg.domain.map.impl.still.res.MapEctype;
import com.joymeng.slg.domain.map.impl.still.res.MapResource;
import com.joymeng.slg.domain.map.impl.still.res.ResourceRefreshAble;
import com.joymeng.slg.domain.map.impl.still.role.MapBarracks;
import com.joymeng.slg.domain.map.impl.still.role.MapCity;
import com.joymeng.slg.domain.map.impl.still.role.MapCityMove;
import com.joymeng.slg.domain.map.impl.still.role.MapFortress;
import com.joymeng.slg.domain.map.impl.still.union.MapRadiation;
import com.joymeng.slg.domain.map.impl.still.union.MapUnionBuild;
import com.joymeng.slg.domain.map.impl.still.union.MapUnionCity;
import com.joymeng.slg.domain.map.physics.MapCell;
import com.joymeng.slg.domain.map.physics.MapCellType;
import com.joymeng.slg.domain.map.physics.PointVector;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.timer.TimerLastType;
import com.joymeng.slg.net.resp.CommunicateResp;
import com.joymeng.slg.world.GameConfig;
import com.joymeng.slg.world.TaskPool;

public class BigMapWorld implements Instances {

	private static final int PHYSIC_DATA_FLAG_RESIST = 1;

	private static final int PHYSIC_DATA_FLAG_SLOW = 1 << 1;

	AtomicLong idCreater = new AtomicLong(1);
	
	private static BigMapWorld instance = new BigMapWorld();

	Object positionLocker = new Object();

	MapCell[] mapCells;// 地图格子数据

	public static BigMapWorld getInstance() {
		return instance;
	}

	public MapProxy create(boolean insert, MapCellType type) {
		MapProxy proxy = new MapProxy();
		proxy.setCellType(type);
		if (insert) {
			insertObj(proxy);
		}
		return proxy;
	}

	public <T extends MapObject> T create(Class<T> clazz, boolean insert) {
		try {
			T t = clazz.newInstance();
			if (insert) {
				insertObj(t);
			}
			return t;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void insertObj(MapObject obj) {
		obj.id = idCreater.incrementAndGet();
		obj.addSelf();
	}

	/***
	 * 保存
	 */
	public void save() {
		//玩家主城保存
		List<MapCity> citys = world.getListObjects(MapCity.class);
		for (int i = 0 ; i < citys.size() ; i++){
			MapCity city = citys.get(i);
			city.save();
		}
		//保存不动的部队
		List<GarrisonTroops> occupers = world.getListObjects(GarrisonTroops.class);
		for (int i = 0 ; i < occupers.size() ; i++){
			GarrisonTroops occuper = occupers.get(i);
			occuper.save();
			MapObject obj = searchObject(occuper.getPosition());
			if (obj != null) {
				obj.save();
			}
		}
		//保存行军部队
		List<ExpediteTroops> expedites = world.getListObjects(ExpediteTroops.class);
		for (int i = 0 ; i < expedites.size() ; i++){
			ExpediteTroops expedite = expedites.get(i);
			expedite.save();
			MapObject obj = searchObject(expedite.getStartPosition());
			if (obj != null) {//出发点保存
				obj.save();
			}
			obj = searchObject(expedite.getTargetPosition());
			if (obj != null ) {//结束点建造保存
				obj.save();
			}
		}
		//保存要塞
		List<MapFortress> fortresses = world.getListObjects(MapFortress.class);
		for (int i = 0 ; i < fortresses.size() ; i++){
			MapFortress fortress = fortresses.get(i);
			fortress.save();
		}
		//保存军营
		List<MapBarracks> barrackses = world.getListObjects(MapBarracks.class);
		for (int i = 0 ; i < barrackses.size() ; i++){
			MapBarracks barracks = barrackses.get(i);
			barracks.save();
		}
		//城市保存
		List<MapUnionCity> unionCitys = world.getListObjects(MapUnionCity.class);
		for (int i = 0 ; i < unionCitys.size() ; i++){
			MapUnionCity city = unionCitys.get(i);
			city.save();
		}
		//核辐射保存
		List<MapRadiation> radiations = world.getListObjects(MapRadiation.class);
		for (int i = 0 ; i < radiations.size() ; i++){
			MapRadiation radiation = radiations.get(i);
			radiation.save();
		}
		worldSInfo.save();
		forbidden.save();
	}

	public MapCell getMapCell(int position) {
		if (position < 0 || position > GameConfig.MAP_WIDTH * GameConfig.MAP_HEIGHT) {
			return null;
		}
		return mapCells[position];
	}

	/**
	 * 浏览地图一个矩形区域
	 * @param role
	 * @param resp
	 * @param centerX
	 * @param centerY
	 * @param width
	 * @param height
	 */
	public void searchAround(Role role, CommunicateResp resp, int centerX,int centerY, int width, int height) {
		List<Integer> indexs = MapUtil.getRangeIndexs(centerX, centerY, width,height);
		List<MapObject> objs = new ArrayList<MapObject>();
		List<ExpediteTroops> expedites = new ArrayList<ExpediteTroops>();
		MapUtil.searchDatas(indexs,objs,expedites);
		resp.add(objs.size());// int 格子数量
		for (int i = 0 ; i < objs.size() ; i++){
			MapObject obj = objs.get(i);
			int col = PointVector.getX(obj.getPosition());
			int row = PointVector.getY(obj.getPosition());
			resp.add(col);//int 在地图格子坐标x
			resp.add(row);//int 在地图格子坐标y
			resp.add(obj.getId());//long 在地图格子坐标x
			resp.add(obj.cellType().ordinal());//int 地图固定对象类型编号
			resp.add(obj);
		}
		//行军部队信息
		resp.add(expedites.size());//int 行军部队数量
		for (int i = 0 ; i  < expedites.size() ; i++){
			ExpediteTroops expedite = expedites.get(i);
			expedite.addLook(role.getId());
			resp.add(expedite);
		}
	}

	/**
	 * 玩家主城被打爆了后,随机一个新位置
	 * 
	 * @param obj
	 * @return
	 */
	public int randomDrop(MapObject obj) {
		synchronized (positionLocker) {
			int len = GameConfig.MAP_WIDTH * GameConfig.MAP_HEIGHT;
			while (true) {
				int pos = MathUtils.random(len);
				if (checkPosition(obj, pos)) {
					setPositionsType(MapUtil.computeIndexs(pos, obj.getVolume()),MapCellType.MAP_CELL_TYPE_RESIST);
					return pos;
				}
			}
		}
	}

	/**
	 * 玩家注册时候降临的逻辑，出现在策划设计的新手区域
	 * @param obj
	 */
	public void bornAtBigMap(MapObject obj) {
		int rectIndex = MathUtils.random(1,4);
		final String rectName = String.valueOf(rectIndex);
		List<UserDistribution> fileDatas = dataManager.serachList(UserDistribution.class,new SearchFilter<UserDistribution>(){
			@Override
			public boolean filter(UserDistribution data) {
				return data.getName().equals(rectName);
			}
		});
		Collections.sort(fileDatas);
		for (int i = 0 ; i < fileDatas.size() ; i++){
			UserDistribution data = fileDatas.get(i);
			List<Integer> indexs = MapUtil.getRangeIndexs(data.getCenterX(), data.getCenterY(), data.getRangeX(),data.getRangeY());
			int roleCount = 0;
			for (int j = 0 ; j  < indexs.size() ; ){
				int indexId = indexs.get(j).intValue();
				if (mapCells[indexId].getType() == MapCellType.MAP_CELL_TYPE_ROLE_CITY) {
					roleCount ++;
				}else if (mapCells[indexId].getType() != MapCellType.MAP_CELL_TYPE_NONE) {// 空地
					indexs.remove(j);
					continue;
				}
				j++;
			}
			int count = data.getCount() - roleCount;// 需要刷新的个数
			if (count <= 0) {
				continue;
			}
			int maxCount = 500;// 防止死循环
			while (maxCount > 0) {
				maxCount--;
				int index = MathUtils.random(indexs.size());
				int position = indexs.get(index).intValue();
				if (checkAndUpdatePosition(obj, position)) {// 判断是否能放下，如果呢过发下就发下
					return;
				}
			}
		}
	}

	/**
	 * 判断并设置位置
	 * 
	 * @param obj
	 * @param position
	 * @return
	 */
	public boolean checkAndUpdatePosition(MapObject obj, int position) {
		if (obj == null) {
			return false;
		}
		if (checkPosition(obj, position)) {
			updatePosition(obj, position);
			return true;
		}
		return false;
	}

	/**
	 * 在某个地方强制放一个建筑
	 * 
	 * @param obj
	 * @param old
	 * @param pos
	 * 
	 */
	public void forceInsert(MapObject old, MapObject obj, int pos) {
		synchronized (positionLocker) {
			if (old != null) {
				List<Integer> indexs = old.reject();
				for (int i = 0 ; i < indexs.size() ; i++){
					Integer index = indexs.get(i);
					MapCell mapCell = mapCells[index.intValue()];
					mapCell.clear(MapCellType.MAP_CELL_TYPE_NONE);
				}
			}
			List<Integer> indexs = MapUtil.computeIndexs(pos, obj.getVolume());
			for (int i = 0 ; i < indexs.size() ; i++){
				Integer index = indexs.get(i);
				mapCells[index.intValue()].clear(MapCellType.MAP_CELL_TYPE_RESIST);
			}
			mapCells[pos].init(obj);
			obj.setPosition(pos);
		}
	}

	/**
	 * 清理成空地
	 * 
	 * @param obj
	 */
	public void clearPosition(MapObject obj) {
		synchronized (positionLocker) {
			List<Integer> indexs = obj.reject();
			for (int i = 0 ; i < indexs.size() ; i++){
				Integer index = indexs.get(i);
				MapCell mapCell = mapCells[index.intValue()];
				mapCell.clear(MapCellType.MAP_CELL_TYPE_NONE);
			}
		}
	}

	/**
	 * 设置一些坐标格子的状态
	 * 
	 * @param indexs
	 * @param type
	 */
	public void setPositionsType(List<Integer> indexs, MapCellType type) {
		synchronized (positionLocker) {
			for (int i = 0 ; i < indexs.size() ; i++){
				Integer index = indexs.get(i);
				MapCell mapCell = mapCells[index.intValue()];
				mapCell.clear(type);
			}
		}
	}
	
	/**
	 * 判断位置能不能放的下
	 * 
	 * @param radius
	 * @param position
	 * @return
	 */
	public boolean checkPosition(int radius, int position) {
		synchronized (positionLocker) {
			List<Integer> indexs = MapUtil.computeIndexs(position,radius);
			if (indexs != null) {
				if (radius == 0 && indexs.size() > 0) {
					return true;
				} else if (indexs.size() > 0 && indexs.size() == (radius * 2 + 1) * (radius * 2 + 1)) {// 需要的格子够
					//所有的排斥格子都必须是空地
					int count = 0;
					for (int i = 0 ; i < indexs.size() ; i++){
						Integer index = indexs.get(i);
						MapCellType cellType = mapCells[index.intValue()].getType();
						if (cellType == MapCellType.MAP_CELL_TYPE_NONE /*|| (objType.isProxy() && cellType == objType)*/) {
							count++;
						}
					}
					return count == indexs.size();
				}
			}
			return false;
		}
	}
	
	/**
	 * 判断位置能不能放的下
	 * 
	 * @param obj
	 * @param position
	 * @return
	 */
	public boolean checkPosition(MapObject obj, int position) {
		return checkPosition(obj.getVolume(),position);
	}

	/**
	 * 设置位置信息
	 * 
	 * @param obj
	 * @param position
	 */
	public void updatePosition(MapObject obj, int position) {
		synchronized (positionLocker) {
			if (obj != null) {
				MapCellType type = obj.cellType();// 如果是代理类,周围就是代理本身的类型
				MapCellType other = type.isProxy() ? type : MapCellType.MAP_CELL_TYPE_RESIST;
				List<Integer> indexs = MapUtil.computeIndexs(position,obj.getVolume());
				for (int i = 0 ; i < indexs.size() ; i++){// 把其他格子填充成阻挡层
					Integer index = indexs.get(i);
					mapCells[index].clear(other);
				}
				mapCells[position].init(obj);
				obj.setPosition(position);
			} else {// 没有对象的时候就设置成空地
				mapCells[position].clear(MapCellType.MAP_CELL_TYPE_NONE);
			}
		}
	}

	public void clearIndexs(List<Integer> indexs) {
		for (int i = 0; i < indexs.size();) {
			Integer index = indexs.get(i);
			if (mapCells[index].getType() != MapCellType.MAP_CELL_TYPE_NONE) {
				indexs.remove(i);
			} else {
				i++;
			}
		}
	}
	
	private void sqlJoin(StringBuffer sb,String tableName,String colName,String newName){
		sb.append(tableName + "." + colName + " as " + newName);
	}
	
	/**
	 * 加载所有玩家对象
	 */
	private void loadRoles() throws Exception {
		GameLog.info("load all roles to map");
		StringBuffer sb = new StringBuffer();
		sb.append("select ");
		sqlJoin(sb,DaoData.TABLE_RED_ALERT_CITY,DaoData.RED_ALERT_GENERAL_ID,DaoData.RED_ALERT_GENERAL_ID);
		sb.append(",");
		sqlJoin(sb,DaoData.TABLE_RED_ALERT_CITY,DaoData.RED_ALERT_GENERAL_POSITION,DaoData.RED_ALERT_GENERAL_POSITION);
		sb.append(",");
		sqlJoin(sb,DaoData.TABLE_RED_ALERT_CITY,DaoData.RED_ALERT_CITY_MESS,DaoData.RED_ALERT_CITY_MESS);
		sb.append(",");
		sqlJoin(sb,DaoData.TABLE_RED_ALERT_CITY,DaoData.RED_ALERT_GENERAL_UID,DaoData.RED_ALERT_GENERAL_UID);
		sb.append(",");
		sqlJoin(sb,DaoData.TABLE_RED_ALERT_CITY,DaoData.RED_ALERT_CITY_STATUS,DaoData.RED_ALERT_CITY_STATUS);//城池状态
		sb.append(",");
		sqlJoin(sb,DaoData.TABLE_RED_ALERT_CITY,DaoData.RED_ALERT_CITY_LEVEL,DaoData.RED_ALERT_GENERAL_LEVEL);
		sb.append(",");
		sqlJoin(sb,DaoData.TABLE_RED_ALERT_CITY,DaoData.RED_ALERT_CITY_RADARLVL,DaoData.RED_ALERT_CITY_RADARLVL);//雷达等级
		sb.append(",");
		sqlJoin(sb,DaoData.TABLE_RED_ALERT_ROLE,DaoData.RED_ALERT_GENERAL_NAME,DaoData.RED_ALERT_GENERAL_NAME);
		sb.append(",");
		sqlJoin(sb,DaoData.TABLE_RED_ALERT_ROLE,DaoData.RED_ALERT_GENERAL_UNION_ID,DaoData.RED_ALERT_GENERAL_UNION_ID);
		sb.append(",");
		sqlJoin(sb,DaoData.TABLE_RED_ALERT_ROLE,DaoData.RED_ALERT_ROLE_ICON_TYPE,DaoData.RED_ALERT_ROLE_ICON_TYPE);
		sb.append(",");
		sqlJoin(sb,DaoData.TABLE_RED_ALERT_ROLE,DaoData.RED_ALERT_ROLE_ICON_ID,DaoData.RED_ALERT_ROLE_ICON_ID);
		sb.append(",");
		sqlJoin(sb,DaoData.TABLE_RED_ALERT_ROLE,DaoData.RED_ALERT_ROLE_ICON_NAME,DaoData.RED_ALERT_ROLE_ICON_NAME);
		sb.append(" from " + DaoData.TABLE_RED_ALERT_ROLE);
		sb.append(" inner join " + DaoData.TABLE_RED_ALERT_CITY);
		sb.append(" on " + DaoData.TABLE_RED_ALERT_ROLE + "." + DaoData.RED_ALERT_ROLE_ID + "=" + DaoData.TABLE_RED_ALERT_CITY + "." + DaoData.RED_ALERT_GENERAL_UID);
		String sql  = sb.toString();
		List<Map<String, Object>> datas = dbMgr.getGameDao().getDatasBySql(sql);
		if (datas != null) {
			for (int i = 0 ; i < datas.size() ; i++){
				Map<String, Object> map = datas.get(i);
				SqlData sd = new SqlData(map);
				MapCity mapCity = create(MapCity.class,true);
				mapCity.loadFromData(sd);
				updatePosition(mapCity,mapCity.getPosition());
			}
		}
	}

	/**
	 * 加载所有刷新需要保存的对象
	 */
	private void loadRefreshObjs() throws Exception {
		GameLog.info("load all saved resources to map");
		List<Map<String, Object>> datas = dbMgr.getGameDao().getDatasByTableName(DaoData.TABLE_RED_ALERT_RESOURCES);
		if (datas != null) {
			for (int i = 0 ; i < datas.size() ; i++){
				Map<String, Object> map = datas.get(i);
				MapResource resource = create(MapResource.class, true);
				resource.loadFromData(new SqlData(map));
				updatePosition(resource, resource.getPosition());
			}
		}
		//已保存的资源田加载后就删除
		String delSql = "delete from " + DaoData.TABLE_RED_ALERT_RESOURCES;
		dbMgr.getGameDao().getSimpleJdbcTemplate().update(delSql);
		GameLog.info("load all saved monster to map");
		datas = dbMgr.getGameDao().getDatasByTableName(DaoData.TABLE_RED_ALERT_MONSTER);
		if (datas != null) {
			for (int i = 0 ; i < datas.size() ; i++){
				Map<String, Object> map = datas.get(i);
				MapMonster monster = create(MapMonster.class,true);
				monster.loadFromData(new SqlData(map));
				updatePosition(monster,monster.getPosition());
			}
		}
		delSql = "delete from " + DaoData.TABLE_RED_ALERT_MONSTER;
		dbMgr.getGameDao().getSimpleJdbcTemplate().update(delSql);
	}

	/**
	 * 加载所有驻防队伍
	 */
	private void loadGarrisons() throws Exception {
		GameLog.info("load all garrisons to map");
		List<Map<String, Object>> datas = dbMgr.getGameDao().getDatasByTableName(DaoData.TABLE_RED_ALERT_GARRISON);
		if (datas != null) {
			for (int i = 0 ; i < datas.size() ; i++){
				Map<String, Object> map = datas.get(i);
				GarrisonTroops occuper = new GarrisonTroops();
				occuper.loadFromData(new SqlData(map));
				occuper.addSelf();
			}
		}
	}

	/**
	 * 加载所有行军部队
	 */
	private void loadExpeditions() throws Exception {
		GameLog.info("load all expeditions to map");
		List<Map<String, Object>> datas = dbMgr.getGameDao().getDatasByTableName(DaoData.TABLE_RED_ALERT_ROLEEXPEDITE);
		if (datas != null) {
			for (int i = 0 ; i < datas.size() ; i++){
				Map<String, Object> map = datas.get(i);
				ExpediteTroops expedite = new ExpediteTroops();
				expedite.loadFromData(new SqlData(map));
				expedite.addSelf();
				MapCell s_cell = getMapCell(expedite.getStartPosition());
				s_cell.expedite(expedite.getId());
				MapCell t_cell = getMapCell(expedite.getTargetPosition());
				t_cell.expedite(expedite.getId());
			}
		}
	}

	/**
	 * 根据导出文件刷新资源点
	 */
	private void refreshResources() throws Exception {
		GameLog.info("start refresh resources at "+ TimeUtils.nowStr());
		if (GameConfig.BIG_MAP_USE_NEW_MONSTER){
			List<Resourcerefresh> rfs = dataManager.serachList(Resourcerefresh.class);
			for (int i = 0 ; i < rfs.size() ; i++){
				Resourcerefresh rf = rfs.get(i);
				ResourceRefreshAble rr = new ResourceRefreshAble(rf.getId(),true);
				rr.run();
				long time = rf.getRefreshTime();
				if (time > 0){
					long delay = (i + 1) * 60 + time;
					taskPool.scheduleAtFixedRate(null,rr,delay,time,TimeUnit.SECONDS);
				}
			}
		}else{
			List<DistributionData> fileDatas = dataManager.serachList(DistributionData.class);
			for (int i = 0 ; i < fileDatas.size() ; i++){
				DistributionData data = fileDatas.get(i);
				List<Integer> indexs = MapUtil.getRangeIndexs(data.getCenterX(),data.getCenterY(), data.getRangeX(), data.getRangeY());
				int count = Math.min(data.getCount(), indexs.size());
				int have = getRefreshAliveCount(data.getType() == 1,data.getId(),false);
				if (have >= count) {
					continue;
				}
				count -= have;//需要刷新的个数
				int[][] typeIndexs = data.computeTypeIndexs();
				if (data.getType() == 1) {// 资源点
					int[][] leveIndexs = data.computeLevelIndexs();
					do {
						MapResource obj = create(MapResource.class, false);
						int index = MathUtils.random(indexs.size());
						int position = indexs.get(index).intValue();
						if (!checkPosition(obj, position)) {// 如果这个位置放不下
							continue;
						}
						int typeIndex = MathUtils.getRandomInt(typeIndexs[0],typeIndexs[1]);
						String typeKey = data.getNeedDistribution().get(typeIndex).getpName();
						int levelIndex = MathUtils.getRandomInt(leveIndexs[0],leveIndexs[1]);
						String levelStr = data.getNeedProbavility().get(levelIndex).getpName();
						int level = Integer.parseInt(levelStr);
						insertObj(obj);
						obj.setLevel(level);
						obj.setKey(typeKey);
						obj.setRefreshId(data.getId());
						obj.initOutPut();
						updatePosition(obj, position);// 如果能放下就放这里
						clearIndexs(indexs);// 移除被占的格子
						count--;
					} while (count > 0 && indexs.size() > 0);
				} else if (data.getType() == 2) {//怪物
					do {
						MapMonster monster = create(MapMonster.class,false);
						int index = MathUtils.random(indexs.size());
						int position = indexs.get(index).intValue();
						if (!checkPosition(monster, position)) {// 如果这个位置放不下
							continue;
						}
						int typeIndex = MathUtils.getRandomInt(typeIndexs[0],typeIndexs[1]);
						String typeKey = data.getNeedDistribution().get(typeIndex).getpName();
						Monster monsterData = dataManager.serach(Monster.class,typeKey);
						if (monsterData == null) {
							GameLog.error("策划SB,把怪物类型字符串填错了");
							break;
						}
						monster.setKey(typeKey);
						monster.setLevel(monsterData.getLevel());
						monster.setRefreshId(data.getId());
						insertObj(monster);
						updatePosition(monster,position);//如果能放下就放这里
						clearIndexs(indexs);// 移除被占的格子
						count--;
					} while (count > 0 && indexs.size() > 0);
				}
			}
		}
		GameLog.info("end refresh resources at " + TimeUtils.nowStr());
	}

	private void refreshMonsters(){
		if (!GameConfig.BIG_MAP_USE_NEW_MONSTER){
			return;
		}
		GameLog.info("start refresh monsters at "+ TimeUtils.nowStr());
		List<Monsterrefresh> mfs = dataManager.serachList(Monsterrefresh.class);
		for (int i = 0 ; i < mfs.size() ; i++){
			Monsterrefresh mf = mfs.get(i);
			if (mf.getActivity().equals("false")){
				MonsterRefreshAble rr = new MonsterRefreshAble(mf.getId(),true);
				rr.run();
				long time = mf.getRefreshTime();
				if (time > 0){
					long delay = (i + 1) * 60 + time;
					taskPool.scheduleAtFixedRate(null,rr,delay,time,TimeUnit.SECONDS);
				}
			}
		}
		GameLog.info("start end monsters at "+ TimeUtils.nowStr());
	}
	
	/**
	 * 加载物理层数据
	 * 
	 * @throws Exception
	 */
	private void loadPhysic() throws Exception {
		GameLog.info("load physic to map");
		File file = new File(Const.RES_PATH + "Physic.json");
		InputStream in = new FileInputStream(file);
		JoyBuffer buffer = JoyBuffer.allocate(1024);
		byte[] data = new byte[1024];
		while (true) {
			int len = in.read(data);
			if (len == -1) {
				break;
			}
			buffer.put(data, 0, len);
		}
		in.close();
		String str = new String(buffer.arrayToPosition());
		List<String> temps = JsonUtil.JsonToObjectList(str, String.class);
		Map<Integer, Integer> physics = new HashMap<Integer, Integer>();
		for (int i = 0 ; i < temps.size() ; i++){
			String s = temps.get(i);
			String[] ss = s.split(",");
			int x = Integer.parseInt(ss[0]);
			int y = Integer.parseInt(ss[1]);
			int physic = Integer.parseInt(ss[2]);
			int index = PointVector.getPosition(x,y);
			physics.put(index, physic);
		}
		if (mapCells == null) {
			int maxIndex = GameConfig.MAP_WIDTH * GameConfig.MAP_HEIGHT;
			mapCells = new MapCell[maxIndex];
			for (int i = 0; i < maxIndex; i++) {
				if (physics.containsKey(i)) {
					int physic = physics.get(i);
					int a = physic & PHYSIC_DATA_FLAG_RESIST;
					int b = physic & PHYSIC_DATA_FLAG_SLOW;
					if (a == PHYSIC_DATA_FLAG_RESIST) {// 阻挡
						mapCells[i] = new MapCell(
								MapCellType.MAP_CELL_TYPE_RESIST);
					} else {// 空地
						mapCells[i] = new MapCell(
								MapCellType.MAP_CELL_TYPE_NONE);
					}
					if (b == PHYSIC_DATA_FLAG_SLOW) {// 减速
						mapCells[i].setSlow(true);
					}
				} else {
					mapCells[i] = new MapCell(MapCellType.MAP_CELL_TYPE_NONE);
				}
			}
		}
	}

	/**
	 * 加载代理类和据点
	 */
	@SuppressWarnings("unchecked")
	private void loadProxy() throws Exception {
		GameLog.info("load all proxy to map");
		List<Map<String, Object>> datas = dbMgr.getGameDao().getDatasByTableName(DaoData.TABLE_RED_ALERT_MAPOBJ);
		if (datas != null) {
			for (int i = 0 ; i < datas.size() ; i++){
				Map<String, Object> map = datas.get(i);
				String typeStr = map.get(DaoData.RED_ALERT_GENERAL_TYPE).toString();
				Class<? extends MapObject> clazz = (Class<? extends MapObject>) Class.forName(typeStr);
				MapObject obj = create(clazz, true);
				obj.loadFromData(new SqlData(map));
				updatePosition(obj, obj.getPosition());
			}
		}
	}

	/**
	 * 加载要塞
	 */
	private void loadFortresses() throws Exception {
		GameLog.info("load all fortresses to map");
		List<Map<String, Object>> datas = dbMgr.getGameDao()
				.getDatasByTableName(DaoData.TABLE_RED_ALERT_FORTRESS);
		if (datas != null) {
			for (int i = 0 ; i < datas.size() ; i++){
				Map<String, Object> map = datas.get(i);
				MapFortress fortress = create(MapFortress.class, true);
				fortress.loadFromData(new SqlData(map));
				updatePosition(fortress, fortress.getPosition());
			}
		}
	}

	/**
	 * 加载迁城点
	 */
	private void loadCityMove() throws Exception {
		GameLog.info("load all cityMoves to map");
		List<Map<String, Object>> datas = dbMgr.getGameDao()
				.getDatasByTableName(DaoData.TABLE_RED_ALERT_CITY_MOVE);
		if (datas != null) {
			for (int i = 0 ; i < datas.size() ; i++){
				Map<String, Object> map = datas.get(i);
				MapCityMove move = create(MapCityMove.class, true);
				move.loadFromData(new SqlData(map));
				updatePosition(move, move.getPosition());
			}
		}
	}
	
	/**
	 * 刷新副本建筑
	 * @throws Exception
	 */
	private void refeshEctype() throws Exception {
		GameLog.info("refesh all Ectypes to map");
		List<FubenDistributionData> fileDatas = dataManager.serachList(FubenDistributionData.class);
		for (int i = 0 ; i < fileDatas.size() ; i++){
			FubenDistributionData data = fileDatas.get(i);
			int row = data.getCenterY();
			int col = data.getCenterX();
			int position = PointVector.getPosition(col,row);
			MapEctype ectype = create(MapEctype.class, false);
			if (checkPosition(ectype, position)) {
				insertObj(ectype);
				String key = data.getNeedDistribution().get(0).getpName();
				ectype.setBulidKey(key);
				updatePosition(ectype, position);
			}
		}
	}
	
	/**
	 * 刷新军营
	 */
	private void refeshCamp() throws Exception {
		GameLog.info("load all barracks to map");
		// 先加载用户军营
		List<Map<String, Object>> datas = dbMgr.getGameDao().getDatasByTableName(DaoData.TABLE_RED_ALERT_BARRACKS);
		if (datas != null) {
			for (int i = 0 ; i <  datas.size() ; i++){
				Map<String, Object> map =  datas.get(i);
				MapBarracks barracks = create(MapBarracks.class, true);
				barracks.loadFromData(new SqlData(map));
				updatePosition(barracks, barracks.getPosition());
			}
		}
		// 再加载npc军营
		List<CampDistributionData> fileDatas = dataManager.serachList(CampDistributionData.class);
		for (int i = 0 ; i <  fileDatas.size() ; i++){
			CampDistributionData data =  fileDatas.get(i);
			int row = data.getCenterY();
			int col = data.getCenterX();
			int position = PointVector.getPosition(col,row);
			MapBarracks barracks = searchObject(position);
			if (barracks != null) {
				continue;
			}
			barracks = create(MapBarracks.class,false);
			if (checkPosition(barracks,position)){
				int[][] typeIndexs = data.computeLevelIndexs();
				int typeIndex = MathUtils.getRandomInt(typeIndexs[0],typeIndexs[1]);
				String buildKey = data.getNeedProbavility().get(typeIndex).getpName();
				barracks.setBuildkey(buildKey);
				barracks.setRefreshId(data.getId());
				insertObj(barracks);
				updatePosition(barracks,position);
				barracks.initNpc(data);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void loadNpcCity() throws Exception {
		GameLog.info("load all npc city to map");
		//加载城市建筑
		List<Map<String, Object>> sqlDatas = dbMgr.getGameDao().getDatasByTableName(DaoData.TABLE_RED_ALERT_UNION_BUILD);
		for (int i = 0 ; i < sqlDatas.size() ; i++){
			Map<String, Object> map = sqlDatas.get(i);
			String classType = map.get(DaoData.RED_ALERT_GENERAL_TYPE).toString();
			Class<? extends MapUnionBuild> clazz = (Class<? extends MapUnionBuild>) Class.forName(classType);
			MapUnionBuild unionBuild = create(clazz, true);
			unionBuild.loadFromData(new SqlData(map));
			updatePosition(unionBuild, unionBuild.getPosition());
		}
		sqlDatas = dbMgr.getGameDao().getDatasByTableName(DaoData.TABLE_RED_ALERT_NPC_CITY);
		List<NPCDistributionData> fileDatas = dataManager.serachList(NPCDistributionData.class);
		for (int i = 0 ; i <  fileDatas.size() ; i++){
			NPCDistributionData data =  fileDatas.get(i);
			MapUnionCity unionCity = null;
			Npccity npc = dataManager.serach(Npccity.class, data.getId());
			for (int j = 0 ; j < sqlDatas.size() ; j++){
				Map<String, Object> map = sqlDatas.get(j);
				String key = map.get(DaoData.RED_ALERT_NPC_CITY_KEY).toString();
				if (key.equals(data.getId())) {
					unionCity = create(MapUnionCity.class, true);
					unionCity.loadFromData(new SqlData(map));
					unionCity.initMonster(npc);
					break;
				}
			}
			if (unionCity == null) {
				unionCity = create(MapUnionCity.class, true);
				unionCity.init(data,npc);
			}
			unionCity.initConstBuilds(npc);
			updatePosition(unionCity, unionCity.getPosition());
		}
	}

	/**
	 * 服务器启动的时候加载大地图数据
	 */
	public void load() throws Exception {
		GameLog.info("init world map start");
		loadPhysic();
		loadGarrisons();
		loadExpeditions();
		loadNpcCity();
		loadProxy();
		loadRoles();
		loadRefreshObjs();
		loadFortresses();
		loadCityMove();
		refeshCamp();
		refeshEctype();
		refreshResources();
		refreshMonsters();
		registThread();
		errorTroopBack();
		GameLog.info("init world map end");
	}
	
	private void errorTroopBack() {
		GameLog.info("fix error troopses");
		List<GarrisonTroops> troopses = world.getListObjects(GarrisonTroops.class);
		for (int i = 0 ; i < troopses.size() ; i++){
			GarrisonTroops troops = troopses.get(i);
			MapObject target = searchObject(troops.getPosition());
			if (target == null || !target.isMyUnionMember(troops.getTroops().getInfo())){
				GameLog.info("GarrisonTroops error go back form " + troops.getPosition() + ",uid=" + troops.getTroops().getInfo().getUid());
				troops.backToCome();
			}
		}
	}

	public boolean drawMapImage(Map<String, String> flags) {
		try {
			int rect = 12 , scal = 1;
			BufferedImage img = new BufferedImage(GameConfig.MAP_WIDTH * rect,GameConfig.MAP_HEIGHT * rect, BufferedImage.TYPE_INT_BGR);
			Graphics2D g2d = img.createGraphics();
			g2d.setColor(Color.gray);
			for (int i = scal ; i < GameConfig.MAP_WIDTH ; i+= scal){
				g2d.drawLine(i * rect , 0 , i * rect, GameConfig.MAP_HEIGHT  * rect);
			}
			for (int i = scal ; i < GameConfig.MAP_HEIGHT ; i += scal){
				g2d.drawLine(0 , i * rect , GameConfig.MAP_WIDTH  * rect , i * rect);
			}
			boolean show_res = flags.get("res_check").equals("true");
			if (show_res){
				List<MapResource> reses = world.getListObjects(MapResource.class);
				for (int i = 0 ; i <  reses.size() ; i++){
					MapResource res =  reses.get(i);
					Color color = null;
					String str = null;
					String levelStr = String.valueOf(res.getLevel() - 1);
					String typeKey = res.getKey();
					if (typeKey.equals("food")) {
						str = "f" + levelStr;
						color = Color.orange;
					} else if (typeKey.equals("metal")) {
						str = "m" + levelStr;
						color = Color.blue;
					} else if (typeKey.equals("oil")) {
						str = "o" + levelStr;
						color = Color.green;
					} else if (typeKey.equals("alloy")) {
						str = "a" + levelStr;
						color = Color.pink;
					}
					PointVector pv = MapUtil.getPointVector(res.getPosition());
					g2d.setColor(color);
					g2d.drawString(str,(int) pv.x * rect + rect , (int) pv.y * rect + rect);
				}
			}
			boolean show_monster = flags.get("monster_check").equals("true");
			if (show_monster){
				List<MapMonster> monsters = world.getListObjects(MapMonster.class);
				for (int i = 0 ; i <  monsters.size() ; i++){
					MapMonster monster =  monsters.get(i);
					String str = "g" + (monster.getLevel() - 1);
					PointVector pv = MapUtil.getPointVector(monster.getPosition());
					g2d.setColor(Color.red);
					g2d.drawString(str,(int) pv.x * rect + rect , (int) pv.y * rect + rect);
				}
			}
			boolean role_city_show = flags.get("role_city_check").equals("true");
			if (role_city_show){
				List<MapCity> rcs = world.getListObjects(MapCity.class);
				Color color = new Color(1,0.5f,0.5f);
				g2d.setColor(color);
				for (int i = 0 ; i <  rcs.size() ; i++){
					MapCity rc =  rcs.get(i);
					PointVector pv = MapUtil.getPointVector(rc.getPosition());
					g2d.drawString("rc",(int) pv.x * rect + rect , (int) pv.y * rect + rect);
				}
			}
			boolean fortress_show = flags.get("fortress_check").equals("true");
			if (fortress_show){
				List<MapFortress> mfs = world.getListObjects(MapFortress.class);
				Color color = new Color(1,0.5f,0.5f);
				g2d.setColor(color);
				for (int i = 0 ; i <  mfs.size() ; i++){
					MapFortress mf =  mfs.get(i);
					PointVector pv = MapUtil.getPointVector(mf.getPosition());
					g2d.drawString("rf",(int) pv.x * rect + rect , (int) pv.y * rect + rect);
				}
			}
			boolean barracks_show = flags.get("barracks_check").equals("true");
			if (barracks_show){
				List<MapBarracks> mbs = world.getListObjects(MapBarracks.class);
				Color color = new Color(1,0.5f,0.2f);
				g2d.setColor(color);
				for (int i = 0 ; i <  mbs.size(); i++){
					MapBarracks mb =  mbs.get(i);
					PointVector pv = MapUtil.getPointVector(mb.getPosition());
					g2d.drawString("rb",(int) pv.x * rect + rect , (int) pv.y * rect + rect);
				}
			}
			boolean union_city_show = flags.get("union_city_check").equals("true");
			if (union_city_show){
				List<MapUnionCity> mucs = world.getListObjects(MapUnionCity.class);
				Color color = new Color(1,0.5f,0.8f);
				g2d.setColor(color);
				for (int i = 0 ; i <  mucs.size(); i++){
					MapUnionCity muc =  mucs.get(i);
					PointVector pv = MapUtil.getPointVector(muc.getPosition());
					g2d.drawString("uc",(int) pv.x * rect + rect , (int) pv.y * rect + rect);
				}
			}
			boolean union_build_show = flags.get("union_build_check").equals("true");
			if (union_build_show){
				List<MapUnionCity> mucs = world.getListObjects(MapUnionCity.class);
				Color color = new Color(1,0.5f,0.8f);
				g2d.setColor(color);
				for (int i = 0 ; i <  mucs.size(); i++){
					MapUnionCity muc =  mucs.get(i);
					List<Integer> poses = muc.getBuilds();
					for (int j = 0 ; j <  poses.size(); j++){
						Integer pos =  poses.get(j);
						PointVector pv = MapUtil.getPointVector(pos);
						g2d.drawString("ub",(int) pv.x * rect + rect , (int) pv.y * rect + rect);
					}
				}
			}
			boolean data_show = flags.get("data_check").equals("true");
			if (data_show){
				g2d.setColor(Color.lightGray);
				for (int pos = 0 ; pos < mapCells.length ; pos++){
					MapCell cell = mapCells[pos];
					if (cell.getTypeKey() == null && cell.getType() == MapCellType.MAP_CELL_TYPE_RESIST){
						PointVector pv = MapUtil.getPointVector(pos);
						g2d.fillRect((int) pv.x*rect,(int)pv.y*rect,rect,rect);
					}
				}
			}
			ImageIO.write(img,"png", new File("web/image/showMap.png"));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 获取某个刷新块上对象的数量
	 * @param isRes
	 * @param id
	 * @param delFlag
	 * @return
	 */
	public int getRefreshAliveCount(boolean isRes, String id,boolean delFlag) {
		int count = 0;
		if (isRes) {
			List<MapResource> resources = world.getListObjects(MapResource.class);
			for (int i = 0 ; i <  resources.size(); i++){
				MapResource resource =  resources.get(i);
				if (resource.getRefreshId().equals(id)) {
					boolean add = true;
					if (delFlag){
						if (!resource.isLock()){
							add = false;
							resource.remove();
						}
					}
					if (add){
						count++;
					}
				}
			}
		} else {
			List<MapMonster> monsters = world.getListObjects(MapMonster.class);
			for (int i = 0 ; i <  monsters.size(); i++){
				MapMonster monster =  monsters.get(i);
				if (monster.getRefreshId().equals(id)) {
					boolean add = true;
					if (delFlag){
						if (!monster.isLock()){
							add = false;
							monster.remove();
						}
					}
					if (add){
						count++;
					}
				}
			}
		}
		return count;
	}

	private void registThread() {
		// 两小时刷新一次资源
		if (GameConfig.BIG_MAP_USE_NEW_MONSTER){
			taskPool.scheduleAtFixedRate(null, new Runnable() {
				@Override
				public void run() {
					try {
						refreshResources();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}, 2 * TaskPool.SECONDS_PER_HOUR,2 * TaskPool.SECONDS_PER_HOUR,TimeUnit.SECONDS);
		}
		taskPool.scheduleAtFixedRate(null, new Runnable() {
			@Override
			public void run() {
				GameLog.info("start save mapWorld datas");
				save();
			}
		}, 2 * TaskPool.SECONDS_PER_HOUR , 2 * TaskPool.SECONDS_PER_HOUR,TimeUnit.SECONDS);
	}

	/**
	 * 找到某种类型的静止地图对象
	 * @param position
	 * @return
	 * 
	 */
	@SuppressWarnings("unchecked")
	public <T extends MapObject> T searchObject(MapCell cell) {
		if (cell.getTypeKey() != null) {
			return (T) world.getObject(cell.getTypeKey(), cell.getId());
		}
		return null;
	}

	/**
	 * 找到某种类型的静止地图对象
	 * 
	 * @param position
	 * @return
	 */
	public <T extends MapObject> T searchObject(int position) {
		if (position < 0 || position > mapCells.length - 1) {
			return null;
		}
		return searchObject(mapCells[position]);
	}

	/**
	 * 通过目标对象判断是去干吗
	 * 
	 * @param targetCell
	 * @return
	 */
	public TimerLastType getExpediteOutType(MapCell targetCell, long unionId) {
		if (targetCell.getTypeKey() != null) {
			return TimerLastType.TIME_EXPEDITE_FIGHT;
		} else {
			return TimerLastType.TIME_EXPEDITE_GARRISON;
		}
	}

	public int getFortressesCount(long uid){
		int count = 0;
		List<MapFortress> alls = world.getListObjects(MapFortress.class);
		for (int i = 0 ; i <  alls.size(); i++){
			MapFortress fortress =  alls.get(i);
			if (!fortress.isRemoving() && fortress.isMyMaster(uid)) {
				count ++;
			}
		}
		List<ExpediteTroops> expedites = world.getListObjects(ExpediteTroops.class);
		for (int i = 0 ; i <  expedites.size(); i++){
			ExpediteTroops expedite =  expedites.get(i);
			if (expedite.getLeader().getInfo().getUid() == uid && 
				expedite.getTimer().getType() == TimerLastType.TIME_EXPEDITE_CREATE_FORTRESS){
				count++;
			}
		}
		return count;
	}
	
	/**
	 * 只是要塞
	 * @param uid
	 * @return
	 */
	public List<MapFortress> getFortresses(long uid) {
		List<MapFortress> result = new ArrayList<MapFortress>();
		List<MapFortress> alls = world.getListObjects(MapFortress.class);
		for (int i = 0 ; i <  alls.size(); i++){
			MapFortress fortress =  alls.get(i);
			if (!fortress.isRemoving() && fortress.isMyMaster(uid)) {
				result.add(fortress);
			}
		}
		return result;
	}
	
	/*
	 * 获取离玩家最近的废墟
	 */
	public MapEctype getNearestEctype(Role role) {
		int pos = role.getCity(0).getPosition();
		PointVector posv = MapUtil.getPointVector(pos);
		
		float minDist = 0;
		int index = -1;
		List<MapEctype> list = world.getListObjects(MapEctype.class); 
		if (list.isEmpty()) {
			GameLog.error("BigMapWorld MapEctype list is empty");
			return null;
		}
		
		for (int i = 0; i < list.size(); i++) {
			MapEctype ectype = list.get(i);
			PointVector posvv = MapUtil.getPointVector(ectype.getPosition());
			if (index == -1 || minDist > posv.distance(posvv)) {
				index = i;
				minDist = posv.distance(posvv);
			}
		}
		if (index == -1) {
			GameLog.error("getNearestEctype error, no nearest ectype");
			return list.get(0);
		}
		return list.get(index);
	}

	/**
	 * 包括军营的要塞
	 * @param uid
	 * @return
	 */
	public List<MapFortress> getAllFortresses(long uid) {
		List<MapFortress> result = new ArrayList<MapFortress>();
		result.addAll(getFortresses(uid));
		List<MapBarracks> barracks = world.getListObjects(MapBarracks.class);
		for (int i = 0 ; i <  barracks.size(); i++){
			MapBarracks barrack =  barracks.get(i);
			if (!barrack.isRemoving() && barrack.isMyMaster(uid)) {
				result.add(barrack);
			}
		}
		return result;
	}

	public boolean checkMovingCity(Role role) {
		List<MapCityMove> alls = world.getListObjects(MapCityMove.class);
		for (int i = 0 ; i <  alls.size(); i++){
			MapCityMove move =  alls.get(i);
			if (move.getInfo().getUid() == role.getId()) {
				return true;
			}
		}
		
		List<ExpediteTroops> expedites = world.getListObjects(ExpediteTroops.class);
		for (int i = 0 ; i <  expedites.size(); i++){
			ExpediteTroops expedite =  expedites.get(i);
			if (expedite.getTimer().getType() == TimerLastType.TIME_EXPEDITE_CREATE_MOVE
				&& expedite.getLeader().getInfo().getUid() == role.getId()) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 判断我的行军部队是否与副本有关
	 * @param role
	 * @param pos
	 * @return
	 */
	public boolean checkIsMovingToEctype(Role role,int pos) {
		List<ExpediteTroops> expedites = world.getListObjects(ExpediteTroops.class);
		for (int i = 0 ; i <  expedites.size(); i++){
			ExpediteTroops expedite =  expedites.get(i);
			if (expedite.getTargetPosition() == pos &&
				expedite.getTimer().getType() == TimerLastType.TIME_GO_TO_ECTYPE
				&& expedite.getLeader().getInfo().getUid() == role.getId()){//目标点是副本
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 与我相关的驻防部队
	 * @param uid
	 * @return
	 */
	public List<GarrisonTroops> getRelevanceGarrisons(long uid) {
		List<GarrisonTroops> result = new ArrayList<GarrisonTroops>();
		List<GarrisonTroops> occupers = world.getListObjects(GarrisonTroops.class);
		for (int i = 0 ; i <  occupers.size(); i++){
			GarrisonTroops occuper =  occupers.get(i);
			if (occuper.getTroops().getInfo().getUid() == uid) {
				result.add(occuper);
			}
		}
		return result;
	}

	/**
	 * 和我相关的行军部队,这个是包括敌人打我的
	 * @param uid
	 * @return
	 */
	public List<ExpediteTroops> getRelevanceRoleExpedites(long uid) {
		List<ExpediteTroops> result = new ArrayList<ExpediteTroops>();
		List<ExpediteTroops> expedites = world.getListObjects(ExpediteTroops.class);
		for (int i = 0 ; i <  expedites.size(); i++){
			ExpediteTroops expedite =  expedites.get(i);
			if (expedite.isRemoving()) {
				continue;
			}
			if (!result.contains(expedite) && expedite.relevance(uid)) {// 我是进攻方
				result.add(expedite);
				continue;
			}
			Role role = world.getRole(uid);
			MapObject obj = searchObject(expedite.getTargetPosition());
			if (obj != null && !result.contains(expedite)) {// 我是防御方
				boolean insert = obj.getInfo().getUid() == uid;
				if (!insert && role.getUnionId() != 0 && obj.getInfo().getUnionId() == role.getUnionId()) {
					insert = true;
				}
				insert = insert ? insert : obj.isRivalry(uid, expedite);
				if (insert) {
					result.add(expedite);
				}
			}
		}
		return result;
	}

	/**
	 * 我的正在行军的部队
	 * @param uid
	 * @return
	 */
	public List<ExpediteTroops> getMyRoleExpedites(long uid) {
		List<ExpediteTroops> result = new ArrayList<ExpediteTroops>();
		List<ExpediteTroops> expedites = world.getListObjects(ExpediteTroops.class);
		for (int i = 0 ; i <  expedites.size(); i++){
			ExpediteTroops expedite =  expedites.get(i);
			if (expedite.relevance(uid)) {
				result.add(expedite);
			}
		}
		return result;
	}

	/**
	 * 获取和我相关的集结数据，包括我这里出发的，别人集结打我的
	 * 
	 * @param uid
	 * @return
	 */
	public List<MassTroops> getRelevanceRoleMasses(long uid) {
		List<MassTroops> result = new ArrayList<MassTroops>();
		List<MapCity> citys = world.getListObjects(MapCity.class);
		for (int i = 0 ; i <  citys.size(); i++){
			MapCity city =  citys.get(i);
			MassTroops mass = city.getMass();
			if (mass == null) {
				continue;
			}
			if (city.getInfo().getUid() == uid || mass.getTargetInfo().getUid() == uid) {
				result.add(mass);
			}
		}
		return result;
	}

	/**
	 * 玩家部队回去的类型判断
	 * 
	 * @param expedite
	 * @return
	 */
	public TimerLastType getBackExpedteType(ExpediteTroops expedite) {
		TroopsData leader = expedite.getLeader();
		MapCell targetCell = mapWorld.getMapCell(leader.getComePosition());
		TimerLastType type = TimerLastType.TIME_ARMY_BACK;
		MapObject obj = searchObject(targetCell);
		if (obj != null&& (obj instanceof MapFortress || obj instanceof MapBarracks)) {
			type = TimerLastType.TIME_ARMY_BACK_FORTRESS;
		} else {
			boolean flag = true;
			MapCity city = null;
			if (obj instanceof MapCity) {
				if (obj.getInfo().getUid() == leader.getInfo().getUid()) {
					flag = false;
				}
			}
			if (flag) {// 如果要回去的地方不是我自己主城
				city = mapWorld.searchMapCity(leader.getInfo());
				leader.setComePosition(city.getPosition());
			}
		}
		return type;
	}

	public MapCity searchMapCity(MapRoleInfo info) {
		return searchMapCity(info.getUid(), info.getCityId());
	}
	
	public List<MapCity> searchMapCity(long uid) {
		List<MapCity> result = new ArrayList<MapCity>();
		List<MapCity> mapCitys = world.getListObjects(MapCity.class);
		for (int i = 0 ; i <  mapCitys.size(); i++){
			MapCity mapCity =  mapCitys.get(i);
			MapRoleInfo info = mapCity.getInfo();
			if (info.getUid() == uid) {
				result.add(mapCity);
			}
		}
		return result;
	}
	
	public MapCity searchMapCity(long uid, int cityId) {
		List<MapCity> mapCitys = world.getListObjects(MapCity.class);
		for (int i = 0 ; i <  mapCitys.size(); i++){
			MapCity mapCity =  mapCitys.get(i);
			MapRoleInfo info = mapCity.getInfo();
			if (info.getUid() == uid && info.getCityId() == cityId) {
				return mapCity;
			}
		}
		return null;
	}
	
	public List<MapCityMove> searchCityMove(long uid) {
		List<MapCityMove> alls = world.getListObjects(MapCityMove.class);
		List<MapCityMove> result = new ArrayList<MapCityMove>();
		for (int i = 0 ; i <  alls.size(); i++){
			MapCityMove move =  alls.get(i);
			if (move.getInfo().getUid() == uid){
				result.add(move);
			}
		}
		return result;
	}
	
	public MapCityMove searchCityMove(long uid, int cityId) {
		List<MapCityMove> alls = world.getListObjects(MapCityMove.class);
		for (int i = 0 ; i <  alls.size(); i++){
			MapCityMove move =  alls.get(i);
			MapRoleInfo info = move.getInfo();
			if (info.getUid() == uid && info.getCityId() == cityId) {
				return move;
			}
		}
		return null;
	}

	public List<MapUnionCity> searchUnionCity(long id) {
		List<MapUnionCity> citys = new ArrayList<MapUnionCity>();
		List<MapUnionCity> all = world.getListObjects(MapUnionCity.class);
		for (int i = 0 ; i <  all.size(); i++){
			MapUnionCity city =  all.get(i);
			if (city.getUnionId() == id) {
				citys.add(city);
			}
		}
		return citys;
	}
	
	public int searchUnionBuildsNum(long id, int level){
		int num = 0;
		List<MapUnionCity> citys = searchUnionCity(id);
		for(MapUnionCity city : citys){
			List<Integer> buildPosLst = city.getBuilds();
			for(int pos : buildPosLst){
				MapUnionBuild build = mapWorld.searchObject(pos);
				if(build != null && build.getLevel() >= level){
					num ++;
				}
			}
		}
		return num;
	}
	
	/**
	 * 生成一个关卡
	 * @param sceneId
	 * @return
	 */
	public Scene createScene(String sceneId) {
		Ruinscheckpoin checkpoin = dataManager.serach(Ruinscheckpoin.class,sceneId);
		if (checkpoin == null) {
			GameLog.error("read RuinsCheckpoin is fail");
			return null;
		}
		String[] monsterValue = new String[checkpoin.getMonster().size()];
		int[] monsterRate = new int[checkpoin.getMonster().size()];
		for (int i = 0; i < checkpoin.getMonster().size(); i++) {
			String monster = checkpoin.getMonster().get(i);
			String[] params = monster.split(":");
			monsterValue[i] = params[0];
			monsterRate[i] = Integer.parseInt(params[1]);
		}
		String monsterId = MathUtils.getRandomObj(monsterValue, monsterRate);
		Monster monster = dataManager.serach(Monster.class,monsterId);
		TroopsData troops = TroopsData.create(monster,0);
		Map<Byte,Map<String,Integer>> packages = new HashMap<Byte,Map<String,Integer>>();
		MapUtil.drop(monster.getDroplist(), packages);
		Scene scene = new Scene(sceneId, 0, checkpoin.getDieProbability(), troops, packages, "", checkpoin);
		return scene;
	}
	
	public MapMonster searchMonsterNearby(int pos){
		int radius = 1;
		int x = PointVector.getX(pos);
		int y = PointVector.getY(pos);
		do{
			int left  = Math.min(0,x-radius);
			int right = (int)Math.max(x+radius,GameConfig.MAP_WIDTH);
			int up    = (int)Math.min(0,y-radius);
			int down  = (int)Math.max(y+radius,GameConfig.MAP_CELL_HEIGHT);
			MapCell cell = null;
			MapMonster monster = null;
			for (int i = left ; i <= right ; i++){
				int position = PointVector.getPosition(i,up);
				cell = mapCells[position];
				if (cell.getType() == MapCellType.MAP_CELL_TYPE_MONSTER){
					monster = searchObject(cell);
					if (monster != null){
						return monster;
					}
				}
				position = PointVector.getPosition(i,down);
				cell = mapCells[position];
				if (cell.getType() == MapCellType.MAP_CELL_TYPE_MONSTER){
					monster = searchObject(cell);
					if (monster != null){
						return monster;
					}
				}
			}
			for (int i = up ; i <= down ; i++){
				int position = PointVector.getPosition(left,i); 
				cell = mapCells[position];
				if (cell.getType() == MapCellType.MAP_CELL_TYPE_MONSTER){
					monster = searchObject(cell);
					if (monster != null){
						return monster;
					}
				}
				position = PointVector.getPosition(right,i); 
				cell = mapCells[position];
				if (cell.getType() == MapCellType.MAP_CELL_TYPE_MONSTER){
					monster = searchObject(cell);
					if (monster != null){
						return monster;
					}
				}
			}
			radius++;
		}while(radius < GameConfig.MAP_WIDTH / 2);
		return null;
	}
}
