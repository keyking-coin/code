package com.keyking.coin.service.dao.row;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.keyking.coin.service.domain.deal.DealOrder;

public class DealOrderRow implements RowMapper<DealOrder> {

	@Override
	public DealOrder mapRow(ResultSet rs, int arg1) throws SQLException {
		DealOrder order = new DealOrder();
		order.setId(rs.getLong("id"));
		order.setDealId(rs.getLong("dealId"));
		order.setBuyId(rs.getLong("buyId"));
		order.strToTimes(rs.getString("times"));
		order.setNum(rs.getInt("num"));
		order.setPrice(rs.getFloat("price"));
		order.appraiseDeserialize(rs.getString("appraise"));
		order.setState(rs.getByte("state"));
		order.setHelpFlag(rs.getByte("helpFlag"));
		order.setRevoke(rs.getByte("_revoke") == 1);
		return order;
	}

}
