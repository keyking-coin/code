package com.keyking.coin.service.dao.row;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.keyking.coin.service.domain.broker.UserBroker;

public class UserBrokerRow implements RowMapper<UserBroker> {

	@Override
	public UserBroker mapRow(ResultSet rs, int i) throws SQLException {
		UserBroker ub = new UserBroker();
		ub.setUid(rs.getLong("uid"));
		ub.setBid(rs.getLong("bid"));
		ub.setAccount(rs.getString("account"));
		ub.setTel(rs.getString("tel"));
		return ub;
	}

}
