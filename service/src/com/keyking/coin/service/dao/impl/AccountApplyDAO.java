package com.keyking.coin.service.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.keyking.coin.service.dao.row.AccountApplyRow;
import com.keyking.coin.service.domain.user.AccountApply;
import com.keyking.coin.util.ServerLog;

public class AccountApplyDAO extends JdbcDaoSupport {
	
	AccountApplyRow row = new AccountApplyRow();
	
	private static String INSERT_SQL_STR = "insert into accountApply (bourse,bankName,tel,email,indentFront,indentBack,bankFront,completed)values(?,?,?,?,?,?,?,?)";
	
	private static String DELETE_SQL_STR = "delete from accountApply where 1=1 and id=?";
	
	private static String SELECT_SQL_STR_ONE = "select * from accountApply where id=?";
	
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
					ps.setByte(cursor++,(byte)(apply.isCompleted() ? 1 : 0));
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
	
	public synchronized boolean delete(long id) {
		try {
			getJdbcTemplate().update(DELETE_SQL_STR,id);
			return true;
		} catch (DataAccessException e) {
			ServerLog.error("delete accountApply error",e);
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
}
