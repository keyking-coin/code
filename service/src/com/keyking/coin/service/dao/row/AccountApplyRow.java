package com.keyking.coin.service.dao.row;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.keyking.coin.service.domain.user.AccountApply;

public class AccountApplyRow implements RowMapper<AccountApply> {

	@Override
	public AccountApply mapRow(ResultSet rs, int arg1) throws SQLException {
		AccountApply apply = new AccountApply();
		apply.setId(rs.getLong("id"));
		apply.setBourse(rs.getString("bourse"));
		apply.setBankName(rs.getString("bankName"));
		apply.setTel(rs.getString("tel"));
		apply.setEmail(rs.getString("email"));
		apply.setIndentFront(rs.getString("indentFront"));
		apply.setIndentBack(rs.getString("indentBack"));
		apply.setBankFront(rs.getString("bankFront"));
		apply.setState(rs.getByte("state"));
		apply.setReason(rs.getString("reason"));
		return apply;
	}
}
