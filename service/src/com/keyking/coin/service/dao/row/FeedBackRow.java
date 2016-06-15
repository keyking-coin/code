package com.keyking.coin.service.dao.row;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.keyking.coin.service.domain.fb.FeedBack;

public class FeedBackRow implements RowMapper<FeedBack> {

	@Override
	public FeedBack mapRow(ResultSet rs, int arg1) throws SQLException {
		FeedBack fb = new FeedBack();
		fb.setTime(rs.getString("time"));
		fb.setContent(rs.getString("content"));
		return fb;
	}
}
