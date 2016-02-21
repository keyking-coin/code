package com.keyking.coin.service.net;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.resp.exp.NotIsSerializeEntity;

public class ParametersEntity {
	
	List<Object> values = new ArrayList<Object>();
	
	@SuppressWarnings("unchecked")
	public void serialize(DataBuffer out) throws Exception{
		for (Object obj : values){
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
				for (SerializeEntity entity : col){
					entity.serialize(out);
				}
			}else{
				throw new NotIsSerializeEntity(obj.getClass());
			}
		}
	}
	
	public void put(Object obj){
		values.add(obj);
	}
	
	public void put(int index , Object obj){
		values.add(index,obj);
	}
}
