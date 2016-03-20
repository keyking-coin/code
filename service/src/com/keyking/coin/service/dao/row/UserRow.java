package com.keyking.coin.service.dao.row;


import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.keyking.coin.service.domain.user.UserCharacter;

public class UserRow implements RowMapper<UserCharacter>{

	@Override
	public UserCharacter mapRow(ResultSet rs, int i) throws SQLException {
		UserCharacter user = new UserCharacter();
		user.setId(rs.getLong("id"));
		user.setAccount(rs.getString("account"));
		user.setPwd(rs.getString("pwd"));
		user.setFace(rs.getString("face"));
		user.setNikeName(rs.getString("nikeName"));
		user.setTitle(rs.getString("title"));
		user.setRegistTime(rs.getString("registTime"));
		user.setName(rs.getString("name"));
		user.deserializeAddresses(rs.getString("address"));
		user.setAge(rs.getInt("age"));
		user.setIdentity(rs.getString("identity"));
		user.deserializeUser(rs.getString("seller"));
		user.setPush(rs.getByte("push"));
		user.setSignature(rs.getString("signature"));
		String str = rs.getString("recharge");
		user.getRecharge().deserialize(str);
		str = rs.getString("bankAccount");
		user.getBankAccount().deserialize(str);
		str = rs.getString("credit");
		user.getCredit().deserialize(str);
		str = rs.getString("forbid");
		user.getForbid().deserialize(str);
		user.setBreach(rs.getByte("breach"));
		str = rs.getString("favorites");
		user.deserializeFavorites(str);
		str = rs.getString("use_permission");
		user.getPermission().deserialize(str);
		user.setOther(rs.getString("other"));
		return user;
	}
}
 
 
 
