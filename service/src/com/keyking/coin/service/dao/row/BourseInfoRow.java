package com.keyking.coin.service.dao.row;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.keyking.coin.service.domain.bourse.BourseInfo;

public class BourseInfoRow implements RowMapper<BourseInfo>{

	@Override
	public BourseInfo mapRow(ResultSet rs, int arg1) throws SQLException {
		BourseInfo info = new BourseInfo();
		info.setName(rs.getString("name"));
		info.setUrl(rs.getString("url"));
		info.setType(rs.getByte("type"));
		return info;
	}

}
