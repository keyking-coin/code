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
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.keyking.coin.service.dao.row.TimelineRow;
import com.keyking.coin.service.domain.time.TimeLine;
import com.keyking.coin.util.ServerLog;

public class TimeLineDAO extends JdbcDaoSupport {
	
	TimelineRow row = new TimelineRow();
	
	private static String INSERT_SQL_STR = "insert into timeline (type,title,createTime,contents)values(?,?,?,?)";
	
	private static String DELETE_SQL_STR = "delete from timeline where id=?";
	
	private static String SELECT_SQL_STR_MORE = "select * from timeline where createTime>=? and createTime<=?";
	
	public synchronized boolean insert(final TimeLine timeLine) {
		try {
			KeyHolder key = new GeneratedKeyHolder();
			getJdbcTemplate().update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection conn)throws SQLException {
					PreparedStatement ps = conn.prepareStatement(INSERT_SQL_STR, Statement.RETURN_GENERATED_KEYS);
					int cursor = 1;
					ps.setByte(cursor++,timeLine.getType());
					ps.setString(cursor++,timeLine.getTitle());
					ps.setString(cursor++,timeLine.getTime());
					ps.setString(cursor++,timeLine.getTime());
					ps.setString(cursor++,timeLine.contentToStr());
					return ps;
				}
			},key);
			timeLine.setId(key.getKey().longValue());
			return true;
		} catch (Exception e) {
			ServerLog.error("insert timeLine error",e);
			return false;
		}
	}
	
	public synchronized boolean delete(long id){
		try {
			getJdbcTemplate().update(DELETE_SQL_STR,id);
		} catch (DataAccessException e) {
			ServerLog.error("delete timeLine error",e);
			return false;
		}
		return true;
	}
	
	public List<TimeLine> search(String start , String end){
		List<TimeLine> times = null;
		try {
			Timestamp timeStart = Timestamp.valueOf(start);
			Timestamp timeEnd = Timestamp.valueOf(end);
			times = getJdbcTemplate().query(SELECT_SQL_STR_MORE,row,timeStart,timeEnd);
		} catch (DataAccessException e) {
			//e.printStackTrace();
		}
		return times;
	}
}
