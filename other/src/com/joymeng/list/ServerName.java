package com.joymeng.list;

import org.w3c.dom.Element;

public class ServerName {
	int id;
	String name;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void decode(Element sne) {
		id = Integer.parseInt(sne.getAttribute("id"));
		name = sne.getAttribute("name");
	}
}
