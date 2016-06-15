package com.keyking.coin.service.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;

import com.keyking.coin.service.dao.row.FeedBackRow;
import com.keyking.coin.service.domain.fb.FeedBack;
import com.keyking.coin.util.ServerLog;

public class FeedBackDAO extends BaseDAO{
	FeedBackRow row = new FeedBackRow();
	private static String INSERT_SQL_STR = "insert into feedback (time,content)values(?,?)";
	private static String SELECT_SQL_STR = "select * from feedback where time=?";
	private static String UPDATE_SQL_STR = "update feedback set content=? where time=?";
	
	public synchronized boolean insert(final FeedBack fb) {
		try {
			getJdbcTemplate().update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection conn)throws SQLException {
					PreparedStatement ps = conn.prepareStatement(INSERT_SQL_STR, Statement.NO_GENERATED_KEYS);
					int cursor = 1;
					ps.setString(cursor++,fb.getTime());
					ps.setString(cursor++,fb.getContent());
					return ps;
				}
			});
			return true;
		} catch (Exception e) {
			ServerLog.error("insert feedback error",e);
			return false;
		}
	}

	public synchronized boolean save(FeedBack fb) {
		try {
			getJdbcTemplate().update(UPDATE_SQL_STR,fb.getContent(),fb.getTime());
		} catch (DataAccessException e) {
			ServerLog.error("save feedback error",e);
			return false;
		}
		return true;
	}
	
	public synchronized FeedBack search(String str) {
		FeedBack fb = null;
		try {
			fb = getJdbcTemplate().queryForObject(SELECT_SQL_STR,row,str);
		} catch (DataAccessException e) {
			return null;
		}
		return fb;
	}
}
