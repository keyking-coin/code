package com.joymeng.slg.net;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.joymeng.log.GameLog;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.exp.SerializeEntityError;

public class ParametersEntity {
	
	List<Object> values = new ArrayList<Object>();
	
	public void serialize(JoyBuffer out) throws Exception{
		for (int i = 0 ; i < values.size() ; i++){
			Object obj = values.get(i);
			serialize(obj,out);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void serialize(Object obj,JoyBuffer out) throws Exception{
		if (obj instanceof Byte){
			out.put(Byte.parseByte(obj.toString()));
		}else if (obj instanceof Short){
			out.putShort(Short.parseShort(obj.toString()));
		}else if (obj instanceof Integer){
			out.putInt(Integer.parseInt(obj.toString()));
		}else if (obj instanceof Long){
			out.putLong(Long.parseLong(obj.toString()));
		}else if (obj instanceof String){
			out.putPrefixedString(obj.toString(),JoyBuffer.STRING_TYPE_SHORT);
		}else if (obj instanceof Boolean){
			Boolean flag = (Boolean)obj;
			out.put((byte)(flag.booleanValue() ? 1 : 0));
		}else if (obj instanceof Object[]){
			Object[] objs = (Object[])obj;
			out.putInt(objs.length);
			for (int i = 0 ; i < objs.length ; i++){
				Object o = objs[i];
				serialize(o,out);
			}
		}else if (obj instanceof byte[]){
			byte[] objs = (byte[])obj;
			out.putInt(objs.length);
			out.put(objs);
		}else if (obj instanceof SerializeEntity){
			SerializeEntity serialize = (SerializeEntity)obj;
			serialize.serialize(out);
		}else if (obj instanceof Collection<?>){
			Collection<Object> col = (Collection<Object>)obj;
			out.putInt(col.size());
			for (Object o : col){
				serialize(o,out);
			}
		}else{
			SerializeEntityError error = new SerializeEntityError(obj.getClass());
			GameLog.error(error.getMessage(),error);
			throw error;
		}
	}
	
	public void put(Object obj){
		if (obj instanceof Collection<?>){
			List<Object> temp = new ArrayList<Object>();
			temp.addAll((Collection<?>)obj);
			values.add(temp);
		}else if (obj instanceof Object[]){
			Object[] src = (Object[])obj;
			Object[] temp = new Object[src.length];
			System.arraycopy(src, 0,temp,0,src.length);
			values.add(temp);
		}else{
			values.add(obj);
		}
	}
	
	public void put(int index,Object obj){
		if (obj instanceof Collection<?>){
			List<Object> temp = new ArrayList<Object>();
			temp.addAll((Collection<?>)obj);
			values.set(index,temp);
		}else if (obj instanceof Object[]){
			Object[] src = (Object[])obj;
			Object[] temp = new Object[src.length];
			System.arraycopy(src, 0,temp,0,src.length);
			values.set(index,temp);
		}else{
			values.set(index,obj);
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T> T get(int index){
		return (T)values.get(index);
	}
	
	public void clear(){
		values.clear();
	}
	
	@Override
	public String toString() {
		return JSON.toJSONString(values);
	}
}
