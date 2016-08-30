package com.joymeng.http.dao.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

import com.joymeng.common.util.JsonUtil;
import com.joymeng.slg.dao.SqlData;
import com.joymeng.slg.domain.object.army.data.Army;

public class WebArmyDAO extends SimpleJdbcDaoSupport{
	List<Map<String, Object>> fields = null;
	public Army search(String id) throws Exception{
		SqlData data = getData("army","id",id);
		if (data == null){
			return null;
		}
		Army army = new Army();
		for (int i = 0 ; i < fields.size() ; i++){
			Map<String,Object> map = fields.get(i);
			String fieldName = map.get("Field").toString();
			Object value = data.get(fieldName);
			Field field = null;
			try {
				field = Army.class.getDeclaredField(fieldName);
			} catch (Exception e) {
				//e.printStackTrace();
			}
			if (field != null){
				field.setAccessible(true);
				Object _value = null;
				if (field.getType() == String.class){
					_value = value.toString();
				}else if (field.getType() == byte.class){
					String str = value.toString();
					int index = str.lastIndexOf(".");
					if (index > 0){
						str = str.substring(0,index);
					}
					_value = Byte.valueOf(str);
				}else if (field.getType() == float.class){
					_value = Float.valueOf(value.toString());
				}else if (field.getType() == List.class){
					_value = JsonUtil.JsonToObjectList(value.toString(),String.class);
				}else if (field.getType() == int.class){
					String str = value.toString();
					int index = str.lastIndexOf(".");
					if (index > 0){
						str = str.substring(0,index);
					}
					_value = Integer.valueOf(str);
				}else{
					_value = value;
				}
				field.set(army,_value);
			}
		}
		return army;
	}
	
	public SqlData getData(String table,Object... params) {
		StringBuffer sqlbuff = new StringBuffer(256);
		sqlbuff.append("select * from ").append(table).append(" where 1=1");
		Object[] objs = new Object[params.length / 2];
		for (int i = 0 ; i < params.length ; i += 2) {
			sqlbuff.append(" and " + params[i] + "=?");
			objs[i / 2] = params[i+1];
		}
		String sql = sqlbuff.toString();
		List<SqlData> list = getSqlDatas(sql,objs);
		if (list != null && list.size() > 0) {
			return list.get(0);
		} else{
			return null;
		}
	}
	
	private List<SqlData> getSqlDatas(String sql,Object... objs){
		List<Map<String, Object>> list = getSimpleJdbcTemplate().queryForList(sql,objs);
		if (list != null && list.size() > 0) {
			List<SqlData> result = new ArrayList<SqlData>();
			for (int i = 0 ; i < list.size() ; i ++) {
				Map<String, Object> lis = list.get(i);
				result.add(new SqlData(lis));
			}
			return result;
		} else{
			return null;
		}
	}
	
	public void initFields() {
		fields = getSimpleJdbcTemplate().queryForList("desc army");
	}
}
