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
		time.setBourseFlag(rs.getByte("flag"));
		time.setBourse(rs.getString("bourse"));
		time.setUrl(rs.getString("url"));
		Date date = rs.getDate("startTime");
		time.setStartTime(date.toString());
		date = rs.getDate("endTime");
		time.setEndTime(date.toString());
		return time;
	}
}
