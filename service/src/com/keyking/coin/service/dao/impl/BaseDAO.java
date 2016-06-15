package com.keyking.coin.service.dao.impl;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class BaseDAO extends JdbcDaoSupport{
		
	protected synchronized boolean check(String tableName,Object id){
		String sql = "select count(*) from " + tableName + " where id=?";
		int count = 0 ;
		try {
			count = getJdbcTemplate().queryForInt(sql,id);
		} catch (DataAccessException e) {
			
		}
		return count > 0;
	}
	
	public synchronized boolean delete(String tableName,Object id){
		String sql = "delete from " + tableName + " where id=?";
		try {
			int count = getJdbcTemplate().update(sql,id);
			return count > 0;
		} catch (DataAccessException e) {
			
		}
		return false;
	}
}
