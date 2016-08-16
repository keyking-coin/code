package com.keyking.coin.service.dao;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.keyking.coin.util.Instances;
import com.keyking.coin.util.ServerLog;
import com.keyking.coin.util.XmlUtils;

public class PrimaryKey implements Instances{
	
	static PrimaryKey instance = new PrimaryKey();
	
	Map<String,AtomicLong> keys = new HashMap<String,AtomicLong>();
	
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
			AtomicLong al = new AtomicLong(value);
			keys.put(table,al);
		}
	}
	
	public long key(TableName tableName){
		AtomicLong al = null;
		String key = tableName.getTable();
		if (keys.containsKey(key)){
			al = keys.get(key);
		}else{
			al = new AtomicLong(0);
			keys.put(key,al);
		}
		return al.incrementAndGet();
	}
}
