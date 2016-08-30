package com.joymeng.slg.domain.activity.data;

import org.w3c.dom.Element;

import com.joymeng.common.util.expression.ProtoExpression;
import com.joymeng.slg.domain.object.role.Role;

public class Condition {
	String value;
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void decode(Element e) {
		value    = e.getAttribute("value");
	}

	public boolean check(Role role) {
		String temp = value.replaceAll("uid",String.valueOf(role.getId()));
		try {
			Object result = ProtoExpression.ExecuteExpression(temp);
			return Boolean.parseBoolean(result.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
