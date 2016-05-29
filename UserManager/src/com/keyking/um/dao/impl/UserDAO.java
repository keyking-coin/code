package com.keyking.um.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;

import com.keyking.um.dao.TableName;
import com.keyking.um.dao.row.UserRow;
import com.keyking.um.data.UserCharacter;

public class UserDAO extends BaseDAO {
	
	private static String INSERT_SQL_STR = "insert into " + TableName.TABLE_NAME_USER.getKey() + " (id,fid,account,pwd,name,registTime)values(?,?,?,?,?,?)";
	
	private static String UPDATE_SQL_STR = "update " + TableName.TABLE_NAME_USER.getKey() + " set fid=?,pwd=?,name=? where id=?";

	private static String LOGIN_SQL_STR  = "select * from " + TableName.TABLE_NAME_USER.getKey() + " where account=? and pwd=?";

	private static String CHECK_SQL_STR1 = "select * from " + TableName.TABLE_NAME_USER.getKey() + " where account=?";
	
	private static String CHECK_SQL_STR3 = "select * from " + TableName.TABLE_NAME_USER.getKey() + " where id=?";
	
	private UserRow userRow = new UserRow();
	
	public UserCharacter search(String str) {
		UserCharacter user = null;
		try {
			user = getJdbcTemplate().queryForObject(CHECK_SQL_STR1,userRow,str);
		} catch (DataAccessException e) {
			return null;
		}
		return user;
	}
	
	public UserCharacter search(long id) {
		UserCharacter user = null;
		try {
			user = getJdbcTemplate().queryForObject(CHECK_SQL_STR3,userRow,id);
		} catch (DataAccessException e) {
			return null;
		}
		return user;
	}
	
	public UserCharacter login(String accout, String pwd) {
		UserCharacter user = null;
		try {
			user = getJdbcTemplate().queryForObject(LOGIN_SQL_STR,userRow,accout,pwd);
		} catch (DataAccessException e) {
			
		}
		return user;
	}

	public synchronized boolean insert(final UserCharacter user) {
		try {
			getJdbcTemplate().update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection conn)throws SQLException {
					PreparedStatement ps = conn.prepareStatement(INSERT_SQL_STR, Statement.NO_GENERATED_KEYS);
					int cursor = 1;
					ps.setLong(cursor++,user.getId());
					ps.setLong(cursor++,user.getFid());
					ps.setString(cursor++,user.getAccount());
					ps.setString(cursor++,user.getPwd());
					ps.setString(cursor++,user.getName());
					ps.setString(cursor++,user.getRegistTime());
					return ps;
				}
			});
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public synchronized boolean update(UserCharacter user){
		try {
			getJdbcTemplate().update(UPDATE_SQL_STR,user.getFid(),user.getPwd(),user.getName(),user.getId());
		} catch (DataAccessException e) {
			return false;
		}
		return true;
	}
	
	public synchronized boolean save(UserCharacter user) {
		if (check(TableName.TABLE_NAME_USER.getKey(),user.getId())){
			return update(user);
		}else{
			return insert(user);
		}
	}

	public List<UserCharacter> loadAll() {
		List<UserCharacter> users = null;
		try {
			users = getJdbcTemplate().query("select * from " + TableName.TABLE_NAME_USER.getKey() + " where 1=1",userRow);
		} catch (DataAccessException e) {
			
		}
		return users;
	}
}
 
 
 
