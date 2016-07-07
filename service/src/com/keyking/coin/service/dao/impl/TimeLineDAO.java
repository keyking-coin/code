package com.keyking.coin.service.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;

import com.keyking.coin.service.dao.row.TimelineRow;
import com.keyking.coin.service.domain.time.TimeLine;
import com.keyking.coin.util.ServerLog;

public class TimeLineDAO extends BaseDAO {
	
	TimelineRow row = new TimelineRow();
	private static String INSERT_SQL_STR = "insert into timeline (id,type,title,url,startTime,endTime)values(?,?,?,?,?,?)";
	private static String SELECT_SQL_STR_MORE = "select * from timeline where createTime>=? and createTime<=?";
	private static String UPDATE_SQL_STR = "update timeline set type=?,title=?,url=?,startTime=?,endTime=? where id=?";
	
	public synchronized boolean insert(final TimeLine timeLine) {
		try {
			getJdbcTemplate().update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection conn)throws SQLException {
					PreparedStatement ps = conn.prepareStatement(INSERT_SQL_STR, Statement.NO_GENERATED_KEYS);
					int cursor = 1;
					ps.setLong(cursor++,timeLine.getId());
					ps.setByte(cursor++,timeLine.getType());
					ps.setString(cursor++,timeLine.getTitle());
					ps.setString(cursor++,timeLine.getUrl());
					ps.setString(cursor++,timeLine.getStartTime());
					ps.setString(cursor++,timeLine.getEndTime());
					return ps;
				}
			});
			return true;
		} catch (Exception e) {
			ServerLog.error("insert timeLine error",e);
			return false;
		}
	}
	
	public synchronized boolean delete(long id){
		try {
			return delete("timeline",id);
		} catch (DataAccessException e) {
			ServerLog.error("delete timeLine error",e);
			return false;
		}
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
	
	private synchronized boolean update(TimeLine timeLine) {
		try {
			getJdbcTemplate().update(UPDATE_SQL_STR,timeLine.getType(),timeLine.getTitle(),
					                                timeLine.getUrl(),timeLine.getStartTime(),
					                                timeLine.getEndTime(),timeLine.getId());
		} catch (DataAccessException e) {
			ServerLog.error("save timeline error",e);
			return false;
		}
		return true;
	}
	
	public synchronized boolean save(TimeLine timeLine){
		if (check("timeline",timeLine.getId())){
			return update(timeLine);
		}else{
			return insert(timeLine);
		}
	}

	
}
