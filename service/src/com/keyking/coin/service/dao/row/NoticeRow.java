package com.keyking.coin.service.dao.row;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.joda.time.DateTime;
import org.springframework.jdbc.core.RowMapper;

import com.keyking.coin.service.domain.other.NoticeEntity;
import com.keyking.coin.util.TimeUtils;

public class NoticeRow implements RowMapper<NoticeEntity> {

	@Override
	public NoticeEntity mapRow(ResultSet arg0, int arg1) throws SQLException {
		NoticeEntity notice = new NoticeEntity();
		long time = arg0.getLong("_time");
		DateTime dt = TimeUtils.getTime(time);
		notice.set_time(time);
		notice.setTime(dt.toString(TimeUtils.FORMAT_DAY));
		notice.setTitle(arg0.getString("title"));
		notice.setBody(arg0.getString("body"));
		notice.setType(arg0.getByte("type"));
		return notice;
	}
}
