package com.keyking.coin.service.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;

import com.keyking.coin.service.dao.TableName;
import com.keyking.coin.service.dao.row.BrokerRow;
import com.keyking.coin.service.domain.broker.Broker;

public class BrokerDAO extends BaseDAO {
	
	private static String INSERT_SQL_STR  = "insert into brokers (id,name,des)values(?,?,?)";
	private static String SEARCH_SQL_STR  = "select * from brokers where id=?";
	private static String UPDATE_SQL_STR  = "update brokers set name=?,des=? where id=?";
	BrokerRow brokerRow = new BrokerRow();
	
	
	public synchronized boolean insert(final Broker broker) {
		try {
			getJdbcTemplate().update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection conn)throws SQLException {
					PreparedStatement ps = conn.prepareStatement(INSERT_SQL_STR, Statement.NO_GENERATED_KEYS);
					int cursor = 1;
					ps.setLong(cursor++,broker.getId());
					ps.setString(cursor++,broker.getName());
					ps.setString(cursor++,broker.getDes());
					return ps;
				}
			});
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public Broker search(long id) {
		Broker broker = null;
		try {
			broker = getJdbcTemplate().queryForObject(SEARCH_SQL_STR,brokerRow,id);
		} catch (DataAccessException e) {
			
		}
		return broker;
	}
	
	public synchronized boolean update(Broker broker){
		try {
			getJdbcTemplate().update(UPDATE_SQL_STR,broker.getName(),broker.getDes(),broker.getId());
		} catch (DataAccessException e) {
			return false;
		}
		return true;
	}
	
	public synchronized boolean save(Broker broker) {
		if (check(TableName.TABLE_NAME_BROKER.getTable(),broker.getId())){
			return update(broker);
		}else{
			return insert(broker);
		}
	}
	
	public synchronized boolean delete(Broker broker) {
		return delete("brokers",broker.getId());
	}
	
	public List<Broker> loadAll() {
		List<Broker> result = null;
		try {
			result = getJdbcTemplate().query("select * from brokers where 1=1",brokerRow);
		} catch (DataAccessException e) {
			
		}
		return result;
	}
}
