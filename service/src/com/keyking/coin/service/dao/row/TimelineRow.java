package com.keyking.coin.service.dao.row;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.keyking.coin.service.domain.time.TimeLine;

public class TimelineRow implements RowMapper<TimeLine> {

	@Override
	public TimeLine mapRow(ResultSet rs, int arg1) throws SQLException {
		TimeLine time = new TimeLine();
		time.setId(rs.getLong("id"));
		time.setType(rs.getByte("type"));
		time.setTitle(rs.getString("title"));
		Date date = rs.getDate("createTime");
		//Timestamp ts = rs.getTimestamp("createTime");
		time.setTime(date.toString());
		time.strToContents(rs.getString("contents"));
		return time;
	}

}
