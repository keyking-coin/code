package com.keyking.coin.service.dao.row;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.springframework.jdbc.core.RowMapper;

import com.keyking.coin.service.domain.deal.Revert;

public class RevertRow implements RowMapper<Revert> {

	@Override
	public Revert mapRow(ResultSet rs, int i) throws SQLException {
		Revert revert = new Revert();
		revert.setNeedSave(false);
		revert.setId(rs.getLong("id"));
		revert.setDependentId(rs.getLong("dependentId"));
		revert.setUid(rs.getLong("uid"));
		revert.setContext(rs.getString("context"));
		revert.setTar(rs.getLong("tar"));
		Timestamp ts = rs.getTimestamp("createTime");
		revert.setCreateTime(ts.toString().substring(0,19));
		revert.setRevoke(rs.getByte("_revoke") == 1);
		return revert;
	}
}
