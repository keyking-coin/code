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
		String face = rs.getString("face");
		if (face.startsWith("http://")){
			int index = face.lastIndexOf("/");
			face = face.substring(index + 1,face.length());
		}
		user.setFace(face);
		user.setNikeName(rs.getString("nikeName"));
		user.setTitle(rs.getString("title"));
		String time = rs.getString("registTime");
		time = time.substring(0,19);
		user.setRegistTime(time);
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
		user.setDeposit(rs.getFloat("deposit"));
		user.setFather(rs.getLong("father"));
		user.setBroker(rs.getLong("broker"));
		user.deserializeFlags(rs.getString("flags"));
		return user;
	}
}
 
 
 
