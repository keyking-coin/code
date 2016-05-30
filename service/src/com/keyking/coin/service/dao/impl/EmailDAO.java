package com.keyking.coin.service.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;

import com.keyking.coin.service.dao.row.EmailRow;
import com.keyking.coin.service.domain.email.Email;
import com.keyking.coin.util.ServerLog;

public class EmailDAO extends BaseDAO {
	
	EmailRow row = new EmailRow();
	
	private static String INSERT_SQL_STR = "insert into email (id,type,status,senderId,userId,time,theme,content)values(?,?,?,?,?,?,?,?)";
	private static String UPDATE_SQL_STR = "update email set status=? where id=?";
	private static String SELECT_SQL_STR_MORE = "select * from email where userId=?";
	
	public synchronized boolean insert(final Email email) {
		try {
			getJdbcTemplate().update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection conn)throws SQLException {
					PreparedStatement ps = conn.prepareStatement(INSERT_SQL_STR, Statement.NO_GENERATED_KEYS);
					int cursor = 1;
					ps.setLong(cursor++,email.getId());
					ps.setByte(cursor++,email.getType());
					ps.setByte(cursor++,email.getStatus());
					ps.setLong(cursor++,email.getSenderId());
					ps.setLong(cursor++,email.getUserId());
					ps.setString(cursor++,email.getTime());
					ps.setString(cursor++,email.getTheme());
					ps.setString(cursor++,email.getContent());
					return ps;
				}
			});
			return true;
		} catch (Exception e) {
			ServerLog.error("insert email error",e);
			return false;
		}
	}
	
	private synchronized boolean update(Email email){
		try {
			getJdbcTemplate().update(UPDATE_SQL_STR,email.getStatus(),email.getId());
		} catch (DataAccessException e) {
			ServerLog.error("save email error",e);
			return false;
		}
		return true;
	}
	
	public synchronized boolean save(Email email) {
		if (check("email",email.getId())){
			return update(email);
		}else{
			return insert(email);
		}
	}
	
	public synchronized boolean delete(Email email) {
		try {
			return delete("email",email.getId());
		} catch (DataAccessException e) {
			ServerLog.error("delete email error",e);
			return false;
		}
	}
	
	public List<Email> load(long uid){
		List<Email> emails = null;
		try {
			emails = getJdbcTemplate().query(SELECT_SQL_STR_MORE,row,uid);
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
		return emails;
	}
}
