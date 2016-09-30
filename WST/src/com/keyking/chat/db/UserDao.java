package com.keyking.chat.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.keyking.chat.StartInit;

public class UserDao extends JdbcDaoSupport {
	static String INSERT_SQL_STR = "insert into _user (account,pwd,name,nickName,email,tel,registTime)values(?,?,?,?,?,?,?)";
	static String UPDATE_SQL_STR = "update _user set pwd=?,name=?,nickName=?,email=?,tel=? where id=?";
	static String LOGIN_SQL_STR  = "select * from _user where account=? and pwd=?";
	static String LOAD_SQL_STR   = "select * from _user where 1=1";
	UserRow row = new UserRow();
	
	public UserData loadOne(String account,String pwd){
		UserData user = null;
		try {
			user = getJdbcTemplate().queryForObject(LOGIN_SQL_STR,row,account,pwd);
		} catch (Exception e) {
			StartInit.context.log("not find user account=" + account);
		}
		return user;
	}
	
	public List<UserData> loadAll(){
		List<UserData> users = new ArrayList<UserData>();
		try {
			users = getJdbcTemplate().query(LOAD_SQL_STR,row);
		} catch (Exception e) {
			StartInit.context.log("load users because : " + e.getMessage());
		}
		return users;
	}
	
	public boolean insert(final UserData user) {
		try {
			KeyHolder key = new GeneratedKeyHolder();
			getJdbcTemplate().update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection conn)
						throws SQLException {
					PreparedStatement ps = conn.prepareStatement(INSERT_SQL_STR, Statement.RETURN_GENERATED_KEYS);
					int cursor = 1;
					ps.setString(cursor++, user.getAccount());
					ps.setString(cursor++, user.getPwd());
					ps.setString(cursor++, user.getName());
					ps.setString(cursor++, user.getNickName());
					ps.setString(cursor++, user.getEmail());
					ps.setString(cursor++, user.getTel());
					ps.setString(cursor++, user.getRegistTime());
					return ps;
				}
			}, key);
			long uid = key.getKey().longValue();
			user.setId(uid);
			StartInit.context.log("insert user ok id = " + uid);
			return true;
		} catch (Exception e) {
			StartInit.context.log("insert user error because : " + e.getMessage());
			return false;
		}
	}
	
	public boolean save(UserData user) {
		try {
			getJdbcTemplate().update(UPDATE_SQL_STR,user.getPwd(),user.getName(),user.getNickName(),user.getEmail(),user.getTel());
			StartInit.context.log("save user ok id = " + user.getId());
			return true;
		} catch (Exception e) {
			StartInit.context.log("save user error because : " + e.getMessage());
			return false;
		}
	}
}
