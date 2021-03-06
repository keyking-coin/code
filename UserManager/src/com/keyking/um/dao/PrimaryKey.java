package com.keyking.um.dao;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.keyking.util.Instances;
import com.keyking.util.ServerLog;
import com.keyking.util.XmlUtils;

public class PrimaryKey implements Instances{
	
	static PrimaryKey instance = new PrimaryKey();
	
	Map<String,Long> keys = new HashMap<String,Long>();
	
	public static PrimaryKey getInstance(){
		return instance;
	}
	
	public void load() throws Exception{
		File file = new File("conf/PrimaryKeys.xml");
		Document document = XmlUtils.load(file);
		Element element   = document.getDocumentElement();
		Element[] elements = XmlUtils.getChildrenByName(element,"PrimaryKey");
		for (int i = 0; i < elements.length; ++i) {
			String sql = XmlUtils.getAttribute(elements[i],"sql");
			String table = XmlUtils.getAttribute(elements[i],"table");
			long value = DB.getUserDao().getJdbcTemplate().queryForLong(sql + table);
			ServerLog.info("table <" + table + "> ----- PrimaryKey is ----> " + value);
			keys.put(table,value);
		}
	}
	
	public long key(String table){
		synchronized (keys) {
			long value = 0;
			if (keys.containsKey(table)){
				value = keys.get(table).longValue();
			}
			value ++;
			keys.put(table,value);
			return value;
		}
	}
}
