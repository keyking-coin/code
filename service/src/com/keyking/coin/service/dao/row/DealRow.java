package com.keyking.coin.service.dao.row;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.springframework.jdbc.core.RowMapper;

import com.keyking.coin.service.domain.deal.Deal;

public class DealRow implements RowMapper<Deal> {

	@Override
	public Deal mapRow(ResultSet rs, int i) throws SQLException {
		Deal deal = new Deal();
		deal.setId(rs.getLong("id"));
		deal.setUid(rs.getLong("uid"));
		deal.setSellFlag(rs.getByte("sellFlag"));
		deal.setType(rs.getByte("type"));
		deal.setBourse(rs.getString("bourse"));
		deal.setName(rs.getString("name"));
		deal.setPrice(rs.getFloat("price"));
		deal.setMonad(rs.getString("monad"));
		deal.setNum(rs.getInt("num"));
		deal.setValidTime(rs.getString("validTime"));
		Timestamp ts = rs.getTimestamp("createTime");
		deal.setCreateTime(ts.toString().substring(0,19));
		deal.setOther(rs.getString("other"));
		deal.setRevoke(rs.getByte("_revoke") == 1);
		deal.setNeedDeposit(rs.getFloat("needDeposit"));
		deal.setHelpFlag(rs.getByte("helpFlag"));
		deal.setLastIssue(rs.getString("lastIssue"));
		return deal;
	}

}
