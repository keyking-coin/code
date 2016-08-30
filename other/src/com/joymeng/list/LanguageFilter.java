package com.joymeng.list;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import com.joymeng.services.utils.XmlUtils;

public class LanguageFilter {
	String id;
	List<ServerName> names = new ArrayList<ServerName>();
	
	
	public String getId() {
		return id;
	}

	public List<ServerName> getNames() {
		return names;
	}


	public void decode(Element le) {
		id = le.getAttribute("id");
	    Element[] snes = XmlUtils.getChildrenByName(le,"ServerName");
		for (int i = 0 ; i < snes.length ; i++){
			Element sne = snes[i];
			ServerName sn = new ServerName();
			sn.decode(sne);
			names.add(sn);
		}
	}


	public String check(int serverId) {
		for (int i = 0 ; i < names.size() ; i++){
			ServerName sn = names.get(i);
			if (sn.id == serverId){
				return sn.getName();
			}
		}
		return null;
	}
	
}
