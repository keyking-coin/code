package com.keyking.admin.net;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.keyking.admin.net.exp.RequestSerializeException;

public class ParametersEntity {
	
	List<Object> values = new ArrayList<Object>();
	
	public void serialize(DataBuffer out) throws Exception{
		for (Object obj : values){
			serialize(out,obj);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void serialize(DataBuffer out,Object obj) throws Exception{
		if (obj instanceof Byte){
			out.put(Byte.parseByte(obj.toString()));
		}else if (obj instanceof Short){
			out.putShort(Short.parseShort(obj.toString()));
		}else if (obj instanceof Integer){
			out.putInt(Integer.parseInt(obj.toString()));
		}else if (obj instanceof Long){
			out.putLong(Long.parseLong(obj.toString()));
		}else if (obj instanceof String){
			out.putUTF(obj.toString());
		}else if (obj instanceof Boolean){
			boolean flag = ((Boolean)obj).booleanValue();
			out.put((byte)(flag ? 1 : 0));
		}else if (obj instanceof byte[]){
			byte[] datas = (byte[])obj;
			out.putInt(datas.length);
			out.put(datas);
		}else if (obj instanceof SerializeEntity){
			SerializeEntity serialize = (SerializeEntity)obj;
			serialize.serialize(out);
		}else if (obj instanceof Collection<?>){
			Collection<SerializeEntity> col = (Collection<SerializeEntity>)obj;
			out.putInt(col.size());
			for (Object data : col){
				serialize(out,data);
			}
		}else{
			throw new RequestSerializeException(obj);
		}
	}
	
	public void add(Object obj){
		values.add(obj);
	}
	
	public void add(int index , Object obj){
		values.add(index,obj);
	}
	
	public Object get(int index){
		return values.get(index);
	}
}
