package com.keyking.coin.service.dao;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.keyking.coin.util.Instances;
import com.keyking.coin.util.ServerLog;
import com.keyking.coin.util.XmlUtils;

public class PrimaryKey implements Instances{
	
	static PrimaryKey instance = new PrimaryKey();
	
	Map<String,Long> keys = new HashMap<String,Long>();
	
	Map<String,Object> lockers = new HashMap<String,Object>();
	
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
			lockers.put(table,new Object());
		}
	}
	
	public long key(TableName tableName){
		Object locker = lockers.get(tableName.getTable());
		synchronized (locker) {
			long value = 0;
			if (keys.containsKey(tableName.getTable())){
				value = keys.get(tableName.getTable()).longValue();
			}
			value ++;
			keys.put(tableName.getTable(),value);
			return value;
		}
	}
}
