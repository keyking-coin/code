package com.keyking.coin.service.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;

import com.keyking.coin.service.dao.row.UserBrokerRow;
import com.keyking.coin.service.domain.broker.UserBroker;

public class UserBrokerDAO extends BaseDAO {
	private static String INSERT_SQL_STR  = "insert into ubs (uid,bid,account,tel)values(?,?,?,?)";
	private static String UPDATE_SQL_STR  = "update ubs set account=?,tel=? where uid=? and bid=?";
	private static String SEARCH_SQL_STR  = "select * from ubs where uid=? and bid=?";
	private static String SEARCH_SQL_STR_MORE1  = "select * from ubs where uid=?";
	private static String SEARCH_SQL_STR_MORE2  = "select * from ubs where bid=?";
	private static final String SQL_COUNT_NUM = "select count(*) from ubs where uid=? and bid=?";
	
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
			result = getJdbcTemplate().query("select * ubs where 1=1",userBrokerRow);
		} catch (DataAccessException e) {
			
		}
		return result;
	}
	
	public boolean check(long uid,long bid) {
		int num = 0;
		try {
			num = getJdbcTemplate().queryForInt(SQL_COUNT_NUM,uid,bid);
		} catch (Exception e) {
			
		}
		return num > 0;
	}
	
	public synchronized boolean save(UserBroker ub) {
		if (check(ub.getUid(),ub.getBid())){
			return update(ub);
		}else{
			return insert(ub);
		}
	}
}
