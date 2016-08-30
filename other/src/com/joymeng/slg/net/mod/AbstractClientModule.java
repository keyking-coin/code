package com.joymeng.slg.net.mod;

import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.net.ParametersEntity;

/**
 * 模块抽象类
 * @author tanyong
 *
 */
public abstract class AbstractClientModule implements ClientModule {
	
	protected ParametersEntity params = new ParametersEntity();

	public ParametersEntity getParams() {
		return params;
	}
	
	@Override
	public void serialize(JoyBuffer out) {
		try {
			params.serialize(out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void add(Object obj){
		params.put(obj);
	}
	
	public void add(int index,Object obj){
		params.put(index,obj);
	}
}
