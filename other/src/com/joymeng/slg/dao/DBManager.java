package com.joymeng.slg.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.joymeng.log.GameLog;

public class DBManager {
	private Map<String, List<String>> TABLE_FIELDS = new HashMap<String, List<String>>();
	private static DBManager dbManager = null;
	private ApplicationContext context = null;
	private GameDAO gameDao = null;

	public static DBManager getInstance() {
		if (dbManager == null) {
			dbManager = new DBManager();
		}
		return dbManager;
	}

	public void init() {
		GameLog.info(">>>>>>>> START DATABASE DAO   <<<<<<<<");
		context = new ClassPathXmlApplicationContext("applicationContext.xml");
		gameDao = (GameDAO) context.getBean("gameDao");
		try {
			List<Map<String, Object>> lis = gameDao.getSimpleJdbcTemplate().queryForList("show tables");
			for (int i = 0 ; i < lis.size() ; i++){
				Map<String, Object> obj = lis.get(i);
				for (Object table : obj.values()){
					String tableName = table.toString();
					TABLE_FIELDS.put(tableName,getFieldOfTable(tableName));
					GameLog.info("get the fields of the table whoes name is <" + tableName + ">");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<String> getFields(String table) {
		return TABLE_FIELDS.get(table);
	}
	
	public List<String> getFieldOfTable(String table) {
		List<Map<String, Object>> list = gameDao.getSimpleJdbcTemplate().queryForList("desc " + table);
		List<String> fields = new ArrayList<String>(list.size());
		for (int i = 0 ; i < list.size() ; i++){
			Map<String, Object> map = list.get(i);
			fields.add(map.get("Field").toString());
		}
		return fields;
	}
	
	public GameDAO getGameDao() {
		return gameDao;
	}

}
