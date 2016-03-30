package com.keyking.coin.service.system;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.keyking.coin.util.XmlUtils;

public class SystemContext {
	private static SystemContext instance = new SystemContext();
	
	public static SystemContext getInstance(){
		return instance;
	}
	
	public void load(){
		try {
			Document document = XmlUtils.load("content.xml");
			Element element   = document.getDocumentElement();
			XmlUtils.getChildByName(element,"notice");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
