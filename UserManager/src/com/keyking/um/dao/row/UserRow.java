package com.keyking.um.dao.row;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.springframework.jdbc.core.RowMapper;

import com.keyking.um.data.UserCharacter;

public class UserRow implements RowMapper<UserCharacter>{

	@Override
	public UserCharacter mapRow(ResultSet rs, int i) throws SQLException {
		UserCharacter user = new UserCharacter();
		user.setId(rs.getLong("id"));
		user.setAccount(rs.getString("account"));
		user.setPwd(rs.getString("pwd"));
		user.setName(rs.getString("name"));
		Timestamp ts = rs.getTimestamp("registTime");
		user.setRegistTime(ts.toString().substring(0,19));
		return user;
	}
}
 
 
 
