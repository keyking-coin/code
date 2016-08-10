package com.keyking.coin.service.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.keyking.coin.service.dao.row.AccountApplyRow;
import com.keyking.coin.service.domain.user.AccountApply;
import com.keyking.coin.util.ServerLog;

public class AccountApplyDAO extends BaseDAO {
	
	AccountApplyRow row = new AccountApplyRow();
	private static String INSERT_SQL_STR = "insert into accountApply (bourse,bankName,tel,email,indentFront,indentBack,bankFront,state,reason)values(?,?,?,?,?,?,?,?,?)";
	private static String SELECT_SQL_STR_ONE = "select * from accountApply where id=?";
	private static String SELECT_SQL_STR_MORE = "select * from accountApply where 1=1";
	
	public synchronized boolean insert(final AccountApply apply) {
		try {
			KeyHolder key = new GeneratedKeyHolder();
			getJdbcTemplate().update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection conn)throws SQLException {
					PreparedStatement ps = conn.prepareStatement(INSERT_SQL_STR, Statement.RETURN_GENERATED_KEYS);
					int cursor = 1;
					ps.setString(cursor++,apply.getBourse());
					ps.setString(cursor++,apply.getBankName());
					ps.setString(cursor++,apply.getTel());
					ps.setString(cursor++,apply.getEmail());
					ps.setString(cursor++,apply.getIndentFront());
					ps.setString(cursor++,apply.getIndentBack());
					ps.setString(cursor++,apply.getBankFront());
					ps.setByte(cursor++,apply.getState());
					ps.setString(cursor++,apply.getReason());
					return ps;
				}
			},key);
			apply.setId(key.getKey().longValue());
			return true;
		} catch (Exception e) {
			ServerLog.error("insert accountApply error",e);
			return false;
		}
	}
	
	
	public AccountApply search(long id) {
		AccountApply apply = null;
		try {
			apply = getJdbcTemplate().queryForObject(SELECT_SQL_STR_ONE,row,id);
		} catch (DataAccessException e) {
			
		}
		return apply;
	}
	
	public List<AccountApply> load(){
		List<AccountApply> applys = null;
		try {
			applys = getJdbcTemplate().query(SELECT_SQL_STR_MORE,row);
		} catch (DataAccessException e) {
			//e.printStackTrace();
		}
		return applys;
	}
}
