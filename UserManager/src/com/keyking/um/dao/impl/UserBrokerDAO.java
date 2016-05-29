package com.keyking.um.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;

import com.keyking.um.dao.TableName;
import com.keyking.um.dao.row.UserBrokerRow;
import com.keyking.um.data.UserBroker;

public class UserBrokerDAO extends BaseDAO {
	private static String INSERT_SQL_STR  = "insert into " + TableName.TABLE_NAME_UB.getKey() + " (uid,bid,account,tel)values(?,?,?,?)";
	private static String UPDATE_SQL_STR  = "update " + TableName.TABLE_NAME_UB.getKey() + " set account=?,tel=? where uid=? and bid=?";
	private static String SEARCH_SQL_STR  = "select * from " + TableName.TABLE_NAME_UB.getKey() + " where uid=? and bid=?";
	private static String SEARCH_SQL_STR_MORE1  = "select * from " + TableName.TABLE_NAME_UB.getKey() + " where uid=?";
	private static String SEARCH_SQL_STR_MORE2  = "select * from " + TableName.TABLE_NAME_UB.getKey() + " where bid=?";
	
	UserBrokerRow userBrokerRow = new UserBrokerRow();
	
	public synchronized boolean insert(final UserBroker ub) {
		try {
			getJdbcTemplate().update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection conn)throws SQLException {
					PreparedStatement ps = conn.prepareStatement(INSERT_SQL_STR, Statement.NO_GENERATED_KEYS);
					int cursor = 1;
					ps.setLong(cursor++,ub.getUid());
					ps.setLong(cursor++,ub.getBid());
					ps.setString(cursor++,ub.getAccount());
					ps.setString(cursor++,ub.getTel());
					return ps;
				}
			});
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public synchronized boolean update(UserBroker ub){
		try {
			getJdbcTemplate().update(UPDATE_SQL_STR,ub.getAccount(),ub.getTel(),ub.getUid(),ub.getBid());
		} catch (DataAccessException e) {
			return false;
		}
		return true;
	}
	
	public UserBroker search(long uid,long bid) {
		UserBroker ub = null;
		try {
			ub = getJdbcTemplate().queryForObject(SEARCH_SQL_STR,userBrokerRow,uid,bid);
		} catch (DataAccessException e) {
			
		}
		return ub;
	}
	
	public List<UserBroker> searchList1(long uid) {
		List<UserBroker> ubs = null;
		try {
			ubs = getJdbcTemplate().query(SEARCH_SQL_STR_MORE1,userBrokerRow,uid);
		} catch (DataAccessException e) {
			
		}
		return ubs;
	}
	
	public List<UserBroker> searchList2(long bid) {
		List<UserBroker> ubs = null;
		try {
			ubs = getJdbcTemplate().query(SEARCH_SQL_STR_MORE2,userBrokerRow,bid);
		} catch (DataAccessException e) {
			
		}
		return ubs;
	}
	
	public List<UserBroker> loadAll() {
		List<UserBroker> result = null;
		try {
			result = getJdbcTemplate().query("select * " + TableName.TABLE_NAME_BROKER.getKey() + " where 1=1",userBrokerRow);
		} catch (DataAccessException e) {
			
		}
		return result;
	}
}
