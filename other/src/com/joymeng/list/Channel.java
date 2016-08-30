package com.joymeng.list;

import org.w3c.dom.Element;

public class Channel {
	String id;
	String name;
	
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public void decode(Element ce) {
		id   = ce.getAttribute("id");
		name = ce.getAttribute("name");
	}
}
