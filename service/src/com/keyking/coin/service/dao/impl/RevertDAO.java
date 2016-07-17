package com.keyking.coin.service.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;

import com.keyking.coin.service.dao.TableName;
import com.keyking.coin.service.dao.row.RevertRow;
import com.keyking.coin.service.domain.deal.Revert;
import com.keyking.coin.util.ServerLog;

public class RevertDAO extends BaseDAO {
	
	private static String INSERT_SQL_STR = "insert into deal_revert (id,dependentId,uid,tar,context,createTime,_revoke)values(?,?,?,?,?,?,?)";
	private static String SELECT_SQL_STR = "select * from deal_revert where _revoke=0 and dependentId=?";
	private static String UPDATE_SQL_STR = "update deal_revert set _revoke=? where id=?";
	
	RevertRow row = new RevertRow();
	
	public synchronized boolean insert(final Revert revert) {
		try {
			getJdbcTemplate().update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection conn)throws SQLException {
					PreparedStatement ps = conn.prepareStatement(INSERT_SQL_STR, Statement.NO_GENERATED_KEYS);
					int cursor = 1;
					ps.setLong(cursor++,revert.getId());
					ps.setLong(cursor++,revert.getDependentId());
					ps.setLong(cursor++,revert.getUid());
					ps.setLong(cursor++,revert.getTar());
					ps.setString(cursor++,revert.getContext());
					ps.setString(cursor++,revert.getCreateTime());
					ps.setInt(cursor++, revert.isRevoke() ? 1 : 0);
					return ps;
				}
			});
			return true;
		} catch (Exception e) {
			ServerLog.error("insert revert error",e);
			return false;
		}
	}
	
	public List<Revert> search(long id) {
		List<Revert> reverts = null;
		try {
			reverts = getJdbcTemplate().query(SELECT_SQL_STR,row,id);
		} catch (DataAccessException e) {
			
		}
		return reverts;
	}
	
	public synchronized boolean update(Revert revert) {
		try {
			getJdbcTemplate().update(UPDATE_SQL_STR , revert.isRevoke() ? 1 : 0 , revert.getId());
		} catch (DataAccessException e) {
			ServerLog.error("save revert error",e);
			return false;
		}
		return true;
	}
	
	public synchronized boolean save(Revert revert) {
		if (check(TableName.TABLE_NAME_REVERT.getTable(),revert.getId())){
			return update(revert);
		}else{
			return insert(revert);
		}
	}
}
