package com.joymeng.slg.net.resp;

import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyResponse;
import com.joymeng.slg.net.ParametersEntity;

public class TransmissionResp extends JoyResponse {
	
	private static final long serialVersionUID = 1L;
	
	ParametersEntity params = new ParametersEntity();
	
	public TransmissionResp() {
		super(9999);
	}

	@Override
	protected void _serialize(JoyBuffer out) {
		try {
			params.serialize(out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void _deserialize(JoyBuffer in) {
		
	}

	public ParametersEntity getParams() {
		return params;
	}
}
