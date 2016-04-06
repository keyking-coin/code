package com.keyking.admin.net.handler;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import com.keyking.admin.net.DataBuffer;
import com.keyking.admin.net.NetUtil;
import com.keyking.admin.net.resp.ResultCallBack;

public class NetConnectHandler extends IoHandlerAdapter {

	@Override
	public void messageReceived(IoSession session, Object message)throws Exception {
		if (message instanceof DataBuffer){
			DataBuffer data = (DataBuffer)message;
			String logicName = data.getUTF();
			ResultCallBack callBack = NetUtil.getInstance().getResultCallBack(logicName);
			if (callBack != null){
				int result = data.getInt();
				if (result == 0){
					callBack.succ(data);
				}else{
					callBack.fail(data);
				}
			}
		}
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		super.sessionClosed(session);
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		super.sessionCreated(session);
	}
	
}
