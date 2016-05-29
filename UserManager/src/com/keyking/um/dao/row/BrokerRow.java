package com.keyking.um.dao.row;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import com.keyking.um.data.Broker;

public class BrokerRow implements RowMapper<Broker> {

	@Override
	public Broker mapRow(ResultSet arg0, int arg1) throws SQLException {
		Broker broker = new Broker();
		
		return broker;
	}

}
