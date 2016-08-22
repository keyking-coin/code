package com.keyking.coin.service.dao.impl;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.keyking.coin.service.dao.row.NoticeRow;
import com.keyking.coin.service.domain.other.NoticeEntity;

public class NoticeDAO extends BaseDAO {
	
	private static String SELECT_SQL_STR = "select * from notice where type=?";
	
	private static String SELECT_SQL_ONE = "select * from notice where _time=?";
	
	NoticeRow row = new NoticeRow();
	
	public List<NoticeEntity> search(int type) {
		List<NoticeEntity> notices = null;
		try {
			notices = getJdbcTemplate().query(SELECT_SQL_STR,row,type);
		} catch (DataAccessException e) {
			return null;
		}
		return notices;
	}
	
	public NoticeEntity search(long time) {
		NoticeEntity entity = null;
		try {
			entity = getJdbcTemplate().queryForObject(SELECT_SQL_ONE,row,time);
		} catch (DataAccessException e) {
			return null;
		}
		return entity;
	}
}
