package com.keyking.coin.service.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.keyking.coin.service.dao.row.DealOrderRow;
import com.keyking.coin.service.domain.deal.DealOrder;
import com.keyking.coin.util.ServerLog;

public class DealOrderDAO extends JdbcDaoSupport {
	
	private static String INSERT_SQL_STR = "insert into deal_order (id,dealId,buyId,times,num,price,appraise,state,helpFlag,_revoke)values(?,?,?,?,?,?,?,?,?,?)";
	
	private static String UPDATE_SQL_STR = "update deal_order set dealId=?,buyId=?,times=?,num=?,price=?,appraise=?,state=?,helpFlag=?,_revoke=? where id=?";
	
	private static String SELECT_SQL_STR = "select * from deal_order where dealId=?";
	
	DealOrderRow row = new DealOrderRow();
	
	public synchronized boolean insert(final DealOrder order) {
		try {
			getJdbcTemplate().update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection conn)throws SQLException {
					PreparedStatement ps = conn.prepareStatement(INSERT_SQL_STR, Statement.NO_GENERATED_KEYS);
					int cursor = 1;
					ps.setLong(cursor++,order.getId());
					ps.setLong(cursor++,order.getDealId());
					ps.setLong(cursor++,order.getBuyId());
					ps.setString(cursor++,order.timesTostr());
					ps.setInt(cursor++,order.getNum());
					ps.setFloat(cursor++,order.getPrice());
					ps.setString(cursor++,order.appraiseSerialize());
					ps.setByte(cursor++,order.getState());
					ps.setByte(cursor++,order.getHelpFlag());
					ps.setByte(cursor++,(byte)(order.isRevoke() ? 1 : 0));
					return ps;
				}
			});
			return true;
		} catch (Exception e) {
			ServerLog.error("insert dealOrder error",e);
			return false;
		}
	}
	
	public synchronized boolean update(DealOrder order) {
		try {
			getJdbcTemplate().update(UPDATE_SQL_STR,
					order.getDealId(),order.getBuyId(),
					order.timesTostr(),order.getNum(),
					order.getPrice(),order.appraiseSerialize(),
					order.getState(),order.getHelpFlag(),
					(order.isRevoke() ? 1 : 0),order.getId());
		} catch (DataAccessException e) {
			ServerLog.error("save dealOrder error",e);
			return false;
		}
		return true;
	}
	
	public synchronized boolean save(DealOrder order) {
		if (check(order.getId())){
			return update(order);
		}else{
			return insert(order);
		}
	}
	
	public List<DealOrder> search(long dealId){
		List<DealOrder> orders = null;
		try {
			orders = getJdbcTemplate().query(SELECT_SQL_STR,row,dealId);
		} catch (DataAccessException e) {
			
		}
		return orders;
	}
	
	private static String CHECK_COUNT_SQL = "select count(*) from deal_order where id=?";
	private synchronized boolean check(long uid){
		int count = 0 ;
		try {
			count = getJdbcTemplate().queryForInt(CHECK_COUNT_SQL,uid);
		} catch (DataAccessException e) {
			
		}
		return count > 0;
	}
}
