package com.joymeng.slg.domain.actvt.data;

import com.joymeng.slg.domain.actvt.DTManager.DataKey;

public class Activity_newserverbuff implements DataKey 
{
	String id;
	String type;
	String content;
	int number;
	String typeId;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	@Override
	public Object key() {
		return id;
	}
}
