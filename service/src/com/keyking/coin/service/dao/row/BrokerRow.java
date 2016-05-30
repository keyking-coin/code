package com.keyking.coin.service.dao.row;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.keyking.coin.service.domain.broker.Broker;

public class BrokerRow implements RowMapper<Broker> {

	@Override
	public Broker mapRow(ResultSet rs, int arg1) throws SQLException {
		Broker broker = new Broker();
		broker.setId(rs.getLong("id"));
		broker.setName(rs.getString("name"));
		broker.setDes(rs.getString("des"));
		return broker;
	}

}
