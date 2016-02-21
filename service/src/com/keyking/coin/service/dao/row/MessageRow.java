package com.keyking.coin.service.dao.row;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.springframework.jdbc.core.RowMapper;

import com.keyking.coin.service.domain.friend.Message;

public class MessageRow implements RowMapper<Message> {

	@Override
	public Message mapRow(ResultSet rs, int arg1) throws SQLException {
		Message message = new Message();
		message.setId(rs.getLong("id"));
		message.setActors(rs.getString("actors"));
		message.setSendId(rs.getLong("sendId"));
		Timestamp ts = rs.getTimestamp("time");
		message.setTime(ts.toString().substring(0,19));
		message.setContent(rs.getString("content"));
		message.setType(rs.getByte("type"));
		message.setShowTime(rs.getByte("showTime"));
		return message;
	}

}
