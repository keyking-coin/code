package com.keyking.coin.service.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.keyking.coin.service.dao.row.NoticeRow;
import com.keyking.coin.service.domain.other.NoticeEntity;
import com.keyking.coin.util.ServerLog;

public class OtherDAO extends JdbcDaoSupport {
	
	NoticeRow noticeRow = new NoticeRow();
	
	private static String INSERT_SQL_STR = "insert into notice (_time,title,body)values(?,?,?)";
	
	private static String DELETE_SQL_STR = "delete from notice where time=?";
	
	private static String SELECT_SQL_STR_MORE = "select * from notice where _time>=? and _time<=?";
	
	public synchronized boolean insert(final NoticeEntity notice) {
		try {
			getJdbcTemplate().update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection conn)throws SQLException {
					PreparedStatement ps = conn.prepareStatement(INSERT_SQL_STR, Statement.NO_GENERATED_KEYS);
					int cursor = 1;
					ps.setString(cursor++,notice.getTime());
					ps.setString(cursor++,notice.getTitle());
					ps.setString(cursor++,notice.getBody());
					return ps;
				}
			});
			return true;
		} catch (Exception e) {
			ServerLog.error("insert notice error",e);
			return false;
		}
	}
	
	public synchronized boolean delete(String time){
		try {
			getJdbcTemplate().update(DELETE_SQL_STR,time);
		} catch (DataAccessException e) {
			ServerLog.error("delete notice error",e);
			return false;
		}
		return true;
	}
	
	public List<NoticeEntity> search(String start , String end){
		List<NoticeEntity> times = null;
		try {
			Timestamp timeStart = Timestamp.valueOf(start);
			Timestamp timeEnd = Timestamp.valueOf(end);
			times = getJdbcTemplate().query(SELECT_SQL_STR_MORE,noticeRow,timeStart,timeEnd);
		} catch (DataAccessException e) {
			//e.printStackTrace();
		}
		return times;
	}
}