package com.keyking.coin.service.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;

import com.keyking.coin.service.dao.TableName;
import com.keyking.coin.service.dao.row.UserRow;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.util.ServerLog;

public class UserDAO extends BaseDAO {
	
	private static String INSERT_SQL_STR = "insert into users (id,account,pwd,face,nikeName,title,registTime,name,address,signature,recharge,bankAccount,credit,forbid,breach,favorites,use_permission,other,deposit,father,flags)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
	private static String UPDATE_SQL_STR = "update users set pwd=?,face=?,nikeName=?,title=?,registTime=?,name=?,address=?,age=?,identity=?,signature=?,recharge=?,bankAccount=?,seller=?,push=?,credit=?,forbid=?,breach=?,favorites=?,use_permission=?,other=?,deposit=?,father=?,broker=?,flags=? where id=?";

	private static String LOGIN_SQL_STR  = "select * from users where account=? and pwd=?";

	private static String CHECK_SQL_STR1 = "select * from users where account=?";
	
	private static String CHECK_SQL_STR2 = "select * from users where nikeName=?";
	
	private static String CHECK_SQL_STR3 = "select * from users where id=?";
	
	private static String LOAD_ALL_STR = "select * from users where 1=1";
	
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
	
	public UserCharacter checkNikeName(String nickName) {
		UserCharacter user = null;
		try {
			user = getJdbcTemplate().queryForObject(CHECK_SQL_STR2,userRow,nickName);
		} catch (DataAccessException e) {
			
		}
		return user;
	}
	
	public UserCharacter search(long id) {
		UserCharacter user = null;
		try {
			user = getJdbcTemplate().queryForObject(CHECK_SQL_STR3,userRow,id);
		} catch (DataAccessException e) {
			//e.printStackTrace();
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
					ps.setString(cursor++,user.getAccount());
					ps.setString(cursor++,user.getPwd());
					ps.setString(cursor++,user.getFace());
					ps.setString(cursor++,user.getNikeName());
					ps.setString(cursor++,user.getTitle());
					ps.setString(cursor++,user.getRegistTime());
					ps.setString(cursor++,user.getName());
					ps.setString(cursor++,user.serializeAddresses());
					ps.setString(cursor++,user.getSignature());
					ps.setString(cursor++,user.getRecharge().serialize());
					ps.setString(cursor++,user.getBankAccount().serialize());
					ps.setString(cursor++,user.getCredit().serialize());
					ps.setString(cursor++,user.getForbid().serialize());
					ps.setByte(cursor++,user.getBreach());
					ps.setString(cursor++,user.serializeFavorites());
					ps.setString(cursor++,user.getPermission().serialize());
					ps.setString(cursor++,user.getOther());
					ps.setFloat(cursor++,user.getDeposit());
					ps.setLong(cursor++,user.getFather());
					ps.setString(cursor++,user.serializeFlags());
					return ps;
				}
			});
			return true;
		} catch (Exception e) {
			ServerLog.error("insert user error",e);
			return false;
		}
	}
	
	public synchronized boolean update(UserCharacter user){
		try {
			getJdbcTemplate().update(UPDATE_SQL_STR,user.getPwd(),
					          user.getFace(),user.getNikeName(),
					          user.getTitle(),user.getRegistTime(),
					          user.getName(),user.serializeAddresses(),
					          user.getAge(),user.getIdentity(),
					          user.getSignature(),user.getRecharge().serialize(),
					          user.getBankAccount().serialize(),user.serializeUser(),
					          user.getPush(),user.getCredit().serialize(),
					          user.getForbid().serialize(),user.getBreach(),
					          user.serializeFavorites(),user.getPermission().serialize(),
					          user.getOther(),user.getDeposit(),
					          user.getFather(),user.getBroker(),
					          user.serializeFlags(),user.getId());
		} catch (DataAccessException e) {
			ServerLog.error("save user error",e);
			return false;
		}
		return true;
	}
	
	public synchronized boolean save(UserCharacter user) {
		if (check(TableName.TABLE_NAME_USER.getTable(),user.getId())){
			return update(user);
		}else{
			return insert(user);
		}
	}
	
	public void searchFuzzy(String key, List<UserCharacter> users) {
		StringBuffer sb = new StringBuffer();
		sb.append("select * from users where ");
		if (users.size() > 0){
			sb.append("id not in(");
			for (UserCharacter user : users){
				sb.append(user.getId() + ",");
			}
			sb.deleteCharAt(sb.length()-1);
			sb.append(") and ");
		}
		sb.append("(account like \'%" + key + "\'");
		sb.append(" or account like \'%" + key + "%\'");
		sb.append(" or account like \'" + key + "%\'");
		sb.append(" or name like \'%" + key + "\'");
		sb.append(" or name like \'%" + key + "%\'");
		sb.append(" or name like \'" + key + "%\'");
		sb.append(" or nikeName like \'%" + key + "\'");
		sb.append(" or nikeName like \'%" + key + "%\'");
		sb.append(" or nikeName like \'" + key + "%\')");
		List<UserCharacter> temps = getJdbcTemplate().query(sb.toString(),userRow);
		if (temps != null){
			for (UserCharacter t : temps){
				boolean flag = true;
				for (UserCharacter user : users){
					if (user.getId() == t.getId()){
						flag = false;
						break;
					}
				}
				if (flag){
					users.add(t);
				}
			}
		}
	}
	
	public List<UserCharacter> loadAll() {
		List<UserCharacter> users = null;
		try {
			users = getJdbcTemplate().query(LOAD_ALL_STR,userRow);
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
		return users;
	}
}
 
 
 
