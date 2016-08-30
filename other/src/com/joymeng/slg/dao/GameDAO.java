package com.joymeng.slg.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import com.joymeng.Instances;
import com.joymeng.log.GameLog;

public class GameDAO extends SimpleJdbcDaoSupport implements Instances {

	private GameDAO() {
		
	}
	
	public void update(String table, Map<String, Object> params,String... wheres) {
		StringBuffer sqlbuff = new StringBuffer(256);
		sqlbuff.append("update ").append(table).append(" set ");
		for (String key : params.keySet()) {
			boolean isWhereStr = false;
			for (int i = 0 ; i < wheres.length ; i++){
				String where = wheres[i];
				if (key.equals(where)) {
					isWhereStr = true;
					break;
				}
			}
			if (!isWhereStr)
				sqlbuff.append(key).append("=:").append(key).append(',');
		}
		sqlbuff.deleteCharAt(sqlbuff.length() - 1);
		if (wheres.length > 0) {
			sqlbuff.append(" where ");
			for (int i = 0; i < wheres.length; ++i) {
				sqlbuff.append(wheres[i]).append("=:").append(wheres[i]);
				if (i != wheres.length - 1) {
					sqlbuff.append(" and ");
				}
			}
		}
		getSimpleJdbcTemplate().update(sqlbuff.toString(), params);
	}
	
	public void saveDaoData(DaoData daoData,Map<String, Object> data) {
		data.clear();
		daoData.saveToData(new SqlData(data));
		String table = daoData.table();
		List<String> fields = dbMgr.getFields(table);
		Map<String,Object> map = new HashMap<String,Object>();
		if (fields != null) {
			for (int i = 0 ; i < fields.size() ; i++){
				String key = fields.get(i);
				Object val = data.get(key);
				if (val != null) {
					map.put(key,val);
				}
			}
		}
		if (map.size() > 0){
			String[] wheres = daoData.wheres();
			if (daoData.delete()){
				delete(table,map,wheres);
			}else{
				if (!isDataExist(table,map,wheres)){//数据不存在就插入
					insertData(table,map);
				}else{
					update(table,map,wheres);
				}
				daoData.over();
			}
		}
	}
	
	/****
	 * 红警项目开始
	 */
	Map<String,SimpleJdbcInsert> simpleJdbcInserts = new HashMap<String, SimpleJdbcInsert>();
	
	public long getPrimaryKeyData(String sql){
		return getSimpleJdbcTemplate().queryForLong(sql);
	}
	
	public boolean delete(String table,Map<String,Object> params,String... wheres){
		StringBuffer sqlbuff = new StringBuffer(256);
		sqlbuff.append("delete from ").append(table).append(" where 1=1");
		for (String key : params.keySet()) {
			for (int i = 0 ; i < wheres.length ; i++){
				String where = wheres[i];
				if (key.equals(where)) {
					sqlbuff.append(" and " + where + "=:" + where);
					break;
				}
			}
		}
		String sql = sqlbuff.toString();
		int result = getSimpleJdbcTemplate().update(sql,params);
		return result != 0;
	}
	
	public boolean isDataExist(String table,Map<String,Object> params,String... wheres){
		StringBuffer sqlbuff = new StringBuffer(256);
		sqlbuff.append("select count(*) from ").append(table).append(" where 1=1");
		for (String key : params.keySet()) {
			for (int i = 0 ; i < wheres.length ; i++){
				String where = wheres[i];
				if (key.equals(where)) {
					sqlbuff.append(" and " + where + "=:" + where);
					break;
				}
			}
		}
		String sql = sqlbuff.toString();
		int count = getSimpleJdbcTemplate().queryForInt(sql,params);
		return count > 0;
	}
	
	public void insertData(String table,Map<String, Object> map){
		SimpleJdbcInsert insert  = simpleJdbcInserts.get(table);
		if (insert == null){
			List<String> fields = dbMgr.getFieldOfTable(table);
			if (fields == null){
				GameLog.error("Can't find table's fields whoes name is " + table);
				return;
			}
			String[] sfields = new String[fields.size()];
			sfields = fields.toArray(sfields);
			insert = new SimpleJdbcInsert(getDataSource())
			.withTableName(table)
			.usingColumns(sfields);
			simpleJdbcInserts.put(table,insert);
		}
		synchronized (insert){
			insert.execute(map);
		}
	}
	
	public List<SqlData> getSqlDatas(String sql,Object... objs){
		List<Map<String, Object>> list = getSimpleJdbcTemplate().queryForList(sql,objs);
		if (list != null && list.size() > 0) {
			List<SqlData> result = new ArrayList<SqlData>();
			for (Map<String, Object> lis : list){
				result.add(new SqlData(lis));
			}
			return result;
		} else{
			return null;
		}
	}
	
	public SqlData getData(String table,Object... params) {
		StringBuffer sqlbuff = new StringBuffer(256);
		sqlbuff.append("select * from ").append(table).append(" where 1=1");
		Object[] objs = new Object[params.length / 2];
		for (int i = 0 ; i < params.length ; i += 2) {
			sqlbuff.append(" and " + params[i] + "=?");
			objs[i / 2] = params[i+1];
		}
		String sql = sqlbuff.toString();
		List<SqlData> list = getSqlDatas(sql,objs);
		if (list != null && list.size() > 0) {
			return list.get(0);
		} else{
			return null;
		}
	}
	
	public List<SqlData> getDatas(String table,Object... params) {
		StringBuffer sqlbuff = new StringBuffer(256);
		sqlbuff.append("select * from ").append(table).append(" where 1=1");
		Object[] objs = new Object[params.length / 2];
		for (int i = 0 ; i < params.length ; i += 2) {
			sqlbuff.append(" and " + params[i] + "=?");
			objs[i / 2] = params[i+1];
		}
		String sql = sqlbuff.toString();
		return getSqlDatas(sql,objs);
	}

	/**
	 * 通过表名获取这个表所有的数据
	 * @param table
	 * @return
	 */
	public List<Map<String,Object>> getDatasByTableName(String table) {
		return getDatasBySql("select * from " + table);
	}
	
	public List<Map<String,Object>> getDatasBySql(String sql) {
		List<Map<String,Object>> list = getSimpleJdbcTemplate().queryForList(sql);
		return list;
	}
}
