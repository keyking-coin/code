package com.keyking.coin.service.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;

import com.keyking.coin.service.dao.TableName;
import com.keyking.coin.service.dao.row.BourseInfoRow;
import com.keyking.coin.service.domain.bourse.BourseInfo;
import com.keyking.coin.util.ServerLog;

public class BourseDAO extends BaseDAO {
	private static String INSERT_SQL_STR = "insert into bourse (name,url,type)values(?,?,?)";
	private static String UPDATE_SQL_STR = "update bourse set url=?,type=? where name=?";
	private static String SELECT_SQL_STR = "select * from bourse where name=?";
	private static String SELECT_SQL_STR_MORE = "select * from bourse where 1=1";
	private static String SELECT_SQL_STR_TYPE = "select * from bourse where type=?";
	
	BourseInfoRow row = new BourseInfoRow();
	
	public synchronized boolean insert(final BourseInfo info) {
		try {
			getJdbcTemplate().update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection conn)throws SQLException {
					PreparedStatement ps = conn.prepareStatement(INSERT_SQL_STR, Statement.NO_GENERATED_KEYS);
					int cursor = 1;
					ps.setString(cursor++,info.getName());
					ps.setString(cursor++,info.getUrl());
					ps.setByte(cursor++,info.getType());
					return ps;
				}
			});
			return true;
		} catch (Exception e) {
			ServerLog.error("insert ad error",e);
			return false;
		}
	}
	
	@Override
	protected synchronized boolean check(String tableName,Object id){
		String sql = "select count(*) from " + tableName + " where name=?";
		int count = 0 ;
		try {
			count = getJdbcTemplate().queryForInt(sql,id);
		} catch (DataAccessException e) {
			
		}
		return count > 0;
	}
	
	public synchronized boolean save(BourseInfo info) {
		if (check(TableName.TABLE_NAME_BOURSE.getTable(),info.getName())){
			return update(info);
		}else{
			return insert(info);
		}
	}
	
	private synchronized boolean update(BourseInfo info) {
		try {
			getJdbcTemplate().update(UPDATE_SQL_STR,info.getUrl(),info.getType(),info.getName());
		} catch (DataAccessException e) {
			ServerLog.error("save ad error",e);
			return false;
		}
		return true;
	}
	
	public BourseInfo search(String name) {
		BourseInfo info = null;
		try {
			info = getJdbcTemplate().queryForObject(SELECT_SQL_STR,row,name);
		} catch (DataAccessException e) {
			return null;
		}
		return info;
	}

	public List<BourseInfo> load() {
		List<BourseInfo> infos = null;
		try {
			infos = getJdbcTemplate().query(SELECT_SQL_STR_MORE,row);
		} catch (DataAccessException e) {
			//e.printStackTrace();
		}
		return infos;
	}
	
	public synchronized boolean delete(String tableName,String name){
		String sql = "delete from " + tableName + " where name=?";
		try {
			int count = getJdbcTemplate().update(sql,name);
			return count > 0;
		} catch (DataAccessException e) {
			
		}
		return false;
	}
	
	public List<BourseInfo> load(int type) {
		List<BourseInfo> infos = null;
		try {
			infos = getJdbcTemplate().query(SELECT_SQL_STR_TYPE,row,type);
		} catch (DataAccessException e) {
			//e.printStackTrace();
		}
		return infos;
	}
}
