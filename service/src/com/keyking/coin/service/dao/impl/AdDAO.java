package com.keyking.coin.service.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;

import com.keyking.coin.service.dao.TableName;
import com.keyking.coin.service.dao.row.AdRow;
import com.keyking.coin.service.domain.ad.ADEntity;
import com.keyking.coin.util.ServerLog;

public class AdDAO extends BaseDAO {
	
	private static String INSERT_SQL_STR = "insert into ad (id,url,pic,rank)values(?,?,?,?)";
	private static String UPDATE_SQL_STR = "update ad set url=?,pic=?,rank=? where id=?";
	private static String SELECT_SQL_STR = "select * from ad where id=?";
	private static String SELECT_SQL_STR_MORE = "select * from ad where 1=1 order by rank asc";
	AdRow row = new AdRow();
	
	public synchronized boolean insert(final ADEntity ad) {
		try {
			getJdbcTemplate().update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection conn)throws SQLException {
					PreparedStatement ps = conn.prepareStatement(INSERT_SQL_STR, Statement.NO_GENERATED_KEYS);
					int cursor = 1;
					ps.setLong(cursor++,ad.getId());
					ps.setString(cursor++,ad.getUrl());
					ps.setString(cursor++,ad.getPic());
					ps.setInt(cursor++,ad.getRank());
					return ps;
				}
			});
			return true;
		} catch (Exception e) {
			ServerLog.error("insert ad error",e);
			return false;
		}
	}
	
	public ADEntity search(long id) {
		ADEntity ad = null;
		try {
			ad = getJdbcTemplate().queryForObject(SELECT_SQL_STR,row,id);
		} catch (DataAccessException e) {
			return null;
		}
		return ad;
	}
	
	public synchronized boolean save(ADEntity ad) {
		if (check(TableName.TABLE_NAME_AD.getTable(),ad.getId())){
			return update(ad);
		}else{
			return insert(ad);
		}
	}

	private synchronized boolean update(ADEntity ad) {
		try {
			getJdbcTemplate().update(UPDATE_SQL_STR,ad.getUrl(),ad.getPic(),ad.getRank(),ad.getId());
		} catch (DataAccessException e) {
			ServerLog.error("save ad error",e);
			return false;
		}
		return true;
	}
	
	public List<ADEntity> load(){
		List<ADEntity> ads = null;
		try {
			ads = getJdbcTemplate().query(SELECT_SQL_STR_MORE,row);
		} catch (DataAccessException e) {
			//e.printStackTrace();
		}
		return ads;
	}
}
