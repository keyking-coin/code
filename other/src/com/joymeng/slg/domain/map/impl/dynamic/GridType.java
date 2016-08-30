package com.joymeng.slg.domain.map.impl.dynamic;

import com.joymeng.Instances;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.domain.object.IObject;

public class GridType implements Instances{
	
	Class<? extends IObject> type = null;
	
	long id;
	
	public Class<? extends IObject> getType() {
		return type;
	}
	
	public void setType(Class<? extends IObject> type) {
		this.type = type;
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T object(){
		if (type != null){
			return (T)world.getObject(type,id);
		}
		return null;
	}

	public void serialize(JoyBuffer out) {
		if (type == GarrisonTroops.class){
			out.putInt(1);//1 固定部队数据
			GarrisonTroops garrison = object();
			garrison.serialize(out);
		}else{
			out.putInt(2);//2 移动部队数据
			ExpediteTroops expedite = object();
			expedite.serialize(out);
		}
	}
}
