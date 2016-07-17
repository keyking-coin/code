package com.keyking.coin.service.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;

import com.keyking.coin.service.dao.TableName;
import com.keyking.coin.service.dao.row.DealRow;
import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.util.ServerLog;

public class DealDAO extends BaseDAO {
	
	private static String INSERT_SQL_STR = "insert into deal (id,uid,sellFlag,type,bourse,name,price,monad,num,validTime,createTime,other,_revoke,needDeposit,helpFlag,lastIssue,_lock)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
	private static String UPDATE_SQL_STR = "update deal set type=?,bourse=?,name=?,price=?,monad=?,num=?,validTime=?,other=?,_revoke=?,needDeposit=?,helpFlag=?,lastIssue=?,_lock=? where id=?";
	
	private static String SELECT_SQL_STR_ONE = "select * from deal where id=?";
	
	private static String LOAD_ALL_STR = "select * from deal where 1=1";
	
	DealRow row = new DealRow();
	
	public synchronized boolean insert(final Deal deal) {
		try {
			getJdbcTemplate().update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection conn)throws SQLException {
					PreparedStatement ps = conn.prepareStatement(INSERT_SQL_STR, Statement.NO_GENERATED_KEYS);
					int cursor = 1;
					ps.setLong(cursor++,deal.getId());
					ps.setLong(cursor++,deal.getUid());
					ps.setByte(cursor++, deal.getSellFlag());
					ps.setByte(cursor++,deal.getType());
					ps.setString(cursor++,deal.getBourse());
					ps.setString(cursor++,deal.getName());
					ps.setFloat(cursor++,deal.getPrice());
					ps.setString(cursor++,deal.getMonad());
					ps.setInt(cursor++,deal.getNum());
					ps.setString(cursor++,deal.getValidTime());
					ps.setString(cursor++,deal.getCreateTime());
					ps.setString(cursor++,deal.getOther());
					ps.setByte(cursor++,(byte)(deal.isRevoke() ? 1 : 0));
					ps.setFloat(cursor++,deal.getNeedDeposit());
					ps.setByte(cursor++,deal.getHelpFlag());
					ps.setString(cursor++,deal.getLastIssue());
					ps.setByte(cursor++,(byte)(deal.isLock() ? 1 : 0));
					return ps;
				}
			});
			return true;
		} catch (Exception e) {
			ServerLog.error("insert deal error",e);
			return false;
		}
	}
	
	public synchronized boolean update(Deal deal) {
		try {
			getJdbcTemplate().update(UPDATE_SQL_STR,
					deal.getType(),deal.getBourse(),
					deal.getName(),deal.getPrice(),
					deal.getMonad(),deal.getNum(),
					deal.getValidTime(),deal.getOther(),
					deal.isRevoke()?1:0,deal.getNeedDeposit(),
					deal.getHelpFlag(),deal.getLastIssue(),
					deal.isLock()?1:0,deal.getId());
		} catch (DataAccessException e) {
			ServerLog.error("save deal error",e);
			return false;
		}
		return true;
	}
	
	public synchronized boolean save(Deal deal) {
		if (check(TableName.TABLE_NAME_DEAL.getTable(),deal.getId())){
			return update(deal);
		}else{
			return insert(deal);
		}
	}
	
	public Deal search(long id) {
		Deal deal = null;
		try {
			deal = getJdbcTemplate().queryForObject(SELECT_SQL_STR_ONE,row,id);
		} catch (DataAccessException e) {
			
		}
		return deal;
	}
	
	public List<Deal> loadAll() {
		List<Deal> deals = null;
		try {
			deals = getJdbcTemplate().query(LOAD_ALL_STR,row);
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
		return deals;
	}
	
	public List<Deal> searchAll(String start , String end) {
		List<Deal> deals = null;
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("select * from deal where unix_timestamp(createTime) >= ");
			sb.append("unix_timestamp(\'" + start + "\') and unix_timestamp(createTime) <= ");
			sb.append("unix_timestamp(\'" + end + "\')");
			deals = getJdbcTemplate().query(sb.toString(),row);
		} catch (DataAccessException e) {
			
		}
		return deals;
	}
}
