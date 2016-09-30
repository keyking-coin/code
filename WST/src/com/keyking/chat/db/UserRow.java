package com.keyking.chat.db;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class UserRow implements RowMapper<UserData> {
	@Override
	public UserData mapRow(ResultSet arg0, int arg1) throws SQLException {
		UserData user = new UserData();
		user.setId(arg0.getLong("id"));
		user.setAccount(arg0.getString("account"));
		user.setPwd(arg0.getString("pwd"));
		user.setName(arg0.getString("name"));
		user.setNickName(arg0.getString("nickName"));
		user.setEmail(arg0.getString("email"));
		user.setTel(arg0.getString("tel"));
		user.setRegistTime(arg0.getString("registTime"));
		return user;
	}
}
