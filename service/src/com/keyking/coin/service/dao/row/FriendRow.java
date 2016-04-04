package com.keyking.coin.service.dao.row;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.springframework.jdbc.core.RowMapper;

import com.keyking.coin.service.domain.friend.Friend;

public class FriendRow implements RowMapper<Friend> {

	@Override
	public Friend mapRow(ResultSet rs, int arg1) throws SQLException {
		Friend friend = new Friend();
		friend.setNeedSave(false);
		friend.setUid(rs.getLong("uid"));
		friend.setFid(rs.getLong("fid"));
		Timestamp ts = rs.getTimestamp("time");
		friend.setTime(ts.toString().substring(0,19));
		friend.setPass(rs.getByte("pass"));
		friend.setOther(rs.getString("other"));
		return friend;
	}

}
