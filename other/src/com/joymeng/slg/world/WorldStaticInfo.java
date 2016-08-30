package com.joymeng.slg.world;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joymeng.Instances;
import com.joymeng.slg.dao.DaoData;
import com.joymeng.slg.dao.SqlData;
import com.joymeng.slg.domain.map.impl.still.union.MapUnionCity;

public class WorldStaticInfo implements Instances, DaoData {
	private static WorldStaticInfo instance = new WorldStaticInfo();
	boolean savIng = false;
	
	public static WorldStaticInfo getInstance() {
		return instance;
	}

	Map<String, Long> killMonsters = new HashMap<String, Long>();

	public Map<String, Long> getKillMonsters() {
		return killMonsters;
	}

	public void setKillMonsters(Map<String, Long> killMonsters) {
		this.killMonsters = killMonsters;
	}

	public void addKillMonster(String mId) {
		if (killMonsters.get(mId) == null) {
			killMonsters.put(mId, (long) 1);
		} else {
			killMonsters.put(mId, killMonsters.get(mId) + 1);
		}
	}

	public long getKillMonster(String mId) {
		if (killMonsters.get(mId) == null) {
			return 0;
		} else {
			return killMonsters.get(mId);
		}
	}

	public long getAllKillMonster() {
		long count = 0;
		for (String monster : killMonsters.keySet()) {
			count += killMonsters.get(monster);
		}
		return count;
	}
	
	public void load() {
		List<SqlData> datas = dbMgr.getGameDao().getDatas(DaoData.TABLE_RED_ALERT_WD_ST);
		if (datas == null) {
			return;
		}
		for (int i = 0 ; i < datas.size() ; i++){
			SqlData data = datas.get(i);
			loadFromData(data);
		}
	}

	@Override
	public String table() {
		return TABLE_RED_ALERT_WD_ST;
	}

	@Override
	public String[] wheres() {
		String[] result = new String[1];
		result[0] = DaoData.RED_ALERT_MONSTER_ID;
		return result;
	}

	@Override
	public boolean delete() {
		return false;
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
		String mId = data.getString(DaoData.RED_ALERT_MONSTER_ID);
		long num = data.getLong(DaoData.RED_ALERT_MONSTER_NUM);
		killMonsters.put(mId, num);
	}

	@Override
	public void saveToData(SqlData data) {
		for (Map.Entry<String, Long> mapSet : killMonsters.entrySet()) {
			data.put(DaoData.RED_ALERT_MONSTER_ID, mapSet.getKey());
			data.put(DaoData.RED_ALERT_MONSTER_NUM, mapSet.getValue());
		}
	}

	public int getOcpCitysByLevel(int level) {
		int num = 0;
		List<MapUnionCity> allUnionCitys = world.getListObjects(MapUnionCity.class);
		for (MapUnionCity mapCity : allUnionCitys) {
			if (mapCity.getState() == 2 && mapCity.getLevel() >= level) {// 被联盟占领状态
				num++;
			}
		}
		return num;
	}
	
	public int getAllOcpCitys() {
		int num = 0;
		List<MapUnionCity> allUnionCitys = world.getListObjects(MapUnionCity.class);
		for (MapUnionCity mapCity : allUnionCitys) {
			if (mapCity.getState() == 2) {// 被联盟占领状态
				num++;
			}
		}
		return num;
	}

	@Override
	public void over() {
		savIng = false;
	}

	@Override
	public boolean saving() {
		return savIng;
	}
}
