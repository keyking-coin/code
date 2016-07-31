package com.keyking.coin.service.dao.row;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.keyking.coin.service.domain.ad.ADEntity;

public class AdRow implements RowMapper<ADEntity> {

	@Override
	public ADEntity mapRow(ResultSet rs, int arg1) throws SQLException {
		ADEntity ad =new ADEntity();
		ad.setId(rs.getLong("id"));
		ad.setUrl(rs.getString("url"));
		ad.setPic(rs.getString("pic"));
		return ad;
	}

}
