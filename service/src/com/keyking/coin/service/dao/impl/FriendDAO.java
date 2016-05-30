package com.keyking.coin.service.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import com.keyking.coin.service.dao.row.FriendRow;
import com.keyking.coin.service.domain.friend.Friend;
import com.keyking.coin.util.ServerLog;

public class FriendDAO extends BaseDAO {
	
	FriendRow row = new FriendRow();
	private static String INSERT_SQL_STR = "insert into friend (uid,fid,pass,time,other)values(?,?,?,?,?)";
	private static final String SQL_COUNT_FRIEND = "select count(*) from friend where uid=? and fid=?";
	private static String DELETE_SQL_STR = "delete from friend where uid=? and fid=?";
	private static String SELECT_SQL_STR = "select * from friend where uid=?";
	private static String UPDATE_SQL_STR = "update friend set pass=? where uid=? and fid=?";
	
	public synchronized boolean insert(final Friend friend) {
		try {
			getJdbcTemplate().update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection conn)throws SQLException {
					PreparedStatement ps = conn.prepareStatement(INSERT_SQL_STR, Statement.NO_GENERATED_KEYS);
					int cursor = 1;
					ps.setLong(cursor++,friend.getUid());
					ps.setLong(cursor++,friend.getFid());
					ps.setByte(cursor++,friend.getPass());
					ps.setString(cursor++,friend.getTime());
					ps.setString(cursor++,friend.getOther());
					return ps;
				}
			});
			return true;
		} catch (Exception e) {
			ServerLog.error("insert friend error",e);
			return false;
		}
	}
	
	public synchronized boolean delete(long uid,long fid) {
		try {
			getJdbcTemplate().update(DELETE_SQL_STR,uid,fid);
		} catch (DataAccessException e) {
			ServerLog.error("delete friend error",e);
			return false;
		}
		return true;
	}
	
	public boolean check(long uid,long fid) {
		int num = 0;
		try {
			num = getJdbcTemplate().queryForInt(SQL_COUNT_FRIEND,uid,fid);
		} catch (Exception e) {
			
		}
		return num > 0;
	}
	
	private boolean update(Friend friend){
		try {
			getJdbcTemplate().update(UPDATE_SQL_STR,friend.getPass(),friend.getUid(),friend.getFid());
		} catch (DataAccessException e) {
			ServerLog.error("save friend error",e);
			return false;
		}
		return true;
	}
	
	public synchronized boolean save(Friend friend) {
		if (check(friend.getUid(),friend.getFid())){
			return update(friend);
		}else{
			return insert(friend);
		}
	}
	
	public List<Friend> search(long uid) {
		List<Friend> friends = null;
		try {
			friends = getJdbcTemplate().query(SELECT_SQL_STR,row,uid);
		} catch (DataAccessException e) {
			
		}
		return friends;
	}
}
