package com.keyking.coin.service.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;

import com.keyking.coin.service.dao.TableName;
import com.keyking.coin.service.dao.row.MessageRow;
import com.keyking.coin.service.domain.friend.Message;
import com.keyking.coin.util.ServerLog;

public class MessageDAO extends BaseDAO {
	MessageRow row = new MessageRow();
	private static String INSERT_SQL_STR = "insert into message (id,actors,sendId,time,content,type,look,showTime)values(?,?,?,?,?,?,?,?)";
	private static String SELECT_SQL_STR = "select * from message where actors in (";
	private static String UPDATE_SQL_STR = "update message set look=? where id=?";
	
	public synchronized boolean insert(final Message message) {
		try {
			getJdbcTemplate().update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection conn)throws SQLException {
					PreparedStatement ps = conn.prepareStatement(INSERT_SQL_STR, Statement.NO_GENERATED_KEYS);
					int cursor = 1;
					ps.setLong(cursor++, message.getId());
					ps.setString(cursor++,message.getActors());
					ps.setLong(cursor++,message.getSendId());
					ps.setString(cursor++,message.getTime());
					ps.setString(cursor++,message.getContent());
					ps.setByte(cursor++,message.getType());
					ps.setByte(cursor++,message.getLook());
					ps.setByte(cursor++,message.isShowTime());
					return ps;
				}
			});
			return true;
		} catch (Exception e) {
			ServerLog.error("insert message error",e);
			return false;
		}
	}
	
	public boolean update(Message message) {
		try {
			getJdbcTemplate().update(UPDATE_SQL_STR,message.getLook(),message.getId());
		} catch (DataAccessException e) {
			ServerLog.error("save friend error",e);
			return false;
		}
		return true;
	}
	
	public synchronized boolean save(Message message) {
		if (check(TableName.TABLE_NAME_MESSAGE.getTable(),message.getId())){
			return update(message);
		}else{
			return insert(message);
		}
	}
	
	public List<Message> search(String ins) {
		List<Message> messages = null;
		try {
			messages = getJdbcTemplate().query(SELECT_SQL_STR + ins + ")",row);
		} catch (DataAccessException e) {
			
		}
		return messages;
	}
	
	public synchronized boolean delete(Message message) {
		try {
			return delete(TableName.TABLE_NAME_MESSAGE.getTable(),message.getId());
		} catch (DataAccessException e) {
			ServerLog.error("delete message error",e);
			return false;
		}
	}
}
