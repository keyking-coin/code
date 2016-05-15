package com.keyking.coin.service.dao.row;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.keyking.coin.service.domain.email.Email;

public class EmailRow implements RowMapper<Email> {

	@Override
	public Email mapRow(ResultSet rs,int i) throws SQLException {
		Email email = new Email();
		email.setId(rs.getLong("id"));
		email.setType(rs.getByte("type"));
		email.setUserId(rs.getLong("userId"));
		email.setSenderId(rs.getLong("senderId"));
		email.setStatus(rs.getByte("status"));
		email.setTheme(rs.getString("theme"));
		email.setTime(rs.getString("time"));
		email.setContent(rs.getString("content"));
		return email;
	}

}
