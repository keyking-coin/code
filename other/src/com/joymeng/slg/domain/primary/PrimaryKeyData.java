package com.joymeng.slg.domain.primary;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.joymeng.Const;
import com.joymeng.Instances;
import com.joymeng.log.GameLog;
import com.joymeng.services.utils.XmlUtils;
import com.joymeng.slg.dao.DaoData;

public class PrimaryKeyData implements Instances{
	
	private static PrimaryKeyData instance = new PrimaryKeyData();
	
	public static PrimaryKeyData getInstance(){
		return instance;
	}
	
	Map<String,AtomicLong> keyDatas = new HashMap<String,AtomicLong>();
	
	public void load(){
		try {
			Document document = XmlUtils.load(Const.CONF_PATH + "PrimaryKeyDatas.xml");
			Element element = document.getDocumentElement();
			Element[] elements = XmlUtils.getChildrenByName(element,"PrimaryKeyData");
			for (int i = 0; i < elements.length ; ++i) {
				String sql   = XmlUtils.getAttribute(elements[i],"sql");
				String table = XmlUtils.getAttribute(elements[i],"table");
				long num = dbMgr.getGameDao().getPrimaryKeyData(sql);
				GameLog.info("table <" + table + "> primaryKey is " + num);
				AtomicLong al = new AtomicLong(num);
				keyDatas.put(table,al);
			}
			AtomicLong ea = keyDatas.get(DaoData.TABLE_RED_ALERT_ROLEEXPEDITE);
			AtomicLong ga = keyDatas.get(DaoData.TABLE_RED_ALERT_GARRISON);
			long max = Math.max(ea.get(), ga.get());//去驻防部队和行军部队最大的编号,作为部队编号
			ea.set(max);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public long key(String table){
		AtomicLong  al =  keyDatas.get(table);
		if (al == null){
			al = new AtomicLong(0);
			keyDatas.put(table,al);
		}
		return al.incrementAndGet();
	}
}
