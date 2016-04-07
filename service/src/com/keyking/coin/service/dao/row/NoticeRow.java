package com.keyking.coin.service.dao.row;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.keyking.coin.service.domain.other.NoticeEntity;

public class NoticeRow implements RowMapper<NoticeEntity> {

	@Override
	public NoticeEntity mapRow(ResultSet arg0, int arg1) throws SQLException {
		NoticeEntity notice = new NoticeEntity();
		notice.setTime(arg0.getString("_time"));
		notice.setTitle(arg0.getString("title"));
		notice.setBody(arg0.getString("body"));
		notice.setType(arg0.getByte("type"));
		return notice;
	}
}
