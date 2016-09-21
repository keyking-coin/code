package com.joymeng.slg.net.resp;

import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyResponse;
import com.joymeng.slg.net.ParametersEntity;

@SuppressWarnings("serial")
public class CommunicateResp extends JoyResponse {
	
	ParametersEntity params = new ParametersEntity();

	public CommunicateResp(int protocolId) {
		super(123456);
		add(protocolId);
		add(JOY_RESP_SUCC);
	}

	@Override
	protected void _serialize(JoyBuffer out) {
		try {
			JoyBuffer temp = JoyBuffer.allocate(128);
			params.serialize(temp);
			MessageSendUtil.checkAndZip(out,temp.arrayToPosition(),getUserInfo(),false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void _deserialize(JoyBuffer joybuffer) {
		
	}
	
	public void add(Object obj){
		params.put(obj);
	}
	
	public void add(int index,Object obj){
		params.put(index,obj);
	}

	public void fail() {
		add(1,JOY_RESP_FAIL);
	}
	
	public boolean isSucc(){
		byte flag = params.get(1);
		return flag == JOY_RESP_SUCC;
	}
}
