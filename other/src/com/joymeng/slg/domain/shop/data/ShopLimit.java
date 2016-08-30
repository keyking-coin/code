package com.joymeng.slg.domain.shop.data;

import com.joymeng.Instances;
import com.joymeng.slg.dao.DaoData;
import com.joymeng.slg.dao.SqlData;

/**
 * 服务器限购数据
 * @author tanyong
 *
 */
public class ShopLimit implements DaoData,Instances {
	String id;
	int num;
	boolean savIng = false;
	
	public ShopLimit(){
		
	}
	
	public ShopLimit(String id){
		this.id = id;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}
	
	public void addNum(int num){
		this.num += num;
		this.num = Math.max(0,num);
	}
	
	@Override
	public String table() {
		return TABLE_RED_ALERT_SHOP_LIMIT;
	}

	@Override
	public String[] wheres() {
		return new String[]{RED_ALERT_GENERAL_ID};
	}

	@Override
	public boolean delete() {
		return false;
	}

	@Override
	public void insertData(SqlData data) {
		saveToData(data);
	}

	@Override
	public void save() {
		if (savIng){
			return;
		}
		savIng = true;
		taskPool.saveThread.addSaveData(this);
	}

	@Override
	public void loadFromData(SqlData data) {
		id     = data.getString(RED_ALERT_GENERAL_ID);
		num    = data.getInt(RED_ALERT_GENERAL_NUM);
	}

	@Override
	public void saveToData(SqlData data) {
		data.put(RED_ALERT_GENERAL_ID,id);
		data.put(RED_ALERT_GENERAL_NUM,num);
	}

	@Override
	public void over() {
		savIng = false;
	}

	@Override
	public boolean saving() {
		return savIng;
	}
}
